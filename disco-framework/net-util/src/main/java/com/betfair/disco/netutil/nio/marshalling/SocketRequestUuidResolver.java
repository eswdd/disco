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

package uk.co.exemel.disco.netutil.nio.marshalling;

import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.core.api.builder.DehydratedExecutionContextBuilder;
import uk.co.exemel.disco.transport.api.DehydratedExecutionContextComponent;
import uk.co.exemel.disco.transport.api.SingleComponentResolver;
import uk.co.exemel.disco.util.RequestUUIDImpl;

/**
 * Default Socket UUID resolver.
 */
public class SocketRequestUuidResolver<Void> extends SingleComponentResolver<SocketContextResolutionParams, Void> {

    public SocketRequestUuidResolver() {
        super(DehydratedExecutionContextComponent.RequestUuid);
    }

    @Override
    public void resolve(SocketContextResolutionParams params, Void ignore, DehydratedExecutionContextBuilder builder) {
        RequestUUID requestUUID = resolve(params);
        builder.setRequestUUID(requestUUID);
    }

    protected RequestUUID resolve(SocketContextResolutionParams params) {
        RequestUUID requestUUID;
        if (params.getUuid() != null) {
            requestUUID = new RequestUUIDImpl(params.getUuid());
        } else {
            requestUUID = new RequestUUIDImpl();
        }
        return requestUUID;
    }
}
