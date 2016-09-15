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
package com.hp.ov.sdk.rest.http.core.client;

public enum ApiVersion {
    V_120 (120),
    V_200 (200),
    V_201 (201),
    V_300 (300);

    private final int value;

    private ApiVersion(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ApiVersion fromStringValue(String value) throws NumberFormatException {
        if (value != null) {
            for (ApiVersion element : ApiVersion.values()) {
                if (element.value == Integer.parseInt(value)) {
                    return element;
                }
            }
        }

        return null;
    }
}
