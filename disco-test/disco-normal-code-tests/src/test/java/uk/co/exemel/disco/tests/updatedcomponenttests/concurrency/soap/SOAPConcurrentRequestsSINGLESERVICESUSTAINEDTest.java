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

// Originally from UpdatedComponentTests/Concurrency/SOAP/SOAP_ConcurrentRequests_SINGLE_SERVICE_SUSTAINED.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.concurrency.soap;

import uk.co.exemel.disco.testing.concurrency.SOAPConcurrencySingleServiceTest;
import uk.co.exemel.disco.testing.concurrency.SOAPConcurrenyResultBean;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Ensure that when sustained SOAP requests are performed against Disco, each request is successfully sent and the response to each is correctly  handled
 */
public class SOAPConcurrentRequestsSINGLESERVICESUSTAINEDTest {
    @Test
    public void doTest() throws Exception {
        // Execute the test, making the given number of SOAP calls to the testComplexMutator operation on the same thread
        SOAPConcurrenyResultBean executeTest1 = new SOAPConcurrencySingleServiceTest().executeTest(20, 10, "TestComplexMutator");
        // Get the expected responses to the requests made
        Map<String, HttpResponseBean> getExpectedResponses4 = executeTest1.getExpectedResponses();
        // Check the actual responses against the expected ones (with a date tolerance of 2000ms)
        Long oldTolerance = AssertionUtils.setDateTolerance(2000L);
        try {
            AssertionUtils.multiAssertEquals(getExpectedResponses4, executeTest1.getActualResponses());
        }
        finally {
            AssertionUtils.setDateTolerance(oldTolerance);
        }
    }

}
