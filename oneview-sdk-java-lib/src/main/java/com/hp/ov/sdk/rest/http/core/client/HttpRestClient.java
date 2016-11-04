/*
 * (C) Copyright 2015-2016 Hewlett Packard Enterprise Development LP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hp.ov.sdk.rest.http.core.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.hp.ov.sdk.constants.SdkConstants;
import com.hp.ov.sdk.dto.Patch;
import com.hp.ov.sdk.exceptions.SDKApplianceNotReachableException;
import com.hp.ov.sdk.exceptions.SDKBadRequestException;
import com.hp.ov.sdk.exceptions.SDKErrorEnum;
import com.hp.ov.sdk.exceptions.SDKForbiddenException;
import com.hp.ov.sdk.exceptions.SDKInternalServerErrorException;
import com.hp.ov.sdk.exceptions.SDKInvalidArgumentException;
import com.hp.ov.sdk.exceptions.SDKMethodNotAllowed;
import com.hp.ov.sdk.exceptions.SDKResourceNotFoundException;
import com.hp.ov.sdk.exceptions.SDKUnauthorizedException;
import com.hp.ov.sdk.rest.http.core.ContentType;
import com.hp.ov.sdk.rest.http.core.HttpMethod;
import com.hp.ov.sdk.rest.http.core.UrlParameter;
import com.hp.ov.sdk.util.JsonSerializer;
import com.hp.ov.sdk.util.UrlUtils;
import com.hpe.i3s.dto.deployment.goldenimage.GoldenImageFile;

public class HttpRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRestClient.class);

    /*
    TODO this could be replaced by a one way converter (Object to JSON).
    We can also consider to have a Map containing several converters and
    choose the appropriate converter according to the Content-Type.
     */
    private final JsonSerializer serializer;
    private final CloseableHttpClient httpClient;
    private final SDKConfiguration config;

    public HttpRestClient(SDKConfiguration sdkConfiguration, SSLContext sslContext) {
        this.config = sdkConfiguration;
        this.serializer = new JsonSerializer();
        this.httpClient = this.buildHttpClient(sslContext);
    }

    @VisibleForTesting
    protected HttpRestClient(SDKConfiguration sdkConfiguration,
            JsonSerializer serializer,
            CloseableHttpClient httpClient) {

        this.config = sdkConfiguration;
        this.serializer = serializer;
        this.httpClient = httpClient;
    }

    private CloseableHttpClient buildHttpClient(SSLContext sslContext) {
        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslFactory)
                .build();

        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        manager.setMaxTotal(config.getClientMaxNumberOfConnections());
        manager.setDefaultMaxPerRoute(config.getClientMaxNumberOfConnections());

        RequestConfig requestConfig = RequestConfig.custom()
                .setAuthenticationEnabled(false)
                .setContentCompressionEnabled(false)
                .setConnectTimeout(5 * 1000)
                .setConnectionRequestTimeout(5 * 1000)
                .setSocketTimeout(config.getClientSocketTimeout() * 1000)
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(manager)
                .build();
    }

    public void shutdown() {
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                LOGGER.info("I/O exception while shutting down HTTP client", e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }

    /**
     * Sends the request to OV and reads the response.
     *
     * @param sessionId OV session token ID.
     * @param request contains the details specific to the current request.
     *
     * @return
     *  A string representing the response data.
     * @throws
     *  SDKBadRequestException on unsupported method (PUT, GET..)
     **/
    public String sendRequest(String sessionId, Request request) throws SDKBadRequestException {
        URI uri = buildURI(request);

        LOGGER.debug("Using URL: " + uri.toString());

        if (request.getType() == null) {
            throw new SDKBadRequestException(SDKErrorEnum.badRequestError, SdkConstants.APPLIANCE);
        }

        HttpRequestBase requestBase;

        switch (request.getType()) {
            case POST:
                HttpPost post = new HttpPost(uri);
                fillRequestEntity(post, request);
                requestBase = post;
                break;
            case GET:
                requestBase = new HttpGet(uri);
                break;
            case PATCH:
                HttpPatch patch = new HttpPatch(uri);
                HttpEntity entity;

                // Switches uses empty patch requests (refresh)
                if (Patch.class.isInstance(request.getEntity())) {
                    entity = EntityBuilder.create()
                            .setText(serializer.toJsonArray((Patch) request.getEntity(),
                                    config.getOneViewApiVersion()))
                            .setContentType(toApacheContentType(request.getContentType())).build();
                } else {
                    entity = EntityBuilder.create()
                            .setText(serializer.toJson(request.getEntity(),
                                    config.getOneViewApiVersion()))
                            .setContentType(toApacheContentType(request.getContentType())).build();
                }
                patch.setEntity(entity);

                requestBase = patch;
                break;
            case PUT:
                HttpPut put = new HttpPut(uri);
                fillRequestEntity(put, request);
                requestBase = put;
                break;
            case DELETE:
                requestBase = new HttpDelete(uri);
                break;
            default:
                // Since request type is an ENUM, there is no way this will be executed
                LOGGER.error("Request type not supported.");
                throw new SDKBadRequestException(SDKErrorEnum.badRequestError, SdkConstants.APPLIANCE);
        }

        setRequestHeaders(sessionId, requestBase);

        return getResponse(sessionId, requestBase, request.isForceReturnTask(), request.getDownloadPath());
    }

    private void fillRequestEntity(HttpEntityEnclosingRequestBase base, Request request) {
        if (request.hasEntity()) {
            HttpEntity entity;
            ContentType contentType = request.getContentType();

            //TODO add support for Content-Type "application/json-patch+json" (PATCH)

            if (ContentType.APPLICATION_JSON == contentType) {
                String textEntity;

                if (request.getEntity() instanceof List) {
                    textEntity = serializer.toJsonArray((List) request.getEntity(), config.getOneViewApiVersion());
                } else {
                    textEntity = serializer.toJson(request.getEntity(), config.getOneViewApiVersion());
                }

                entity = EntityBuilder.create()
                        .setContentType(toApacheContentType(contentType))
                        .setText(textEntity)
                        .build();
            } else if (ContentType.TEXT_PLAIN == contentType) {
                entity = EntityBuilder.create()
                        .setContentType(toApacheContentType(contentType))
                        .setText(request.getEntity().toString())
                        .build();
            } else if (ContentType.MULTIPART_FORM_DATA == contentType) {
                Object content = request.getEntity();

                if (content instanceof GoldenImageFile) {
                    GoldenImageFile goldenImageFile = (GoldenImageFile) request.getEntity();

                    entity = MultipartEntityBuilder.create()
                            .setContentType(toApacheContentType(contentType))
                            .addBinaryBody("file", goldenImageFile.getFile())
                            .addTextBody("name", goldenImageFile.getName())
                            .addTextBody("description", goldenImageFile.getDescription())
                            .build();
                } else {
                    File file = (File) request.getEntity();
                    entity = MultipartEntityBuilder.create()
                            .setContentType(toApacheContentType(contentType))
                            .addBinaryBody("file", file)
                            .build();

                    //TODO add support for custom headers in Request object
                    base.setHeader("uploadfilename", file.getName());
                }
            } else {
                LOGGER.error("Unknown entity Content-Type");

                throw new SDKInvalidArgumentException(SDKErrorEnum.internalServerError, "Unknown entity Content-Type");
            }
            base.setEntity(entity);
        }
    }

    private org.apache.http.entity.ContentType toApacheContentType(ContentType contentType) {
        return org.apache.http.entity.ContentType.create(contentType.getMimeType(), contentType.getCharset());
    }

    /**
     * Builds the URI used in the request.
     *
     * @param request request parameters.
     *
     * @return request URI
     *
     * @throws SDKBadRequestException
     */
    private URI buildURI(Request request) throws SDKBadRequestException {
        try {
            URIBuilder uri = new URIBuilder(UrlUtils.createRestUrl(request.getHostname(), request.getUri()));

            for (UrlParameter entry : request.getQuery()) {
                uri.addParameter(entry.getKey(), entry.getValue());
            }
            return uri.build();
        } catch (URISyntaxException e) {
            throw new SDKBadRequestException(SDKErrorEnum.badRequestError, SdkConstants.APPLIANCE, e);
        }
    }

    /**
     * Configure the request headers.
     *
     * @param sessionId
     *  session token ID.
     * @param request
     *  Request information
     */
    private void setRequestHeaders(String sessionId, HttpUriRequest request) {
        if (StringUtils.isNotBlank(sessionId)) {
            request.setHeader("Auth", sessionId);
        }
        request.setHeader("Accept", "application/json");
        request.setHeader("accept-language", "en_US");
        request.setHeader("X-Api-Version", String.valueOf(config.getOneViewApiVersion().getValue()));
    }

    /**
     * Gets the response from HPE OneView.
     *
     * @param sessionId OV session token ID.
     * @param request
     *  Request information.
     * @param forceReturnTask
     *  Forces the check for the Location header (task) even when the response code is not 202.
     * @param downloadPath
     *  The directory where a binary response will be downloaded.
     * @return {@link String} object containing the response of the request
     */
    private String getResponse(String sessionId, HttpUriRequest request, boolean forceReturnTask, String downloadPath) {
        HttpResponse response = null;
        String responseBody;

        try {
            response = httpClient.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            LOGGER.debug("Response code: " + responseCode);

            if (responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                responseBody = "{}";
            } else {
                Header contentType = response.getEntity().getContentType();

                if (contentType != null
                        && ContentType.APPLICATION_OCTET_STREAM.getMimeType().equals(contentType.getValue())) {
                    // Downloadable file
                    if (downloadPath == null) {
                         downloadPath = config.getImageStreamerDownloadFolder();
                    }
                    String filePath = downloadFile(downloadPath, response);

                    return filePath;
                } else {
                    responseBody = EntityUtils.toString(response.getEntity());
                }
            }

            LOGGER.info("Response Body: " + responseBody);
            checkResponse(responseCode);

            if (forceReturnTask || ((responseCode == HttpURLConnection.HTTP_ACCEPTED) && (responseBody.length() == 0)
                    && response.containsHeader(HttpHeaders.LOCATION))) {
                // Response contains a task
                String restUri = response.getFirstHeader(HttpHeaders.LOCATION).getValue();
                restUri = restUri.substring(restUri.indexOf("/rest"));
                LOGGER.debug("Starting to monitor task " + restUri);

                Request taskRequest = new Request(HttpMethod.GET, restUri);
                taskRequest.setHostname(request.getURI().getHost() + ":" + request.getURI().getPort());

                return this.sendRequest(sessionId, taskRequest);
            }
        } catch (IOException e) {
            LOGGER.error("IO Error: ", e);
            throw new SDKBadRequestException(SDKErrorEnum.badRequestError, SdkConstants.APPLIANCE, e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        return responseBody;
    }

    private String downloadFile(final String downloadPath, HttpResponse response) {
        if (!new File(downloadPath).isDirectory()) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, SdkConstants.DOWNLOAD_PATH);
        }

        String fileSize = response.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue();
        String fileName = null;
        Header contentDisposition = response.getFirstHeader("Content-Disposition");

        for (HeaderElement element : contentDisposition.getElements()) {
            NameValuePair fileNameParameter = element.getParameterByName("filename");

            if (fileNameParameter != null) {
                fileName = fileNameParameter.getValue()
                        .replaceAll(",", ".")
                        .replaceAll(":", ".");
            }
        }

        LOGGER.info("File \"" + fileName + "\" (" + fileSize + " bytes) is being downloaded to " + downloadPath);

        String filePath = downloadPath + fileName;
        File file = new File(filePath);
        try(BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

            int inByte;

            while((inByte = bis.read()) != -1) {
                bos.write(inByte);
            }
        } catch (IOException e) {
            LOGGER.warn("Error downloading file {}", fileName, e);

            throw new SDKBadRequestException(SDKErrorEnum.internalError, SdkConstants.DOWNLOAD_PATH, e);
        }

        return filePath;
    }

    /**
     * Checks the HTTP response codes, on error throws the correct exception.
     * Sets the exception cause as e if it throws one.
     *
     * @param responseCode
     *  response code.
     * @return
     *  the response code.
     */
    private int checkResponse(final int responseCode) {
        switch (responseCode) {
        case HttpsURLConnection.HTTP_OK:
        case HttpsURLConnection.HTTP_CREATED:
        case HttpsURLConnection.HTTP_ACCEPTED:
        case HttpsURLConnection.HTTP_NOT_AUTHORITATIVE:
        case HttpsURLConnection.HTTP_NO_CONTENT:
        case HttpsURLConnection.HTTP_RESET:
        case HttpsURLConnection.HTTP_PARTIAL:
            return responseCode;

        case HttpsURLConnection.HTTP_NOT_FOUND:
            throw new SDKResourceNotFoundException(SDKErrorEnum.resourceNotFound, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_BAD_REQUEST:
            throw new SDKBadRequestException(SDKErrorEnum.badRequestError, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_FORBIDDEN:
            throw new SDKForbiddenException(SDKErrorEnum.forbiddenRequestError, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_BAD_METHOD:
            throw new SDKMethodNotAllowed(SDKErrorEnum.methodNotFound, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_UNAUTHORIZED:
            throw new SDKUnauthorizedException(SDKErrorEnum.unauthorized, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_CONFLICT:
        case HttpsURLConnection.HTTP_PRECON_FAILED:
        case HttpsURLConnection.HTTP_UNSUPPORTED_TYPE:
        case HttpsURLConnection.HTTP_INTERNAL_ERROR:
        case HttpsURLConnection.HTTP_UNAVAILABLE:
            throw new SDKInternalServerErrorException(SDKErrorEnum.internalServerError, SdkConstants.APPLIANCE);
        case HttpsURLConnection.HTTP_BAD_GATEWAY:
        case HttpsURLConnection.HTTP_CLIENT_TIMEOUT:
        case HttpsURLConnection.HTTP_ENTITY_TOO_LARGE:
        case HttpsURLConnection.HTTP_GATEWAY_TIMEOUT:
        case HttpsURLConnection.HTTP_GONE:
        case HttpsURLConnection.HTTP_LENGTH_REQUIRED:
        case HttpsURLConnection.HTTP_MOVED_PERM:
        case HttpsURLConnection.HTTP_MOVED_TEMP:
        case HttpsURLConnection.HTTP_MULT_CHOICE:
        case HttpsURLConnection.HTTP_NOT_ACCEPTABLE:
        case HttpsURLConnection.HTTP_NOT_IMPLEMENTED:
        case HttpsURLConnection.HTTP_NOT_MODIFIED:
        case HttpsURLConnection.HTTP_PAYMENT_REQUIRED:
        case HttpsURLConnection.HTTP_PROXY_AUTH:
        case HttpsURLConnection.HTTP_REQ_TOO_LONG:
        case HttpsURLConnection.HTTP_SEE_OTHER:
        case HttpsURLConnection.HTTP_USE_PROXY:
        case HttpsURLConnection.HTTP_VERSION:
        default:
            throw new SDKApplianceNotReachableException(SDKErrorEnum.applianceNotReachable, SdkConstants.APPLIANCE);
        }
    }

}
