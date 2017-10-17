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

// Originally from UpdatedComponentTests/StandardTesting/SOAP/SOAP_RequestTypes_DateTimeSet_24Hour.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.soap;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when a SOAP request is received, Disco correctly throws an error when 24:00:00 is entered as a time for one of the dates in a dateTimeSet parameter (should be 00:00 the next day)
 */
public class SOAPRequestTypesDateTimeSet24HourTest {
    @Test
    public void doTest() throws Exception {
        // Create the SOAP request as an XML Document (with a dateTimeSet parameter containing a date with a time set to 24:00:00)
        XMLHelpers xMLHelpers1 = new XMLHelpers();
        Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<DateTimeSetOperationRequest><message><dateTimeSet><Date>2009-06-02T24:00:00.000Z</Date><Date>2009-06-03T24:00:00.000Z</Date></dateTimeSet></message></DateTimeSetOperationRequest>");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager2.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager2;

        hbean.setServiceName("Baseline");

        hbean.setVersion("v2");
        // Set the created SOAP request as the PostObject
        hbean.setPostObjectForRequestType(createAsDocument1, "SOAP");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());
        // Make the SOAP call to the operation
        hinstance.makeSoapDiscoHTTPCalls(hbean);
        // Create the expected response object as an XML document (fault)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument9 = xMLHelpers4.getXMLObjectFromString("<soapenv:Fault><faultcode>soapenv:Client</faultcode><faultstring>DSC-0044</faultstring><detail/></soapenv:Fault>");

        // Check the response is as expected
        HttpResponseBean response5 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.SOAP);
        AssertionUtils.multiAssertEquals(createAsDocument9, response5.getResponseObject());

        // generalHelpers.pauseTest(3000L);
        // Check the log entries are as expected

        DiscoManager discoManager8 = DiscoManager.getInstance();
        discoManager8.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp7, new AccessLogRequirement("87.248.113.14", "/BaselineService/v2", "BadRequest") );
    }

}
