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

// Originally from ClientTests/Transport/ResponseTypes/Client_Rescript_EnumResponse.xls;
package uk.co.exemel.disco.tests.clienttests.responsetypes;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.enumerations.SimpleValidValue;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.core.api.client.EnumWrapper;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure an operation performed against disco via a disco client can have an enum response object
 */
public class ClientEnumResponseTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up and call the method using rescript transport
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        
        EnumWrapper<SimpleValidValue> response = client.callWithEnumResponse(context);
        assertEquals(com.betfair.baseline.v2.enumerations.SimpleValidValue.WEASEL, response.getValue());
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
