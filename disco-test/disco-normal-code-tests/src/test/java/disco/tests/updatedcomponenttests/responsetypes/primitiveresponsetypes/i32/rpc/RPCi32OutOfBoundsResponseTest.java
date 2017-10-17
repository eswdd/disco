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

// Originally from UpdatedComponentTests/ResponseTypes/PrimitiveResponseTypes/i32/RPC/RPC_i32OutOfBoundsResponse.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.responsetypes.primitiveresponsetypes.i32.rpc;

import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Test that the Disco service returns a correct error when value passed  for i32 is out of type bounds (Batched JSON)
 */
public class RPCi32OutOfBoundsResponseTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager discoManager = discoManager1;
        // Set Disco Fault Controller attributes
        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map[] mapArray2 = new Map[2];
        mapArray2[0] = new HashMap();
        mapArray2[0].put("method","i32SimpleTypeEcho");
        mapArray2[0].put("params","[21474836470]");
        mapArray2[0].put("id","1");
        mapArray2[1] = new HashMap();
        mapArray2[1].put("method","i32SimpleTypeEcho");
        mapArray2[1].put("params","[21474836470]");
        mapArray2[1].put("id","2");
        callBean.setBatchedRequests(mapArray2);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean response = callBean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers4 = new DiscoHelpers();
        Map<String, Object> map5 = discoHelpers4.convertBatchedResponseToMap(response);
        AssertionUtils.multiAssertEquals("{\"id\":1,\"error\":{\"message\":\"DSC-0044\",\"code\":-32602},\"jsonrpc\":\"2.0\"}", map5.get("response1"));
        AssertionUtils.multiAssertEquals("{\"id\":2,\"error\":{\"message\":\"DSC-0044\",\"code\":-32602},\"jsonrpc\":\"2.0\"}", map5.get("response2"));
        AssertionUtils.multiAssertEquals(200, map5.get("httpStatusCode"));
        AssertionUtils.multiAssertEquals("OK", map5.get("httpStatusText"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected


        discoManager.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
