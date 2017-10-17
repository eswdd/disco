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

import uk.co.exemel.disco.core.api.ev.ClientExecutionResult;
import uk.co.exemel.disco.core.api.ev.ExecutionObserver;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.transcription.ParameterType;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for @see InvocationResponseImpl
 */
public class InvocationResponseImplTest {

    @Test
    public void testSuccess() {
        final String result = "result";

        ExecutionObserver successObserver = new ExecutionObserver() {
            @Override
            public void onResult(ExecutionResult executionResult) {
                assertTrue(executionResult.getResultType() == ExecutionResult.ResultType.Success);
                assertEquals(executionResult.getResult(), result);
                assertEquals(((ClientExecutionResult)executionResult).getResultSize(), 45);
            }
        };

        InvocationResponseImpl impl = new InvocationResponseImpl(result);
        impl.recreate(successObserver, new ParameterType(String.class, null), 45);
    }

    @Test
    public void testException() {
        final DiscoException ex = new DiscoServiceException(ServerFaultCode.FrameworkError, "service is down");

        ExecutionObserver exceptionObserver = new ExecutionObserver() {
            @Override
            public void onResult(ExecutionResult executionResult) {
                assertTrue(executionResult.getResultType() == ExecutionResult.ResultType.Fault);
                assertEquals(executionResult.getFault(), ex);
                assertEquals(((ClientExecutionResult)executionResult).getResultSize(), 65);
            }
        };


        InvocationResponseImpl impl = new InvocationResponseImpl(null, ex);

        impl.recreate(exceptionObserver, new ParameterType(String.class, null), 65);
    }
}
