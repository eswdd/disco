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

package uk.co.exemel.disco.tests.updatedcomponenttests.logging;

import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.ServiceLogRequirement;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

import static com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON;


/**
 * Ensure that jetty does not produce warnings in suppressed classes.
 * These classes marked in jetty-transport-defaults.properties
 */
public class LoggingCheckJettySuppressTest {

    @Test
    public void SuppressInvalidCookieHeader() throws Exception {
        DiscoManager discoManager = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager.getNewHttpCallBean();

        Timestamp startTime = new Timestamp(System.currentTimeMillis());

        callBean.setServiceName("baseline", "discoBaseline");
        callBean.setOperationName("stringSimpleTypeEcho", "stringEcho");
        callBean.setVersion("v2");
        callBean.setQueryParams(Collections.singletonMap("msg", "foo"));
        callBean.setAlternativeURL("/cookie");
        callBean.setHeaderParams(Collections.singletonMap("Cookie", "Invalid {[Cookie]-Name}=SomeValue"));

        discoManager.makeRestDiscoHTTPCall(callBean, DiscoMessageProtocolRequestTypeEnum.RESTJSON,
                DiscoMessageContentTypeEnum.JSON);

        Map<DiscoMessageProtocolResponseTypeEnum, HttpResponseBean> responses = callBean.getResponses();
        AssertionUtils.multiAssertEquals("\"foo\"", responses.get(RESTJSONJSON).getResponseObject());

        expectNoWarningsAfter(discoManager, startTime);
    }

    @Test
    public void SuppressEofWarning() throws Exception {
        DiscoManager discoManager = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager.getNewHttpCallBean();

        Timestamp startTime = new Timestamp(System.currentTimeMillis());

        Socket socket = new Socket(callBean.getHost(), Integer.parseInt(callBean.getPort()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("GET /discoBaseline/v2.8/simple/sleep?sleep=500 HTTP/1.1");
        out.println("Host: " + callBean.getHost());
        out.println("Accept-Encoding: gzip");
        out.println();

        socket.setSoLinger(true, 0); // RST
        socket.close();

        expectNoWarningsAfter(discoManager, startTime);
    }

    private void expectNoWarningsAfter(DiscoManager discoManager, Timestamp startTime) throws IOException, InterruptedException {
        discoManager.verifyNoServiceLogEntriesAfterDate(startTime, 2000, new ServiceLogRequirement("WARN", true));
    }

}
