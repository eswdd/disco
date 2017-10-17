/*
 * Copyright 2014, The Sporting Exchange Limited
 * Copyright 2015, Simon Matić Langford
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

package uk.co.exemel.disco.client;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.core.api.ev.TimeConstraints;
import uk.co.exemel.disco.marshalling.api.databinding.Marshaller;
import uk.co.exemel.disco.util.RequestUUIDImpl;
import uk.co.exemel.disco.util.UUIDGeneratorImpl;
import org.eclipse.jetty.client.api.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Date: 30/01/2013
 * Time: 15:14
 */
public class JettyDiscoRequestFactoryTest {


    @Mock
    private ClientCallContext mockContext;
    @Mock
    private Message mockMessage;
    @Mock
    private Marshaller mockMarshaller;
    @Mock
    private GeoLocationDetails mockGeoLocation;
    @Mock
    private TimeConstraints mockTimeConstraints;

    private JettyDiscoRequestFactory factory = new JettyDiscoRequestFactory(new HttpContextEmitter<Request>(new DefaultGeoLocationSerializer(), "X-REQUEST-UUID", "X-REQUEST-UUID-PARENTS"));

    private String uri = "http://Some.uri";
    private String contentType = "application/X-my-type";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Answer<Void> postAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                ByteArrayOutputStream os = (ByteArrayOutputStream) invocationOnMock.getArguments()[0];

                os.write("some post data".getBytes());
                return null;
            }
        };

        when(mockMessage.getHeaderMap()).thenReturn(Collections.<String, Object>emptyMap());
        when(mockMessage.getRequestBodyMap()).thenReturn(Collections.<String, Object>singletonMap("key", "value"));
        doAnswer(postAnswer).when(mockMarshaller).marshall(any(ByteArrayOutputStream.class), anyObject(),
                anyString(), eq(true));
        RequestUUIDImpl.setGenerator(new UUIDGeneratorImpl());
    }

    /*
    @Test
    public void shouldCreateGetRequest() {
        DiscoHttpExchange httpExchange = factory.create(uri, "GET", mockMessage, mockMarshaller, contentType,
                mockContext);

        httpExchange.getRequestFields();
        assertEquals("GET", httpExchange.getMethod());
        assertEquals(uri, httpExchange.getURL());
        assertEquals(3, httpExchange.getRequestFields().size());
    }

    @Test
    public void shouldCreatePostRequest() throws Exception {
        DiscoHttpExchange httpExchange = factory.create(uri, "POST", mockMessage, mockMarshaller, contentType,
                mockContext);

        httpExchange.getRequestFields();
        assertEquals("POST", httpExchange.getMethod());
        assertEquals(uri, httpExchange.getURL());
        assertEquals(4, httpExchange.getRequestFields().size());
        assertEquals("some post data", IOUtils.toString(httpExchange.getRequestContentSource()));
    }
*/
    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotCreateUnknownMethod() {
        factory.create(uri, "TRACE", mockMessage, mockMarshaller, contentType, mockContext, mockTimeConstraints);
    }

}


