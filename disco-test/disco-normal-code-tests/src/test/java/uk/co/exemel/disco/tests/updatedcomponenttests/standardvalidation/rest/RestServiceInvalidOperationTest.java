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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_Service_InvalidOperation.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

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
import java.util.Map;

/**
 * Ensure that Disco returns the correct 404 fault (DSC-0021), when a REST request is made to an invalid operation
 */
public class RestServiceInvalidOperationTest {
    @Test
    public void doTest() throws Exception {
        // Create the HttpCallBean
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean httpCallBeanBaseline = discoManager1.getNewHttpCallBean();
        DiscoManager discoManagerBaseline = discoManager1;
        // Get the disco logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "discoBaseline");
        
        httpCallBeanBaseline.setVersion("v2");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = discoManager2.getNewHttpCallBean("87.248.113.14");
        discoManager2 = discoManager2;
        
        discoManager2.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set the request to call an invalid operation (one that is not defined on the baseline service)
        getNewHttpCallBean2.setOperationName("invalidoperation");
        
        getNewHttpCallBean2.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean2.setVersion("v2");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the invalid operation
        discoManager2.makeRestDiscoHTTPCalls(getNewHttpCallBean2);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers4.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0021</faultstring><detail/></fault>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes11 = discoManager2.convertResponseToRestTypes(createAsDocument10, getNewHttpCallBean2);
        // Check the 4 responses are as expected (Not Found)
        HttpResponseBean response5 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 404, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Not Found", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 404, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Not Found", response6.getHttpStatusText());
        
        HttpResponseBean response7 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 404, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Not Found", response7.getHttpStatusText());
        
        HttpResponseBean response8 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 404, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Not Found", response8.getHttpStatusText());
        // Check the log entries are as expected
        
        DiscoManager discoManager10 = DiscoManager.getInstance();
        discoManager10.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp8, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/invalidoperation", "NotFound"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/invalidoperation", "NotFound"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/invalidoperation", "NotFound"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/invalidoperation", "NotFound") );
    }

}
