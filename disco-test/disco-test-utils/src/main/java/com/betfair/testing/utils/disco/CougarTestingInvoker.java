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

package com.betfair.testing.utils.disco;

import com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import com.betfair.testing.utils.disco.manager.LogTailer;


/**
 * New interface for writing integration tests via.
 */
public interface DiscoTestingInvoker {


    DiscoTestingInvoker setService(String serviceName);

    DiscoTestingInvoker setService(String serviceName, String path);

    DiscoTestingInvoker setVersion(String version);

    DiscoTestingInvoker setOperation(String operation);

    DiscoTestingInvoker setOperation(String operation, String operationPath);

    DiscoTestingInvoker addHeaderParam(String key, String value);

    DiscoTestingInvoker addQueryParam(String key, String value);

    DiscoTestingInvoker makeMatrixRescriptCalls(DiscoMessageContentTypeEnum... mediaTypes);

    DiscoTestingInvoker setExpectedResponse(DiscoMessageContentTypeEnum mediaType, String response);

    DiscoTestingInvoker setExpectedHttpResponse(int code, String text);

    DiscoTestingInvoker verify();

    LogTailer.LogLine[] getRequestLogEntries();

    DiscoTestingInvoker setSoapBody(String body);

    DiscoTestingInvoker makeSoapCall();

    DiscoTestingInvoker addJsonRpcMethodCall(String id, String method, String body);
    DiscoTestingInvoker addJsonRpcExpectedResponse(String body);

    DiscoTestingInvoker makeJsonRpcCalls();
}
