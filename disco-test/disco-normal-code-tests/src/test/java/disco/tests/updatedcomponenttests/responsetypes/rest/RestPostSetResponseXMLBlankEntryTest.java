/*
 * Copyright 2013, The Sporting Exchange Limited
 * Copyright 2014, Simon Matić Langford
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

// Originally from UpdatedComponentTests/ResponseTypes/REST/Rest_Post_SetResponse_XML_BlankEntry.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.responsetypes.rest;

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
 * Ensure that when a Rest (XML) Post operation is performed against Disco, passing a Set with a blank entry in the post body, it is correctly de-serialized, processed, returned the correct response.
 */
public class RestPostSetResponseXMLBlankEntryTest {
    @Test
    public void doTest() throws Exception {

        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean1 = discoManager1.getNewHttpCallBean();
        discoManager1 = discoManager1;


        getNewHttpCallBean1.setOperationName("TestSimpleSetGet", "simpleSetGet");

        getNewHttpCallBean1.setServiceName("baseline", "discoBaseline");

        getNewHttpCallBean1.setVersion("v2");

        getNewHttpCallBean1.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<inputSet><String>bbb string</String><String/><String>ddd string</String><String>ccc string</String></inputSet>".getBytes())));


        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());

        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.XML);

        discoManager1.makeRestDiscoHTTPCall(getNewHttpCallBean1, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTXML, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);

        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers3.getXMLObjectFromString("<TestSimpleSetGetResponse><String>bbb string</String><String/><String>ddd string</String><String>ccc string</String></TestSimpleSetGetResponse>");

        JSONHelpers jSONHelpers4 = new JSONHelpers();
        JSONObject createAsJSONObject11 = jSONHelpers4.createAsJSONObject(new JSONObject("{\"response\": [\"bbb string\",\"\",\"ddd string\",\"ccc string\"]}"));

        HttpResponseBean response5 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(createAsDocument10, (Document)response5.getResponseObject(), "/TestSimpleSetGetResponse");
        AssertionUtils.multiAssertEquals((int) 200, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response5.getHttpStatusText());

        HttpResponseBean response6 = getNewHttpCallBean1.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(createAsJSONObject11, (JSONObject)response6.getResponseObject(), "/response");
        AssertionUtils.multiAssertEquals((int) 200, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response6.getHttpStatusText());

        // generalHelpers.pauseTest(500L);


        discoManager1.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp7, new RequestLogRequirement("2.8", "testSimpleSetGet"),new RequestLogRequirement("2.8", "testSimpleSetGet") );
    }

}
