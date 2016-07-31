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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.hp.ov.sdk.dto.HttpMethodType;
import com.hp.ov.sdk.rest.http.core.UrlParameter;

public class Request {

    private final HttpMethodType type;
    private final String uri;

    private Map<String, String> query;
    private Optional<Object> entity;
    private boolean forceTaskReturn;
    private int timeout;

    public Request(HttpMethodType type, String uri) {
        this(type, uri, null);
    }

    public Request(HttpMethodType type, String uri, Object entity) {
        this.type = type;
        this.uri = uri;

        this.entity = Optional.fromNullable(entity);
        this.forceTaskReturn = false;
        this.timeout = 60000;
    }

    public HttpMethodType getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public Request addQuery(UrlParameter query) {
        this.queryMap().put(query.getKey(), query.getValue());

        return this;
    }

    public Map<String, String> getQuery() {
        return ImmutableMap.copyOf(this.queryMap());
    }

    private Map<String, String> queryMap() {
        if (query == null) {
            query = new LinkedHashMap();
        }
        return query;
    }

    public Object getEntity() {
        return entity.orNull();
    }

    public void setEntity(Object entity) {
        this.entity = Optional.fromNullable(entity);
    }

    public boolean isForceTaskReturn() {
        return forceTaskReturn;
    }

    public void setForceTaskReturn(boolean forceTaskReturn) {
        this.forceTaskReturn = forceTaskReturn;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof Request) {
            Request request = (Request) o;

            return new EqualsBuilder()
                    .append(forceTaskReturn, request.forceTaskReturn)
                    .append(type, request.type)
                    .append(uri, request.uri)
                    .append(query, request.query)
                    .append(entity, request.entity)
                    .append(timeout, request.timeout)
                    .isEquals();

        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(type)
                .append(uri)
                .append(query)
                .append(entity)
                .append(timeout)
                .append(forceTaskReturn)
                .toHashCode();
    }
}