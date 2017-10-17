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

// Originally from UpdatedComponentTests/StandardTesting/REST/Rest_Post_HeaderParam_NonMandatory_NotSet.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rest;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a Rest (XML/JSON) Post operation is performed against Disco, non-mandatory Header Params do not have to be set in the request or the response.
 */
public class RestPostHeaderParamNonMandatoryNotSetTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;
        
        getNewHttpCallBean1.setOperationName("nonMandatoryParamsOperation");
        
        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean1.setVersion("v2");
        // Set the request parameters (not setting the non mandatory header parameter)
        Map map2 = new HashMap();
        map2.put("queryParam","queryParamString");
        getNewHttpCallBean1.setQueryParams(map2);
        
        getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><bodyParameter1>postBodyParamString1</bodyParameter1><bodyParameter2>postBodyParamString2</bodyParameter2></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        // Create the expected response as an XML document (not expecting the not set unmandatory header parameter)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers4.getXMLObjectFromString("<NonMandatoryParamsOperationResponseObject><bodyParameter1>postBodyParamString1</bodyParameter1><bodyParameter2>postBodyParamString2</bodyParameter2><queryParameter>queryParamString</queryParameter></NonMandatoryParamsOperationResponseObject>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes11 = discoManager1.convertResponseToRestTypes(createAsDocument10, getNewHttpCallBean1);
        // Check the 4 responses are as expected
        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response6.getHttpStatusText());
        
        HttpResponseBean response7 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response7.getHttpStatusText());
        
        HttpResponseBean response8 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes11.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response8.getHttpStatusText());
        
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp8, new RequestLogRequirement("2.8", "nonMandatoryParamsOperation"),new RequestLogRequirement("2.8", "nonMandatoryParamsOperation"),new RequestLogRequirement("2.8", "nonMandatoryParamsOperation"),new RequestLogRequirement("2.8", "nonMandatoryParamsOperation") );
    }

}
