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

package uk.co.exemel.disco.transport.api.protocol.http.jsonrpc;

import uk.co.exemel.disco.core.api.OperationBindingDescriptor;
import uk.co.exemel.disco.core.api.ev.OperationKey;

/**
 * OperationKey only binding descriptor implementation for JsonRpc
 */
public class JsonRpcOperationBindingDescriptor implements OperationBindingDescriptor {
    private OperationKey operationKey;

    public JsonRpcOperationBindingDescriptor(OperationKey operationKey) {
        this.operationKey = operationKey;
    }
    @Override
    public OperationKey getOperationKey() {
        return operationKey;
    }
}
