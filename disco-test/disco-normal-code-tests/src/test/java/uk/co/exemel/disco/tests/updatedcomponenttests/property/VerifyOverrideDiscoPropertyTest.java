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

package uk.co.exemel.disco.tests.updatedcomponenttests.property;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class VerifyOverrideDiscoPropertyTest {
    @Test
    public void
    doTest() throws Exception {
        verifyPropertyValue("some.random.property.1", "overrides");
        verifyPropertyValue("some.random.property.2", "application");
        verifyPropertyValue("some.random.property.3", "default");
    }

    public void verifyPropertyValue(String propertyname, String propertyvalue) throws Exception {
        DiscoManager discoManager = DiscoManager.getInstance();

        HttpCallBean getNewHttpCallBean = discoManager.getNewHttpCallBean("87.248.113.14");
        getNewHttpCallBean.setOperationName("echoDiscoPropertyValue", "propertyEcho");
        getNewHttpCallBean.setServiceName("baseline", "discoBaseline");
        getNewHttpCallBean.setVersion("v2");
        Map map = new HashMap();
        map.put("propertyName", propertyname);

        getNewHttpCallBean.setQueryParams(map);
// Make the 4 REST calls to the operation
        discoManager.makeRestDiscoHTTPCalls(getNewHttpCallBean);
// Create the expected response as an XML document
        XMLHelpers xMLHelpers = new XMLHelpers();
        Document createAsDocument = xMLHelpers.createAsDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<String>" + propertyvalue + "</String>").getBytes())));
        Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes = discoManager.convertResponseToRestTypes(createAsDocument, getNewHttpCallBean);
// Check the 4 responses are as expected
        HttpResponseBean response = getNewHttpCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(convertResponseToRestTypes.get(DiscoMessageProtocolRequestTypeEnum.RESTXML), response.getResponseObject());
    }
}
