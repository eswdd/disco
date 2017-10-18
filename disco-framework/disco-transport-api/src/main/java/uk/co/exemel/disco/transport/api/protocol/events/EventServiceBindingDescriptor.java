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

package uk.co.exemel.disco.transport.api.protocol.events;

import uk.co.exemel.disco.core.api.BindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceVersion;

/**
 * This interface contains a set of Event bindings that make up the set of events exposed on
 * a particular event transport.
 * @see uk.co.exemel.disco.transport.api.protocol.events.EventBindingDescriptor
 */
public interface EventServiceBindingDescriptor extends BindingDescriptor {
    /**
     * Returns the set of events exposed through this event service
     * @return returns an array of EventBindingDescriptors exposed by this Service
     */
    public EventBindingDescriptor[] getEventBindings();

    /**
     * @return - returns the name of the service
     */
    public String getServiceName();

    /**
     * @return - returns the BSIDL namespace of the service
     */
    public String getServiceNamespace();

    /**
     * @return returns the version of the service
     */
    public ServiceVersion getServiceVersion();
}
