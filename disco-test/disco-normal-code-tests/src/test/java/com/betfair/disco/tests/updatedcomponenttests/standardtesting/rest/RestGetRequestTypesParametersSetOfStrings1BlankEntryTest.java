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

// Originally from UpdatedComponentTests/StandardTesting/REST/Rest_Get_RequestTypes_Parameters_SetOfStrings_1BlankEntry.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when the 4 supported Rest XML/JSON Gets are performed, Disco can handle a blank Set of Strings being passed in the Header and Query parameters
 */
public class RestGetRequestTypesParametersSetOfStrings1BlankEntryTest {
    @Test
    public void doTest() throws Exception {
        // Create the HttpCallBean
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean httpCallBeanBaseline = discoManager1.getNewHttpCallBean();
        DiscoManager discoManagerBaseline = discoManager1;
        // Get the disco logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "discoBaseline");
        
        httpCallBeanBaseline.setVersion("v2");
        
        httpCallBeanBaseline.setOperationName("stringSetOperation");
        // Set the parameters to blank sets of strings
        Map map2 = new HashMap();
        map2.put("HeaderParam","");
        httpCallBeanBaseline.setHeaderParams(map2);
        
        Map map3 = new HashMap();
        map3.put("queryParam","");
        httpCallBeanBaseline.setQueryParams(map3);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManagerBaseline.makeRestDiscoHTTPCalls(httpCallBeanBaseline);
        // Create the expected response as an XML document
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document expectedResponseXML = xMLHelpers5.getXMLObjectFromString("<StringSetOperationResponse><NonMandatoryParamsOperationResponseObject><headerParameter/><queryParameter/></NonMandatoryParamsOperationResponseObject></StringSetOperationResponse>");
        // Create the expected response as a JSON object
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject expectedResponseJSON = jSONHelpers6.createAsJSONObject(new JSONObject("{queryParameter:\"\",headerParameter:\"\"}"));
        // Check the 4 responses are as expected
        HttpResponseBean response7 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(expectedResponseXML, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        
        HttpResponseBean response8 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(expectedResponseJSON, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response8.getHttpStatusText());
        
        HttpResponseBean response9 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(expectedResponseJSON, response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response9.getHttpStatusText());
        
        HttpResponseBean response10 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(expectedResponseXML, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response10.getHttpStatusText());
        // Check the log entries are as expected
        
        discoManagerBaseline.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "stringSetOperation"),new RequestLogRequirement("2.8", "stringSetOperation"),new RequestLogRequirement("2.8", "stringSetOperation"),new RequestLogRequirement("2.8", "stringSetOperation") );
    }

}
