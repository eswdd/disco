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

package uk.co.exemel.disco.tests.clienttests.standardtesting;

import com.betfair.baseline.v2.BaselineSyncClient;
import com.betfair.baseline.v2.enumerations.ClientServerEnum;
import com.betfair.baseline.v2.enumerations.EnumHandlingParam2Enum;
import com.betfair.baseline.v2.to.EnumHandling;
import uk.co.exemel.disco.core.api.exception.DiscoClientException;
import uk.co.exemel.disco.core.api.exception.DiscoMarshallingException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.tests.clienttests.ClientTestsHelper;
import uk.co.exemel.disco.tests.clienttests.DiscoClientWrapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 *
 */
public class ClientEnumInComplexTypeHandlingModesTest {

    @Test(dataProvider = "TransportType")
    public void clientSoftModeRecognizedServerValue(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt, false);


        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        EnumHandling response = client.enumHandling(discoClientWrapper.getCtx(), req, false);

        assertEquals(ClientServerEnum.ClientServer, response.getParam1());
        assertEquals("ClientServer", response.getRawParam1Value());
        assertEquals(EnumHandlingParam2Enum.ClientServer, response.getParam2());
        assertEquals("ClientServer", response.getRawParam2Value());
    }

    @Test(dataProvider = "TransportType")
    public void clientSoftModeUnrecognizedServerValue(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt, false);

        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        EnumHandling response = client.enumHandling(discoClientWrapper.getCtx(), req, true);

        assertEquals(ClientServerEnum.UNRECOGNIZED_VALUE, response.getParam1());
        assertEquals("ServerOnly", response.getRawParam1Value());
        assertEquals(EnumHandlingParam2Enum.UNRECOGNIZED_VALUE, response.getParam2());
        assertEquals("ServerOnly", response.getRawParam2Value());

    }

    @Test(dataProvider = "TransportType")
    public void clientHardModeRecognizedServerValue(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt, true);

        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        EnumHandling response = client.enumHandling(discoClientWrapper.getCtx(), req, false);

        assertEquals(ClientServerEnum.ClientServer, response.getParam1());
        assertEquals("ClientServer", response.getRawParam1Value());
        assertEquals(EnumHandlingParam2Enum.ClientServer, response.getParam2());
        assertEquals("ClientServer", response.getRawParam2Value());
    }

    @Test(dataProvider = "TransportType")
    public void clientHardModeUnrecognizedServerValue(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt, true);


        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        try {
            client.enumHandling(discoClientWrapper.getCtx(), req, true);
            fail("Expected an exception here");
        }
        catch (DiscoClientException cfe) {
            assertEquals(toString(cfe), ServerFaultCode.ClientDeserialisationFailure, cfe.getServerFaultCode());
        }
    }

    @Test(dataProvider = "TransportType")
    public void serverHardModeRecognizedClientValue(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt);


        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        EnumHandling response = client.enumHandling(discoClientWrapper.getCtx(), req, false);

        assertEquals(ClientServerEnum.ClientServer, response.getParam1());
        assertEquals("ClientServer", response.getRawParam1Value());
        assertEquals(EnumHandlingParam2Enum.ClientServer, response.getParam2());
        assertEquals("ClientServer", response.getRawParam2Value());
    }

    @Test(dataProvider = "TransportType")
    public void serverHardModeUnrecognizedClientValue1(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt);

        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientOnly);
        req.setParam2(EnumHandlingParam2Enum.ClientServer);

        BaselineSyncClient client = discoClientWrapper.getClient();
        try {
            client.enumHandling(discoClientWrapper.getCtx(), req, false);
        }
        catch (DiscoClientException cfe) {
            assertEquals(toString(cfe), ServerFaultCode.ServerDeserialisationFailure, cfe.getServerFaultCode());
        }
    }

    @Test(dataProvider = "TransportType")
    public void serverHardModeUnrecognizedClientValue2(DiscoClientWrapper.TransportType tt) throws Exception {
        DiscoClientWrapper discoClientWrapper = DiscoClientWrapper.getInstance(tt);

        EnumHandling req = new EnumHandling();
        req.setParam1(ClientServerEnum.ClientServer);
        req.setParam2(EnumHandlingParam2Enum.ClientOnly);

        BaselineSyncClient client = discoClientWrapper.getClient();
        try {
            client.enumHandling(discoClientWrapper.getCtx(), req, false);
        }
        catch (DiscoClientException cfe) {
            assertEquals(toString(cfe), ServerFaultCode.ServerDeserialisationFailure, cfe.getServerFaultCode());
        }
    }

    @DataProvider(name="TransportType")
    public Object[][] clients() {
        return ClientTestsHelper.clientsToTest();
    }

    private String toString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
