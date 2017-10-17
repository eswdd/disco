/*
 * Copyright 2013, The Sporting Exchange Limited
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

package uk.co.exemel.disco.baseline;

import com.betfair.baseline.v2.enumerations.PreOrPostInterceptorException;
import com.betfair.baseline.v2.enumerations.SimpleExceptionErrorCodeEnum;
import com.betfair.baseline.v2.exception.SimpleException;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.core.api.ev.*;
import uk.co.exemel.disco.core.impl.ev.PostProcessingInterceptorWrapper;

import java.util.List;

public class CheckedExceptionPostProcInterceptor implements ExecutionPostProcessor {
    @Override
    public InterceptorResult invoke(ExecutionContext ctx, OperationKey key, Object[] args, ExecutionResult result) {
        InterceptorResult interceptorResult;

        if (key.getOperationName().equals("interceptorCheckedExceptionOperation") &&
            args[0] instanceof PreOrPostInterceptorException &&
            ((PreOrPostInterceptorException)args[0]) == PreOrPostInterceptorException.POST) {

            interceptorResult = new InterceptorResult(InterceptorState.FORCE_ON_EXCEPTION,
                    new SimpleException(ResponseCode.BadRequest,
                            SimpleExceptionErrorCodeEnum.GENERIC,
                            "An anticipated post-execution BSIDL defined checked exception"));
        } else {
            interceptorResult = new InterceptorResult(InterceptorState.CONTINUE);
        }
        return interceptorResult;
    }

    @Override
    public String getName() {
        return "CheckedExceptionPostProcInterceptor";
    }
}
