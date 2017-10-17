/*
 * Copyright 2013, Simon Matić Langford
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

// Originally from UpdatedComponentTests/StandardValidation/RPC/RPC_ENUM_Param_IncorrectDataType.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rpc;

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
 * Ensure that Disco returns the correct fault when a  RPC request with Enum param  is made with incorrect data type parameter
 */
public class RPCENUMParamUnrecognizedValueTest {
    @Test(groups = {"json-rpc", "enums"})
    public void doTest() throws Exception {
        // Create the HttpCallBean
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean httpCallBeanBaseline = discoManager1.getNewHttpCallBean();
        // Get the disco logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "discoBaseline");

        httpCallBeanBaseline.setVersion("v2");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager2.getNewHttpCallBean("87.248.113.14");
        DiscoManager discoManager = discoManager2;

        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map[] mapArray3 = new Map[4];
        mapArray3[0] = new HashMap();
        mapArray3[0].put("method","enumOperation");
        mapArray3[0].put("params","[\"FooHeader\", \"FooQuery\",{\"bodyParameter\":\"FooBody\"}]");
        mapArray3[0].put("id","\"Call with correct params\"");
        mapArray3[1] = new HashMap();
        mapArray3[1].put("method","enumOperation");
        mapArray3[1].put("params","[\"UNRECOGNIZED_VALUE\", \"FooQuery\",{\"bodyParameter\":\"FooBody\"}]");
        mapArray3[1].put("id","\"Incorrect Header param\"");
        mapArray3[2] = new HashMap();
        mapArray3[2].put("method","enumOperation");
        mapArray3[2].put("params","[\"FooHeader\", \"UNRECOGNIZED_VALUE\",{\"bodyParameter\":\"FooBody\"}]");
        mapArray3[2].put("id","\"Incorrect Query param\"");
        mapArray3[3] = new HashMap();
        mapArray3[3].put("method","enumOperation");
        mapArray3[3].put("params","[\"FooHeader\", \"FooQuery\",{\"bodyParameter\":\"UNRECOGNIZED_VALUE\"}]");
        mapArray3[3].put("id","\"Incorrect Body param\"");
        callBean.setBatchedRequests(mapArray3);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean actualResponseJSON = callBean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers5 = new DiscoHelpers();
        Map<String, Object> map6 = discoHelpers5.convertBatchedResponseToMap(actualResponseJSON);
        AssertionUtils.multiAssertEquals("{\"id\":\"Call with correct params\",\"result\":{\"headerParameter\":\"FooHeader\",\"queryParameter\":\"FooQuery\",\"bodyParameter\":\"FooBody\"},\"jsonrpc\":\"2.0\"}", map6.get("responseCall with correct params"));
        AssertionUtils.multiAssertEquals("{\"id\":\"Incorrect Header param\",\"error\":{\"message\":\"DSC-0044\",\"code\":-32602},\"jsonrpc\":\"2.0\"}", map6.get("responseIncorrect Header param"));
        AssertionUtils.multiAssertEquals("{\"id\":\"Incorrect Query param\",\"error\":{\"message\":\"DSC-0044\",\"code\":-32602},\"jsonrpc\":\"2.0\"}", map6.get("responseIncorrect Query param"));
        AssertionUtils.multiAssertEquals("{\"id\":\"Incorrect Body param\",\"error\":{\"message\":\"DSC-0044\",\"code\":-32602},\"jsonrpc\":\"2.0\"}", map6.get("responseIncorrect Body param"));
        AssertionUtils.multiAssertEquals(200, map6.get("httpStatusCode"));
        AssertionUtils.multiAssertEquals("OK", map6.get("httpStatusText"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "enumOperation") );

        DiscoManager discoManager10 = DiscoManager.getInstance();
        discoManager10.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
