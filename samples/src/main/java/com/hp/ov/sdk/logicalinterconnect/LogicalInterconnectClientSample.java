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
package com.hp.ov.sdk.logicalinterconnect;

import java.util.ArrayList;
import java.util.List;

import com.hp.ov.sdk.dto.Command;
import com.hp.ov.sdk.dto.LiFirmware;
import com.hp.ov.sdk.dto.LogicalInterconnectCollectionV2;
import com.hp.ov.sdk.dto.PhysicalInterconnectFirmware;
import com.hp.ov.sdk.dto.TaskResourceV2;
import com.hp.ov.sdk.dto.generated.LogicalInterconnects;
import com.hp.ov.sdk.exceptions.SDKApplianceNotReachableException;
import com.hp.ov.sdk.exceptions.SDKBadRequestException;
import com.hp.ov.sdk.exceptions.SDKInvalidArgumentException;
import com.hp.ov.sdk.exceptions.SDKNoResponseException;
import com.hp.ov.sdk.exceptions.SDKNoSuchUrlException;
import com.hp.ov.sdk.exceptions.SDKResourceNotFoundException;
import com.hp.ov.sdk.exceptions.SDKTasksException;
import com.hp.ov.sdk.rest.client.FirmwareDriverClient;
import com.hp.ov.sdk.rest.client.FirmwareDriverClientImpl;
import com.hp.ov.sdk.rest.client.LogicalInterconnectClient;
import com.hp.ov.sdk.rest.client.LogicalInterconnectClientImpl;
import com.hp.ov.sdk.rest.http.core.client.RestParams;
import com.hp.ov.sdk.util.UrlUtils;
import com.hp.ov.sdk.util.samples.HPOneViewCredential;

/*
 * LogicalInterconnectClientSample, sample program consumes the available networks through the interconnect uplinks and
 * downlink capabilities through a physical server’s interfaces of HP OneView. It invokes APIs of LogicalInterconnectClient
 *  which is in sdk library to perform GET/PUT operations on logical interconnect resource
 */
public class LogicalInterconnectClientSample {

    private final LogicalInterconnectClient logicalInterconnectClient;
    private final FirmwareDriverClient firmwareDriverClient;

    private RestParams params;
    private TaskResourceV2 taskResourceV2;

    // These are variables to be defined by user
    // ================================
    private static final String sppName = "HP Service Pack For ProLiant OneView 2014 11 13";
    private static final String resourceName = "Encl1-LI";
    private static final String resourceId = "5566a380-afd4-4e5a-ad15-b3a3cfe1fcdf";

    // InterconnectUri
    private static final String interconnectNameOne = "Encl1, interconnect 1";
    private static final String interconnectNameTwo = "Encl1, interconnect 2";
    // ================================

    private LogicalInterconnectClientSample() {
        this.logicalInterconnectClient = LogicalInterconnectClientImpl.getClient();
        this.firmwareDriverClient = FirmwareDriverClientImpl.getClient();
    }

