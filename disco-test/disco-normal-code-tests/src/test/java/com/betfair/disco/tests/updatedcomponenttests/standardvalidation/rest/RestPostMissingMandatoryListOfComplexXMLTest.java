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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_Post_MissingMandatory_ListOfComplex_XML.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that the correct fault is returned when, a Rest(XML) Post operation is performed against Disco, passing a List of complex object in the post body, where the complex object  contained in the list has a mandatory field missing
 */
public class RestPostMissingMandatoryListOfComplexXMLTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;
        // Turn detailed faults off
        discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

        getNewHttpCallBean1.setOperationName("listOfComplexOperation");

        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean1.setVersion("v2");
        // Set the body param to a list of complex objects where one of the entries is missing mandatory fields
        getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<inputList><ComplexObject/></inputList>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());
        // Make REST XML call to the operation requesting an XML response
        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.XML);
        // Make REST XML call to the operation requesting a JSON response
        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document createAsDocument12 = xMLHelpers3.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0018</faultstring><detail/></fault>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes13 = discoManager1.convertResponseToRestTypes(createAsDocument12, getNewHttpCallBean1);
        // Check the 2 responses are as expected
        HttpResponseBean response4 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes13.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response4.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response4.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response4.getHttpStatusText());

        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes13.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response5.getHttpStatusText());

        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        discoManager1.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp7, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/listOfComplexOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/listOfComplexOperation", "BadRequest") );
    }

}
