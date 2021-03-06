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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_PostBodyElement_List_null_JSON.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco returns the correct fault, when a REST JSON request contains a null list body parameter
 */
public class RestIDLPostBodyElementListnullJSONTest {
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
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = discoManager2.getNewHttpCallBean("87.248.113.14");
        discoManager2 = discoManager2;
        
        discoManager2.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        
        getNewHttpCallBean2.setOperationName("testLargePostQA");
        
        getNewHttpCallBean2.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean2.setVersion("v2");
        // Set the JSON body parameter, including a null list object
        Map map3 = new HashMap();
        map3.put("RESTJSON","{\"largeRequest\":{\"oddOrEven\":\"ODD\",\"objects\":null,\"size\":1}}");
        getNewHttpCallBean2.setPostQueryObjects(map3);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
        // Make REST JSON call to the operation requesting an XML response
        discoManager2.makeRestDiscoHTTPCall(getNewHttpCallBean2, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.XML);
        // Make REST JSON call to the operation requesting a JSON response
        discoManager2.makeRestDiscoHTTPCall(getNewHttpCallBean2, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document createAsDocument12 = xMLHelpers5.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0018</faultstring><detail/></fault>");
        // Convert expected response to a JSON object for comparison with JSON actual response
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject convertXMLDocumentToJSONObjectRemoveRootElement13 = jSONHelpers6.convertXMLDocumentToJSONObjectRemoveRootElement(createAsDocument12);
        // Check the 2 responses are as expected (Bad Request)
        HttpResponseBean response7 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(createAsDocument12, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());
        
        HttpResponseBean response8 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertXMLDocumentToJSONObjectRemoveRootElement13, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response8.getHttpStatusText());
        
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        DiscoManager discoManager11 = DiscoManager.getInstance();
        discoManager11.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp9, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/testLargePostQA", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/testLargePostQA", "BadRequest") );
    }

}
