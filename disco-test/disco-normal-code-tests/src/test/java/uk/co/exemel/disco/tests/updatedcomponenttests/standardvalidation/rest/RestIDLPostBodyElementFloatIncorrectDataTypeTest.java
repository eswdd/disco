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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_PostBodyElement_Float_IncorrectDataType.xls;
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
 * Ensure that Disco returns the correct fault, when a REST request contains a non Float as a Float body parameter
 */
public class RestIDLPostBodyElementFloatIncorrectDataTypeTest {
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

        getNewHttpCallBean2.setOperationName("floatOperation");

        getNewHttpCallBean2.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean2.setVersion("v2");

        Map map3 = new HashMap();
        map3.put("HeaderParam","5.43");
        getNewHttpCallBean2.setHeaderParams(map3);

        Map map4 = new HashMap();
        map4.put("queryParam","3455.54");
        getNewHttpCallBean2.setQueryParams(map4);
        // Set the Float body parameter as a non Float
        getNewHttpCallBean2.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><bodyParameter>dfdsfsf</bodyParameter></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp11 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManager2.makeRestDiscoHTTPCalls(getNewHttpCallBean2);
        // Create the expected response to XML requests as an XML document (XML Fault)
        XMLHelpers xMLHelpers6 = new XMLHelpers();
        Document createAsDocument13 = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Create the expected response to JSON requests as an XML document (JSON Fault)
        XMLHelpers xMLHelpers7 = new XMLHelpers();
        Document createAsDocument14 = xMLHelpers7.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Convert expected response to XML requests to JSON object
        JSONHelpers jSONHelpers8 = new JSONHelpers();
        JSONObject convertXMLDocumentToJSONObjectRemoveRootElement15 = jSONHelpers8.convertXMLDocumentToJSONObjectRemoveRootElement(createAsDocument13);
        // Convert expected response to JSON requests to JSON object
        JSONHelpers jSONHelpers9 = new JSONHelpers();
        JSONObject convertXMLDocumentToJSONObjectRemoveRootElement16 = jSONHelpers9.convertXMLDocumentToJSONObjectRemoveRootElement(createAsDocument14);
        // Check the 4 responses are as expected (Bad Request)
        HttpResponseBean response10 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(createAsDocument13, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response10.getHttpStatusText());

        HttpResponseBean response11 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertXMLDocumentToJSONObjectRemoveRootElement16, response11.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response11.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response11.getHttpStatusText());

        HttpResponseBean response12 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertXMLDocumentToJSONObjectRemoveRootElement15, response12.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response12.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response12.getHttpStatusText());

        HttpResponseBean response13 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(createAsDocument14, response13.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response13.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response13.getHttpStatusText());
        // Check the log entries are as expected

        DiscoManager discoManager15 = DiscoManager.getInstance();
        discoManager15.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp11, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/floatOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/floatOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/floatOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/floatOperation", "BadRequest") );
    }

}
