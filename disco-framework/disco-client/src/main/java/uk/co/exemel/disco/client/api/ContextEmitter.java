/*
 * Copyright 2014, Simon Matic Langford
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

package uk.co.exemel.disco.client.api;

import uk.co.exemel.disco.client.ClientCallContext;

/**
 * Defines an emitter of contextual info.
 */
public interface ContextEmitter<HR,C> {
    void emit(ClientCallContext ctx, HR request, C container);
}
