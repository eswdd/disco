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

// Originally from UpdatedComponentTests/StandardTesting/RPC/RPC_Post_RequestTypes_DateTimeMap_PositiveOffset.xls;
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
 * Ensure that Disco can handle the dateTimeMap data type with positive offsets in the post body of an RPC request
 */
public class RPCPostRequestTypesDateTimeMapPositiveOffsetTest {
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
        Map[] mapArray2 = new Map[4];
        mapArray2[0] = new HashMap();
        mapArray2[0].put("method","dateTimeMapOperation");
        mapArray2[0].put("params","[{\"dateTimeMap\":{\"date1\":\"2009-06-01T15:50:00.435+02:00\",\"date2\":\"2009-06-02T15:50:00.435+02:00\"}}]");
        mapArray2[0].put("id","\"DateTimeMapPosOff\"");
        mapArray2[1] = new HashMap();
        mapArray2[1].put("method","dateTimeMapOperation");
        mapArray2[1].put("params","[{\"dateTimeMap\":{\"date1\":\"2009-02-01T15:50:00.435+02:00\",\"date2\":\"2009-02-02T15:50:00.435+02:00\"}}]");
        mapArray2[1].put("id","\"DateTimeMapPosOff2\"");
        mapArray2[2] = new HashMap();
        mapArray2[2].put("method","dateTimeMapOperation");
        mapArray2[2].put("params","[{\"dateTimeMap\":{\"date1\":\"2009-06-01T15:50:00.435+02:30\",\"date2\":\"2009-06-02T15:50:00.435+02:30\"}}]");
        mapArray2[2].put("id","\"DateTimeMapPosOffHoursAndMins\"");
        mapArray2[3] = new HashMap();
        mapArray2[3].put("method","dateTimeMapOperation");
        mapArray2[3].put("params","[{\"dateTimeMap\":{\"date1\":\"2009-01-01T00:01:00.435+14:00\",\"date2\":\"2009-01-01T00:01:00.435+14:00\"}}]");
        mapArray2[3].put("id","\"DateTimeMapPosOffRoll\"");
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
        AssertionUtils.multiAssertEquals("{\"id\":\"DateTimeMapPosOff\",\"result\":{\"responseMap\":{\"date2\":\"2009-06-02T13:50:00.435Z\",\"date1\":\"2009-06-01T13:50:00.435Z\"}},\"jsonrpc\":\"2.0\"}", map5.get("responseDateTimeMapPosOff"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DateTimeMapPosOff2\",\"result\":{\"responseMap\":{\"date2\":\"2009-02-02T13:50:00.435Z\",\"date1\":\"2009-02-01T13:50:00.435Z\"}},\"jsonrpc\":\"2.0\"}", map5.get("responseDateTimeMapPosOff2"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DateTimeMapPosOffHoursAndMins\",\"result\":{\"responseMap\":{\"date2\":\"2009-06-02T13:20:00.435Z\",\"date1\":\"2009-06-01T13:20:00.435Z\"}},\"jsonrpc\":\"2.0\"}", map5.get("responseDateTimeMapPosOffHoursAndMins"));
        AssertionUtils.multiAssertEquals("{\"id\":\"DateTimeMapPosOffRoll\",\"result\":{\"responseMap\":{\"date2\":\"2008-12-31T10:01:00.435Z\",\"date1\":\"2008-12-31T10:01:00.435Z\"}},\"jsonrpc\":\"2.0\"}", map5.get("responseDateTimeMapPosOffRoll"));
        AssertionUtils.multiAssertEquals("OK", map5.get("httpStatusText"));
        AssertionUtils.multiAssertEquals(200, map5.get("httpStatusCode"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "dateTimeMapOperation"),new RequestLogRequirement("2.8", "dateTimeMapOperation"),new RequestLogRequirement("2.8", "dateTimeMapOperation"),new RequestLogRequirement("2.8", "dateTimeMapOperation") );
        
        DiscoManager discoManager9 = DiscoManager.getInstance();
        discoManager9.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
