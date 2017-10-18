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

// Originally from UpdatedComponentTests/StandardTesting/REST/Rest_Post_RequestTypes_Bytes.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rest;

import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco can handle Bytes in the post body, header params and query params
 */
public class RestPostRequestTypesBytesTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager1;
        HttpCallBean hbean = getNewHttpCallBean1;
        // Get logging attribute for getting log entries later
        hbean.setOperationName("byteOperation");
        hbean.setServiceName("baseline","discoBaseline");
        hbean.setVersion("v2");
        // Set each of the parameter types to contain a byte datatype object for zero
        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put("HeaderParam","127");
        hbean.setHeaderParams(headerParams);
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("queryParam","23");
        hbean.setQueryParams(queryParams);
        hbean.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><bodyParameter>EYitl82RbhhPWMZKw2MNlxF4kIGuX03TWEPUBbAxaBs=</bodyParameter></message>".getBytes())));
        // Get current time for getting log entries later
        Timestamp getTimeAsTimeStamp9 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        hinstance.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        // Create the expected response as an XML document
        Document createAsDocument11 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ByteOperationResponseObject><bodyParameter>EYitl82RbhhPWMZKw2MNlxF4kIGuX03TWEPUBbAxaBs=</bodyParameter><headerParameter>127</headerParameter><queryParameter>23</queryParameter></ByteOperationResponseObject>".getBytes()));
        // Convert the expected response to REST types for comparison with actual responses
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes12 = hinstance.convertResponseToRestTypes(createAsDocument11, hbean);
        // Check the 4 responses are as expected
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes12.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML).getResponseObject());
        AssertionUtils.multiAssertEquals(200, hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML).getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML).getHttpStatusText());
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes12.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getResponseObject());
        AssertionUtils.multiAssertEquals(200, hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON).getHttpStatusText());
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes12.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON), hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON).getResponseObject());
        AssertionUtils.multiAssertEquals(200, hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON).getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON).getHttpStatusText());
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes12.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getResponseObject());
        AssertionUtils.multiAssertEquals(200, hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML).getHttpStatusText());
        // Check the log entries are as expected
        hinstance.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp9,
                new RequestLogRequirement("2.8", "byteOperation"),
                new RequestLogRequirement("2.8", "byteOperation"),
                new RequestLogRequirement("2.8", "byteOperation"),
                new RequestLogRequirement("2.8", "byteOperation"));
    }

}
