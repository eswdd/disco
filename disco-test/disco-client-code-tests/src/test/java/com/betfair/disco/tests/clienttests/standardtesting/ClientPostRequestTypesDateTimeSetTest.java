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

// Originally from ClientTests/Transport/StandardTesting/Client_Rescript_Post_RequestTypes_DateTimeSet.xls;
package uk.co.exemel.disco.tests.clienttests.standardtesting;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.to.BodyParamDateTimeSetObject;
import com.betfair.baseline.v2.to.DateTimeSetOperationResponseObject;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure that when a dateTimeSet is passed in a body parameter to disco via a disco client the request is sent and the response is handled correctly
 */
public class ClientPostRequestTypesDateTimeSetTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Set up the client to use rescript transport
        DiscoClientWrapper discoClientWrapper1 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper1;
        BaselineSyncClient client = discoClientWrapper1.getClient();
        ExecutionContext context = discoClientWrapper1.getCtx();
        // Create date to be put in set
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        Date dateParam1 = discoClientResponseTypeUtils2.createDateFromString("2009-06-01T13:50:00.0Z");
        // Create date to be put in set
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        Date dateParam2 = discoClientResponseTypeUtils3.createDateFromString("2009-06-01T14:50:00.0Z");
        // Create set of previously created dates
        DiscoHelpers helper = new DiscoHelpers();
        Date convertedDate1 = helper.convertToSystemTimeZone("2009-06-01T13:50:00.0Z");
        Date convertedDate2 = helper.convertToSystemTimeZone("2009-06-01T14:50:00.0Z");
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils4 = new DiscoClientResponseTypeUtils();
        Set<Date> dateSet = discoClientResponseTypeUtils4.createSetOfDates(Arrays.asList(convertedDate1, convertedDate2));
        // Create date set object to pass as parameter (using previously created set of dates)
        BodyParamDateTimeSetObject bodyParamDateTimeSetObject5 = new BodyParamDateTimeSetObject();
        bodyParamDateTimeSetObject5.setDateTimeSet(dateSet);
        BodyParamDateTimeSetObject bodyParam = bodyParamDateTimeSetObject5;
        // Make call to the method via client and store the result
        DateTimeSetOperationResponseObject response6 = client.dateTimeSetOperation(context, bodyParam);
        Set returnSet = response6.getResponseSet();
        // Validate the response is as expected
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils7 = new DiscoClientResponseTypeUtils();
        String dates = discoClientResponseTypeUtils7.formatSetOfDatesToString(returnSet);

        assertEquals(dates, dates);
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
