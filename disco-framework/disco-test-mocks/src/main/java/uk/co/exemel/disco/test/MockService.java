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

package uk.co.exemel.disco.test;

import uk.co.exemel.disco.api.RequestContext;
import uk.co.exemel.disco.api.Result;
import uk.co.exemel.disco.api.Service;
import uk.co.exemel.disco.api.annotations.IDLService;
import uk.co.exemel.disco.api.fault.DiscoApplicationException;


@IDLService(name="MockService", version="v1.0")
public interface MockService extends Service {
    public Result testMethod(RequestContext ctx, String name) throws DiscoApplicationException;
}
