/*
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

import uk.co.exemel.disco.api.DehydratedExecutionContext;
import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.core.api.ServiceBindingDescriptor;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.transport.api.CommandResolver;
import uk.co.exemel.disco.transport.api.TransportCommand.CommandStatus;
import uk.co.exemel.disco.transport.api.protocol.http.HttpCommand;
import uk.co.exemel.disco.transport.api.DehydratedExecutionContextResolution;
import uk.co.exemel.disco.util.ServletResponseFileStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;

/**
 * Command processor handles invalid service context path. Sends a 404 status and logs the request.
 */
@ManagedResource
public class ServiceNotFoundHttpCommandProcessor extends AbstractHttpCommandProcessor<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceNotFoundHttpCommandProcessor.class);

    public ServiceNotFoundHttpCommandProcessor(DehydratedExecutionContextResolution contextResolution, String requestTimeoutHeader) {
        super(Protocol.RESCRIPT, contextResolution, requestTimeoutHeader);
        setName("ServiceNotFoundHttpCommandProcessor");
        setPriority(0);
    }

	@Override
	protected CommandResolver<HttpCommand> createCommandResolver(
            HttpCommand command, Tracer tracer) {
		throw new DiscoServiceException(ServerFaultCode.NoSuchService, "Service does not exist");
	}

	@Override
	protected void writeErrorResponse(HttpCommand command,
                                      DehydratedExecutionContext context, DiscoException e, boolean traceStarted) {
        try {
            if (command.getStatus() == CommandStatus.InProgress) {
                try {
                    int bytesWritten = ServletResponseFileStreamer.getInstance().stream404ToResponse(command.getResponse());
                    logAccess(command, resolveContextForErrorHandling(context, command), 0, bytesWritten, null, null, ResponseCode.NotFound);
                } catch (IOException ex) {
                    LOGGER.error("Unable to write error response", ex);
                } finally {
                    command.onComplete();
                    // no attempt to stop tracing here since it will never have started due to the timing of the call to createCommandResolver()
                }
            }
        }
        finally {
            if (context != null && traceStarted) {
                tracer.end(context.getRequestUUID());
            }
        }
    }

	@Override
	public void bind(ServiceBindingDescriptor operation) {

	}

	@Override
	public void onDiscoStart() {

	}

}
