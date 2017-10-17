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

// Originally from ClientTests/Transport/ResponseTypes/Client_Rescript_MapOfIntegersResponse.xls;
package uk.co.exemel.disco.tests.clienttests.responsetypes;

import com.betfair.baseline.v2.BaselineSyncClient;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure that when a i32MapSimpleTypeEcho operation is performed against disco via a disco client the request is sent and the response is handled correctly
 */
public class ClientMapOfIntegersResponseTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up client
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        // Build the expected response map
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        Map<Integer, Integer> inputmap = discoClientResponseTypeUtils2.buildIntMap("1,2", "11,12");
        // Call the method using rescript transport and check that the recieved map response is as expected
        Map<Integer, Integer> resultmap = client.i32MapSimpleTypeEcho(context, inputmap);
        // Call the method using rescript transport and check that the recieved map response is as expected
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        boolean response = discoClientResponseTypeUtils3.compareMaps(inputmap, resultmap);
        assertEquals(true, response);
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
