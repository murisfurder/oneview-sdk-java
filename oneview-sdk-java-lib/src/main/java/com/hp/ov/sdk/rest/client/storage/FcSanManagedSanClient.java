/*
 * (C) Copyright 2016 Hewlett Packard Enterprise Development LP
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
package com.hp.ov.sdk.rest.client.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.ov.sdk.adaptors.FcIssueResponseAdaptor;
import com.hp.ov.sdk.constants.ResourceUris;
import com.hp.ov.sdk.dto.EndpointResponse;
import com.hp.ov.sdk.dto.EndpointsCsvFileResponse;
import com.hp.ov.sdk.dto.FcSansManagedSanTask;
import com.hp.ov.sdk.dto.HttpMethodType;
import com.hp.ov.sdk.dto.ResourceCollection;
import com.hp.ov.sdk.dto.SanRequest;
import com.hp.ov.sdk.dto.SanResponse;
import com.hp.ov.sdk.dto.TaskResourceV2;
import com.hp.ov.sdk.rest.client.BaseClient;
import com.hp.ov.sdk.rest.http.core.UrlParameter;
import com.hp.ov.sdk.rest.http.core.client.Request;
import com.hp.ov.sdk.util.UrlUtils;

public class FcSanManagedSanClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FcSanManagedSanClient.class);

    private final BaseClient baseClient;

    public FcSanManagedSanClient(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Retrieves the {@link SanResponse} details for the specified managed SAN.
     *
     * @param resourceId managed SAN resource identifier as seen in HPE OneView.
     *
     * @return {@link SanResponse} object containing the details.
     */
    public SanResponse getById(String resourceId) {
        LOGGER.info("FcSanManagedSanClient : getById : Start");

        SanResponse san = baseClient.getResource(
                UrlUtils.createUrl(ResourceUris.FC_SANS_MANAGED_SAN_URI, resourceId), SanResponse.class);

        LOGGER.info("FcSanManagedSanClient : getById : End");

        return san;
    }

    /**
     * Retrieves a {@link ResourceCollection}&lt;{@link SanResponse}&gt; containing details
     * for all the available managed SANs found under the current HPE OneView.
     *
     * @return {@link ResourceCollection}&lt;{@link SanResponse}&gt; containing
     * the details for all found managed SANs.
     */
    public ResourceCollection<SanResponse> getAll() {
        LOGGER.info("FcSanManagedSanClient : getAll : Start");

        ResourceCollection<SanResponse> sans = baseClient.getResourceCollection(
                ResourceUris.FC_SANS_MANAGED_SAN_URI, SanResponse.class);

        LOGGER.info("FcSanManagedSanClient : getAll : End");

        return sans;
    }

    /**
     * Retrieves a {@link ResourceCollection}&lt;{@link SanResponse}&gt; containing details
     * for the available managed SANs found under the current HPE OneView that match the name.
     *
     * @param name managed SAN name as seen in HPE OneView.
     *
     * @return {@link ResourceCollection}&lt;{@link SanResponse}&gt; containing
     * the details for the found managed SANs.
     */
    public ResourceCollection<SanResponse> getByName(String name) {
        LOGGER.info("FcSanManagedSanClient : getByName : Start");

        ResourceCollection<SanResponse> sans = baseClient.getResourceCollection(
                ResourceUris.FC_SANS_MANAGED_SAN_URI, SanResponse.class,
                UrlParameter.getFilterByNameParameter(name));

        LOGGER.info("FcSanManagedSanClient : getByName : End");

        return sans;
    }

    /**
     * Updates a managed SAN identified by the given resource identifier.
     *
     * @param resourceId managed SAN resource identifier as seen in HPE OneView.
     * @param sanRequest object containing the managed SAN details.
     *
     * @return {@link SanResponse} containing the updated managed SAN details.
     */
    public SanResponse update(String resourceId, SanRequest sanRequest) {
        LOGGER.info("FcSanManagedSanClient : update : Start");

        Request request = new Request(HttpMethodType.PUT,
                UrlUtils.createUrl(ResourceUris.FC_SANS_MANAGED_SAN_URI, resourceId), sanRequest);

        SanResponse updatedSan = baseClient.executeRequest(request, SanResponse.class);

        LOGGER.info("FcSanManagedSanClient : update : End");

        return updatedSan;
    }

    /**
     * Reports all endpoints known within the specified SAN and the data about each endpoint.
     *
     * @param resourceId managed SAN resource identifier as seen in HPE OneView.
     *
     * @return {@link ResourceCollection}&lt;{@link EndpointResponse}&gt; containing
     * the details for all found endpoints of a given managed SAN.
     */
    public ResourceCollection<EndpointResponse> getEndpoints(String resourceId) {
        LOGGER.info("FcSanManagedSanClient : getEndpoints : Start");

        ResourceCollection<EndpointResponse> sanEndpoints = baseClient.getResourceCollection(
                UrlUtils.createUrl(ResourceUris.FC_SANS_MANAGED_SAN_URI, resourceId,
                        ResourceUris.FC_SANS_MANAGED_SAN_ENDPOINTS),
                EndpointResponse.class);

        LOGGER.info("FcSanManagedSanClient : getEndpoints : End");

        return sanEndpoints;
    }

    /**
     * Creates a new SAN issues report indicating any connectivity issues in the specified SAN.
     *
     * @param resourceId managed SAN resource identifier as seen in HPE OneView.
     * @param aSync flag to indicate whether the request should be processed
     * synchronously or asynchronously.
     *
     * @return {@link FcSansManagedSanTask} which contains the task status for the process
     *          and the report data.
     */
    public FcSansManagedSanTask createIssuesReport(String resourceId, boolean aSync) {
        LOGGER.info("FcSanManagedSanClient : createIssuesReport : Start");

        Request request = new Request(HttpMethodType.POST,
                UrlUtils.createUrl(ResourceUris.FC_SANS_MANAGED_SAN_URI, resourceId,
                        ResourceUris.FC_SANS_MANAGED_SAN_ISSUES));

        TaskResourceV2 taskResource = this.baseClient.executeMonitorableRequest(request, aSync);

        LOGGER.info("FcSanManagedSanClient : createIssuesReport : End");

        return new FcSansManagedSanTask(taskResource, FcIssueResponseAdaptor.getInstance());
    }

    /**
     * Creates an endpoints CSV file for a SAN. Clients should perform a GET request on the
     * URI in the response to download the file.
     *
     * @param resourceId managed SAN resource identifier as seen in HPE OneView.
     *
     * @return {@link EndpointsCsvFileResponse} contains the data of the created CSV file.
     */
    public EndpointsCsvFileResponse createEndpointsCsv(String resourceId) {
        LOGGER.info("FcSanManagedSanClient : createEndpointsCsv : Start");

        Request request = new Request(HttpMethodType.POST,
                UrlUtils.createUrl(ResourceUris.FC_SANS_MANAGED_SAN_URI, resourceId,
                        ResourceUris.FC_SANS_MANAGED_SAN_ENDPOINTS));

        EndpointsCsvFileResponse endpointsCsvFileResponse = this.baseClient.executeRequest(request,
                EndpointsCsvFileResponse.class);

        LOGGER.info("FcSanManagedSanClient : createEndpointsCsv : End");

        return endpointsCsvFileResponse;
    }
}