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

package uk.co.exemel.disco.core.impl.exception;

import uk.co.exemel.disco.api.fault.DiscoApplicationException;
import uk.co.exemel.disco.core.api.ev.ExecutionObserver;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.core.api.exception.*;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.impl.ev.ServiceExceptionHandlingObserver;
import uk.co.exemel.disco.test.MockException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class ServiceExceptionHandlingObserverTest {

    private ExecutionObserver mockObserver;
    private ArgumentCaptor<ExecutionResult> captor;
    private ServiceExceptionHandlingObserver observer;

    @Before
    public void before() {
        mockObserver = mock(ExecutionObserver.class);
        captor = ArgumentCaptor.forClass(ExecutionResult.class);
        observer = new ServiceExceptionHandlingObserver(mockObserver);
    }

    private ExecutionResult execute(ExecutionResult arg) {
        observer.onResult(arg);
        verify(mockObserver).onResult(captor.capture());
        return captor.getValue();
    }

    @Test
    public void successResult() {
        String text = "message";
        ExecutionResult er = execute(new ExecutionResult(text));

        assertEquals(ExecutionResult.ResultType.Success, er.getResultType());
        assertEquals(text, er.getResult());
    }

    @Test
    public void discoException() {
        DiscoException ce = new DiscoFrameworkException("Wibble");
        ExecutionResult er = execute(new ExecutionResult(ce));

        assertEquals(ExecutionResult.ResultType.Fault, er.getResultType());
        assertEquals(ce, er.getFault());
    }

    @Test
    public void discoClientException() {
        DiscoException ce = new ClientException(ServerFaultCode.ServiceCheckedException,"message",new MockException());
        ExecutionResult er = execute(new ExecutionResult(ce));

        assertEquals(ExecutionResult.ResultType.Fault, er.getResultType());
        assertTrue(er.getFault() instanceof DiscoServiceException);
        assertEquals(ServerFaultCode.ServiceRuntimeException, er.getFault().getServerFaultCode());
        assertEquals(ce, er.getFault().getCause());
    }

    @Test
    public void serviceCheckedException() {
        DiscoApplicationException cae = new MockException();
        ExecutionResult er = execute(new ExecutionResult(cae));

        assertEquals(ExecutionResult.ResultType.Fault, er.getResultType());
        assertEquals(cae, er.getFault().getCause());
    }

    private class ClientException extends DiscoClientException {
        private ClientException(ServerFaultCode fault, String message, DiscoApplicationException dae) {
            super(fault, message, dae);
        }
    }
}
