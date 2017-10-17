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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_HeaderParam_SetOfSimpleTypeEnums_1BlankEntry.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco returns the correct fault, when a request passes a blank set of simple type enum
 */
public class RestIDLHeaderParamSetOfSimpleTypeEnums1BlankEntryTest {
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
        
        discoManagerBaseline.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        
        httpCallBeanBaseline.setOperationName("simpleEnumSetOperation");
        // Set the Set of Simple Type Enum header parameter to a blank Set
        Map map2 = new HashMap();
        map2.put("HeaderParam",null);
        httpCallBeanBaseline.setHeaderParams(map2);
        
        Map map3 = new HashMap();
        map3.put("queryParam","FOOBAR,BARFOO");
        httpCallBeanBaseline.setQueryParams(map3);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManagerBaseline.makeRestDiscoHTTPCalls(httpCallBeanBaseline);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document xmlDocument = xMLHelpers5.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0018</faultstring><detail/></fault>");
        // Convert the expected response to JSON for comparison with actual responses
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject jsonResponse = jSONHelpers6.convertXMLDocumentToJSONObjectRemoveRootElement(xmlDocument);
        // Check the 4 responses are as expected (Bad Request)
        HttpResponseBean response7 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(xmlDocument, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());
        
        HttpResponseBean response8 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(jsonResponse, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response8.getHttpStatusText());
        
        HttpResponseBean response9 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(jsonResponse, response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response9.getHttpStatusText());
        
        HttpResponseBean response10 = httpCallBeanBaseline.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(xmlDocument, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response10.getHttpStatusText());
        // Check the log entries are as expected
        
    }

}
