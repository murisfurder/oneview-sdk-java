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
package com.hp.ov.sdk.rest.client.networking;

import com.hp.ov.sdk.dto.networking.fcoenetworks.FcoeNetwork;
import com.hp.ov.sdk.rest.client.common.CreatableResource;
import com.hp.ov.sdk.rest.client.common.DeletableResource;
import com.hp.ov.sdk.rest.client.common.SearchableResource;
import com.hp.ov.sdk.rest.client.common.UpdatableResource;
import com.hp.ov.sdk.rest.reflect.Api;

@Api(FcoeNetworkClient.FCOE_NETWORK_URI)
public interface FcoeNetworkClient extends
        SearchableResource<FcoeNetwork>,
        CreatableResource<FcoeNetwork>,
        UpdatableResource<FcoeNetwork>,
        DeletableResource {

    String FCOE_NETWORK_URI = "/rest/fcoe-networks";

}
