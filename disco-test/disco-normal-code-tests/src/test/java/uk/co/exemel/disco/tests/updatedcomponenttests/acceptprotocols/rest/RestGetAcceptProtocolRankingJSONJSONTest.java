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

// Originally from UpdatedComponentTests/AcceptProtocols/Rest/Rest_Get_AcceptProtocolRankingJSONJSON.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.acceptprotocols.rest;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a Rest JSON Get is performed on Disco, with JSON the highest ranked response protocol, the response is in JSON
 */
public class RestGetAcceptProtocolRankingJSONJSONTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean();
        discoManager1 = discoManager1;

        getNewHttpCallBean1.setOperationName("testSimpleGet", "simple");

        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean1.setVersion("v2");

        Map map2 = new HashMap();
        map2.put("message","foo");
        getNewHttpCallBean1.setQueryParams(map2);
        // Set the response protocols (with json ranked highest)
        Map map3 = new HashMap();
        map3.put("application/text","q=70");
        map3.put("application/xml","q=10");
        map3.put("application/json","q=20");
        getNewHttpCallBean1.setAcceptProtocols(map3);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the JSON call to the operation
        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, DiscoMessageProtocolRequestTypeEnum.RESTJSON);
        // Create the expected response as an XML document
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers5.getXMLObjectFromString("<SimpleResponse><message>foo</message></SimpleResponse>");
        // Convert the expected response to JSON (the response type expected to be used)
        JSONHelpers jSONHelpers6 = new JSONHelpers();
        JSONObject convertXMLDocumentToJSONObjectRemoveRootElement11 = jSONHelpers6.convertXMLDocumentToJSONObjectRemoveRootElement(createAsDocument10);
        // Check the response is as expected (and in JSON)
        HttpResponseBean getResponseObjectsByEnum12 = getNewHttpCallBean1.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.REST);
        AssertionUtils.multiAssertEquals(convertXMLDocumentToJSONObjectRemoveRootElement11, getResponseObjectsByEnum12.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, getResponseObjectsByEnum12.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", getResponseObjectsByEnum12.getHttpStatusText());
        // Check the response header is as expected (JSON)
        Map<String, String> map8 = getResponseObjectsByEnum12.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("application/json", map8.get("Content-Type"));
        // Check the log entries are as expected

        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp8, new RequestLogRequirement("2.8", "testSimpleGet") );

        discoManager1.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp8, new AccessLogRequirement(null, null, "Ok") );
    }

}
