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

// Originally from ClientTests/Transport/StandardTesting/Client_Rescript_Get_RequestTypes_Parameters_ListOfSimpleTypeEnum.xls;
package uk.co.exemel.disco.tests.clienttests.standardtesting;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.enumerations.SimpleEnum;
import com.betfair.baseline.v2.to.NonMandatoryParamsOperationResponseObject;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure that when a simpleEnumList is passed to disco via a disco client the request is sent and the response is handled correctly
 */
public class ClientGetRequestTypesParametersListOfSimpleTypeEnumTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up the client to use rescript transport
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        // Build list to pass as parameter
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        List<SimpleEnum> queryList = discoClientResponseTypeUtils2.buildEnumList("FOOFOO,BARBAR,FOOFOO");
        // Build list to pass as parameter
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        List<SimpleEnum> headerList = discoClientResponseTypeUtils3.buildEnumList("FOO,FOOBAR,BARFOO");
        // Make call to the method via client and validate the same lists have been echoed in response
        NonMandatoryParamsOperationResponseObject response4 = client.simpleEnumListOperation(context, headerList, queryList);
        assertEquals("FOOFOO,BARBAR,FOOFOO", response4.getQueryParameter());
        assertEquals("FOO,FOOBAR,BARFOO", response4.getHeaderParameter());
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
