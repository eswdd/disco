/*
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

package uk.co.exemel.disco.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.co.exemel.disco.core.api.exception.DiscoValidationException;
import org.junit.Before;
import org.junit.Test;

import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.transport.api.protocol.http.rescript.RescriptOperationBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.http.rescript.RescriptParamBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.http.rescript.RescriptParamBindingDescriptor.ParamSource;

public class MessageBuilderTest {
	MessageBuilder mb;
	Parameter[] mockParameters;
	Parameter mockParameter;
	RescriptParamBindingDescriptor mockParamBindingDescriptor;
	RescriptOperationBindingDescriptor mockOperationBinding;

	@Before
	public void setup() {
		mb = new MessageBuilder();
		mockParameters = new Parameter[1];
		mockParameter = mock(Parameter.class);
		when(mockParameter.isMandatory()).thenReturn(false);
		when(mockParameter.getName()).thenReturn("PARAMETER");
		mockParameters[0] = mockParameter;
		mockParamBindingDescriptor = mock(RescriptParamBindingDescriptor.class);
		mockOperationBinding = mock(RescriptOperationBindingDescriptor.class);
	}

	@Test
	public void testQueryParmConstruction() {

		when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.QUERY);
		when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

		Message message = mb.build(new Object[] {"test"}, mockParameters, mockOperationBinding);
		assertNotNull(message);
		assertEquals(0, message.getHeaderMap().size());
		assertEquals(0, message.getRequestBodyMap().size());
		assertEquals(1, message.getQueryParmMap().size());
	}

    @Test
    public void testQueryParmConstructionWithNullsNonMandatoryParams() {

        when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.QUERY);
        when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

        Message message = mb.build(new Object[0], mockParameters, mockOperationBinding);
        assertNotNull(message);
        assertEquals(0, message.getHeaderMap().size());
        assertEquals(0, message.getRequestBodyMap().size());
        assertEquals(0, message.getQueryParmMap().size());
    }


	@Test
	public void testBodyConstruction() {
		when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.BODY);
		when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

		Message message = mb.build(new Object[] {"test"}, mockParameters, mockOperationBinding);
		assertNotNull(message);
		assertEquals(0, message.getHeaderMap().size());
		assertEquals(1, message.getRequestBodyMap().size());
		assertEquals(0, message.getQueryParmMap().size());
	}

    @Test
    public void testBodyConstructionWithNullsInNonMandatoryParams() {
        when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.BODY);
        when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

        Message message = mb.build(new Object[0], mockParameters, mockOperationBinding);
        assertNotNull(message);
        assertEquals(0, message.getHeaderMap().size());
        assertEquals(0, message.getQueryParmMap().size());
        assertEquals(1, message.getRequestBodyMap().size());
        assertEquals(1, message.getRequestBodyMap().keySet().size());
        assertEquals(null, message.getRequestBodyMap().entrySet().iterator().next().getValue());
    }


	@Test
	public void testHeaderConstruction() {

		when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.HEADER);
		when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

		Message message = mb.build(new Object[] {"test"}, mockParameters, mockOperationBinding);
		assertNotNull(message);
		assertEquals(1, message.getHeaderMap().size());
		assertEquals(0, message.getRequestBodyMap().size());
		assertEquals(0, message.getQueryParmMap().size());
	}

    @Test
    public void testHeaderConstructionWithNullNonMandatoryArg() {

        when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.HEADER);
        when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

        Message message = mb.build(new Object[0], mockParameters, mockOperationBinding);
        assertNotNull(message);
        assertEquals(0, message.getHeaderMap().size());
        assertEquals(0, message.getRequestBodyMap().size());
        assertEquals(0, message.getQueryParmMap().size());
    }


	@Test
	public void testMandatoryCheck() {
		when(mockParameter.isMandatory()).thenReturn(true);

		when(mockParamBindingDescriptor.getSource()).thenReturn(ParamSource.HEADER);
		when(mockOperationBinding.getHttpParamBindingDescriptor("PARAMETER")).thenReturn(mockParamBindingDescriptor);

		try {
			mb.build(new Object[] {null}, mockParameters, mockOperationBinding);
			fail();

		} catch (DiscoValidationException a) {
			//pass
		}
	}

}
