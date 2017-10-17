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

package uk.co.exemel.disco.client.factory;

import uk.co.exemel.disco.api.Service;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.api.security.IdentityTokenResolver;
import uk.co.exemel.disco.client.HttpClientExecutable;
import uk.co.exemel.disco.client.SyncHttpTransportFactory;
import uk.co.exemel.disco.client.api.ContextEmitter;
import uk.co.exemel.disco.core.api.ServiceDefinition;
import uk.co.exemel.disco.core.api.ev.ExecutionVenue;
import uk.co.exemel.disco.core.api.ev.RegisterableClientExecutableResolver;
import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import uk.co.exemel.disco.core.api.client.ExceptionFactory;
import uk.co.exemel.disco.core.impl.DiscoIntroductionService;
import uk.co.exemel.disco.core.impl.ev.ClientServiceRegistration;
import uk.co.exemel.disco.transport.api.protocol.http.HttpServiceBindingDescriptor;
import org.springframework.core.io.Resource;

import java.util.concurrent.Executor;

/**
 * Base disco client service factory that registers client services
 * with the specified parameters.  Currently works with only synchronous
 * http transport.
 * - Creates a new instance of the transport
 * - Initialises the executable resolver with the transport and the operation keys
 * - Packages the whole thing into a service registration request and introduces the service to EV
 */
public abstract class AbstractDiscoClientFactory {

    private SyncHttpTransportFactory syncHttpTransportFactory;
    private DiscoIntroductionService discoIntroductionService;
    protected ExecutionVenue executionVenue;
    protected Executor executor;

    protected void registerClient(Service service, String endpointUrl, String namespace,
                                  HttpServiceBindingDescriptor serviceBindingDescriptor, ExceptionFactory exceptionFactory,
                                  ServiceDefinition serviceDefinition, RegisterableClientExecutableResolver executableResolver,
                                  IdentityResolver identityResolver, IdentityTokenResolver identityTokenResolver) {
        registerClient(service, endpointUrl, namespace, serviceBindingDescriptor, exceptionFactory, serviceDefinition,
                executableResolver, identityResolver, identityTokenResolver, false, null, null, null, null, false);
    }

    protected void registerClient(Service service, String endpointUrl, String namespace,
                                  HttpServiceBindingDescriptor serviceBindingDescriptor, ExceptionFactory exceptionFactory,
                                  ServiceDefinition serviceDefinition, RegisterableClientExecutableResolver executableResolver,
                                  IdentityResolver identityResolver, IdentityTokenResolver identityTokenResolver,
                                  boolean sslEnabled, Resource keyStore, String keyPassword, Resource trustStore,
                                  String trustPassword, boolean hostnameVerificationDisabled) {

        try {
            // Initialise transport
            HttpClientExecutable transport = syncHttpTransportFactory.getHttpTransport(endpointUrl, serviceBindingDescriptor,
                    exceptionFactory, sslEnabled, keyStore, keyPassword,
                    trustStore, trustPassword, hostnameVerificationDisabled);
            transport.setIdentityTokenResolver(identityTokenResolver);
            transport.setIdentityResolver(identityResolver);
            transport.init();

            // Initialise executable resolver
            executableResolver.setDefaultOperationTransport(transport);

            // TODO : Wire in the eventTransport
            // executableResolver.setEventTransport(eventTransport);
            executableResolver.init();

            // Package up the registration request
            ClientServiceRegistration clientServiceRegistration = new ClientServiceRegistration();
            clientServiceRegistration.setResolver(executableResolver);
            clientServiceRegistration.setServiceDefinition(serviceDefinition);
            clientServiceRegistration.setNamespace(namespace);

            // register with ev
            discoIntroductionService.registerService(clientServiceRegistration);
        } catch (Exception ex) {
            throw new DiscoFrameworkException("Error while registering client with ev", ex);
        }
    }

    public void setDiscoIntroductionService(DiscoIntroductionService discoIntroductionService) {
        this.discoIntroductionService = discoIntroductionService;
    }

    public void setSyncHttpTransportFactory(SyncHttpTransportFactory syncHttpTransportFactory) {
        this.syncHttpTransportFactory = syncHttpTransportFactory;
    }

    public void setExecutionVenue(ExecutionVenue executionVenue) {
        this.executionVenue = executionVenue;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
