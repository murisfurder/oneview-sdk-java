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

package com.hp.ov.sdk.messaging.scmb;

import com.hp.ov.sdk.dto.BaseModelResource;
import com.hp.ov.sdk.messaging.core.BaseMessageModel;

public class ScmbTypedMessage<T extends BaseModelResource> extends BaseMessageModel<T> {

    private static final long serialVersionUID = -6123819933524554432L;

}