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

package uk.co.exemel.disco.core.impl.ev;

import uk.co.exemel.disco.api.ContainerContext;
import uk.co.exemel.disco.api.Service;
import uk.co.exemel.disco.core.api.BindingDescriptorRegistrationListener;
import uk.co.exemel.disco.core.api.ServiceRegistrar;
import uk.co.exemel.disco.core.api.ev.*;

import java.util.Iterator;

/**
 * Instantiate one of these to introduce a service to disco
 */
public class ClientServiceRegistration extends AbstractServiceRegistration {

    @Override
    public void introduceServiceToEV(ExecutionVenue ev, ServiceRegistrar serviceRegistrar, CompoundExecutableResolver compoundExecutableResolver) {
        setService(NULL_SERVICE);
        super.introduceServiceToEV(ev, serviceRegistrar, compoundExecutableResolver);
    }

    @Override
    public void introduceServiceToTransports(Iterator<? extends BindingDescriptorRegistrationListener> transports) {
        //There is no need to introduce a client service to the transports for anything (events are handled by the resolver)
    }

    private static Service NULL_SERVICE = new Service() {

        @Override
        public void init(ContainerContext cc) {
        }
    };
}
