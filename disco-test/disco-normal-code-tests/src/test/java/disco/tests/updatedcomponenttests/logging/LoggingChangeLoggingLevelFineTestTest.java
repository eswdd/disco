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

// Originally from UpdatedComponentTests/Logging/Logging_ChangeLoggingLevel_Fine_Test.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.logging;

import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.ServiceLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that the disco server logs correctly according to the logging level set. (Set level to FINE)
 */
public class LoggingChangeLoggingLevelFineTestTest {
    @Test
    public void doTest() throws Exception {
        // Set up a Http Call Bean to make a request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager1.getNewHttpCallBean();
        DiscoManager hinstance = discoManager1;
        try {
            // Set up another Http Call Bean to make a request
            HttpCallBean hbean_2 = hinstance.getNewHttpCallBean();
            // Set up the first call bean to change the log level to FINE
            hbean.setOperationName("changeLogLevel");

            Map map2 = new HashMap();
            map2.put("logName","service");
            map2.put("level","DEBUG");
            hbean.setQueryParams(map2);

            hbean.setServiceName("baseline", "discoBaseline");

            hbean.setVersion("v2");
            // Make the REST JSON call to the operation to change the log level
            hinstance.makeRestDiscoHTTPCall(hbean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry
            hbean_2.setOperationName("logMessage");

            hbean_2.setServiceName("baseline", "discoBaseline");

            hbean_2.setVersion("v2");
            // Set the log entry level to be FINEST
            Map map3 = new HashMap();
            map3.put("logString","DEBUG-Log-Message");
            map3.put("logLevel","DEBUG");
            hbean_2.setQueryParams(map3);
            // Get current time for getting log entries later
//            Thread.sleep(60000);

            Timestamp getTimeAsTimeStamp13 = new Timestamp(System.currentTimeMillis());
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry with level INFO
            Map map6 = new HashMap();
            map6.put("logString","INFO-Log-Message");
            map6.put("logLevel","INFO");
            hbean_2.setQueryParams(map6);
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry with level WARNING
            Map map7 = new HashMap();
            map7.put("logString","WARNING-Log-Message");
            map7.put("logLevel","WARN");
            hbean_2.setQueryParams(map7);
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry with level SEVERE
            Map map8 = new HashMap();
            map8.put("logString","ERROR-Log-Message");
            map8.put("logLevel","ERROR");
            hbean_2.setQueryParams(map8);
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry with level SEVERE
            // Assert that only entries with a log level >= FINE have been logged (no entries were made after level set to INFO so doesn't apply)
            discoManager1.verifyServiceLogEntriesAfterDate(getTimeAsTimeStamp13, new ServiceLogRequirement("uk.co.exemel.disco.baseline.BaselineServiceImpl DEBUG - DEBUG-Log-Message")
                                                                                , new ServiceLogRequirement("uk.co.exemel.disco.baseline.BaselineServiceImpl INFO - INFO-Log-Message")
                                                                                , new ServiceLogRequirement("uk.co.exemel.disco.baseline.BaselineServiceImpl WARN - WARNING-Log-Message")
                    , new ServiceLogRequirement("uk.co.exemel.disco.baseline.BaselineServiceImpl ERROR - ERROR-Log-Message"));
        }
        finally {
            // Set log level back to the default level (WARNING)
            Map map2 = new HashMap();
            map2.put("logName","service");
            map2.put("level","WARNING");
            hbean.setQueryParams(map2);
            hinstance.makeRestDiscoHTTPCall(hbean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        }
    }

}
