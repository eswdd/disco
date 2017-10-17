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

// Originally from UpdatedComponentTests/Logging/Logging_CheckForStartupMessage_Test.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.logging;

import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that the server log contains a message to say that the server was started successfully (test does not start the baseline, still needs to be done beforehand)
 */
public class LoggingCheckForStartupMessageTestTest {
    @Test
    public void doTest() throws Exception {
        // Set up a Http Call Bean to make a request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager1.getNewHttpCallBean();
        DiscoManager manager = discoManager1;
        // Set up the call bean to change the log level to INFO
        callBean.setOperationName("changeLogLevel");
        
        Map map2 = new HashMap();
        map2.put("logName","service");
        map2.put("level","INFO");
        callBean.setQueryParams(map2);
        
        callBean.setServiceName("baseline", "discoBaseline");
        
        callBean.setVersion("v2");
        // Make the REST JSON call to the operation to change the log level
        manager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Change the call bean to make a log entry
        callBean.setOperationName("logMessage");
        
        Map map3 = new HashMap();
        map3.put("logString","Log-message-to-roll-logs-over-if-needed");
        map3.put("logLevel","INFO");
        callBean.setQueryParams(map3);
        // Make the REST JSON call to the operation
        manager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Set up the call bean to change the log level back to WARNING
        callBean.setOperationName("changeLogLevel");
        
        Map map4 = new HashMap();
        map4.put("logName","service");
        map4.put("level","WARNING");
        callBean.setQueryParams(map4);
        // Make the REST JSON call to the operation to change the log level
        manager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Get the disco logging attribute
        DiscoManager discoManager5 = DiscoManager.getInstance();
        manager = discoManager5;
        // Get the uptime of the baseline
        Integer upTime = manager.getDiscoUpTimeInMins("Uptime");
        // todo: SML: fix this bit..
        // Get the contents of the server log that contains the disco start (possibly an archived version)
//        LogFileBean log = manager.getServerLogContainingDiscoStart(discoManager1.getBaseLogDirectory(), upTime);
        // Get the log contains the statup success message
//        DiscoHelpers discoHelpers6 = new DiscoHelpers();
//        boolean presentInLog = discoHelpers6.checkLogContainsEntryHeaderValue(log, "uk.co.exemel.disco.core.impl.ev.ContainerAwareExecutionVenue INFO - **** DISCO HAS STARTED *****");
//        AssertionUtils.multiAssertEquals(true, presentInLog);
    }

}
