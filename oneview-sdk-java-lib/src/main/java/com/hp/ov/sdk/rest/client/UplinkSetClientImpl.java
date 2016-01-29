/*******************************************************************************
 * (C) Copyright 2015 Hewlett Packard Enterprise Development LP
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
 *******************************************************************************/
package com.hp.ov.sdk.rest.client;

import com.hp.ov.sdk.adaptors.TaskAdaptor;
import com.hp.ov.sdk.adaptors.UplinkSetAdaptor;
import com.hp.ov.sdk.constants.ResourceUris;
import com.hp.ov.sdk.constants.SdkConstants;
import com.hp.ov.sdk.dto.HttpMethodType;
import com.hp.ov.sdk.dto.TaskResourceV2;
import com.hp.ov.sdk.dto.UplinkSetCollectionV2;
import com.hp.ov.sdk.dto.generated.UplinkSets;
import com.hp.ov.sdk.exceptions.SDKErrorEnum;
import com.hp.ov.sdk.exceptions.SDKInvalidArgumentException;
import com.hp.ov.sdk.exceptions.SDKNoResponseException;
import com.hp.ov.sdk.exceptions.SDKResourceNotFoundException;
import com.hp.ov.sdk.rest.http.core.client.HttpRestClient;
import com.hp.ov.sdk.rest.http.core.client.RestParams;
import com.hp.ov.sdk.tasks.TaskMonitorManager;
import com.hp.ov.sdk.util.UrlUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class UplinkSetClientImpl implements UplinkSetClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(UplinkSetClientImpl.class);
    private static final int TIMEOUT = 60000; // in milliseconds = 1 mins

    private final UplinkSetAdaptor adaptor;
    private final TaskAdaptor taskAdaptor;
    private final TaskMonitorManager taskMonitor;

    private JSONObject jsonObject;

    protected UplinkSetClientImpl(UplinkSetAdaptor adaptor, TaskAdaptor taskAdaptor, TaskMonitorManager taskMonitor) {
        this.adaptor = adaptor;
        this.taskAdaptor = taskAdaptor;
        this.taskMonitor = taskMonitor;
    }

    public static UplinkSetClient getClient() {
        return new UplinkSetClientImpl(new UplinkSetAdaptor(),
                TaskAdaptor.getInstance(),
                TaskMonitorManager.getInstance());
    }

    @Override
    public UplinkSets getUplinkSet(final RestParams params, final String resourceId) {
        LOGGER.info("UplinkSetClientImpl : getUplinkSet : Start");

        // validate args
        if (null == params) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.GET);
        params.setUrl(UrlUtils.createRestUrl(params.getHostname(), ResourceUris.UPLINK_SETS_URI, resourceId));

        final String returnObj = HttpRestClient.sendRequestToHPOV(params, null);
        LOGGER.debug("UplinkSetClientImpl : getUplinkSet : response from OV :" + returnObj);
        if (null == returnObj || returnObj.equals("")) {
            throw new SDKNoResponseException(SDKErrorEnum.noResponseFromAppliance, null, null, null, SdkConstants.UPLINKSET, null);
        }
        // Call adaptor to convert to DTO

        final UplinkSets uplinkSetDto = adaptor.buildDto(returnObj);

        LOGGER.debug("UplinkSetClientImpl : getUplinkSet : Name :" + uplinkSetDto.getName());
        LOGGER.info("UplinkSetClientImpl : getUplinkSet : End");

        return uplinkSetDto;
    }

    @Override
    public UplinkSetCollectionV2 getAllUplinkSet(final RestParams params) {
        LOGGER.info("UplinkSetClientImpl : getAllUplinkSet : Start");

        // validate args
        if (null == params) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.GET);
        params.setUrl(UrlUtils.createRestUrl(params.getHostname(), ResourceUris.UPLINK_SETS_URI));

        final String returnObj = HttpRestClient.sendRequestToHPOV(params, null);
        LOGGER.debug("UplinkSetClientImpl : getAllUplinkSet : response from OV :" + returnObj);
        if (null == returnObj || returnObj.equals("")) {
            throw new SDKNoResponseException(SDKErrorEnum.noResponseFromAppliance, null, null, null, SdkConstants.UPLINKSETS, null);
        }
        // Call adaptor to convert to DTO

        final UplinkSetCollectionV2 uplinkSetCollectionDto = adaptor.buildCollectionDto(returnObj);

        LOGGER.debug("UplinkSetClientImpl : getAllUplinkSet : members count :" + uplinkSetCollectionDto.getCount());
        LOGGER.info("UplinkSetClientImpl : getAllUplinkSet : End");

        return uplinkSetCollectionDto;
    }

    @Override
    public TaskResourceV2 deleteUplinkSet(final RestParams params, final String resourceId, final boolean aSync) {
        LOGGER.info("UplinkSetClientImpl : deleteUplinkSet : Start");

        // validate args
        if (null == params) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.APPLIANCE, null);
        }
        // set the additional params
        params.setType(HttpMethodType.DELETE);
        params.setUrl(UrlUtils.createRestUrl(params.getHostname(), ResourceUris.UPLINK_SETS_URI, resourceId));

        final String returnObj = HttpRestClient.sendRequestToHPOV(params, null);
        LOGGER.debug("UplinkSetClientImpl : deleteUplinkSet : response from OV :" + returnObj);

        if (null == returnObj || returnObj.equals("")) {
            throw new SDKNoResponseException(SDKErrorEnum.noResponseFromAppliance, null, null, null, SdkConstants.UPLINKSETS, null);
        }

        TaskResourceV2 taskResourceV2 = taskAdaptor.buildDto(returnObj);

        LOGGER.debug("UplinkSetClientImpl : deleteUplinkSet : returnObj =" + returnObj);
        LOGGER.debug("UplinkSetClientImpl : deleteUplinkSet : taskResource =" + taskResourceV2);

        // check for asyncOrSyncMode. if user is asking async mode, return the
        // directly the TaskResourceV2
        // if user is asking for sync mode, calling the tasking polling method
        // and send the update
        // once task is complete.
        if (taskResourceV2 != null && aSync == false) {
            taskResourceV2 = taskMonitor.checkStatus(params, taskResourceV2.getUri(), TIMEOUT);
        }
        LOGGER.info("UplinkSetClientImpl : deleteUplinkSet : End");

        return taskResourceV2;
    }

    @Override
    public TaskResourceV2 updateUplinkSet(final RestParams params, final String resourceId, final UplinkSets uplinkDto,
            final boolean aSync, final boolean useJsonRequest) {

        LOGGER.info("UplinkSetClientImpl : updateUplinkSet : Start");

        // validate args
        if (null == params) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.APPLIANCE, null);
        }
        // validate params
        if (uplinkDto == null) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.UPLINKSETS, null);
        }
        // set the additional params
        params.setType(HttpMethodType.PUT);
        params.setUrl(UrlUtils.createRestUrl(params.getHostname(), ResourceUris.UPLINK_SETS_URI, resourceId));
        String returnObj = null;

        // TODO
        // check for json request in the input dto. if it is present,
        // then
        // convert that into jsonObject and pass it rest client
        // idea is : user can create json string and call the sdk api.
        // user can save time in creating uplinksets dto.

        jsonObject = adaptor.buildJsonObjectFromDto(uplinkDto);

        returnObj = HttpRestClient.sendRequestToHPOV(params, jsonObject);
        // convert returnObj to taskResource
        TaskResourceV2 taskResourceV2 = taskAdaptor.buildDto(returnObj);

        LOGGER.debug("UplinkSetClientImpl : updateUplinkSet : returnObj =" + returnObj);
        LOGGER.debug("UplinkSetClientImpl : updateUplinkSet : taskResource =" + taskResourceV2);

        // check for aSync flag. if user is asking async mode, return directly
        // the TaskResourceV2
        // if user is asking for sync mode, call task monitor polling method and
        // send the update
        // once task is complete or exceeds the timeout.
        if (taskResourceV2 != null && aSync == false) {
            taskResourceV2 = taskMonitor.checkStatus(params, taskResourceV2.getUri(), TIMEOUT);
        }
        LOGGER.info("UplinkSetClientImpl : updateUplinkSet : End");

        return taskResourceV2;
    }

    @Override
    public UplinkSets getUplinkSetsByName(final RestParams params, final String uplinkSetName) {
        LOGGER.info("UplinkSetClientImpl : getUplinkSetsByName : start");
        final UplinkSetCollectionV2 uplinkSetCollectionDto = getAllUplinkSet(params);

        for (final UplinkSets uplinkSetDto : new ArrayList<>(uplinkSetCollectionDto.getMembers())) {
            if (uplinkSetDto.getName().equals(uplinkSetName)) {
                System.out.println(uplinkSetDto.getName());
                LOGGER.info("UplinkSetClientImpl : getUplinkSetsByName : End");
                return uplinkSetDto;
            }
        }
        LOGGER.error("UplinkSetClientImpl : getUplinkSetsByName : Not found for name :" + uplinkSetName);
        throw new SDKResourceNotFoundException(SDKErrorEnum.resourceNotFound, null, null, null, SdkConstants.UPLINKSET, null);
    }

    @Override
    public TaskResourceV2 createUplinkSet(RestParams params, UplinkSets uplinkSetDto, boolean aSync, boolean useJsonRequest) {
        LOGGER.info("UplinkSetClientImpl : createUplinkSet : Start");
        String returnObj = null;

        // validate args
        if (null == params) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.APPLIANCE, null);
        }
        // validate dto
        if (uplinkSetDto == null) {
            throw new SDKInvalidArgumentException(SDKErrorEnum.invalidArgument, null, null, null, SdkConstants.UPLINKSET, null);
        }
        // set the additional params
        params.setType(HttpMethodType.POST);
        params.setUrl(UrlUtils.createRestUrl(params.getHostname(), ResourceUris.UPLINK_SETS_URI));

        // TODO - check for json request in the input dto. if it is present,
        // then
        // convert that into jsonObject and pass it rest client
        // idea is : user can create json string and call the sdk api.
        // user can save time in creating network dto.

        // create JSON request from dto
        jsonObject = adaptor.buildJsonObjectFromDto(uplinkSetDto);
        returnObj = HttpRestClient.sendRequestToHPOV(params, jsonObject);
        // convert returnObj to taskResource
        TaskResourceV2 taskResourceV2 = taskAdaptor.buildDto(returnObj);

        LOGGER.debug("UplinkSetClientImpl : createUplinkSet : returnObj =" + returnObj);
        LOGGER.debug("UplinkSetClientImpl : createUplinkSet : taskResource =" + taskResourceV2);

        // check for aSync flag. if user is asking async mode, return directly
        // the TaskResourceV2
        // if user is asking for sync mode, call task monitor polling method and
        // send the update
        // once task is complete or exceeds the timeout.
        if (taskResourceV2 != null && aSync == false) {
            taskResourceV2 = taskMonitor.checkStatus(params, taskResourceV2.getUri(), TIMEOUT);
        }
        LOGGER.info("UplinkSetClientImpl : createUplinkSet : End");

        return taskResourceV2;
    }

    @Override
    public String getId(final RestParams creds, final String name) {
        String resourceId = "";
        // fetch resource Id using resource name
        UplinkSets uplinkSetsDto = getUplinkSetsByName(creds, name);

        if (null != uplinkSetsDto.getUri()) {
            resourceId = UrlUtils.getResourceIdFromUri(uplinkSetsDto.getUri());
        }
        return resourceId;
    }
}