    private void getLogicalInterconnectById() throws InstantiationException, IllegalAccessException {
        LogicalInterconnects logicalInterconnectsDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // then make sdk service call to get resource
            logicalInterconnectsDto = logicalInterconnectClient.getLogicalInterconnect(params, resourceId);

            System.out.println("LogicalInterconnectClientTest : " + "getLogicalInterconnectById : logical interconnect"
                    + "  object returned to client : " + logicalInterconnectsDto.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "getLogicalInterconnectById : resource you are looking is not found ");
        } catch (final SDKNoSuchUrlException ex) {
            System.out
                    .println("LogicalInterconnectClientTest : " + "getLogicalInterconnectById : no such url : " + params.getUrl());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : " + "getLogicalInterconnectById : Applicance Not reachabe at : "
                    + params.getHostname());
        } catch (final SDKNoResponseException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "getLogicalInterconnectById : No response from appliance : "
                    + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "getLogicalInterconnectById : arguments are null ");
        }

    }

    private void getAllLogicalInterconnects() throws InstantiationException, IllegalAccessException {
        LogicalInterconnectCollectionV2 logicalInterconnectCollectionDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // then make sdk service call to get resource
            logicalInterconnectCollectionDto = logicalInterconnectClient.getAllLogicalInterconnects(params);

            System.out.println("LogicalInterconnectClientTest : getAllLogicalInterconnects "
                    + ": logical interconnect object returned to client : " + logicalInterconnectCollectionDto.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "getAllLogicalInterconnects : resource you are looking is not found ");
        } catch (final SDKNoSuchUrlException ex) {
            System.out
                    .println("LogicalInterconnectClientTest : " + "getAllLogicalInterconnects : no such url : " + params.getUrl());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : " + "getAllLogicalInterconnects : Applicance Not reachabe at : "
                    + params.getHostname());
        } catch (final SDKNoResponseException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "getAllLogicalInterconnects : No response from appliance : "
                    + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "getAllLogicalInterconnects : arguments are null ");
        }

    }

    private void updateLogicalInterconnectSnmpConfigurationById() throws InstantiationException, IllegalAccessException {
        String resourceId = null;
        LogicalInterconnects logicalInterconnectsDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // fetch logicalInterconnectsDto Uri
            logicalInterconnectsDto = logicalInterconnectClient.getLogicalInterconnectByName(params, resourceName);

            if (null != logicalInterconnectsDto.getUri()) {
                resourceId = UrlUtils.getResourceIdFromUri(logicalInterconnectsDto.getUri());
            }

            logicalInterconnectsDto.getSnmpConfiguration().setReadCommunity("private");
            taskResourceV2 = logicalInterconnectClient.updateLogicalInterconnectSnmpConfigurationById(params, resourceId,
                    logicalInterconnectsDto.getSnmpConfiguration(), false, false);

            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectSnmpConfigurationById : status of "
                    + "logicalInterconnect object returned to client : " + taskResourceV2.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : resource you are looking is not found for update");
        } catch (final SDKBadRequestException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : bad request, try again : "
                    + "may be duplicate resource name or invalid inputs. check inputs and try again");
        } catch (final SDKNoSuchUrlException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : no such url : " + params.getHostname());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : Applicance Not reachabe at : " + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : arguments are null ");
        } catch (final SDKTasksException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectSnmpConfigurationById : errors in task, "
                    + "please check task resource for more details ");
        }
    }

    private void updateLogicalInterconnectComplianceById() throws InstantiationException, IllegalAccessException {
        String resourceId = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // get resource ID
            resourceId = logicalInterconnectClient.getId(params, resourceName);

            taskResourceV2 = logicalInterconnectClient.updateLogicalInterconnectComplianceById(params, resourceId, false);

            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectComplianceById : status of "
                    + "logicalInterconnect object returned to client : " + taskResourceV2.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectComplianceById : resource you are looking is not found for update");
        } catch (final SDKBadRequestException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectComplianceById : bad request, try again : "
                    + "may be duplicate resource name or invalid inputs. check inputs and try again");
        } catch (final SDKNoSuchUrlException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectComplianceById : no such url : "
                    + params.getHostname());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectComplianceById : Applicance Not reachabe at : " + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out
                    .println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectComplianceById : arguments are null ");
        } catch (final SDKTasksException e) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectComplianceById : errors in task, "
                    + "please check task resource for more details ");
        }
    }

    private void updateLogicalInterconnectFirmwareStageById() throws InstantiationException, IllegalAccessException {
        String resourceId = null;
        LiFirmware liFirmwareDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // get resource ID
            resourceId = logicalInterconnectClient.getId(params, resourceName);

            liFirmwareDto = buildLIFirmwareStageDto();
            taskResourceV2 = logicalInterconnectClient.updateLogicalInterconnectFirmwareById(params, resourceId, liFirmwareDto,
                    false, false);

            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareStageById : status of "
                    + "logicalInterconnect object returned to client : " + taskResourceV2.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareStageById : resource you are looking is not found for update");
        } catch (final SDKBadRequestException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareStageById : bad request, try again : "
                    + "may be duplicate resource name or invalid inputs. check inputs and try again");
        } catch (final SDKNoSuchUrlException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareStageById : no such url : "
                    + params.getHostname());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareStageById : Applicance Not reachabe at : " + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareStageById : arguments are null ");
        } catch (final SDKTasksException e) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareStageById : errors in task, "
                    + "please check task resource for more details ");
        }

    }

    private LiFirmware buildLIFirmwareStageDto() {
        final LiFirmware liFirmware = new LiFirmware();

        liFirmware.setCommand(Command.STAGE);
        liFirmware.setSppUri(firmwareDriverClient.getFirmwareDriverByName(params, sppName).getUri());
        liFirmware.setForce(true);
        return liFirmware;
    }

    private void updateLogicalInterconnectFirmwareActiveById() throws InstantiationException, IllegalAccessException {
        String resourceId = null;
        LiFirmware liFirmwareDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            // get resource ID
            resourceId = logicalInterconnectClient.getId(params, resourceName);

            liFirmwareDto = logicalInterconnectClient.getLogicalInterconnectFirmwareById(params, resourceId);
            liFirmwareDto = buildLIFirmwareActiveDto(liFirmwareDto);

            taskResourceV2 = logicalInterconnectClient.updateLogicalInterconnectFirmwareById(params, resourceId, liFirmwareDto,
                    false, false);

            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareActiveById : status of "
                    + "logicalInterconnect object returned to client : " + taskResourceV2.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareActiveById : resource you are looking is not found for update");
        } catch (final SDKBadRequestException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareActiveById : bad request, try again : "
                    + "may be duplicate resource name or invalid inputs. check inputs and try again");
        } catch (final SDKNoSuchUrlException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareActiveById : no such url : "
                    + params.getHostname());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareActiveById : Applicance Not reachabe at : " + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareActiveById : arguments are null ");
        } catch (final SDKTasksException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareActiveById : errors in task, "
                    + "please check task resource for more details ");
        }

    }

    private LiFirmware buildLIFirmwareActiveDto(final LiFirmware initliFirmware) {
        final LiFirmware liFirmware = new LiFirmware();

        liFirmware.setCommand(Command.ACTIVATE);
        liFirmware.setSppUri(firmwareDriverClient.getFirmwareDriverByName(params, sppName).getUri());
        final List<PhysicalInterconnectFirmware> interconnects = new ArrayList<PhysicalInterconnectFirmware>();
        for (int i = 0; i < initliFirmware.getInterconnects().size(); i++) {
            String interconnectName = null;
            if (initliFirmware.getInterconnects().get(i).getInterconnectName().equals(interconnectNameOne)) {
                interconnectName = interconnectNameOne;
            } else if (initliFirmware.getInterconnects().get(i).getInterconnectName().equals(interconnectNameTwo)) {
                interconnectName = interconnectNameTwo;
            }
            if (interconnectName != null) {
                final PhysicalInterconnectFirmware interconnect = new PhysicalInterconnectFirmware();
                interconnect.setInterconnectUri(initliFirmware.getInterconnects().get(i).getInterconnectUri());
                interconnect.setInterconnectName(interconnectName);
                interconnects.add(interconnect);
            }
        }
        liFirmware.setInterconnects(interconnects);

        return liFirmware;
    }

    private void updateLogicalInterconnectFirmwareUpdateById() throws InstantiationException, IllegalAccessException {
        String resourceId = null;
        LiFirmware liFirmwareDto = null;
        try {
            // OneView credentials
            params = HPOneViewCredential.createCredentials();

            liFirmwareDto = buildLIFirmwareUpdateDto();

            // get resource ID
            resourceId = logicalInterconnectClient.getId(params, resourceName);

            taskResourceV2 = logicalInterconnectClient.updateLogicalInterconnectFirmwareById(params, resourceId, liFirmwareDto,
                    false, false);

            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareUpdateById : status of "
                    + "logicalInterconnect object returned to client : " + taskResourceV2.toString());
        } catch (final SDKResourceNotFoundException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareUpdateById : resource you are looking is not found for update"
                    + params.getHostname());
        } catch (final SDKBadRequestException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareUpdateById : bad request, try again : "
                    + "may be duplicate resource name or invalid inputs. check inputs and try again");
        } catch (final SDKNoSuchUrlException ex) {
            System.out.println("LogicalInterconnectClientTest : " + "updateLogicalInterconnectFirmwareUpdateById : no such url : "
                    + params.getHostname());
        } catch (final SDKApplianceNotReachableException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareUpdateById : Applicance Not reachabe at : " + params.getHostname());
        } catch (final SDKInvalidArgumentException ex) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareUpdateById : arguments are null ");
        } catch (final SDKTasksException e) {
            System.out.println("LogicalInterconnectClientTest : "
                    + "updateLogicalInterconnectFirmwareUpdateById : errors in task, "
                    + "please check task resource for more details ");
        }
    }

    private LiFirmware buildLIFirmwareUpdateDto() {
        final LiFirmware liFirmware = new LiFirmware();

        liFirmware.setCommand(Command.UPDATE);
        liFirmware.setSppUri(firmwareDriverClient.getFirmwareDriverByName(params, sppName).getUri());
        liFirmware.setForce(true);
        return liFirmware;
    }

    public static void main(final String[] args) throws Exception {
        LogicalInterconnectClientSample client = new LogicalInterconnectClientSample();

        client.getLogicalInterconnectById();
        client.getAllLogicalInterconnects();
        client.updateLogicalInterconnectComplianceById();
        client.updateLogicalInterconnectFirmwareStageById();

        /* TODO
        *  the methods below could not be executed due to the following error:
        *
        * "recommendedActions": [
        *   "Firmware staging/update operation is only allowed at this state.
        *   Please retry the operation with stage/update command."]
        * "errorCode": "ACTIVATE_OPERATION_NOT_ALLOWED",
        * "message": "Activate operation is not allowed at this state.
        *   One or more interconnects may not have been baselined to this
        *   firmware or all interconnects are already activated/activating to this baseline."

        client.updateLogicalInterconnectFirmwareActiveById();
        client.updateLogicalInterconnectFirmwareUpdateById();

        */

        client.updateLogicalInterconnectSnmpConfigurationById();
    }

}
