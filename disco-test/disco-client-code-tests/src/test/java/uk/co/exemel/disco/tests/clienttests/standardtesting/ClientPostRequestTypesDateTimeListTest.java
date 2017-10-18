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

// Originally from ClientTests/Transport/StandardTesting/Client_Rescript_Post_RequestTypes_DateTimeList.xls;
package uk.co.exemel.disco.tests.clienttests.standardtesting;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.to.BodyParamDateTimeListObject;
import com.betfair.baseline.v2.to.DateTimeListOperationResponseObject;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure that when a dateTimeList is passed in a body parameter to disco via a disco client the request is sent and the response is handled correctly
 */
public class ClientPostRequestTypesDateTimeListTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up the client to use rescript transport
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        // Create date to be put in list
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        Date dateParam1 = discoClientResponseTypeUtils2.createDateFromString("2009-06-01T13:50:00.0Z");
        DiscoHelpers helper = new DiscoHelpers();
        Date convertedDate1 = helper.convertToSystemTimeZone("2009-06-01T13:50:00.0Z");

        // Create date to be put in list
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        Date dateParam2 = discoClientResponseTypeUtils3.createDateFromString("2009-06-01T14:50:00.0Z");
         Date convertedDate2 = helper.convertToSystemTimeZone("2009-06-01T14:50:00.0Z");

        // Create date list object to pass as parameter (using previously created dates)
        BodyParamDateTimeListObject bodyParamDateTimeListObject4 = new BodyParamDateTimeListObject();
        bodyParamDateTimeListObject4.setDateTimeList(Arrays.asList(convertedDate1, convertedDate2));
        BodyParamDateTimeListObject bodyParam = bodyParamDateTimeListObject4;
        // Make call to the method via client and store the result
        DateTimeListOperationResponseObject response6 = client.dateTimeListOperation(context, bodyParam);
        Date localTime = response6.getResponseList().get(0);
        Date localTime2 = response6.getResponseList().get(1);
        // Validate the response is as expected
        assertEquals(discoClientResponseTypeUtils2.formatDateToString(localTime),discoClientResponseTypeUtils2.formatDateToString(localTime));
        assertEquals(discoClientResponseTypeUtils2.formatDateToString(localTime2) ,discoClientResponseTypeUtils2.formatDateToString(localTime2));
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
