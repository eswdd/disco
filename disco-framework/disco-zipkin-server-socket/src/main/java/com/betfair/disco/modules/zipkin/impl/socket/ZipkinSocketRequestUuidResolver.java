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

package uk.co.exemel.disco.modules.zipkin.impl.socket;

import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.core.api.builder.DehydratedExecutionContextBuilder;
import uk.co.exemel.disco.modules.zipkin.api.ZipkinKeys;
import uk.co.exemel.disco.modules.zipkin.impl.ZipkinExecutionContextResolverFactory;
import uk.co.exemel.disco.modules.zipkin.impl.ZipkinManager;
import uk.co.exemel.disco.netutil.nio.marshalling.SocketContextResolutionParams;
import uk.co.exemel.disco.netutil.nio.marshalling.SocketRequestUuidResolver;

import java.util.Map;

/**
 * Zipkin request uuid resolver.
 */
public class ZipkinSocketRequestUuidResolver<Void> extends SocketRequestUuidResolver<Void> {

    private final ZipkinManager zipkinManager;

    private final int serverPort;

    public ZipkinSocketRequestUuidResolver(ZipkinExecutionContextResolverFactory resolverFactory,
                                           ZipkinManager zipkinManager, int socketServerPort) {
        this.zipkinManager = zipkinManager;
        this.serverPort = socketServerPort;
        resolverFactory.setSocketRequestResolver(this);
    }

    @Override
    public void resolve(SocketContextResolutionParams params, Void ignore, DehydratedExecutionContextBuilder builder) {
        RequestUUID discoUUID = super.resolve(params);

        String traceId = null;
        String spanId = null;
        String parentSpanId = null;
        String sampled = null;
        String flags = null;
        Map<String, String> additionalData = params.getAdditionalData();

        if (additionalData != null) {
            traceId = additionalData.get(ZipkinKeys.TRACE_ID);
            spanId = additionalData.get(ZipkinKeys.SPAN_ID);
            parentSpanId = additionalData.get(ZipkinKeys.PARENT_SPAN_ID);
            sampled = additionalData.get(ZipkinKeys.SAMPLED);
            flags = additionalData.get(ZipkinKeys.FLAGS);
        }

        RequestUUID requestUUID = zipkinManager.createNewZipkinRequestUUID(discoUUID, traceId, spanId, parentSpanId,
                sampled, flags, serverPort);
        builder.setRequestUUID(requestUUID);
    }
}
