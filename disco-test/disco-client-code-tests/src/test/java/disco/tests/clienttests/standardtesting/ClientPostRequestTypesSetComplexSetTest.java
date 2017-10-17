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

// Originally from ClientTests/Transport/StandardTesting/Client_Rescript_Post_RequestTypes_Set_ComplexSet.xls;
package uk.co.exemel.disco.tests.clienttests.standardtesting;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.to.BodyParamComplexSetObject;
import com.betfair.baseline.v2.to.ComplexSetOperationResponseObject;
import com.betfair.baseline.v2.to.SomeComplexObject;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Ensure that when a ComplexSet object is passed in parameters to disco via a disco client, the request is sent and the response is handled correctly
 */
public class ClientPostRequestTypesSetComplexSetTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Create a some complex object
        SomeComplexObject someComplexObject1 = new SomeComplexObject();
        someComplexObject1.setStringParameter("String value for aaa");
        SomeComplexObject someComplex1 = someComplexObject1;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        Date dateParam1 = discoClientResponseTypeUtils2.createDateFromString("2009-06-01T13:50:00.0Z");
        
        someComplex1.setDateTimeParameter(dateParam1);
        
        someComplex1.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        List<String> list1 = discoClientResponseTypeUtils3.buildList("aaa List Entry 1,aaa List Entry 2,aaa List Entry 3");
        
        someComplex1.setListParameter(list1);
        // Create another some complex object
        SomeComplexObject someComplexObject4 = new SomeComplexObject();
        someComplexObject4.setStringParameter("String value for bbb");
        SomeComplexObject someComplex2 = someComplexObject4;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils5 = new DiscoClientResponseTypeUtils();
        Date dateParam2 = discoClientResponseTypeUtils5.createDateFromString("2009-06-02T13:50:00.435Z");
        
        someComplex2.setDateTimeParameter(dateParam2);
        
        someComplex2.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.FOOBAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils6 = new DiscoClientResponseTypeUtils();
        List<String> list2 = discoClientResponseTypeUtils6.buildList("bbb List Entry 1,bbb List Entry 2,bbb List Entry 3");
        
        someComplex2.setListParameter(list2);
        // Create another some complex object
        SomeComplexObject someComplexObject7 = new SomeComplexObject();
        someComplexObject7.setStringParameter("String value for ccc");
        SomeComplexObject someComplex3 = someComplexObject7;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils8 = new DiscoClientResponseTypeUtils();
        Date dateParam3 = discoClientResponseTypeUtils8.createDateFromString("2009-06-03T13:50:00.435Z");
        
        someComplex3.setDateTimeParameter(dateParam3);
        
        someComplex3.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.FOO);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils9 = new DiscoClientResponseTypeUtils();
        List<String> list3 = discoClientResponseTypeUtils9.buildList("ccc List Entry 1,ccc List Entry 2,ccc List Entry 3");
        
        someComplex3.setListParameter(list3);
        // Set up the client to use rescript transport
        DiscoClientWrapper discoClientWrapper10 = DiscoClientWrapper.getInstance(tt);
        DiscoClientWrapper wrapper = discoClientWrapper10;
        BaselineSyncClient client = discoClientWrapper10.getClient();
        ExecutionContext context = discoClientWrapper10.getCtx();
        // Build set using complex objects created in the import sheet
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils11 = new DiscoClientResponseTypeUtils();
        Set<SomeComplexObject> complexSet = discoClientResponseTypeUtils11.buildComplexSet(Arrays.asList(someComplex1, someComplex2, someComplex3));
        // Create body parameter to be passed
        BodyParamComplexSetObject bodyParamComplexSetObject12 = new BodyParamComplexSetObject();
        bodyParamComplexSetObject12.setComplexSet(complexSet);
        BodyParamComplexSetObject bodyParam = bodyParamComplexSetObject12;
        // Make call to the method via client and store the response
        ComplexSetOperationResponseObject response13 = client.complexSetOperation(context, bodyParam);
        Set resultSet = response13.getResponseSet();
        // Validate the received set is as expected
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils14 = new DiscoClientResponseTypeUtils();
        boolean result = discoClientResponseTypeUtils14.compareSets(complexSet, resultSet);
        assertEquals(true, result);
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
