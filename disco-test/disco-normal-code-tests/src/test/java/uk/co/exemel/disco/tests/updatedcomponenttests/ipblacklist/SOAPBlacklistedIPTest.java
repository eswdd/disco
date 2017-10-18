/*
 * Copyright 2013, The Sporting Exchange Limited
 * Copyright 2014, Simon Matić Langford
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

// Originally from UpdatedComponentTests/IPBlacklist/SOAP/SOAP_BlacklistedIP.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.ipblacklist;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;

/**
 * Test that the Disco service forbids access for the blacklisted IP (SOAP)
 */
public class SOAPBlacklistedIPTest {
    @Test
    public void doTest_Single_IP() throws Exception {
        String testIPAddress = "192.168.0.1";

        doTest_ExpectedResponse(testIPAddress, false);
    }

    @Test
    public void doTest_FirstItem_IPList() throws Exception {
        String testIPAddress = "192.168.0.1;1.2.3.4;5.6.7.8";

        doTest_ExpectedResponse(testIPAddress, false);
    }

    @Test
    public void doTest_MiddleItem_IPList() throws Exception {
        String testIPAddress = "1.2.3.4;192.168.0.1;5.6.7.8";

        doTest_ExpectedResponse(testIPAddress, true);
    }

    @Test
    public void doTest_LastItem_IPList() throws Exception {
        String testIPAddress = "1.2.3.4;5.6.7.8;9.0.1.2;192.168.0.1";

        doTest_ExpectedResponse(testIPAddress, true);
    }


    private void doTest_ExpectedResponse(String testIPAddress, boolean ok) throws Exception
    {
        // Create a soap request structure as a Document object
        XMLHelpers xMLHelpers1 = new XMLHelpers();
        Document requestDocument = xMLHelpers1.getXMLObjectFromString("<StringSimpleTypeEchoRequest><msg>foo</msg></StringSimpleTypeEchoRequest>");
        // Get an HTTPCallBean
        DiscoManager discoManager = DiscoManager.getInstance();
        HttpCallBean HTTPCallBean = discoManager.getNewHttpCallBean(testIPAddress.replace(";", ","));
        // Set Disco Fault Controller attributes
        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set service name to call
        HTTPCallBean.setServiceName("Baseline");
        // Set service version to call
        HTTPCallBean.setVersion("v2");
        // Set post object and request type
        HTTPCallBean.setPostObjectForRequestType(requestDocument, "SOAP");
        // Get current time

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // Make Disco SOAP call
        discoManager.makeSoapDiscoHTTPCalls(HTTPCallBean);
        // Create a soap response structure as a Document object
        if (!ok) {
            XMLHelpers xMLHelpers4 = new XMLHelpers();
            Document responseDocument = xMLHelpers4.getXMLObjectFromString("<soapenv:Fault><faultcode>soapenv:Client</faultcode><faultstring>DSC-0015</faultstring><detail/></soapenv:Fault>");
            // Get the actual SOAP response and compare it to the expected response
            HttpResponseBean response6 = HTTPCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.SOAP);
            AssertionUtils.multiAssertEquals(responseDocument, response6.getResponseObject());
        }
        // Get access log entries after the time recorded earlier in the test
        discoManager.verifyAccessLogEntriesAfterDate(timestamp, new AccessLogRequirement(testIPAddress, "/BaselineService/v2", ok ? "Ok" : "Forbidden"));
    }
}
