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

package uk.co.exemel.disco.tests.updatedcomponenttests.uuid;

import uk.co.exemel.testing.utils.disco.DiscoBaseline2_8TestingInvoker;
import uk.co.exemel.testing.utils.disco.manager.LogTailer;
import uk.co.exemel.testing.utils.disco.manager.RequestLogTailer;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SoapUuidTest {

    @Test
    public void noParentUuid() {
        LogTailer.LogLine[] requestLogEntries =
                DiscoBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet")
                .setSoapBody("<TestSimpleGetRequest><message>message</message></TestSimpleGetRequest>")
                .makeSoapCall()
                .getRequestLogEntries();
        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertFalse(uuid0.contains(":"));
    }

    // the current soap code doesn't allow us to set http headers - a bit useless, so this don't work until then
    @Test
    public void withOldUuid() {
        LogTailer.LogLine[] requestLogEntries =
                DiscoBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet")
                .setSoapBody("<TestSimpleGetRequest><message>message</message></TestSimpleGetRequest>")
                .addHeaderParam("X-UUID", "localhost-abc123-00001")
                .makeSoapCall()
                .getRequestLogEntries();
        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertEquals(uuid0,"localhost-abc123-00001");
    }

    // the current soap code doesn't allow us to set http headers - a bit useless, so this don't work until then
    @Test
    public void withParentUuid() {
        LogTailer.LogLine[] requestLogEntries =
                DiscoBaseline2_8TestingInvoker.create()
                .setOperation("testSimpleGet")
                .setSoapBody("<TestSimpleGetRequest><message>message</message></TestSimpleGetRequest>")
                .addHeaderParam("X-UUID", "localhost-abc123-00001")
                .addHeaderParam("X-UUID-Parents", "root-abcdef123-00001:prev-abcdef123-00001")
                .makeSoapCall()
                .getRequestLogEntries();

        String expectedUuid = "root-abcdef123-00001:prev-abcdef123-00001:localhost-abc123-00001";

        String uuid0 = (String) requestLogEntries[0].getFields().get(RequestLogTailer.REQUEST_UUID);
        Assert.assertEquals(uuid0,expectedUuid);
    }

}
