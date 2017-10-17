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

// Originally from ClientTests/Transport/ImportSheets/CreateSomeComplexObjectsForComplexDelegate.xls;
package uk.co.exemel.disco.tests.clienttests.importsheets;

import com.betfair.baseline.v2.to.SomeComplexObject;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientResponseTypeUtils;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Import Sheet to construct the expected returned some complex objects from ComplexMapOperation delegate
 */
public class CreateSomeComplexObjectsForComplexDelegateTest {
    @Test(dataProvider = "TransportType")
    public void doTest(DiscoClientWrapper.TransportType tt) throws Exception {
        // Create a some complex object
        SomeComplexObject someComplexObject1 = new SomeComplexObject();
        someComplexObject1.setStringParameter("delegate1");
        SomeComplexObject someComplex1 = someComplexObject1;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils2 = new DiscoClientResponseTypeUtils();
        Date dateParam1 = discoClientResponseTypeUtils2.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex1.setDateTimeParameter(dateParam1);
        
        someComplex1.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils3 = new DiscoClientResponseTypeUtils();
        List<String> list1 = discoClientResponseTypeUtils3.buildList("item1,item2");
        
        someComplex1.setListParameter(list1);
        // Create another some complex object
        SomeComplexObject someComplexObject4 = new SomeComplexObject();
        someComplexObject4.setStringParameter("delegate2");
        SomeComplexObject someComplex2 = someComplexObject4;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils5 = new DiscoClientResponseTypeUtils();
        Date dateParam2 = discoClientResponseTypeUtils5.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex2.setDateTimeParameter(dateParam2);
        
        someComplex2.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils6 = new DiscoClientResponseTypeUtils();
        List<String> list2 = discoClientResponseTypeUtils6.buildList("item1,item2");
        
        someComplex2.setListParameter(list2);
        // Create another some complex object
        SomeComplexObject someComplexObject7 = new SomeComplexObject();
        someComplexObject7.setStringParameter("delegate3");
        SomeComplexObject someComplex3 = someComplexObject7;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils8 = new DiscoClientResponseTypeUtils();
        Date dateParam3 = discoClientResponseTypeUtils8.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex3.setDateTimeParameter(dateParam3);
        
        someComplex3.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils9 = new DiscoClientResponseTypeUtils();
        List<String> list3 = discoClientResponseTypeUtils9.buildList("item1,item2");
        
        someComplex3.setListParameter(list3);
        // Put created objects in a map
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils10 = new DiscoClientResponseTypeUtils();
        Map<String, SomeComplexObject> expectedReturnMap = discoClientResponseTypeUtils10.buildComplexDelegateReturnMap(Arrays.asList(someComplex1, someComplex2, someComplex3));
        // Create a some complex object
        SomeComplexObject someComplexObject11 = new SomeComplexObject();
        someComplexObject11.setStringParameter("delegate1");
        someComplex1 = someComplexObject11;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils12 = new DiscoClientResponseTypeUtils();
        dateParam1 = discoClientResponseTypeUtils12.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex1.setDateTimeParameter(dateParam1);
        
        someComplex1.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils13 = new DiscoClientResponseTypeUtils();
        list1 = discoClientResponseTypeUtils13.buildList("item1,item2");
        
        someComplex1.setListParameter(list1);
        // Create another some complex object
        SomeComplexObject someComplexObject14 = new SomeComplexObject();
        someComplexObject14.setStringParameter("delegate2");
        someComplex2 = someComplexObject14;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils15 = new DiscoClientResponseTypeUtils();
        dateParam2 = discoClientResponseTypeUtils15.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex2.setDateTimeParameter(dateParam2);
        
        someComplex2.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils16 = new DiscoClientResponseTypeUtils();
        list2 = discoClientResponseTypeUtils16.buildList("item1,item2");
        
        someComplex2.setListParameter(list2);
        // Create another some complex object
        SomeComplexObject someComplexObject17 = new SomeComplexObject();
        someComplexObject17.setStringParameter("delegate3");
        someComplex3 = someComplexObject17;
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils18 = new DiscoClientResponseTypeUtils();
        dateParam3 = discoClientResponseTypeUtils18.createDateFromString("1970-01-01T00:01:52.233+0100");
        
        someComplex3.setDateTimeParameter(dateParam3);
        
        someComplex3.setEnumParameter(com.betfair.baseline.v2.enumerations.SomeComplexObjectEnumParameterEnum.BAR);
        
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils19 = new DiscoClientResponseTypeUtils();
        list3 = discoClientResponseTypeUtils19.buildList("item1,item2");
        
        someComplex3.setListParameter(list3);
        // Put created objects in a map
        DiscoClientResponseTypeUtils discoClientResponseTypeUtils20 = new DiscoClientResponseTypeUtils();
        expectedReturnMap = discoClientResponseTypeUtils20.buildComplexDelegateReturnMap(Arrays.asList(someComplex1, someComplex2, someComplex3));
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

}
