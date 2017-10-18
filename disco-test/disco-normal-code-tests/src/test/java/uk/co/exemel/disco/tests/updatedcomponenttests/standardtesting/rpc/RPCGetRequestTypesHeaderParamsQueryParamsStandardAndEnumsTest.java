/*
 * Copyright 2013, The Sporting Exchange Limited
 * Copyright 2015, Simon Matić Langford
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

// Originally from UpdatedComponentTests/StandardTesting/RPC/RPC_Get_RequestTypes_HeaderParams_QueryParams_StandardAndEnums.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rpc;

import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that when a Batched JSON request is performed against a Disco operation with Header Params and Query Params set to standard and Enums values, the correct response is returned.
 */
public class RPCGetRequestTypesHeaderParamsQueryParamsStandardAndEnumsTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager discoManager = discoManager1;
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map[] mapArray2 = new Map[2];
        mapArray2[0] = new HashMap();
        mapArray2[0].put("method","testParameterStylesQA");
        mapArray2[0].put("params","[\"Foo\",\"qp1\",\"2009-06-01T13:50:00.0Z\"]");
        mapArray2[0].put("id","1");
        mapArray2[1] = new HashMap();
        mapArray2[1].put("method","testParameterStylesQA");
        mapArray2[1].put("params","[\"Foo\",\"qp2\",\"2009-06-02T13:50:00.0Z\"]");
        mapArray2[1].put("id","2");
        callBean.setBatchedRequests(mapArray2);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean response = callBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the time to system TimeZone
        DiscoHelpers discoHelpers4 = new DiscoHelpers();
        Date convertedDate1 = discoHelpers4.convertToSystemTimeZone("2009-06-01T13:50:00.0Z");
        // Convert the time to system TimeZone
        DiscoHelpers discoHelpers5 = new DiscoHelpers();
        Date convertedDate2 = discoHelpers5.convertToSystemTimeZone("2009-06-02T13:50:00.0Z");
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers6 = new DiscoHelpers();
        Map<String, Object> map7 = discoHelpers6.convertBatchedResponseToMap(response);
        AssertionUtils.multiAssertEquals("{\"id\":1,\"result\":{\"message\":\"headerParam=Foo,queryParam=qp1,dateQueryParam="+discoHelpers4.dateInUTC(convertedDate1)+"\"},\"jsonrpc\":\"2.0\"}", map7.get("response1"));
        AssertionUtils.multiAssertEquals("{\"id\":2,\"result\":{\"message\":\"headerParam=Foo,queryParam=qp2,dateQueryParam="+discoHelpers4.dateInUTC(convertedDate2)+"\"},\"jsonrpc\":\"2.0\"}", map7.get("response2"));
        AssertionUtils.multiAssertEquals(200, map7.get("httpStatusCode"));
        AssertionUtils.multiAssertEquals("OK", map7.get("httpStatusText"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "testParameterStylesQA"),new RequestLogRequirement("2.8", "testParameterStylesQA") );

        discoManager.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
