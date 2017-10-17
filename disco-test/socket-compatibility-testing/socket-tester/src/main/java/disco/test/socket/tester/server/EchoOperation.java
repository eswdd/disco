/*
 * Copyright 2015, Simon Matić Langford
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

package uk.co.exemel.disco.test.socket.tester.server;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.security.Identity;
import uk.co.exemel.disco.core.api.ev.*;
import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.impl.security.SSLAwareTokenResolver;
import uk.co.exemel.disco.test.socket.tester.common.*;

/**
*
*/
class EchoOperation extends ClientAuthExecutable {

    EchoOperation(boolean needsClientAuth) {
        super(needsClientAuth);
    }

    @Override
    public void execute(ExecutionContext ctx, OperationKey key, Object[] args, ExecutionObserver observer, ExecutionVenue executionVenue, TimeConstraints timeConstraints) {
        if (key.equals(Common.echoOperationDefinition.getOperationKey())) {
            if (!checkClientAuth(ctx,observer))
            {
                return;
            }
            EchoResponse response = new EchoResponse();
            response.setMessage((String) args[0]);
            try {
                ExecutionContextTO responseCtx = (ExecutionContextTO) Conversion.convert(ctx, ExecutionContext.class, ExecutionContextTO.class);
                response.setExecutionContext(responseCtx);
                observer.onResult(new ExecutionResult(response));
            }
            catch (Exception e) {
                observer.onResult(new ExecutionResult(new DiscoServiceException(ServerFaultCode.ServiceRuntimeException,"error",e)));
            }
        }
        else if (key.equals(Common.echoFailureOperationDefinition.getOperationKey())) {
            if (!checkClientAuth(ctx,observer))
            {
                return;
            }
            EchoException ee = new EchoException(EchoExceptionErrorCodeEnum.GENERIC, (String)args[0]);
            observer.onResult(new ExecutionResult(new DiscoServiceException(ServerFaultCode.ServiceCheckedException,"error",ee)));
        }
        else {
            observer.onResult(new ExecutionResult(new DiscoFrameworkException(ServerFaultCode.NoSuchOperation, key.toString())));
        }
    }
}
