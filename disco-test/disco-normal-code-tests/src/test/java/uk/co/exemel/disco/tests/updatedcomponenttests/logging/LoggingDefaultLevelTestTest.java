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

// Originally from UpdatedComponentTests/Logging/Logging_DefaultLevel_Test.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.logging;

import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.ServiceLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that the disco server logs correctly according to the logging level set. (Set level to INFO)
 */
public class LoggingDefaultLevelTestTest {
    @Test
    public void doTest() throws Exception {
        // Set up a Http Call Bean to make a request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager1.getNewHttpCallBean();
        DiscoManager hinstance = discoManager1;
        try {
            // Set up another Http Call Bean to make a request
            HttpCallBean hbean_2 = hinstance.getNewHttpCallBean();
            // Set up the first call bean to change the log level to INFO
            hbean.setOperationName("changeLogLevel");

            Map map2 = new HashMap();
            map2.put("logName","service");
            map2.put("level","INFO");
            hbean.setQueryParams(map2);

            hbean.setServiceName("baseline", "discoBaseline");

            hbean.setVersion("v2");
            // Make the REST JSON call to the operation to change the log level
            hinstance.makeRestDiscoHTTPCall(hbean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry
            hbean_2.setOperationName("logMessage");

            hbean_2.setServiceName("baseline", "discoBaseline");

            hbean_2.setVersion("v2");
            // Set the log entry level to be FINE
            Map map3 = new HashMap();
            map3.put("logString","DEBUG-Log-Message");
            map3.put("logLevel","DEBUG");
            hbean_2.setQueryParams(map3);
//            Thread.sleep(60000);

            // Get current time for getting log entries later

            Timestamp getTimeAsTimeStamp13 = new Timestamp(System.currentTimeMillis());
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
            // Set up the second call bean to create a log entry with level INFO
            Map map5 = new HashMap();
            map5.put("logString","INFO-Log-Message");
            map5.put("logLevel","INFO");
            hbean_2.setQueryParams(map5);
            // Make the REST JSON call to the operation
            hinstance.makeRestDiscoHTTPCall(hbean_2, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
            // Assert that only the second INFO entry is in the log as the FINE entry is below the log level set
            discoManager1.verifyServiceLogEntriesAfterDate(getTimeAsTimeStamp13, new ServiceLogRequirement("uk.co.exemel.disco.baseline.BaselineServiceImpl INFO - INFO-Log-Message"));
        }
        finally {
            // Set log level back to the default level (WARNING)
            Map map2 = new HashMap();
            map2.put("logName","service");
            map2.put("level","WARN");
            hbean.setQueryParams(map2);
            hinstance.makeRestDiscoHTTPCall(hbean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
        }
    }

}
