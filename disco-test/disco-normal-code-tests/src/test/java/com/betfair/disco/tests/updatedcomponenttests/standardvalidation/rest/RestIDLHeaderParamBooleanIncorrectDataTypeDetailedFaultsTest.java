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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_HeaderParam_Boolean_IncorrectDataType_DetailedFaults.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco returns the correct Detailed Fault Message when DefaultFaults are enabled and a request passes a non Boolean object in a Boolean parameter
 */
public class RestIDLHeaderParamBooleanIncorrectDataTypeDetailedFaultsTest {
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
        // Set DefaultFaults to true so whole fault message is returned
        discoManager2.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "true");

        getNewHttpCallBean2.setOperationName("boolOperation");

        getNewHttpCallBean2.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean2.setVersion("v2");
        // Set the Boolean Header param as a non boolean
        Map map3 = new HashMap();
        map3.put("headerParam","sometext");
        getNewHttpCallBean2.setHeaderParams(map3);

        Map map4 = new HashMap();
        map4.put("queryParam","false");
        getNewHttpCallBean2.setQueryParams(map4);

        getNewHttpCallBean2.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><bodyParameter>false</bodyParameter></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp11 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManager2.makeRestDiscoHTTPCalls(getNewHttpCallBean2);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers6 = new XMLHelpers();
        Document xmlResponseToJsonRequest = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail><trace/><message>json: Unable to convert 'sometext' to java.lang.Boolean for parameter: headerParam</message></detail></fault>");
        Document xmlResponseToXmlRequest = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail><trace/><message>xml: Unable to convert 'sometext' to java.lang.Boolean for parameter: headerParam</message></detail></fault>");
        // Create the expected response as a JSON object (Detailed Fault)
        JSONHelpers jSONHelpers7 = new JSONHelpers();
        JSONObject jsonResponseToJsonRequest = jSONHelpers7.createAsJSONObject(new JSONObject("{\"detail\":{\"message\":\"json: Unable to convert 'sometext' to java.lang.Boolean for parameter: headerParam\",\"trace\":\"\"},\"faultcode\":\"Client\",\"faultstring\":\"DSC-0044\"}}"));
        JSONObject jsonResponseToXmlRequest = jSONHelpers7.createAsJSONObject(new JSONObject("{\"detail\":{\"message\":\"xml: Unable to convert 'sometext' to java.lang.Boolean for parameter: headerParam\",\"trace\":\"\"},\"faultcode\":\"Client\",\"faultstring\":\"DSC-0044\"}}"));
        // Check the 4 responses are as expected (Bad Request)
        HttpResponseBean response8 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(xmlResponseToXmlRequest, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response8.getHttpStatusText());

        HttpResponseBean response9 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(jsonResponseToJsonRequest, response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response9.getHttpStatusText());

        HttpResponseBean response10 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(jsonResponseToXmlRequest, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response10.getHttpStatusText());

        HttpResponseBean response11 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(xmlResponseToJsonRequest, response11.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response11.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response11.getHttpStatusText());
        // Check the log entries are as expected
        DiscoManager discoManager12 = DiscoManager.getInstance();
        discoManager12.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp11, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/boolOperation", "BadRequest") );
        // Set DefaultFaults back to false for other tests
        discoManager2.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
    }

}
