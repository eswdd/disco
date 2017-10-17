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
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.impl.security.SSLAwareTokenResolver;

/**
 *
 */
public abstract class ClientAuthExecutable implements Executable {
    private boolean needsClientAuth;

    protected ClientAuthExecutable(boolean needsClientAuth) {
        this.needsClientAuth = needsClientAuth;
    }

    protected boolean checkClientAuth(ExecutionContext ctx, ExecutionObserver observer) {
        if (needsClientAuth) {
            boolean found = false;
            for (Identity id : ctx.getIdentity().getIdentities()) {
                if (id.getCredential() != null && id.getCredential().getName().equals(SSLAwareTokenResolver.SSL_CERT_INFO)) {
                    found = id.getCredential().getValue() != null;
                    if (found) {
                        break;
                    }
                }
            }
            if (!found) {
                observer.onResult(new ExecutionResult(new DiscoServiceException(ServerFaultCode.SecurityException,"missing credentials")));
                return false;
            }
        }
        return true;
    }
}
