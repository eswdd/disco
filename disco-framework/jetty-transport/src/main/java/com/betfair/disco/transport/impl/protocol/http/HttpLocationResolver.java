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

import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.api.security.InferredCountryResolver;
import uk.co.exemel.disco.core.api.builder.DehydratedExecutionContextBuilder;
import uk.co.exemel.disco.transport.api.DehydratedExecutionContextComponent;
import uk.co.exemel.disco.transport.api.SingleComponentResolver;
import uk.co.exemel.disco.transport.api.protocol.http.GeoLocationDeserializer;
import uk.co.exemel.disco.transport.api.protocol.http.HttpCommand;
import uk.co.exemel.disco.util.geolocation.GeoIPLocator;

import javax.servlet.http.HttpServletRequest;

/**
 * Default HTTP location resolution. Delegates to a given geoip locator, passing a deserialised location and optionally an
 * inferred country.
 */
public class HttpLocationResolver<Ignore> extends SingleComponentResolver<HttpCommand, Ignore> {
    private final GeoIPLocator geoIPLocator;
    private final GeoLocationDeserializer geoLocationDeserializer;
    private final InferredCountryResolver<HttpServletRequest> inferredCountryResolver;

    public HttpLocationResolver(GeoIPLocator geoIPLocator, GeoLocationDeserializer geoLocationDeserializer, InferredCountryResolver<HttpServletRequest> inferredCountryResolver) {
        super(DehydratedExecutionContextComponent.Location);
        this.geoIPLocator = geoIPLocator;
        this.geoLocationDeserializer = geoLocationDeserializer;
        this.inferredCountryResolver = inferredCountryResolver;
    }

    @Override
    public void resolve(HttpCommand httpCommand, Ignore ignore, DehydratedExecutionContextBuilder builder) {
        String inferredCountry = null;
        if (inferredCountryResolver != null) {
            inferredCountry = inferredCountryResolver.inferCountry(httpCommand.getRequest());
        }
        GeoLocationDetails geoDetails = geoIPLocator.getGeoLocation(httpCommand.getRequest().getRemoteAddr(), geoLocationDeserializer.deserialize(httpCommand.getRequest(), httpCommand.getRequest().getRemoteAddr()), inferredCountry);
        builder.setLocation(geoDetails);
    }
}
