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

package uk.co.exemel.disco.tests.updatedcomponenttests.connected;

import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Map;

import static org.testng.AssertJUnit.*;


/**
 *
 */
public class ConnectedObjectsTest {

    @Test
    public void inProcess() throws Exception {
        doConnectedObjectTest("IN_PROCESS");
    }

    @Test
    public void socket() throws Exception {
        doConnectedObjectTest("SOCKET");
    }

    private void doConnectedObjectTest(String protocol) throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager.getNewHttpCallBean();

        callBean.setOperationName("testConnectedObjects");
        callBean.setServiceName("baseline", "discoBaseline");
        callBean.setVersion("v2");
        Map<String, String> params = new HashMap<String, String>();
        params.put("protocol", protocol);
        callBean.setQueryParams(params);


        discoManager.makeRestDiscoHTTPCall(callBean, DiscoMessageProtocolRequestTypeEnum.RESTXML, DiscoMessageContentTypeEnum.XML);

        HttpResponseBean response = callBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        Document responseDoc = (Document) response.getResponseObject();

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList testResults = (NodeList) xpath.evaluate("//TestResult", responseDoc, XPathConstants.NODESET);
        for (int i=0; i<testResults.getLength(); i++) {
            Element testResult = (Element) testResults.item(i);
            boolean success = "true".equals(xpath.evaluate("success/text()", testResult));
            if (!success) {
                fail("Fail text: " + xpath.evaluate("failText/text()", testResult));
            }
            assertTrue(xpath.evaluate("description/text()", testResult), success);
        }

        assertEquals("Overall state", "true", xpath.evaluate("//TestResults/success/text()", responseDoc));
    }
}
