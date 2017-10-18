/*
 * Copyright 2014, Simon Matić Langford
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

package uk.co.exemel.disco.transport.impl.protocol.http;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.api.security.InferredCountryResolver;
import uk.co.exemel.disco.transport.api.*;
import uk.co.exemel.disco.transport.api.protocol.http.HttpCommand;
import uk.co.exemel.disco.transport.impl.DehydratedExecutionContextResolutionImpl;
import uk.co.exemel.disco.util.RequestUUIDImpl;
import uk.co.exemel.disco.util.UUIDGeneratorImpl;
import uk.co.exemel.disco.util.geolocation.GeoIPLocator;
import uk.co.exemel.disco.util.geolocation.RemoteAddressUtils;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public abstract class AbstractHttpContextResolutionTest {
    private static final String AZ = "Azerbaijan";

    private HttpCommand command;
    private HttpServletRequest request;
    protected GeoIPLocator geoIPLocator;
    protected RequestTimeResolver requestTimeResolver;
    protected InferredCountryResolver inferredCountryResolver;

    private DehydratedExecutionContextResolutionImpl contextResolution;

    @Before
    public void init() {
        RequestUUIDImpl.setGenerator(new UUIDGeneratorImpl());

        command = mock(HttpCommand.class);
        request = mock(HttpServletRequest.class);
        when(command.getRequest()).thenReturn(request);
        geoIPLocator = mock(GeoIPLocator.class);
        requestTimeResolver = mock(RequestTimeResolver.class);
        inferredCountryResolver = mock(InferredCountryResolver.class);

        DefaultExecutionContextResolverFactory resolverFactory = new DefaultExecutionContextResolverFactory();
        resolverFactory.setGeoIPLocator(geoIPLocator);
        resolverFactory.setGeoLocationDeserializer(new DefaultGeoLocationDeserializer());
        resolverFactory.setInferredCountryResolver(inferredCountryResolver);
        resolverFactory.setRequestTimeResolver(requestTimeResolver);
        resolverFactory.setUnknownCipherKeyLength(1);
        resolverFactory.setUuidHeader("X-UUID");
        resolverFactory.setUuidParentsHeader("X-UUID-Parents");

        contextResolution = new DehydratedExecutionContextResolutionImpl();
        contextResolution.registerFactory(resolverFactory);
        contextResolution.init(false);
    }

    protected abstract Protocol getProtocol();

    @Test
    public void testResolveExecutionContext() throws Exception {
        when(inferredCountryResolver.inferCountry(anyObject())).thenReturn(AZ);
        when(request.getScheme()).thenReturn("http");
        GeoLocationDetails gld = mock(GeoLocationDetails.class);
        //test an empty request
        List<String> ipAddresses =  Collections.emptyList();
        when(geoIPLocator.getGeoLocation(isNull(String.class), eq(ipAddresses), eq(AZ))).thenReturn(gld);
        Date requestTime = new Date();
        when(requestTimeResolver.resolveRequestTime(any())).thenReturn(requestTime);
        ExecutionContext context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertNotNull(context.getRequestUUID());
        assertNotNull(context.getReceivedTime());
        assertEquals(requestTime, context.getRequestTime());
        assertNotNull(context.getLocation());

        //Test request contains uuid, id and remote address
        RequestUUIDImpl uuid = new RequestUUIDImpl();
        when(request.getHeader("X-UUID")).thenReturn(uuid.toString());
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        when(geoIPLocator.getGeoLocation("1.2.3.4", RemoteAddressUtils.parse("1.2.3.4", null), AZ)).thenReturn(gld);
        context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertEquals(uuid, context.getRequestUUID());
        assertEquals(gld, context.getLocation());

        //Test request contains X-Forwarded-For header and resolves geo-location correctly
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.20.30.40");
        when(geoIPLocator.getGeoLocation("1.2.3.4", RemoteAddressUtils.parse("10.20.30.40", null), AZ)).thenReturn(gld);
        context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertEquals(gld, context.getLocation());
    }

    @Test
    public void testResolveExecutionContextWithoutCountryResolver() throws Exception {
        when(request.getScheme()).thenReturn("http");
        GeoLocationDetails gld = mock(GeoLocationDetails.class);
        List ipAddresses = Collections.emptyList();
        when(geoIPLocator.getGeoLocation(isNull(String.class), eq(ipAddresses), isNull(String.class))).thenReturn(gld);
        ExecutionContext context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertNotNull(context.getRequestUUID());
        assertNotNull(context.getReceivedTime());
        assertNotNull(context.getLocation());
        assertNull(context.getLocation().getInferredCountry());

        //Test request contains uuid, id and remote address
        RequestUUIDImpl uuid = new RequestUUIDImpl();
        when(request.getHeader("X-UUID")).thenReturn(uuid.toString());
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        when(geoIPLocator.getGeoLocation("1.2.3.4", RemoteAddressUtils.parse("1.2.3.4", null), null)).thenReturn(gld);
        context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertEquals(uuid, context.getRequestUUID());
        assertEquals(gld, context.getLocation());
        assertNull(context.getLocation().getInferredCountry());

        //Test request contains X-Forwarded-For header and resolves geo-location correctly
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.20.30.40");
        when(geoIPLocator.getGeoLocation("1.2.3.4", RemoteAddressUtils.parse("10.20.30.40", null), null)).thenReturn(gld);
        context = contextResolution.resolveExecutionContext(getProtocol(), command, null);
        assertNotNull(context);
        assertEquals(gld, context.getLocation());
        assertNull(context.getLocation().getInferredCountry());
    }
}
