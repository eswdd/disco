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

package uk.co.exemel.disco.marshalling.api.socket;

import uk.co.exemel.disco.api.DehydratedExecutionContext;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.ev.TimeConstraints;
import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.core.api.transcription.ParameterType;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectInput;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectOutput;
import uk.co.exemel.disco.transport.api.protocol.socket.InvocationRequest;
import uk.co.exemel.disco.transport.api.protocol.socket.InvocationResponse;

import java.io.IOException;
import java.util.Map;

/**
 * This interface describes a component for serialising and deserialising binary
 * transport request/responses
 */
public interface RemotableMethodInvocationMarshaller {

    public void writeInvocationRequest(InvocationRequest request, DiscoObjectOutput out, IdentityResolver identityResolver, Map<String,String> additionalData, byte protocolVersion) throws IOException;

    public void writeInvocationResponse(InvocationResponse response, DiscoObjectOutput out, byte protocolVersion) throws IOException;

    public InvocationResponse readInvocationResponse(ParameterType resultType, DiscoObjectInput in) throws IOException;

    public OperationKey readOperationKey(DiscoObjectInput in) throws IOException;

    public Object [] readArgs(Parameter[] argTypes, DiscoObjectInput in) throws IOException;

    public DehydratedExecutionContext readExecutionContext(DiscoObjectInput in, String remoteAddress, java.security.cert.X509Certificate[] clientCertChain, int transportSecurityStrengthFactor, byte protocolVersion) throws IOException;

    TimeConstraints readTimeConstraintsIfPresent(DiscoObjectInput in, byte protocolVersion) throws IOException;
}
