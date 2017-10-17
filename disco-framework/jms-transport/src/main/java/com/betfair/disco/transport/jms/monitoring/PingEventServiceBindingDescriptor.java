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

package uk.co.exemel.disco.transport.jms.monitoring;

import uk.co.exemel.disco.DiscoVersion;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.core.api.ServiceVersion;
import uk.co.exemel.disco.transport.api.protocol.events.EventBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.events.EventServiceBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.events.jms.JMSEventBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.events.jms.JMSParamBindingDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PingEventServiceBindingDescriptor implements EventServiceBindingDescriptor {

    private EventBindingDescriptor[] eventBindings;
    private ServiceVersion serviceVersion;

    public PingEventServiceBindingDescriptor() {
        List<JMSParamBindingDescriptor> bindingDescriptors = new ArrayList<JMSParamBindingDescriptor>();
        JMSParamBindingDescriptor emissionTime = new JMSParamBindingDescriptor("emissionTime", JMSParamBindingDescriptor.ParamSource.BODY);
        bindingDescriptors.add(emissionTime);
        eventBindings = new EventBindingDescriptor[] { new JMSEventBindingDescriptor("PingEvent",bindingDescriptors,PingEvent.class) };

        serviceVersion = new ServiceVersion("v"+DiscoVersion.getMajorMinorVersion());
    }

    @Override
    public EventBindingDescriptor[] getEventBindings() {
        return eventBindings;
    }

    @Override
    public String getServiceName() {
        return "DiscoSonicTransportMonitoring";
    }

    @Override
    public String getServiceNamespace() {
        return "__disco_internal";
    }

    @Override
    public ServiceVersion getServiceVersion() {
        return serviceVersion;
    }

    @Override
    public Protocol getServiceProtocol() {
        return Protocol.JMS;
    }
}
