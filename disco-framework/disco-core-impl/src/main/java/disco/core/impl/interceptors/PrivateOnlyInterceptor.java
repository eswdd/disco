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

package uk.co.exemel.disco.core.impl.interceptors;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.core.api.ev.ExecutionPreProcessor;
import uk.co.exemel.disco.core.api.ev.ExecutionRequirement;
import uk.co.exemel.disco.core.api.ev.InterceptorResult;
import uk.co.exemel.disco.core.api.ev.InterceptorState;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.impl.AddressClassifier;

/**
 * Prevents access to the detailed healthcheck unless we know the call has come from an internal address.
 */
public class PrivateOnlyInterceptor implements ExecutionPreProcessor {

    private AddressClassifier addressClassifier;

    public PrivateOnlyInterceptor(AddressClassifier addressClassifier) {
        this.addressClassifier = addressClassifier;
    }

    @Override
    public InterceptorResult invoke(ExecutionContext ctx, OperationKey key, Object[] args) {
        GeoLocationDetails gld = ctx.getLocation();

        if (addressClassifier.isPrivateAddress(gld.getRemoteAddr()) || addressClassifier.isLocalAddress(gld.getRemoteAddr())) {
            return new InterceptorResult(InterceptorState.CONTINUE);
        }

        return new InterceptorResult(InterceptorState.FORCE_ON_EXCEPTION, new DiscoServiceException(ServerFaultCode.BannedLocation, "Access not being made from private network location"));
    }

    @Override
    public String getName() {
        return "OnlyPrivateIPs";
    }

    @Override
    public ExecutionRequirement getExecutionRequirement() {
        return ExecutionRequirement.EXACTLY_ONCE;
    }
}
