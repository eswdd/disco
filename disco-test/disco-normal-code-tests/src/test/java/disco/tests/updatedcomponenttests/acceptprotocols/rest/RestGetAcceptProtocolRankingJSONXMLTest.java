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

// Originally from UpdatedComponentTests/AcceptProtocols/Rest/Rest_Get_AcceptProtocolRankingJSONXML.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.acceptprotocols.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a Rest JSON Get is performed on Disco, with XML the highest ranked response protocol, the response is XML
 */
public class RestGetAcceptProtocolRankingJSONXMLTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;

        getNewHttpCallBean1.setOperationName("testSimpleGet", "simple");

        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean1.setVersion("v2");

        Map map2 = new HashMap();
        map2.put("message","foo");
        getNewHttpCallBean1.setQueryParams(map2);
        // Set the response protocols (with xml ranked highest)
        Map map3 = new HashMap();
        map3.put("application/text","q=70");
        map3.put("application/xml","q=20");
        map3.put("application/json","q=10");
        getNewHttpCallBean1.setAcceptProtocols(map3);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the JSON call to the operation
        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON);
        // Create the expected response as an XML document
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers5.getXMLObjectFromString("<TestSimpleGetResponse><SimpleResponse><message>foo</message></SimpleResponse></TestSimpleGetResponse>");
        // Check the response is as expected (and in XML)
        HttpResponseBean getResponseObjectsByEnum11 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.REST);
        AssertionUtils.multiAssertEquals(createAsDocument10, getResponseObjectsByEnum11.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, getResponseObjectsByEnum11.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", getResponseObjectsByEnum11.getHttpStatusText());
        // Check the response header is as expected (XML)
        Map<String, String> map7 = getResponseObjectsByEnum11.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("application/xml", map7.get("Content-Type"));
        // Check the log entries are as expected

        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp8, new RequestLogRequirement("2.8", "testSimpleGet") );

        discoManager1.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp8, new AccessLogRequirement(null, null, "Ok") );
    }

}
