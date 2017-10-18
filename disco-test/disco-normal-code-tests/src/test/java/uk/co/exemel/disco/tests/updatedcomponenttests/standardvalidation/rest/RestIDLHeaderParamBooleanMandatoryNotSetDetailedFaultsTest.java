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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_HeaderParam_Boolean_Mandatory_NotSet_DetailedFaults.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco returns the correct Detailed Fault Message when DefaultFaults are enabled and a request is missing a mandatory Bool header parameter
 */
public class RestIDLHeaderParamBooleanMandatoryNotSetDetailedFaultsTest {
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
        HttpCallBean hbean = discoManager2.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager2;
        // Set DefaultFaults to true so whole fault message is returned
        hinstance.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "true");
        
        hbean.setOperationName("boolOperation");
        
        hbean.setServiceName("baseline", "discoBaseline");
        
        hbean.setVersion("v2");
        // Set the parameters but don't set the mandatory bool header parameter
        Map map3 = new HashMap();
        map3.put("queryParam","true");
        hbean.setQueryParams(map3);
        
        hbean.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><bodyParameter>false</bodyParameter></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp10 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        hinstance.makeRestDiscoHTTPCalls(hbean);
        // Create the expected response as an XML document (Detailed Fault)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document xmlResponse = xMLHelpers5.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0018</faultstring><detail><trace/><message>Mandatory attributes not defined for parameter 'headerParam'</message></detail></fault>");
        // Create the expected response as a JSON object (Detailed Fault)
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject jsonResponse = jSONHelpers6.createAsJSONObject(new JSONObject("{\"detail\":{\"message\":\"Mandatory attributes not defined for parameter 'headerParam'\",\"trace\":\"\"},\"faultcode\":\"Client\",\"faultstring\":\"DSC-0018\"}}"));
        // Check the 4 responses are as expected (Bad Request)
        HttpResponseBean response7 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(xmlResponse, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());
        
        HttpResponseBean response8 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(jsonResponse, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response8.getHttpStatusText());
        
        HttpResponseBean response9 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(jsonResponse, response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response9.getHttpStatusText());
        
        HttpResponseBean response10 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(xmlResponse, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response10.getHttpStatusText());
        // Check the log entries are as expected
        
        DiscoManager discoManager12 = DiscoManager.getInstance();
        discoManager12.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp10, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest") );
        // Set DefaultFaults back to false for other tests
        hinstance.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
    }

}
