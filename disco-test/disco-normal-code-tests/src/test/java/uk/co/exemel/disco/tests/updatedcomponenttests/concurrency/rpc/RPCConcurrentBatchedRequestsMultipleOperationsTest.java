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

// Originally from UpdatedComponentTests/Concurrency/RPC/RPC_ConcurrentBatchedRequests_MultipleOperations.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.concurrency.rpc;

import uk.co.exemel.disco.testing.concurrency.RPCConcurrentBatchedRequestsAcrossMultipleOperations;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when concurrent Batched JSON requests to multiple operations are performed against Disco, each request is successfully sent and the JSON response is correctly handled
 */
public class RPCConcurrentBatchedRequestsMultipleOperationsTest {
    @Test
    public void doTest() throws Exception {
        DiscoManager discoManager1 = DiscoManager.getInstance();
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp2 = new Timestamp(System.currentTimeMillis());
        // Execute the test, creating the given number of threads and making the given number of batched JSON calls to multiple operations per thread
        RPCConcurrentBatchedRequestsAcrossMultipleOperations.RPCConcurrentBatchedRequestsMultipleOsResponseBean executeTest3 = new RPCConcurrentBatchedRequestsAcrossMultipleOperations().executeTest(4, 400);
        // Get the expected responses to the requests made
        Map<String, Map<String, Object>> getExpectedResponses4 = executeTest3.getExpectedResponses();
        // Check the actual responses against the expected ones (with a date tolerance of 2000ms)
        Long oldTolerance = AssertionUtils.setDateTolerance(2000L);
        try {
            AssertionUtils.multiAssertEquals(getExpectedResponses4, executeTest3.getActualResponses());
            // Check the log entries are as expected
            // todo: original test didn't assert anything here...
        }
        finally {
            AssertionUtils.setDateTolerance(oldTolerance);
        }
    }

}
