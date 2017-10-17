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

// Originally from UpdatedComponentTests/VoidTests/REST/REST_Void_MissingMandatory.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.voidtests.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Test a REST call to an operation with a void reponse type fails correctly when missing a mandatory parameter
 */
public class RESTVoidMissingMandatoryTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults","false");
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;
        
        getNewHttpCallBean1.setOperationName("voidResponseOperation");
        
        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean1.setVersion("v2");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp6 = new Timestamp(System.currentTimeMillis());
        // Make the REST calls to the operation
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        // Create the expected response object as an XML document (Fault)
        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document createAsDocument8 = xMLHelpers3.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0018</faultstring><detail/></fault>");
        // Convert the expected response to Rest types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes9 = discoManager1.convertResponseToRestTypes(createAsDocument8, getNewHttpCallBean1);
        // Check the responses are as expected (fault)
        HttpResponseBean response4 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes9.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response4.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response4.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response4.getHttpStatusText());
        
        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes9.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes9.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response6.getHttpStatusText());
        
        HttpResponseBean response7 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes9.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());
        // Check the log entries are as expected
        
        DiscoManager discoManager9 = DiscoManager.getInstance();
        discoManager9.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp6, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/voidResponseOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/voidResponseOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/voidResponseOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/voidResponseOperation", "BadRequest") );
    }

}
