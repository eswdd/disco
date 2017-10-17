/*
 * Copyright 2014, Simon Matić Langford
 * Copyright 2014, The Sporting Exchange Limited
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

package uk.co.exemel.disco.transport.impl.protocol.http;

import uk.co.exemel.disco.core.api.builder.DehydratedExecutionContextBuilder;
import uk.co.exemel.disco.transport.api.DehydratedExecutionContextComponent;
import uk.co.exemel.disco.transport.api.SingleComponentResolver;
import uk.co.exemel.disco.transport.api.protocol.http.HttpCommand;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Default HTTP received time resolver. Sets the received time as 'now'.
 */
public class HttpReceivedTimeResolver<Ignore> extends SingleComponentResolver<HttpCommand, Ignore> {
    public HttpReceivedTimeResolver() {
        super(DehydratedExecutionContextComponent.ReceivedTime);
    }

    @Override
    public void resolve(HttpCommand httpCommand, Ignore ignore, DehydratedExecutionContextBuilder builder) {
        builder.setReceivedTime(new Date());
    }
}
