/*
 * Copyright 2015, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.exemel.disco.modules.zipkin.impl;

import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinData;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinDataBuilder;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinRequestUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base implementation of ZipkinRequestUUID.
 *
 * @see uk.co.exemel.disco.modules.zipkin.api.ZipkinRequestUUID
 */
public class ZipkinRequestUUIDImpl implements ZipkinRequestUUID {

    private RequestUUID discoUuid;

    private ZipkinData zipkinData;

    private ZipkinDataBuilder zipkinDataBuilder;

    public ZipkinRequestUUIDImpl(@Nonnull RequestUUID discoUuid) {
        this(discoUuid, null);
    }

    /**
     * Constuct a Disco/Zipkin Request object.
     *
     * @param discoUuid        Traditional Disco RequestUUID.
     * @param zipkinDataBuilder Zipkin data builder object to be populated later with the span name.
     *                          Passing null here means Zipkin tracing is not enabled for this request.
     */
    public ZipkinRequestUUIDImpl(@Nonnull RequestUUID discoUuid, @Nullable ZipkinDataBuilder zipkinDataBuilder) {
        Objects.requireNonNull(discoUuid);

        this.discoUuid = discoUuid;
        this.zipkinDataBuilder = zipkinDataBuilder;
    }

    @Override
    public String getRootUUIDComponent() {
        return discoUuid.getRootUUIDComponent();
    }

    @Override
    public String getParentUUIDComponent() {
        return discoUuid.getParentUUIDComponent();
    }

    @Override
    public String getLocalUUIDComponent() {
        return discoUuid.getLocalUUIDComponent();
    }

    @Override
    @Nonnull
    public RequestUUID getNewSubUUID() {
        RequestUUID discoSubUuid = discoUuid.getNewSubUUID();

        if (isZipkinTracingEnabled()) {
            // Creating a child zipkin data builder object.
            // The child span name will still need to be set after, as it happened with the original zipkinDataBuilder.
            ZipkinDataBuilder newZipkinDataBuilder = new ZipkinDataImpl.Builder()
                    .traceId(zipkinData.getTraceId())
                    .spanId(ZipkinManager.getRandomLong())
                    .parentSpanId(zipkinData.getSpanId())
                    .port(zipkinData.getPort());

            return new ZipkinRequestUUIDImpl(discoSubUuid, newZipkinDataBuilder);
        } else {
            // If this request is not being traced by Zipkin, the next request can't be traced either.
            return new ZipkinRequestUUIDImpl(discoSubUuid);
        }
    }

    @Override
    @Nonnull
    public ZipkinData getZipkinData() {
        if (zipkinData == null) {
            if (isZipkinTracingEnabled()) {
                throw new IllegalStateException("Zipkin Data is still incomplete");
            } else {
                throw new IllegalStateException("Zipkin tracing is not enabled for this request");
            }
        } else {
            return zipkinData;
        }
    }

    @Override
    public boolean isZipkinTracingEnabled() {
        return zipkinDataBuilder != null;
    }

    @Override
    public boolean isZipkinTracingReady() {
        return zipkinData != null;
    }

    @Override
    public ZipkinData buildZipkinData(@Nonnull String spanName) {
        Objects.requireNonNull(spanName);

        if (zipkinData == null) {
            zipkinData = zipkinDataBuilder.spanName(spanName).build();
            return zipkinData;
        } else {
            throw new IllegalStateException("Span name was already set for this request.");
        }
    }

    /**
     * Returns standard conforming Disco UUID, letting you use your own generator without affecting Zipkin specific
     * fields.
     *
     * @return String representing the Disco request uuid
     */
    @Override
    public String getUUID() {
        return discoUuid.getUUID();
    }

    @Override
    public String toDiscoLogString() {
        return discoUuid.toDiscoLogString();
    }

    @Override
    public String toString() {
        return "ZipkinRequestUUIDImpl{" +
                "discoUuid=" + getUUID() +
                ", zipkinData=" + zipkinData +
                '}';
    }
}
