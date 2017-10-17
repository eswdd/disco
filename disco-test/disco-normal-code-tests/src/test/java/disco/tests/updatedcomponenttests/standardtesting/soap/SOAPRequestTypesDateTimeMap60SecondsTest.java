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

// Originally from UpdatedComponentTests/StandardTesting/SOAP/SOAP_RequestTypes_DateTimeMap_60Seconds.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.soap;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when a SOAP request is received, Disco correctly returns a fault when a dateTimeMap parameter contains a date set to 00:00:60 (should be 00:01:00)
 */
public class SOAPRequestTypesDateTimeMap60SecondsTest {
    @Test
    public void doTest() throws Exception {
        // Create the SOAP request as an XML Document (with a dateTimeMap parameter containing a time set to 00:00:60)
        XMLHelpers xMLHelpers1 = new XMLHelpers();
        Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<DateTimeMapOperationRequest><message><dateTimeMap><entry key=\"date1\"><Date>2009-12-01T00:00:60Z</Date></entry><entry key=\"date2\"><Date>2009-12-01T00:00:60Z</Date></entry></dateTimeMap></message></DateTimeMapOperationRequest>");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager2.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager2;

        hbean.setServiceName("Baseline");

        hbean.setVersion("v2");
        // Set the created SOAP request as the PostObject
        hbean.setPostObjectForRequestType(createAsDocument1, "SOAP");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
        // Make the SOAP call to the operation
        hinstance.makeSoapDiscoHTTPCalls(hbean);
        // Create the expected response object as an XML document (fault)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument11 = xMLHelpers4.getXMLObjectFromString("<soapenv:Fault><faultcode>soapenv:Client</faultcode><faultstring>DSC-0044</faultstring><detail/></soapenv:Fault>");

        // Check the response is as expected
        HttpResponseBean response5 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.SOAP);
        AssertionUtils.multiAssertEquals(createAsDocument11, response5.getResponseObject());

        // generalHelpers.pauseTest(3000L);
        // Check the log entries are as expected

    }

}
