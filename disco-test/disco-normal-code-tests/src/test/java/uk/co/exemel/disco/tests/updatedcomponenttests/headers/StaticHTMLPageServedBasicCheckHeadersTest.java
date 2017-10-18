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

// Originally from UpdatedComponentTests/Headers/StaticHTMLPageServed_Basic_Check_Headers.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.headers;

import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.misc.DocumentHelpers;
import uk.co.exemel.testing.utils.disco.misc.HttpService;
import uk.co.exemel.testing.utils.disco.misc.HttptestPageBean;
import uk.co.exemel.testing.utils.disco.misc.InputStreamHelpers;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.InputStream;

/**
 * Ensure that disco correctly sets the cache headers and monitors for static content requests for a basic HTML resource
 */
public class StaticHTMLPageServedBasicCheckHeadersTest {
    @Test
    public void doTest() throws Exception {
        // Create the HttpCallBean
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean httpCallBeanBaseline = discoManager1.getNewHttpCallBean();
        DiscoManager discoManagerBaseline = discoManager1;
        // Get the disco logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "discoBaseline");
        
        httpCallBeanBaseline.setVersion("v2");
        // Get Expected HTML Response as Input Stream from the given file
        InputStream inputStream = InputStreamHelpers.getInputStreamForResource("static-html/foo.html");
        // Transfrom the input stream into a Document (XML) for assertion
        DocumentHelpers documentHelpers3 = new DocumentHelpers();
        Document expectedDocResponse = documentHelpers3.parseInputStreamToDocument(inputStream, false, false, true, "auto");
        // Load the Static Page shipped from Disco
        HttptestPageBean loadedPage = HttpService.loadPage("http://localhost:8080/static-html/foo.html");
        // Get the loaded page as a document
        Document actualDocument = HttpService.getPageDom(loadedPage);
        AssertionUtils.multiAssertEquals(expectedDocResponse, actualDocument);
        // Check the WebResponse content type header
        AssertionUtils.multiAssertEquals("text/html", loadedPage.getWebResponseHeaderField("CONTENT-TYPE"));
        // Check the WebResponse cache control header
        AssertionUtils.multiAssertEquals("private, max-age=2592000", loadedPage.getWebResponseHeaderField("CACHE-CONTROL"));
    }

}
