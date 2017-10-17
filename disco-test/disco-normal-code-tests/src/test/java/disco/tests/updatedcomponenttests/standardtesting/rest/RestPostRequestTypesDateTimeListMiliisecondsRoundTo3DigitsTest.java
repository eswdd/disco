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

// Originally from UpdatedComponentTests/StandardTesting/REST/Rest_Post_RequestTypes_DateTimeList_Miliiseconds_RoundTo3Digits.xls;
package com.betfair.disco.tests.updatedcomponenttests.standardtesting.rest;

import com.betfair.testing.utils.disco.misc.TimingHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that Disco can handle the dateTimeList data type in the post body containing dates with greater than 3 digits millisecond accuracy
 */
public class RestPostRequestTypesDateTimeListMiliisecondsRoundTo3DigitsTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager1;
        
        hbean.setOperationName("dateTimeListOperation");
        
        hbean.setServiceName("baseline", "discoBaseline");
        
        hbean.setVersion("v2");
        // Create a date time object expected to be in the response object

        String date1 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 1, (int) 1, (int) 13, (int) 50, (int) 0, (int) 434);
        // Create a date time object expected to be in the response object

        String date2 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 1, (int) 2, (int) 13, (int) 50, (int) 0, (int) 434);
        // Set the post body to contain a date time list object containing dates with > 3 digits millisecond accuracy
        hbean.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><dateTimeList><Date>2009-01-01T13:50:00.4343Z</Date><Date>2009-01-02T13:50:00.4343Z</Date></dateTimeList></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        hinstance.makeRestDiscoHTTPCalls(hbean);
        // Create the expected response as an XML document (using the date object created earlier)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document expectedResponseXML = xMLHelpers5.createAsDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<DateTimeListOperationResponseObject><responseList><Date>"+date1+"</Date><Date>"+date2+"</Date></responseList></DateTimeListOperationResponseObject>").getBytes())));
        // Convert expected  response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> expectedResponseREST = hinstance.convertResponseToRestTypes(expectedResponseXML, hbean);
        // Check the 4 responses are as expected
        HttpResponseBean response6 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(expectedResponseREST.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response6.getHttpStatusText());
        
        HttpResponseBean response7 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(expectedResponseREST.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        
        HttpResponseBean response8 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(expectedResponseREST.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response8.getHttpStatusText());
        
        HttpResponseBean response9 = hbean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(expectedResponseREST.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response9.getHttpStatusText());
        
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        hinstance.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp9, new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation") );
    }

}
