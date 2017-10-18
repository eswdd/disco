/*
 * Copyright 2014, Simon Matić Langford
 * Copyright 2014, The Sporting Exchange Limited
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

package uk.co.exemel.testing.utils.disco;

import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.LogTailer;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;
import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.*;

/**
 * New utility for writing integration tests via. Initially just delegates to old code.
 */
class LegacyDiscoTestingInvoker implements DiscoTestingInvoker {

    private DiscoManager discoManager = DiscoManager.getInstance();
    private HttpCallBean httpCallBeanBaseline;

    private String version;
    private String operation;
    private Map<String, String> headerParams = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private int expectedHttpStatusCode;
    private String expectedHttpStatusText;
    private Timestamp timestamp;
    private int numCalls;
    private Document expectedResponseXML;
    private JSONObject expectedResponseJson;
    private LogTailer.LogLine[] requestLogEntries;
    private String soapBody;
    private List<Map<String, String>> jsonCalls;
    private List<String> expectedResponsesJsonRpc;

    public LegacyDiscoTestingInvoker() {
        httpCallBeanBaseline = discoManager.getNewHttpCallBean();
        jsonCalls = new LinkedList<>();
        expectedResponsesJsonRpc = new ArrayList<>();
    }

    public static LegacyDiscoTestingInvoker create() {
        return new LegacyDiscoTestingInvoker();
    }


    public DiscoTestingInvoker setService(String serviceName) {
        return setService(serviceName, serviceName);
    }

    public DiscoTestingInvoker setService(String serviceName, String path) {
        httpCallBeanBaseline.setServiceName(serviceName, path);
        return this;
    }

    public DiscoTestingInvoker setVersion(String version) {
        httpCallBeanBaseline.setVersion("v"+(version.substring(0,version.indexOf("."))));
        this.version = version;
        return this;
    }

    public DiscoTestingInvoker setOperation(String operation) {
        return setOperation(operation,operation);
    }

    public DiscoTestingInvoker setOperation(String operation, String operationPath) {
        httpCallBeanBaseline.setOperationName(operationPath);
        this.operation = operation;
        return this;
    }

    public DiscoTestingInvoker addHeaderParam(String key, String value) {
        headerParams.put(key, value);
        return this;
    }

