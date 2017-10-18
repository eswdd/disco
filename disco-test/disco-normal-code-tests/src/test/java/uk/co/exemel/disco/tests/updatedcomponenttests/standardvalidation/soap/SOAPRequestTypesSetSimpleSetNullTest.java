/*
 * Copyright 2013, The Sporting Exchange Limited
 * Copyright 2014, Simon Matić Langford
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

// Originally from UpdatedComponentTests/StandardValidation/SOAP/SOAP_RequestTypes_Set_SimpleSet_Null.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.soap;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;

/**
 * Ensure that when a SOAP request is received with a null set parameter, Disco returns the correct fault
 */
public class SOAPRequestTypesSetSimpleSetNullTest {
    @Test(dataProvider = "SchemaValidationEnabled")
    public void doTest(boolean schemaValidationEnabled) throws Exception {
        DiscoHelpers helpers = new DiscoHelpers();
        try {
            DiscoManager discoManager = DiscoManager.getInstance();
            helpers.setSOAPSchemaValidationEnabled(schemaValidationEnabled);
            // Create the SOAP request as an XML Document (with a null map parameter)
            XMLHelpers xMLHelpers1 = new XMLHelpers();
            Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<SimpleSetOperationRequest><message></message></SimpleSetOperationRequest>");
            // Set up the Http Call Bean to make the request
            DiscoManager discoManager2 = DiscoManager.getInstance();
            HttpCallBean getNewHttpCallBean2 = discoManager2.getNewHttpCallBean();
            discoManager2 = discoManager2;

            discoManager2.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

            getNewHttpCallBean2.setServiceName("Baseline");

            getNewHttpCallBean2.setVersion("v2");
            // Set the created SOAP request as the PostObject
            getNewHttpCallBean2.setPostObjectForRequestType(createAsDocument1, "SOAP");
            // Get current time for getting log entries later

            Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
            // Make the SOAP call to the operation
            discoManager2.makeSoapDiscoHTTPCalls(getNewHttpCallBean2);
            // Create the expected response object as an XML document (fault)
            XMLHelpers xMLHelpers4 = new XMLHelpers();
            Document createAsDocument10 = xMLHelpers4.getXMLObjectFromString("<soapenv:Fault><faultcode>soapenv:Client</faultcode><faultstring>DSC-0018</faultstring><detail/></soapenv:Fault>");

            // Check the response is as expected
            HttpResponseBean response5 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.SOAP);
            AssertionUtils.multiAssertEquals(createAsDocument10, response5.getResponseObject());

            // generalHelpers.pauseTest(2000L);
            // Check the log entries are as expected

            DiscoHelpers discoHelpers8 = new DiscoHelpers();
            String JavaVersion = discoHelpers8.getJavaVersion();


            DiscoManager discoManager10 = DiscoManager.getInstance();
            discoManager10.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp8, new AccessLogRequirement("87.248.113.14", "/BaselineService/v2", "BadRequest"));
        } finally {
            helpers.setSOAPSchemaValidationEnabled(true);
        }
    }

    @DataProvider(name = "SchemaValidationEnabled")
    public Object[][] versions() {
        return new Object[][]{
                {true}
                , {false}
        };
    }

}
