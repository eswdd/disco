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

package uk.co.exemel.disco.core.impl.ev;

import static org.mockito.Mockito.*;

import uk.co.exemel.disco.api.Validatable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.exemel.disco.core.api.ev.InterceptorResult;
import uk.co.exemel.disco.core.api.ev.InterceptorState;
import uk.co.exemel.disco.core.api.ev.OperationDefinition;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.exception.DiscoValidationException;
import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.core.api.transcription.ParameterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MandatoryCheckInterceptorTest {
    private class MyComplexObject implements Validatable {
        private boolean good = false;

        public MyComplexObject(boolean valid) {
            this.good = valid;
        }


        @Override
        public void validateMandatory() {
            if (!good) {
                throw new IllegalArgumentException("Mandatory field not set");
            }
        }
    };



	private MandatoryCheckInterceptor interceptor;
	private OperationKey mockOperationKey1 = mock(OperationKey.class);
    private OperationKey mockOperationKey2 = mock(OperationKey.class);

	@Before
	public void setup() {
		BaseExecutionVenue mockEV = mock(BaseExecutionVenue.class);

		OperationDefinition mockOperationDefinition1 = mock(OperationDefinition.class);
		Parameter[] parameterArray1 = new Parameter[] {
				new Parameter("1", new ParameterType(String.class,null), true),
				new Parameter("2", new ParameterType(String.class,null), true),
		};
		when(mockOperationDefinition1.getParameters()).thenReturn(parameterArray1);
		when(mockEV.getOperationDefinition(mockOperationKey1)).thenReturn(mockOperationDefinition1);





        OperationDefinition mockOperationDefinition2 = mock(OperationDefinition.class);
        Parameter[] parameterArray2 = new Parameter[] {
                new Parameter("1", new ParameterType(List.class, new ParameterType[] { new ParameterType(MyComplexObject.class, null) }), true),
                new Parameter("2", new ParameterType(Map.class, new ParameterType[] { new ParameterType(String.class, null), new ParameterType(MyComplexObject.class, null) }), false)
        };
        when(mockOperationDefinition2.getParameters()).thenReturn(parameterArray2);
        when(mockEV.getOperationDefinition(mockOperationKey2)).thenReturn(mockOperationDefinition2);

		interceptor = new MandatoryCheckInterceptor(mockEV);
	}

	@Test
	public void testInterceptorWithMissingNullArgument() {
		InterceptorResult result = interceptor.invoke(null, mockOperationKey1, new Object[] {null} );
		assertNotNull(result);
		assertEquals(InterceptorState.FORCE_ON_EXCEPTION, result.getState());
		assertTrue(result.getResult() instanceof DiscoValidationException);
	}

	@Test
	public void testInterceptorWithCorrectArguments() {
		InterceptorResult result = interceptor.invoke(null, mockOperationKey1, new Object[] {"1", "2" } );
		assertEquals(InterceptorState.CONTINUE, result.getState());
	}

	@Test
	public void testInterceptorWithMissingMandatory() {
		InterceptorResult result = interceptor.invoke(null, mockOperationKey1, new Object[] {null, "2" } );
		assertNotNull(result);
		assertEquals(InterceptorState.FORCE_ON_EXCEPTION, result.getState());
		assertTrue(result.getResult() instanceof DiscoValidationException);
	}

    @Test
    public void testInterceptorWithMissingMandatoryAttributesOfCollectionParam() {
        List<MyComplexObject> l = new ArrayList<MyComplexObject>();
        l.add(new MyComplexObject(false));

        Map<String,MyComplexObject> m = new HashMap<String, MyComplexObject>();
        m.put("bob", new MyComplexObject(true));

        InterceptorResult result = interceptor.invoke(null, mockOperationKey2, new Object[] { l, m } );
        assertNotNull(result);
        assertEquals(InterceptorState.FORCE_ON_EXCEPTION, result.getState());
        assertTrue(result.getResult() instanceof DiscoValidationException);
    }

    @Test
    public void testInterceptorWithMissingMandatoryAttributesOfCollectionParam2() {
        List<MyComplexObject> l = new ArrayList<MyComplexObject>();
        l.add(new MyComplexObject(false));

        InterceptorResult result = interceptor.invoke(null, mockOperationKey2, new Object[] { l, null } );
        assertNotNull(result);
        assertEquals(InterceptorState.FORCE_ON_EXCEPTION, result.getState());
        assertTrue(result.getResult() instanceof DiscoValidationException);
    }

    @Test
    public void testInterceptorWithCollections() {
        List<MyComplexObject> l = new ArrayList<MyComplexObject>();
        l.add(new MyComplexObject(true));

        Map<String,MyComplexObject> m = new HashMap<String, MyComplexObject>();
        m.put("bob", new MyComplexObject(true));

        InterceptorResult result = interceptor.invoke(null, mockOperationKey2, new Object[] { l, m } );
        assertEquals(InterceptorState.CONTINUE, result.getState());
        assertNull(result.getResult());
    }

}
