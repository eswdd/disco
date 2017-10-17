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

// Originally from UpdatedComponentTests/StandardTesting/SOAP/SOAP_RequestTypes_Map_ComplexMap.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.soap;

import com.betfair.testing.utils.disco.misc.TimingHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when a SOAP request is received, Disco can handle a ComplexMap datatype parameter
 */
public class SOAPRequestTypesMapComplexMapTest {
    @Test
    public void doTest() throws Exception {
        // Create the date objects expected to be included in the response map

        String convertUTCDateTimeToDiscoFormat1 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 6, (int) 1, (int) 13, (int) 50, (int) 0, (int) 0);
        // Create the date objects expected to be included in the response map

        String convertUTCDateTimeToDiscoFormat2 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 6, (int) 2, (int) 13, (int) 50, (int) 0, (int) 0);
        // Create the date objects expected to be included in the response map

        String convertUTCDateTimeToDiscoFormat3 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 6, (int) 3, (int) 13, (int) 50, (int) 0, (int) 0);
        // Create the SOAP request as an XML Document (with a complex map parameter)
        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument4 = xMLHelpers4.getXMLObjectFromString("<ComplexMapOperationRequest><message><complexMap><entry key=\"aaa\"><SomeComplexObject><stringParameter>String value for aaa</stringParameter><dateTimeParameter>2009-06-01T13:50:00Z</dateTimeParameter><enumParameter>BAR</enumParameter><listParameter><String>aaa List Entry 1</String><String>aaa List Entry 2</String><String>aaa List Entry 3</String></listParameter></SomeComplexObject></entry><entry key=\"ccc\"><SomeComplexObject><stringParameter>String value for ccc</stringParameter><dateTimeParameter>2009-06-03T13:50:00Z</dateTimeParameter><enumParameter>FOO</enumParameter><listParameter><String>ccc List Entry 1</String><String>ccc List Entry 2</String><String>ccc List Entry 3</String></listParameter></SomeComplexObject></entry><entry key=\"bbb\"><SomeComplexObject><stringParameter>String value for bbb</stringParameter><dateTimeParameter>2009-06-02T13:50:00Z</dateTimeParameter><enumParameter>FOOBAR</enumParameter><listParameter><String>bbb List Entry 1</String><String>bbb List Entry 2</String><String>bbb List Entry 3</String></listParameter></SomeComplexObject></entry></complexMap></message></ComplexMapOperationRequest>");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager5 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean5 = discoManager5.getNewHttpCallBean("87.248.113.14");
        discoManager5 = discoManager5;

        getNewHttpCallBean5.setServiceName("Baseline");

        getNewHttpCallBean5.setVersion("v2");
        // Set the created SOAP request as the PostObject
        getNewHttpCallBean5.setPostObjectForRequestType(createAsDocument4, "SOAP");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp10 = new Timestamp(System.currentTimeMillis());
        // Make the SOAP call to the operation
        discoManager5.makeSoapDiscoHTTPCalls(getNewHttpCallBean5);
        // Create the expected response object as an XML document (using the date objects created earlier)
        XMLHelpers xMLHelpers7 = new XMLHelpers();
        Document createAsDocument12 = xMLHelpers7.createAsDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<response><responseMap><entry key=\"aaa\"><SomeComplexObject><stringParameter>String value for aaa</stringParameter><dateTimeParameter>"+convertUTCDateTimeToDiscoFormat1+"</dateTimeParameter><enumParameter>BAR</enumParameter><listParameter><String>aaa List Entry 1</String><String>aaa List Entry 2</String><String>aaa List Entry 3</String></listParameter></SomeComplexObject></entry><entry key=\"bbb\"><SomeComplexObject><stringParameter>String value for bbb</stringParameter><dateTimeParameter>"+convertUTCDateTimeToDiscoFormat2+"</dateTimeParameter><enumParameter>FOOBAR</enumParameter><listParameter><String>bbb List Entry 1</String><String>bbb List Entry 2</String><String>bbb List Entry 3</String></listParameter></SomeComplexObject></entry><entry key=\"ccc\"><stringParameter>String value for ccc</stringParameter><SomeComplexObject><dateTimeParameter>"+convertUTCDateTimeToDiscoFormat3+"</dateTimeParameter><enumParameter>FOO</enumParameter><listParameter><String>ccc List Entry 1</String><String>ccc List Entry 2</String><String>ccc List Entry 3</String></listParameter></SomeComplexObject></entry></responseMap></response>").getBytes())));

        // Check the response is as expected
        HttpResponseBean response8 = getNewHttpCallBean5.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.SOAP);
        AssertionUtils.multiAssertEquals(createAsDocument12, response8.getResponseObject());

        // generalHelpers.pauseTest(2000L);
        // Check the log entries are as expected

        discoManager5.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp10, new RequestLogRequirement("2.8", "complexMapOperation") );
    }

}
