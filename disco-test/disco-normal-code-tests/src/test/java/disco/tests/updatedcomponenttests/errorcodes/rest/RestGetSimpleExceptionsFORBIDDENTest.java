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

// Originally from UpdatedComponentTests/ErrorCodes/Rest/Rest_Get_SimpleExceptions_FORBIDDEN.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.errorcodes.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco can correctly throw a simple exception with error code: FORBIDDEN
 */
public class RestGetSimpleExceptionsFORBIDDENTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;
        
        discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Get the disco log attribute for getting log entries later
        
        getNewHttpCallBean1.setOperationName("testExceptionQA");
        
        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean1.setVersion("v2");
        // Set the query parameter to tell the operation which exception to throw (FORBIDDEN)
        Map map2 = new HashMap();
        map2.put("message","FORBIDDEN");
        getNewHttpCallBean1.setQueryParams(map2);
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        // Create the expected response as an XML document (the required exception)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers4.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>SEX-0004</faultstring><detail><exceptionname>SimpleException</exceptionname><SimpleException><errorCode>FORBIDDEN</errorCode><reason>FORBIDDEN</reason></SimpleException></detail></fault>");
        // Convert the expected response to the various REST types for comparison
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes11 = discoManager1.convertResponseToRestTypes(createAsDocument10, getNewHttpCallBean1);
        // Check the 4 responses are as expected (correct error returned)
        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 401, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Unauthorized", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 401, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Unauthorized", response6.getHttpStatusText());
        
        HttpResponseBean response7 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 401, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Unauthorized", response7.getHttpStatusText());
        
        HttpResponseBean response8 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 401, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Unauthorized", response8.getHttpStatusText());
        // Check the log entries are as expected
    }

}
