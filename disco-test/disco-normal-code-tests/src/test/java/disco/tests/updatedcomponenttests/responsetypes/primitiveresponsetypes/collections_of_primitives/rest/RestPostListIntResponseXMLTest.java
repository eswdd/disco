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

// Originally from UpdatedComponentTests/ResponseTypes/PrimitiveResponseTypes/collections_of_primitives/REST/Rest_Post_ListIntResponse_XML.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.responsetypes.primitiveresponsetypes.collections_of_primitives.rest;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

/**
 * Ensure that when a Rest (XML/JSON) Post operation is performed against Disco, passing a List in the post body, it is correctly de-serialized, processed, returned the correct response.
 */
public class RestPostListIntResponseXMLTest {
    @Test
    public void doTest() throws Exception {
        
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean("87.248.113.14");
        discoManager1 = discoManager1;
        
        
        getNewHttpCallBean1.setOperationName("I32ListSimpleTypeEcho", "i32ListEcho");
        
        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");
        
        getNewHttpCallBean1.setVersion("v2");
        
        getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<msg><Integer>1</Integer><Integer>2</Integer></msg>".getBytes())));
        

        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());
        
        discoManager1.makeRestDiscoHTTPCalls(getNewHttpCallBean1);
        
        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document createAsDocument9 = xMLHelpers3.getXMLObjectFromString("<I32ListSimpleTypeEchoResponse><Integer>1</Integer><Integer>2</Integer></I32ListSimpleTypeEchoResponse>");
        
        JSONHelpers jSONHelpers4 = new JSONHelpers();
        JSONObject createAsJSONObject10 = jSONHelpers4.createAsJSONObject(new JSONObject("{ \"response\":[1,2,]}"));
        
        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(createAsDocument9, response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response5.getHttpStatusText());
        
        HttpResponseBean response6 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(createAsJSONObject10, response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 200, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response6.getHttpStatusText());
        
        // generalHelpers.pauseTest(500L);
        
        
        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp7, new RequestLogRequirement("2.8", "i32ListSimpleTypeEcho"),new RequestLogRequirement("2.8", "i32ListSimpleTypeEcho"),new RequestLogRequirement("2.8", "i32ListSimpleTypeEcho"),new RequestLogRequirement("2.8", "i32ListSimpleTypeEcho") );
    }

}
