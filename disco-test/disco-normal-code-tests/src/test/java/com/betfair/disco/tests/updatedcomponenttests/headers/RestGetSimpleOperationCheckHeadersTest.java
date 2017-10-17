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

// Originally from UpdatedComponentTests/Headers/Rest_Get_SimpleOperation_Check_Headers.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.headers;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a simple Rest XML/JSON Get is performed against Disco, with X_IP in the header set to blank, the operation completes successfully and the headers are set to default values
 */
public class RestGetSimpleOperationCheckHeadersTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request, leaving the X IP header blank
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("");
        discoManager1 = discoManager1;
        // Get the disco log attribute for getting log entries later

        getNewHttpCallBean1.setOperationName("testSimpleGet", "simple");

        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean1.setVersion("v2");

        Map map2 = new HashMap();
        map2.put("message","foo");
        getNewHttpCallBean1.setQueryParams(map2);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        // Create the expected response as an XML document
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument9 = xMLHelpers4.getXMLObjectFromString("<SimpleResponse><message>foo</message></SimpleResponse>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes10 = discoManager1.convertResponseToRestTypes(createAsDocument9, getNewHttpCallBean1);
        // Check the response is as expected (request: XML response:XML)
        HttpResponseBean getResponseObjectsByEnum11 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes10.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), getResponseObjectsByEnum11.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, getResponseObjectsByEnum11.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", getResponseObjectsByEnum11.getHttpStatusText());
        // Check the response headers are as expected (set to default values as no headers were set in the request)
        Map<String, String> map6 = getResponseObjectsByEnum11.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("no-cache", map6.get("Cache-Control"));
        AssertionUtils.multiAssertEquals("application/xml", map6.get("Content-Type"));
        // Check the response is as expected  (request: JSON response: JSON)
        HttpResponseBean response7 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes10.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        // Check the response headers are as expected (set to default values as no headers were set in the request)
        Map<String, String> map8 = getResponseObjectsByEnum11.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("no-cache", map8.get("Cache-Control"));
        AssertionUtils.multiAssertEquals("application/xml", map8.get("Content-Type"));
        // Check the response is as expected  (request: XML response: JSON)
        HttpResponseBean response9 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes10.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response9.getHttpStatusText());
        // Check the response headers are as expected (set to default values as no headers were set in the request)
        Map<String, String> map10 = getResponseObjectsByEnum11.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("no-cache", map10.get("Cache-Control"));
        AssertionUtils.multiAssertEquals("application/xml", map10.get("Content-Type"));
        // Check the response is as expected  (request: JSON response:XML)
        HttpResponseBean response11 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes10.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response11.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response11.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response11.getHttpStatusText());
        // Check the response headers are as expected (set to default values as no headers were set in the request)
        Map<String, String> map12 = getResponseObjectsByEnum11.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("no-cache", map12.get("Cache-Control"));
        AssertionUtils.multiAssertEquals("application/xml", map12.get("Content-Type"));

        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp7, new RequestLogRequirement("2.8", "testSimpleGet"),new RequestLogRequirement("2.8", "testSimpleGet"),new RequestLogRequirement("2.8", "testSimpleGet"),new RequestLogRequirement("2.8", "testSimpleGet") );

        discoManager1.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp7, new AccessLogRequirement(null, null, "Ok"),new AccessLogRequirement(null, null, "Ok"),new AccessLogRequirement(null, null, "Ok"),new AccessLogRequirement(null, null, "Ok") );
    }

}
