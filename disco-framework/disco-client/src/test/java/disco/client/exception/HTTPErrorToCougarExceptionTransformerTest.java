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

package uk.co.exemel.disco.client.exception;

import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.core.api.exception.DiscoClientException;
import uk.co.exemel.disco.core.api.client.ExceptionFactory;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.fault.DiscoFault;
import uk.co.exemel.disco.core.api.fault.FaultDetail;
import uk.co.exemel.disco.logging.DiscoLoggingUtils;
import org.slf4j.LoggerFactory;
import uk.co.exemel.disco.marshalling.api.databinding.FaultUnMarshaller;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit test for HTTPErrorToDiscoExceptionTransformer
 */
public class HTTPErrorToDiscoExceptionTransformerTest {

    @BeforeClass
    public static void suppressLogs() {
        DiscoLoggingUtils.suppressAllRootLoggerOutput();
    }

    @Test
    public void testTransform() throws UnsupportedEncodingException {
        FaultUnMarshaller faultUnMarshaller = mock(FaultUnMarshaller.class);
        ExceptionFactory exceptionFactory = mock(ExceptionFactory.class);
        DiscoFault mockDiscoFault = mock(DiscoFault.class);

        FaultDetail df = mock(FaultDetail.class);
        when(mockDiscoFault.getDetail()).thenReturn(df);
        when(df.getFaultMessages()).thenReturn(Collections.<String[]>emptyList());
        when(faultUnMarshaller.unMarshallFault(any(InputStream.class), anyString())).thenReturn(mockDiscoFault);
        when(exceptionFactory.parseException(
                any(ResponseCode.class), anyString(), anyString(), any(List.class))).thenReturn(
                new DiscoClientException(ServerFaultCode.FrameworkError, "bang"));

        HTTPErrorToDiscoExceptionTransformer tx = new HTTPErrorToDiscoExceptionTransformer(faultUnMarshaller);

        Exception ex = tx.convert(new ByteArrayInputStream("hello".getBytes("UTF-8")), exceptionFactory, HttpServletResponse.SC_NOT_FOUND);
        assertNotNull(ex);
    }

}
