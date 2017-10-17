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

package uk.co.exemel.disco.transport.impl.protocol.http.soap;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.api.security.IdentityToken;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.transport.api.TransportCommand;
import uk.co.exemel.disco.transport.impl.protocol.http.AbstractHttpCommandProcessorTest;
import uk.co.exemel.disco.transport.impl.protocol.http.AbstractHttpContextResolutionTest;
import org.apache.axiom.om.OMElement;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

import javax.ws.rs.core.MediaType;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SoapExecutionContextResolutionTest extends AbstractHttpContextResolutionTest {
    @Override
    protected Protocol getProtocol() {
        return Protocol.SOAP;
    }

}
