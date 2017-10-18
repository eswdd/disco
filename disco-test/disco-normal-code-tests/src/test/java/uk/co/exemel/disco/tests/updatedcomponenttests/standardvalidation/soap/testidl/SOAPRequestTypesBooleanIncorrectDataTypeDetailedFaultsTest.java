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

// Originally from UpdatedComponentTests/StandardValidation/SOAP/Test-IDL/SOAP_RequestTypes_Boolean_Incorrect_DataType_DetailedFaults.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.soap.testidl;

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
import java.util.Map;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Ensure that when a SOAP request is received, the correct Detailed Fault Message is returned when detailed faults are enabled and the request has a boolean parameter set to an object of the wrong type
 */
public class SOAPRequestTypesBooleanIncorrectDataTypeDetailedFaultsTest {
    @Test(dataProvider = "SchemaValidationEnabled")
    public void doTest(boolean schemaValidationEnabled) throws Exception {
        DiscoHelpers helpers = new DiscoHelpers();
        try {
            DiscoManager discoManager = DiscoManager.getInstance();
            helpers.setSOAPSchemaValidationEnabled(schemaValidationEnabled);
            // Create the SOAP request as an XML Document (with an object with a different data type passed in a boolean parameter)
            XMLHelpers xMLHelpers1 = new XMLHelpers();
            Document createAsDocument2 = xMLHelpers1.getXMLObjectFromString("<BoolOperationRequest><headerParam>true</headerParam><queryParam>foo</queryParam><message><bodyParameter>true</bodyParameter></message></BoolOperationRequest>");
            // Set up the Http Call Bean to make the request
            DiscoManager discoManager2 = DiscoManager.getInstance();
            HttpCallBean getNewHttpCallBean3 = discoManager2.getNewHttpCallBean("87.248.113.14");
            DiscoManager discoManager3 = discoManager2;
            // Enable Detailed Faults
            discoManager3.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "true");

            getNewHttpCallBean3.setServiceName("Baseline");

            getNewHttpCallBean3.setVersion("v2");
            // Set the created SOAP request as the PostObject
            getNewHttpCallBean3.setPostObjectForRequestType(createAsDocument2, "SOAP");
            // Get current time for getting log entries later

            Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
            // Make the SOAP call to the operation
            discoManager3.makeSoapDiscoHTTPCalls(getNewHttpCallBean3);
            // Get the SOAP response to the call
            HttpResponseBean actualResponse = getNewHttpCallBean3.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.SOAP);
            // Convert the returned detailed fault object to a map for comparison with expected values
            DiscoHelpers discoHelpers4 = new DiscoHelpers();
            Map<String, String> map5 = discoHelpers4.convertFaultObjectToMap(actualResponse);
            AssertionUtils.multiAssertEquals("soapenv:Client", map5.get("faultCode"));
            AssertionUtils.multiAssertEquals("DSC-0044", map5.get("faultString"));
            if (schemaValidationEnabled) {
                AssertionUtils.multiAssertEquals("soap: cvc-datatype-valid.1.2.1: 'foo' is not a valid value for 'boolean'.", map5.get("faultMessage"));
                assertTrue(map5.get("faultTrace"),map5.get("faultTrace").startsWith("org.xml.sax.SAXParseException"));
            }
            else {
                AssertionUtils.multiAssertEquals("xml: Unable to convert data in request to BOOLEAN for parameter: queryParam", map5.get("faultMessage"));
                assertTrue(map5.get("faultTrace"),map5.get("faultTrace").startsWith("java.lang.IllegalArgumentException"));
            }

            // generalHelpers.pauseTest(500L);
            // Check the log entries are as expected


            DiscoHelpers discoHelpers9 = new DiscoHelpers();
            String JavaVersion = discoHelpers9.getJavaVersion();

            DiscoManager discoManager10 = DiscoManager.getInstance();
            discoManager10.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp9, new AccessLogRequirement("87.248.113.14", "/BaselineService/v2", "BadRequest"));
            // Reset the Detailed Faults attribute for other tests
            discoManager3.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
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
