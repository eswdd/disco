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

// Originally from UpdatedComponentTests/StandardTesting/REST/Rest_Post_RequestTypes_DateTimeList_ZOffset_2.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rest;

import uk.co.exemel.testing.utils.disco.misc.TimingHelpers;
import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

/**
 * Ensure that Disco can handle the dateTimeList data type in the post body containing dates specifying the UTC timezone
 */
public class RestPostRequestTypesDateTimeListZOffset2Test {
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

        String date1 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 2, (int) 1, (int) 11, (int) 50, (int) 0, (int) 435);
        // Create a date time object expected to be in the response object

        String date2 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 2, (int) 1, (int) 12, (int) 50, (int) 0, (int) 435);
        // Set the post body to contain a date time list object containing dates that specify the UTC timezone
        hbean.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><dateTimeList><Date>2009-02-01T11:50:00.435Z</Date><Date>2009-02-01T12:50:00.435Z</Date></dateTimeList></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        hinstance.makeRestDiscoHTTPCalls(hbean);
        // Create the expected response as an XML document (using the date object created earlier)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document expectedResponseXML = xMLHelpers5.createAsDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<DateTimeListOperationResponse><DateTimeListOperationResponseObject><responseList><Date>"+date1+"</Date><Date>"+date2+"</Date></responseList></DateTimeListOperationResponseObject></DateTimeListOperationResponse>").getBytes())));
        // Create the expected response as a JSON object (using the date object created earlier)
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject expectedResponseJSON = jSONHelpers6.createAsJSONObject(new JSONObject("{\"responseList\":[\""+date1+"\",\""+date2+"\"]}"));
        // Check the 4 responses are as expected
        HttpResponseBean response7 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(expectedResponseXML, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        
        HttpResponseBean response8 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(expectedResponseJSON, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response8.getHttpStatusText());
        
        HttpResponseBean response9 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(expectedResponseJSON, response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response9.getHttpStatusText());
        
        HttpResponseBean response10 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(expectedResponseXML, response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response10.getHttpStatusText());
        
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        hinstance.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp8, new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation"),new RequestLogRequirement("2.8", "dateTimeListOperation") );
    }

}