    public DiscoTestingInvoker addQueryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }



    @Override
    public DiscoTestingInvoker makeSoapCall() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.numCalls = 1;

        httpCallBeanBaseline.setHeaderParams(headerParams);
        Document request = new XMLHelpers().getXMLObjectFromString(soapBody);
        httpCallBeanBaseline.setPostObjectForRequestType(request, "SOAP");

        discoManager.makeSoapDiscoHTTPCalls(httpCallBeanBaseline);

        return this;
    }

    public DiscoTestingInvoker makeMatrixRescriptCalls(DiscoMessageContentTypeEnum... mediaTypes) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.numCalls = mediaTypes.length*mediaTypes.length;


        // Set the parameters to empty lists of string
        httpCallBeanBaseline.setHeaderParams(headerParams);
        httpCallBeanBaseline.setQueryParams(queryParams);
        // Get current time for getting log entries later

        timestamp = new Timestamp(System.currentTimeMillis());

        // Make the 4 REST calls to the operation
        // this ignores the media types that have been set
        discoManager.makeRestDiscoHTTPCalls(httpCallBeanBaseline);

        return this;
    }

    @Override
    public DiscoTestingInvoker makeJsonRpcCalls() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.numCalls = 1;

        httpCallBeanBaseline.setHeaderParams(headerParams);
        httpCallBeanBaseline.setJSONRPC(true);
        Map<String,String>[] mapArray = jsonCalls.toArray(new Map[jsonCalls.size()]);
        httpCallBeanBaseline.setBatchedRequests(mapArray);

        discoManager.makeRestDiscoHTTPCall(httpCallBeanBaseline, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);

        return this;
    }

    @Override
    public DiscoTestingInvoker setSoapBody(String body) {
        this.soapBody = body;
        return this;
    }

    @Override
    public DiscoTestingInvoker addJsonRpcMethodCall(String id, String method, String body) {
        Map<String,String> call = new HashMap<>();
        call.put("id",id);
        call.put("method",method);
        call.put("params",body);
        jsonCalls.add(call);
        return this;
    }

    @Override
    public DiscoTestingInvoker addJsonRpcExpectedResponse(String body) {
        expectedResponsesJsonRpc.add(body);
        return this;
    }

    public DiscoTestingInvoker setExpectedResponse(DiscoMessageContentTypeEnum mediaType, String response) {
        if (mediaType == DiscoMessageContentTypeEnum.XML) {
            expectedResponseXML = new XMLHelpers().getXMLObjectFromString(response);
        }
        else if (mediaType == DiscoMessageContentTypeEnum.JSON) {
            try {
                expectedResponseJson = new JSONHelpers().createAsJSONObject(new JSONObject(response));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new IllegalArgumentException("Unexpected media type: "+mediaType);
        }
        return this;
    }

    public DiscoTestingInvoker setExpectedHttpResponse(int code, String text) {
        expectedHttpStatusCode = code;
        expectedHttpStatusText = text;
        return this;
    }

    public DiscoTestingInvoker verify() {
        if (expectedResponseXML != null) {
            // Check the 4 responses are as expected
            HttpResponseBean response7 = httpCallBeanBaseline.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
            AssertionUtils.multiAssertEquals(expectedResponseXML, response7.getResponseObject());
            AssertionUtils.multiAssertEquals(expectedHttpStatusCode, response7.getHttpStatusCode());
            AssertionUtils.multiAssertEquals(expectedHttpStatusText, response7.getHttpStatusText());

            HttpResponseBean response10 = httpCallBeanBaseline.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
            AssertionUtils.multiAssertEquals(expectedResponseXML, response10.getResponseObject());
            AssertionUtils.multiAssertEquals(expectedHttpStatusCode, response10.getHttpStatusCode());
            AssertionUtils.multiAssertEquals(expectedHttpStatusText, response10.getHttpStatusText());
        }

        if (expectedResponseJson != null) {
            HttpResponseBean response8 = httpCallBeanBaseline.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
            AssertionUtils.multiAssertEquals(expectedResponseJson, response8.getResponseObject());
            AssertionUtils.multiAssertEquals(expectedHttpStatusCode, response8.getHttpStatusCode());
            AssertionUtils.multiAssertEquals(expectedHttpStatusText, response8.getHttpStatusText());

            HttpResponseBean response9 = httpCallBeanBaseline.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
            AssertionUtils.multiAssertEquals(expectedResponseJson, response9.getResponseObject());
            AssertionUtils.multiAssertEquals(expectedHttpStatusCode, response9.getHttpStatusCode());
            AssertionUtils.multiAssertEquals(expectedHttpStatusText, response9.getHttpStatusText());
        }

        if (!expectedResponsesJsonRpc.isEmpty()) {
            try {
                HttpResponseBean response = httpCallBeanBaseline.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
                // Convert the returned json object to a map for comparison
                DiscoHelpers discoHelpers = new DiscoHelpers();
                Map<String, Object> map5 = discoHelpers.convertBatchedResponseToMap(response);
                for (int i=0; i<expectedResponsesJsonRpc.size(); i++) {
                    AssertionUtils.multiAssertEquals(expectedResponsesJsonRpc.get(i), map5.get("response"+(i+1)));
                }
                AssertionUtils.multiAssertEquals(expectedHttpStatusCode, map5.get("httpStatusCode"));
                AssertionUtils.multiAssertEquals(expectedHttpStatusText, map5.get("httpStatusText"));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ensureHaveRequestLogEntries();

        return this;
    }

    private void ensureHaveRequestLogEntries() {
        if (requestLogEntries == null) {
            try {
                RequestLogRequirement[] reqs = new RequestLogRequirement[numCalls];
                for (int i=0; i<numCalls; i++) {
                    reqs[i] = new RequestLogRequirement(version, operation);
                }
                requestLogEntries = DiscoManager.getInstance().verifyRequestLogEntriesAfterDate(timestamp, reqs);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public LogTailer.LogLine[] getRequestLogEntries() {
        ensureHaveRequestLogEntries();
        return requestLogEntries;
    }
}
