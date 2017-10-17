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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_PostBodyElement_Map_XML_KeyNotPassed.xls;
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

/**
 * Ensure that Disco returns the correct fault, when a REST XML request contains a map body parameter with a value that has no corresponding key passed
 */
public class RestIDLPostBodyElementMapXMLKeyNotPassedTest {
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

        getNewHttpCallBean2.setOperationName("simpleMapOperation");

        getNewHttpCallBean2.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean2.setVersion("v2");
        // Set the body parameter to a map that contains a value with no corresponding key
        getNewHttpCallBean2.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<BodyParamSimpleMapObject><simpleMap><entry key=\"AAA\"><String>Value for AAA</String></entry><entry key=\"aaa\"><String>Value for aaa</String></entry><entry><String>Value for unset key</String></entry><entry key=\"ccc\"><String>Value for ccc</String></entry></simpleMap></BodyParamSimpleMapObject>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
        // Make REST XML call to the operation requesting an XML response
        discoManager2.makeRestDiscoHTTPCall(getNewHttpCallBean2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.XML);
        // Make REST XML call to the operation requesting a JSON response
        discoManager2.makeRestDiscoHTTPCall(getNewHttpCallBean2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument12 = xMLHelpers4.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Convert expected response to a JSON object for comparison with JSON actual response
        JSONHelpers jSONHelpers5 = new JSONHelpers();
        JSONObject convertXMLDocumentToJSONObjectRemoveRootElement13 = jSONHelpers5.convertXMLDocumentToJSONObjectRemoveRootElement(createAsDocument12);
        // Check the 2 responses are as expected (Bad Request)
        HttpResponseBean response6 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(createAsDocument12, response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response6.getHttpStatusText());

        HttpResponseBean response7 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertXMLDocumentToJSONObjectRemoveRootElement13, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());

        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        DiscoManager discoManager10 = DiscoManager.getInstance();
        discoManager10.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp9, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/simpleMapOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/simpleMapOperation", "BadRequest") );
    }

}
