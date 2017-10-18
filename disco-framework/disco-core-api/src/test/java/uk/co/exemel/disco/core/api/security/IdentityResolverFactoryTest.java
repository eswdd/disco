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

package uk.co.exemel.disco.core.api.security;

import uk.co.exemel.disco.api.DehydratedExecutionContext;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.api.security.IdentityChain;
import uk.co.exemel.disco.api.security.IdentityToken;
import uk.co.exemel.disco.api.security.InvalidCredentialsException;
import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Unit test for @see IdentityResolverFactory
 */
public class IdentityResolverFactoryTest {
    private class TestIdentityResolver implements IdentityResolver {
        @Override
        public void resolve(IdentityChain chain, DehydratedExecutionContext ctx) throws InvalidCredentialsException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public List<IdentityToken> tokenise(IdentityChain chain) {
            return null;
        }
    }

    @Test
    public void test() {
        IdentityResolverFactory factory = new IdentityResolverFactory();

        IdentityResolver identityResolver = new TestIdentityResolver();

        factory.setIdentityResolver(identityResolver);
        assertNotNull(factory.getIdentityResolver());
    }

    @Test(expected = DiscoFrameworkException.class)
    public void testDoubleRegistration() {
        IdentityResolverFactory factory = new IdentityResolverFactory();
        factory.setIdentityResolver(new TestIdentityResolver());
        factory.setIdentityResolver(new TestIdentityResolver());
    }
}
