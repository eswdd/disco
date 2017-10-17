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

// Originally from UpdatedComponentTests/StandardTesting/RPC/RPC_Post_RequestTypes_Doubles.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rpc;

import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco can handle Doubles in the post body, header params and query params of an RPC request
 */
public class RPCPostRequestTypesDoublesTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager discoManager = discoManager1;
        
        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map[] mapArray2 = new Map[5];
        mapArray2[0] = new HashMap();
        mapArray2[0].put("method","doubleOperation");
        mapArray2[0].put("params","[553.36, 78.025,{\"bodyParameter\":0.0006}]");
        mapArray2[0].put("id","\"Doubles\"");
        mapArray2[1] = new HashMap();
        mapArray2[1].put("method","doubleOperation");
        mapArray2[1].put("params","[\"Infinity\", \"-Infinity\",{\"bodyParameter\":\"-Infinity\"}]");
        mapArray2[1].put("id","\"DoublesInfinity\"");
        mapArray2[2] = new HashMap();
        mapArray2[2].put("method","doubleOperation");
        mapArray2[2].put("params","[-553.36, -78.025,{\"bodyParameter\":-0.0006}]");
        mapArray2[2].put("id","\"DoublesNegatives\"");
        mapArray2[3] = new HashMap();
        mapArray2[3].put("method","doubleOperation");
        mapArray2[3].put("params","[-1.7976931344323158E969, 1.7976931348334158E979,{\"bodyParameter\":2233.44}]");
        mapArray2[3].put("id","\"DoublesOutOfRange\"");
        mapArray2[4] = new HashMap();
        mapArray2[4].put("method","doubleOperation");
        mapArray2[4].put("params","[0.0, 0.0,{\"bodyParameter\":0.0}]");
        mapArray2[4].put("id","\"DoublesZeroes\"");
        callBean.setBatchedRequests(mapArray2);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean actualResponseJSON = callBean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers4 = new DiscoHelpers();
        Map<String, Object> map5 = discoHelpers4.convertBatchedResponseToMap(actualResponseJSON);
        AssertionUtils.multiAssertEquals("{\"id\":\"Doubles\",\"result\":{\"headerParameter\":553.36,\"queryParameter\":78.025,\"bodyParameter\":6.0E-4},\"jsonrpc\":\"2.0\"}", map5.get("responseDoubles"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DoublesInfinity\",\"result\":{\"headerParameter\":\"Infinity\",\"queryParameter\":\"-Infinity\",\"bodyParameter\":\"-Infinity\"},\"jsonrpc\":\"2.0\"}", map5.get("responseDoublesInfinity"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DoublesNegatives\",\"result\":{\"headerParameter\":-553.36,\"queryParameter\":-78.025,\"bodyParameter\":-6.0E-4},\"jsonrpc\":\"2.0\"}", map5.get("responseDoublesNegatives"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DoublesOutOfRange\",\"result\":{\"headerParameter\":\"-Infinity\",\"queryParameter\":\"Infinity\",\"bodyParameter\":2233.44},\"jsonrpc\":\"2.0\"}", map5.get("responseDoublesOutOfRange"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DoublesZeroes\",\"result\":{\"headerParameter\":0,\"queryParameter\":0,\"bodyParameter\":0},\"jsonrpc\":\"2.0\"}", map5.get("responseDoublesZeroes"));
        AssertionUtils.multiAssertEquals("OK", map5.get("httpStatusText"));
        AssertionUtils.multiAssertEquals(200, map5.get("httpStatusCode"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "doubleOperation"),new RequestLogRequirement("2.8", "doubleOperation"),new RequestLogRequirement("2.8", "doubleOperation"),new RequestLogRequirement("2.8", "doubleOperation"),new RequestLogRequirement("2.8", "doubleOperation") );
        
        DiscoManager discoManager9 = DiscoManager.getInstance();
        discoManager9.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
