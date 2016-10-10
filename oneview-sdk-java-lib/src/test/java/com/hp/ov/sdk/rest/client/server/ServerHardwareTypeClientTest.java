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

package com.hp.ov.sdk.rest.client.server;

import static com.hp.ov.sdk.rest.client.server.ServerHardwareTypeClient.SERVER_HARDWARE_TYPE_URI;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;
import com.hp.ov.sdk.dto.ResourceCollection;
import com.hp.ov.sdk.dto.servers.serverhardwaretype.ServerHardwareType;
import com.hp.ov.sdk.dto.servers.serverhardwaretype.ServerHardwareTypeUpdate;
import com.hp.ov.sdk.rest.client.BaseClient;
import com.hp.ov.sdk.rest.http.core.HttpMethod;
import com.hp.ov.sdk.rest.http.core.UrlParameter;
import com.hp.ov.sdk.rest.http.core.client.Request;
import com.hp.ov.sdk.rest.http.core.client.TaskTimeout;
import com.hp.ov.sdk.rest.reflect.ClientRequestHandler;

@RunWith(MockitoJUnitRunner.class)
public class ServerHardwareTypeClientTest {

    private static final String ANY_RESOURCE_ID = "random-UUID";
    private static final String ANY_RESOURCE_NAME = "random-Name";

    private BaseClient baseClient = mock(BaseClient.class);
    private ServerHardwareTypeClient client = Reflection.newProxy(ServerHardwareTypeClient.class,
            new ClientRequestHandler<>(baseClient, ServerHardwareTypeClient.class));

    @Test
    public void shouldGetServerHardwareType() {
        client.getById(ANY_RESOURCE_ID);

        String expectedUri = SERVER_HARDWARE_TYPE_URI + "/" + ANY_RESOURCE_ID;
        Request expectedRequest = new Request(HttpMethod.GET, expectedUri);

        then(baseClient).should().executeRequest(expectedRequest, TypeToken.of(ServerHardwareType.class).getType());
    }

    @Test
    public void shouldGetAllServerHardwareTypes() {
        client.getAll();

        Request expectedRequest = new Request(HttpMethod.GET, SERVER_HARDWARE_TYPE_URI);

        then(baseClient).should().executeRequest(expectedRequest,
                new TypeToken<ResourceCollection<ServerHardwareType>>() {}.getType());
    }

    @Test
    public void shouldGetServerHardwareTypeByName() {
        client.getByName(ANY_RESOURCE_NAME);

        Request expectedRequest = new Request(HttpMethod.GET, SERVER_HARDWARE_TYPE_URI);
        expectedRequest.addQuery(UrlParameter.getFilterByNameParameter(ANY_RESOURCE_NAME));

        then(baseClient).should().executeRequest(expectedRequest,
                new TypeToken<ResourceCollection<ServerHardwareType>>() {}.getType());
    }

    @Test
    public void shouldUpdateServerHardwareType() {
        ServerHardwareTypeUpdate serverHardwareTypeUpdate = new ServerHardwareTypeUpdate();

        client.update(ANY_RESOURCE_ID, serverHardwareTypeUpdate);

        String expectedUri = SERVER_HARDWARE_TYPE_URI + "/" + ANY_RESOURCE_ID;
        Request expectedRequest = new Request(HttpMethod.PUT, expectedUri, serverHardwareTypeUpdate);

        then(baseClient).should().executeRequest(expectedRequest, TypeToken.of(ServerHardwareType.class).getType());
    }

    @Test
    public void shouldDeleteServerHardwareType() {
        client.delete(ANY_RESOURCE_ID, TaskTimeout.of(321));

        String expectedUri = SERVER_HARDWARE_TYPE_URI + "/" + ANY_RESOURCE_ID;
        Request expectedRequest = new Request(HttpMethod.DELETE, expectedUri);
        expectedRequest.setTimeout(321);

        then(baseClient).should().executeMonitorableRequest(expectedRequest);
    }

}
