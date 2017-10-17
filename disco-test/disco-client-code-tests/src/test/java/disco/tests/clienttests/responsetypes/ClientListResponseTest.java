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

// Originally from ClientTests/Transport/ResponseTypes/Client_Rescript_ListResponse.xls;
package uk.co.exemel.disco.tests.clienttests.responsetypes;

import com.betfair.baseline.v2.BaselineSyncClient;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure an operation performed against disco via a disco client can have a list as a response object
 */
public class ClientListResponseTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up the client
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        // Build the expected response list
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        List<String> requestList = discoClientResponseTypeUtils2.buildList("aaa,bbb,ccc,ddd");
        // Call the method using rescript transport and check that the returned list is as expected
        List<String> list3 = client.testSimpleListGet(context, requestList);
        assertEquals("aaa", list3.get(0));
        assertEquals("bbb", list3.get(1));
        assertEquals("ccc", list3.get(2));
        assertEquals("ddd", list3.get(3));
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
