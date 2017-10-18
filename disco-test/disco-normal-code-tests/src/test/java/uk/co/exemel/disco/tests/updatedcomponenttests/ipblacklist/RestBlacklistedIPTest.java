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

// Originally from UpdatedComponentTests/IPBlacklist/REST/Rest_BlacklistedIP.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.ipblacklist;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


/**
 * Test that the Disco service forbids the access for the X-Forwarded-For  in the blacklist (REST)
 */
public class RestBlacklistedIPTest {
    String blackListedIP = "192.168.0.1";

    @Test
    public void doTest_Single_IP() throws Exception {
        String testIPAddress = blackListedIP;

        doTest_ExpectedResponse(testIPAddress, false);
    }

    @Test
    public void doTest_FirstItem_IPList() throws Exception {
        String testIPAddress = blackListedIP + ";1.2.3.4;5.6.7.8";

        doTest_ExpectedResponse(testIPAddress, false);
    }

    @Test
    public void doTest_MiddleItem_IPList() throws Exception {
        String testIPAddress = "1.2.3.4;" + blackListedIP + ";5.6.7.8";

        doTest_ExpectedResponse(testIPAddress, true);
    }

    @Test
    public void doTest_LastItem_IPList() throws Exception {
        String testIPAddress = "1.2.3.4;5.6.7.8;9.0.1.2;" + blackListedIP;

        doTest_ExpectedResponse(testIPAddress, true);
    }


    private void doTest_ExpectedResponse(String testIPAddress, boolean ok) throws Exception
    {
        // Get an HTTPCallBean
        DiscoManager discoManager = DiscoManager.getInstance();
        HttpCallBean HTTPCallBean = discoManager.getNewHttpCallBean(testIPAddress.replace(";",","));
        // Set Disco Fault Controller attributes
        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set operation  name
        HTTPCallBean.setOperationName("stringSimpleTypeEcho", "stringEcho");
        // Set service name to call
        HTTPCallBean.setServiceName("baseline", "discoBaseline");
        // Set service version to call
        HTTPCallBean.setVersion("v2");
        // Set Query parameter
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("msg","foo");
        HTTPCallBean.setQueryParams(map2);
        // Get current time

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make Rest calls (makes 4 calls with different content/accept combinations of XML and JSON)
        discoManager.makeRestDiscoHTTPCalls(HTTPCallBean);
        // Create a REST response structure as a Document object
        XMLHelpers xMLHelpers4 = new XMLHelpers();

        int expectedHttpResponseCode = ok ? 200 : 403;
        String expectedHttpResponseText = ok ? "OK" : "Forbidden";
        String expectedResponseText = ok ? "Ok" : "Forbidden";

        // Get the 4 results from the Rest calls and compare to the expected XML and JSON responses
        HttpResponseBean response5 = HTTPCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        HttpResponseBean response6 = HTTPCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        HttpResponseBean response7 = HTTPCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        HttpResponseBean response8 = HTTPCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        if (!ok) {
            Document responseDocument = xMLHelpers4.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0015</faultstring><detail/></fault>");
            // Convert the response document into Rest (XML and JSON) representations
            Map<DiscoMessageProtocolRequestTypeEnum, Object> convertedResponses = discoManager.convertResponseToRestTypes(responseDocument, HTTPCallBean);
            AssertionUtils.multiAssertEquals(convertedResponses.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response5.getResponseObject());
            AssertionUtils.multiAssertEquals(convertedResponses.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response6.getResponseObject());
            AssertionUtils.multiAssertEquals(convertedResponses.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
            AssertionUtils.multiAssertEquals(convertedResponses.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response8.getResponseObject());
        }

        AssertionUtils.multiAssertEquals(expectedHttpResponseCode, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals(expectedHttpResponseText, response5.getHttpStatusText());

        AssertionUtils.multiAssertEquals(expectedHttpResponseCode, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals(expectedHttpResponseText, response6.getHttpStatusText());

        AssertionUtils.multiAssertEquals(expectedHttpResponseCode, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals(expectedHttpResponseText, response7.getHttpStatusText());

        AssertionUtils.multiAssertEquals(expectedHttpResponseCode, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals(expectedHttpResponseText, response8.getHttpStatusText());

        discoManager.verifyAccessLogEntriesAfterDate(timeStamp,
                new AccessLogRequirement(testIPAddress, "/discoBaseline/v2/stringEcho", expectedResponseText),
                new AccessLogRequirement(testIPAddress, "/discoBaseline/v2/stringEcho", expectedResponseText),
                new AccessLogRequirement(testIPAddress, "/discoBaseline/v2/stringEcho", expectedResponseText),
                new AccessLogRequirement(testIPAddress, "/discoBaseline/v2/stringEcho", expectedResponseText));
    }
}
