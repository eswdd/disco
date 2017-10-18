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

package uk.co.exemel.disco.transport.socket;

import uk.co.exemel.disco.core.api.RequestTimer;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectInput;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectOutput;
import org.apache.mina.common.IoSession;

public class SocketTransportRPCCommandImpl extends SocketTransportCommandImpl implements SocketTransportRPCCommand {

	private final DiscoObjectOutput output;

    private RequestTimer timer = new RequestTimer();
    private IoSession session;

    public SocketTransportRPCCommandImpl(DiscoObjectInput input, DiscoObjectOutput output, String remoteAddress, IoSession session) {
        super(input, remoteAddress, session);
		this.output = output;
	}

	@Override
	public DiscoObjectOutput getOutput() {
		return output;
	}
}
