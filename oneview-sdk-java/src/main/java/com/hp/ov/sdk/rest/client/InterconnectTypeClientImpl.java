/*******************************************************************************
 * // (C) Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 *******************************************************************************/
package com.hp.ov.sdk.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.ov.sdk.adaptors.InterconnectTypeAdaptor;
import com.hp.ov.sdk.constants.ResourceUris;
import com.hp.ov.sdk.constants.SdkConstants;
import com.hp.ov.sdk.dto.HttpMethodType;
import com.hp.ov.sdk.dto.InterconnectTypeCollectionV2;
import com.hp.ov.sdk.dto.generated.InterconnectTypes;
import com.hp.ov.sdk.exceptions.SDKErrorEnum;
import com.hp.ov.sdk.exceptions.SDKInvalidArgumentException;
import com.hp.ov.sdk.exceptions.SDKNoResponseException;
import com.hp.ov.sdk.exceptions.SDKResourceNotFoundException;
import com.hp.ov.sdk.rest.http.core.client.HttpRestClient;
import com.hp.ov.sdk.rest.http.core.client.RestParams;
import com.hp.ov.sdk.util.SdkUtils;

@Component
public class InterconnectTypeClientImpl implements InterconnectTypeClient
{

    private static final Logger logger = LoggerFactory
            .getLogger(InterconnectTypeClientImpl.class);
    @Autowired
    private HttpRestClient restClient;

    @Autowired
    private InterconnectTypeAdaptor adaptor;

    @Autowired
    private SdkUtils sdkUtils;

    @Override
    public InterconnectTypes getInterconnectType(final RestParams params,
            final String resourceId)
    {

        logger.info("InterconnectTypeClientImpl : getInterconnectType : Start");

        // validate args
        if (null == params)
        {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument,
                    null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.GET);
        params.setUrl(sdkUtils.createRestUrl(params.getHostname(),
                ResourceUris.INTERCONNECT_TYPE_URI, resourceId));

        final String returnObj = restClient.sendRequestToHPOV(params, null);
        logger.debug("InterconnectTypeClientImpl : getInterconnectType : response from OV :"
                + returnObj);
        if (null == returnObj || returnObj.equals(""))
        {
            throw new SDKNoResponseException(
                    SDKErrorEnum.noResponseFromAppliance, null, null, null,
                    SdkConstants.INTERCONNECT_TYPE, null);
        }
        // Call adaptor to convert to DTO

        final InterconnectTypes interconnectTypeDto = adaptor.buildDto(returnObj);

        logger.debug("InterconnectTypeClientImpl : getInterconnectType : name :"
                + interconnectTypeDto.getName());
        logger.info("InterconnectTypeClientImpl : getInterconnectType : End");

        return interconnectTypeDto;

    }

    @Override
    public InterconnectTypeCollectionV2 getAllInterconnectType(final RestParams params)
    {
        logger.info("InterconnectTypeClientImpl : getAllInterconnectTypeV2s : Start");
        // validate args
        if (null == params)
        {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument,
                    null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.GET);
        params.setUrl(sdkUtils.createRestUrl(params.getHostname(),
                ResourceUris.INTERCONNECT_TYPE_URI));

        // call rest client
        final String returnObj = restClient.sendRequestToHPOV(params, null);
        logger.debug("InterconnectTypeClientImpl : getAllInterconnectTypes : response from OV :"
                + returnObj);

        if (null == returnObj || returnObj.equals(""))
        {
            throw new SDKNoResponseException(
                    SDKErrorEnum.noResponseFromAppliance, null, null, null,
                    SdkConstants.INTERCONNECT_TYPES, null);
        }
        // Call adaptor to convert to DTO

        final InterconnectTypeCollectionV2 interconnectTypeCollectionDto = adaptor.buildCollectionDto(returnObj);

        logger.debug("InterconnectTypeClientImpl : getAllInterconnectTypes : members count :"
                + interconnectTypeCollectionDto.getCount());
        logger.info("InterconnectTypeClientImpl : getAllInterconnectTypes : End");

        return interconnectTypeCollectionDto;
    }

    @Override
    public InterconnectTypes getInterconnectTypeByName(final RestParams params,
            final String name)
    {
        InterconnectTypes interconnectTypeDto = null;
        logger.info("InterconnectTypeClientImpl : getInterconnectTypeByName : Start");
        final String query = "filter=\"name=\'" + name + "\'\"";

        // validate args
        if (null == params)
        {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument,
                    null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.GET);
        params.setUrl(sdkUtils.createRestQueryUrl(params.getHostname(),
                ResourceUris.INTERCONNECT_TYPE_URI, query));

        final String returnObj = restClient.sendRequestToHPOV(params, null);
        logger.debug("InterconnectTypeClientImpl : getInterconnectTypeByName : response from OV :"
                + returnObj);
        if (null == returnObj || returnObj.equals(""))
        {
            throw new SDKNoResponseException(
                    SDKErrorEnum.noResponseFromAppliance, null, null, null,
                    SdkConstants.INTERCONNECT_TYPE, null);
        }
        // Call adaptor to convert to DTO

        final InterconnectTypeCollectionV2 interconnectTypeCollectionDto = adaptor.buildCollectionDto(returnObj);
        if (interconnectTypeCollectionDto.getCount() != 0)
        {
            interconnectTypeDto = interconnectTypeCollectionDto.getMembers()
                    .get(0);
        }
        else
        {
            interconnectTypeDto = null;
        }

        if (interconnectTypeDto == null)
        {
            logger.error("InterconnectTypeClientImpl : getInterconnectTypeByName : resource not Found for name :"
                    + name);
            throw new SDKResourceNotFoundException(
                    SDKErrorEnum.resourceNotFound, null, null, null,
                    SdkConstants.INTERCONNECT_TYPE, null);
        }
        logger.info("InterconnectTypeClientImpl : getInterconnectTypeByName : End");

        return interconnectTypeDto;
    }

}
