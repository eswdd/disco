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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_Post_RequestTypes_DateTime_60Seconds.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

/**
 * Ensure that Disco returns the correct fault, when a REST request has a body parameter that contains a date with the seconds set to 60 (should be rolled to the next minute)
 */
public class RestPostRequestTypesDateTime60SecondsTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        try {
            discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

            getNewHttpCallBean1.setOperationName("dateTimeOperation");

            getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

            getNewHttpCallBean1.setVersion("v2");
            // Set the body parameter to a date time object with seconds incorrectly set to 60
            getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<bodyParamDateTimeObject><dateTimeParameter>2009-12-01T00:00:60.000Z</dateTimeParameter></bodyParamDateTimeObject>".getBytes())));
            // Get current time for getting log entries later

            Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
            // Make the REST JSON call to the operation requesting an XML response
            discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.XML);
            // Make the REST JSON call to the operation requesting a JSON response
            discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
            // Create the expected response as an XML document (Fault)
            XMLHelpers xMLHelpers3 = new XMLHelpers();
            Document expectedXML = xMLHelpers3.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
            // Convert the expected response to a JSON object for comparison with the actual response
            JSONHelpers jSONHelpers4 = new JSONHelpers();
            JSONObject expectedJSON = jSONHelpers4.convertXMLDocumentToJSONObjectRemoveRootElement(expectedXML);
            // Check the 2 responses are as expected (Bad Request)
            AssertionUtils.multiAssertEquals(expectedXML, getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getResponseObject());
            AssertionUtils.multiAssertEquals(400, getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getHttpStatusCode());
            AssertionUtils.multiAssertEquals("Bad Request", getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getHttpStatusText());
            AssertionUtils.multiAssertEquals(expectedJSON,getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getResponseObject());
            AssertionUtils.multiAssertEquals(400, getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getHttpStatusCode());
            AssertionUtils.multiAssertEquals("Bad Request", getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getHttpStatusText());
            // Check the log entries are as expected
            System.err.println(getTimeAsTimeStamp9);
            discoManager1.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp9
                    , new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/dateTimeOperation", "BadRequest")
                    , new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/dateTimeOperation", "BadRequest")
            );
        }
        finally {
            discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "true");
        }
    }

}
