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
<#include "common.ftl">
<#assign service = doc.@name>
// Generated from clientFactory.ftl
package ${package}.${majorVersion};

import ${package}.${majorVersion}.*;
import ${package}.${majorVersion}.exception.*;
import ${package}.${majorVersion}.rescript.*;
import uk.co.exemel.disco.api.Service;
import uk.co.exemel.disco.client.factory.AbstractDiscoClientFactory;
import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.api.security.IdentityTokenResolver;

/**
 * Convenience factory that generates instances of disco clients and introduces them to EV
 */
@SuppressWarnings("all")
public class ${service}ClientFactory extends AbstractDiscoClientFactory {<#t>

    public ${service}SyncClient createSyncHttpClient(String host, String namespace) {
        return createSyncHttpClient(host, namespace, null, null);
    }

    public ${service}SyncClient createSyncHttpClient(String host, String namespace, IdentityResolver identityResolver, IdentityTokenResolver identityTokenResolver) {
        final ${service}SyncClient syncClient = getSyncClient(namespace);
        registerDiscoServiceClient(syncClient, host, namespace, identityResolver, identityTokenResolver);
        return syncClient;
    }

    public ${service}Client createASyncHttpClient(String host, String namespace) {
        return createASyncHttpClient(host, namespace, null, null);
    }

    public ${service}Client createASyncHttpClient(String host, String namespace, IdentityResolver identityResolver, IdentityTokenResolver identityTokenResolver) {
        final ${service}Client aSyncClient = getASyncClient(namespace);
        registerDiscoServiceClient(aSyncClient, host, namespace, identityResolver, identityTokenResolver);
        return aSyncClient;
    }

    private void registerDiscoServiceClient(Service serviceClient, String host, String namespace, IdentityResolver identityResolver, IdentityTokenResolver identityTokenResolver) {
        super.registerClient(serviceClient, host, namespace,
                new ${service}RescriptServiceBindingDescriptor(),
                new ${service}ExceptionFactory(),
                new ${service}ServiceDefinition(),
                new ${service}ClientExecutableResolver(),
                identityResolver, identityTokenResolver);
    }

    private ${service}SyncClient getSyncClient(String namespace) {
        return new ${service}SyncClientImpl(executionVenue, namespace);
    }

    private ${service}Client getASyncClient(String namespace) {
        if (executor == null) {
            throw new DiscoFrameworkException("ASync disco client cannot be created when no custom executor is defined");
        }
        return new ${service}ClientImpl(executionVenue, executor, namespace);
    }
}
