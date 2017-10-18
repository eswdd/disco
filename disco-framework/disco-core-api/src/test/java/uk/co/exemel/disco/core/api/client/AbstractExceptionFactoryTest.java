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

package uk.co.exemel.disco.core.api.client;

import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.api.fault.DiscoApplicationException;
import uk.co.exemel.disco.core.api.exception.DiscoClientException;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Unit test for @see AbstractExceptionFactory
 */
public class AbstractExceptionFactoryTest {

    private static class AEF extends AbstractExceptionFactory {
    };

    private static class TestAppException extends DiscoApplicationException {

        public TestAppException() {
            super(ResponseCode.InternalError, "");
        }

        @Override
        public List<String[]> getApplicationFaultMessages() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getApplicationFaultNamespace() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }


    @Test(expected=IllegalArgumentException.class)
    public void testPrefixExtractor() {
        AEF cut = new AEF();
        assertEquals("DSC", cut.extractExceptionPrefix("DSC-0001"));

        cut.extractExceptionPrefix("XXX");
        fail("Should have thrown an exception due to incorrect exception prefix string");
    }

    @Test
    public void testParseException() {
        String exceptionCode = "DSC-0013";
        AEF cut = new AEF();
        String reason = "too weak";
        DiscoException ce = (DiscoException)cut.parseException(ResponseCode.Forbidden, exceptionCode, reason, null);
        assertNotNull(ce);
        assertTrue(ce instanceof DiscoClientException);
        assertEquals(exceptionCode, ce.getServerFaultCode().getDetail());
        assertEquals(reason, ce.getMessage());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseExceptionWithUnknownPrefix() {
        String exceptionCode = "XXX-0013";
        AEF cut = new AEF();
        DiscoException ce = (DiscoException)cut.parseException(ResponseCode.Forbidden, exceptionCode, null, null);
    }

    @Test
    public void testAdditionalExceptionInstantiator() {
        AEF cut = new AEF();
        String code = "YYY-0031";
        AbstractExceptionFactory.ExceptionInstantiator myEI = new AbstractExceptionFactory.ExceptionInstantiator() {
            @Override
            public Exception createException(ResponseCode responseCode, String prefix, String reason, Map<String,String> exceptionParams) {
                return new TestAppException();
            }
        };

        cut.registerExceptionInstantiator("YYY", myEI);

        DiscoException ce = (DiscoException)cut.parseException(ResponseCode.Forbidden, "DSC-0013", null, null);
        assertNotNull(ce);

        Exception exception = cut.parseException(ResponseCode.InternalError, code, null, null);
        assertNotNull(exception);
        assertTrue(exception instanceof TestAppException);
    }

}
