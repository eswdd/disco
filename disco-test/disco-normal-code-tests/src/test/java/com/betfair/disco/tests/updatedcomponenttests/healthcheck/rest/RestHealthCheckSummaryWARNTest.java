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

// Originally from UpdatedComponentTests/HealthCheck/Rest/Rest_HealthCheck_Summary_WARN.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.healthcheck.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Ensure that when a Disco container is running a service that has a component in a WARN state, the heathcheck summary operation returns OK status (is either FAIL or OK) therefore WARN = OK
 */
public class RestHealthCheckSummaryWARNTest {
    @Test
    public void v3() throws Exception {
        // Set up the Http Call Bean to make the baseline service request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean();
        discoManager1 = discoManager1;
        
        getNewHttpCallBean1.setOperationName("setHealthStatusInfo");
        
        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean1.setVersion("v2");
        // Set the component statuses to be set (With WARN status)
        getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><initialiseHealthStatusObject>true</initialiseHealthStatusObject><DBConnectionStatusDetail>OK</DBConnectionStatusDetail><cacheAccessStatusDetail>WARN</cacheAccessStatusDetail><serviceStatusDetail>OK</serviceStatusDetail></message>".getBytes())));
        // Make the REST call to the set the health statuses
        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML);
        // Set up the Http Call Bean to make the healthcheck service request
        HttpCallBean getNewHttpCallBean7 = discoManager1.getNewHttpCallBean();
        
        getNewHttpCallBean7.setOperationName("isHealthy", "summary");
        
        getNewHttpCallBean7.setServiceName("healthcheck");
        
        getNewHttpCallBean7.setVersion("v3");
        
        getNewHttpCallBean7.setNameSpaceServiceName("Health");
        
        // Make the 4 REST calls to the get the health status summary from the health service
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean7);
        // Create expected response as XML document and JSON object (OK)
        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document expectedXML = xMLHelpers3.getXMLObjectFromString("<IsHealthyResponse><HealthSummaryResponse><healthy>OK</healthy></HealthSummaryResponse></IsHealthyResponse>");
        
        JSONHelpers jSONHelpers4 = new JSONHelpers();
        JSONObject expectedJSON = jSONHelpers4.createAsJSONObject(new JSONObject("{\"healthy\":\"OK\"}"));
        // Validate the response for each REST call
        HttpResponseBean response5 = getNewHttpCallBean7.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(expectedXML, response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean7.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(expectedJSON, response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response6.getHttpStatusText());
        
        HttpResponseBean response7 = getNewHttpCallBean7.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(expectedJSON, response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        
        HttpResponseBean response8 = getNewHttpCallBean7.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(expectedXML, response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response8.getHttpStatusText());
    }

}
