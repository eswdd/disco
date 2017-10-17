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

package uk.co.exemel.disco.transport.socket;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.DehydratedExecutionContext;
import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.api.security.IdentityChain;
import uk.co.exemel.disco.api.security.IdentityToken;
import uk.co.exemel.disco.core.api.OperationBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceVersion;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.core.api.ev.ExecutionVenue;
import uk.co.exemel.disco.core.api.ev.OperationDefinition;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.ev.TimeConstraints;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.security.IdentityResolverFactory;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.core.api.transcription.ParameterType;
import uk.co.exemel.disco.core.impl.DefaultTimeConstraints;
import uk.co.exemel.disco.logging.EventLoggingRegistry;
import uk.co.exemel.disco.marshalling.api.socket.RemotableMethodInvocationMarshaller;
import uk.co.exemel.disco.netutil.nio.DiscoProtocol;
import uk.co.exemel.disco.transport.api.CommandResolver;
import uk.co.exemel.disco.transport.api.CommandValidator;
import uk.co.exemel.disco.transport.api.ExecutionCommand;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectInput;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectOutput;
import uk.co.exemel.disco.netutil.nio.hessian.HessianObjectIOFactory;
import uk.co.exemel.disco.transport.api.protocol.socket.InvocationResponse;
import uk.co.exemel.disco.transport.api.protocol.socket.SocketOperationBindingDescriptor;
import uk.co.exemel.disco.transport.impl.AbstractCommandProcessor;
import uk.co.exemel.disco.util.RequestUUIDImpl;
import uk.co.exemel.disco.util.UUIDGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Required;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for @see SocketTransportCommandProcessorImpl
 */
public class SocketTransportCommandProcessorTest {
    private static final OperationKey key = new OperationKey(new ServiceVersion("v1.0"), "TestingService", "TestCall");

    private static final ParameterType returnType = new ParameterType(String.class, null);

    private static final Object[] args = { "arg1", new Integer(2), Boolean.TRUE};

    private static final long CORRELATION_ID = 9999L;

    private Date receivedTime = new Date();
    private Date requestTime = new Date();

    private final DehydratedExecutionContext ctx = new DehydratedExecutionContext() {

        @Override
        public GeoLocationDetails getLocation() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public IdentityChain getIdentity() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public RequestUUID getRequestUUID() {
            return new RequestUUIDImpl();
        }

        @Override
        public Date getReceivedTime() {
            return receivedTime;
        }

        @Override
        public Date getRequestTime() {
            return requestTime;
        }

        @Override
        public boolean traceLoggingEnabled() {
            return false;
        }

        @Override
        public List<IdentityToken> getIdentityTokens() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setIdentityChain(IdentityChain chain) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getTransportSecurityStrengthFactor() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isTransportSecure() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };


    private SocketTransportCommandProcessor commandProcessor;

    @Mock
    private Tracer tracer;

    @Mock
    private ExecutionVenue ev;

    @Mock
    private Executor executor;

    @Mock
    private RemotableMethodInvocationMarshaller marshaller;

    @Mock
    private EventLoggingRegistry eventLoggingRegistry;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        RequestUUIDImpl.setGenerator(new UUIDGeneratorImpl());

        commandProcessor = new SocketTransportCommandProcessor();
        commandProcessor.setExecutionVenue(ev);
        commandProcessor.setExecutor(executor);
        commandProcessor.setMarshaller(marshaller);
        commandProcessor.setRegistry(eventLoggingRegistry);
        commandProcessor.setIdentityResolverFactory(Mockito.mock(IdentityResolverFactory.class));
        commandProcessor.setTracer(tracer);

    }

    private class SocketTransportCommandProcessorDelegator extends AbstractCommandProcessor<SocketTransportCommand> {

        @Override
        public void bind(ServiceBindingDescriptor operation) {
            commandProcessor.bind(operation);
        }

        @Override
        protected List<CommandValidator<SocketTransportCommand>> getCommandValidators() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public CommandResolver<SocketTransportCommand> createCommandResolver(SocketTransportCommand command, Tracer tracer)  {
            CommandResolver<SocketTransportCommand> commandResolver = commandProcessor.createCommandResolver(command, tracer);
            assertNotNull(commandResolver);

            verify(ev, atLeast(1)).getOperationDefinition(eq(key));

            ExecutionContext resolvedContext = commandResolver.resolveExecutionContext();
            assertTrue(resolvedContext instanceof SocketRequestContextImpl);
            try {
                Field f = SocketRequestContextImpl.class.getDeclaredField("wrapped");
                f.setAccessible(true);
                assertEquals(ctx, f.get(resolvedContext));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

            Iterable<ExecutionCommand> iterable = commandResolver.resolveExecutionCommands();
            assertNotNull(iterable);
            Iterator<ExecutionCommand> iter = iterable.iterator();
            ExecutionCommand cmd = iter.next();
            assertNotNull(cmd);
            assertFalse(iter.hasNext());

            assertArrayEquals("arguments don't match", args, cmd.getArgs());
            assertEquals("Operation Key doesn't match", key, cmd.getOperationKey());

            try {
                final String success="success";
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DiscoObjectOutput dos = new HessianObjectIOFactory(false).newDiscoObjectOutput(bos, DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED);
                dos.flush();
                //Test onResult
                cmd.onResult(new ExecutionResult(success));
                assertArrayEquals("CorrelationId wasn't written to the output stream correctly", bos.toByteArray(), out.toByteArray());
                verify(marshaller).writeInvocationResponse(argThat(matchesSuccessResponse(success)), any(DiscoObjectOutput.class),anyByte());

                out.reset();
                //This is necessary because the mockito gubbins that records an operation cannot handle
                //to calls to the same object (marshaller in this case) so we need to stub a new one and
                //re-add it to the command processor.
                //http://mockito.googlecode.com/svn/branches/1.6/javadoc/org/mockito/Mockito.html
                marshaller = Mockito.mock(RemotableMethodInvocationMarshaller.class);
                commandProcessor.setMarshaller(marshaller);

                //Test onException
                DiscoException ex = new DiscoServiceException(ServerFaultCode.AcceptTypeNotValid, "BANG!");
                cmd.onResult(new ExecutionResult(ex));
                assertArrayEquals("CorrelationId wasn't written to the output stream correctly", bos.toByteArray(), out.toByteArray());
                verify(marshaller).writeInvocationResponse(argThat(matchesExceptionalResponse(ex)), any(DiscoObjectOutput.class),anyByte());


            } catch (IOException ex) {
                fail("Should not have thrown IOException here");
            }
            return commandResolver;
        }

        private ArgumentMatcher<InvocationResponse> matchesSuccessResponse(final Object responseValue) {
            return new ArgumentMatcher<InvocationResponse>() {
                @Override
                public boolean matches(Object argument) {
                    assertTrue(argument instanceof InvocationResponse);
                    InvocationResponse response = (InvocationResponse)argument;
                    assertTrue(response.isSuccess());
                    assertEquals(responseValue, (response.getResult()));
                    assertNull(response.getException());
                    return true;
                }
            };
        }

        private ArgumentMatcher<InvocationResponse> matchesExceptionalResponse(final Object responseValue) {
            return new ArgumentMatcher<InvocationResponse>() {
                @Override
                public boolean matches(Object argument) {
                    assertTrue(argument instanceof InvocationResponse);
                    InvocationResponse response = (InvocationResponse)argument;
                    assertFalse(response.isSuccess());
                    assertEquals(responseValue, ((InvocationResponse)argument).getException());
                    return true;
                }
            };
        }


        public RemotableMethodInvocationMarshaller getMarshaller() {
            return null;
        }

        public EventLoggingRegistry getRegistry() {
            return null;
        }

        @Required
        public void setMarshaller(RemotableMethodInvocationMarshaller marshaller) {
        }

        @Required
        public void setRegistry(EventLoggingRegistry registry) {
        }

        @Override
        public void writeErrorResponse(SocketTransportCommand command, DehydratedExecutionContext context, DiscoException e, boolean traceStarted) {
        }

        @Override
        public ExecutionVenue getExecutionVenue() {
            return null;
        }


        @Override
        public void process(SocketTransportCommand command) {
        }

        @Override
        public void setExecutor(Executor executor) {
        }

        public void onDiscoStart() {
            commandProcessor.onDiscoStart();
        }
    }

    private CommandResolver<SocketTransportCommand> createCommandResolver(TimeConstraints toReturn, Tracer tracer) throws IOException {
        SocketTransportRPCCommand command = Mockito.mock(SocketTransportRPCCommand.class);
        when(command.getOutput()).thenReturn(new HessianObjectIOFactory(false).newDiscoObjectOutput(out, DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED));
        MyIoSession session = new MyIoSession("abc");
        session.setAttribute(DiscoProtocol.PROTOCOL_VERSION_ATTR_NAME, DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED);
        when(command.getSession()).thenReturn(session);

        when(marshaller.readExecutionContext(any(DiscoObjectInput.class), any(String.class), any(X509Certificate[].class), anyInt(), anyByte())).thenReturn(ctx);
        when(marshaller.readOperationKey(any(DiscoObjectInput.class))).thenReturn(key);
        when(marshaller.readArgs(any(Parameter[].class), any(DiscoObjectInput.class))).thenReturn(args);
        when(marshaller.readTimeConstraintsIfPresent(any(DiscoObjectInput.class), anyByte())).thenReturn(toReturn);

        final OperationKey opKey = new OperationKey(new ServiceVersion(1,0), "TestingService", "TestCall");
        OperationDefinition opDef = Mockito.mock(OperationDefinition.class);
        when(opDef.getReturnType()).thenReturn(returnType);
        when(opDef.getOperationKey()).thenReturn(opKey);
        when(ev.getOperationDefinition(key)).thenReturn(opDef);

        SocketTransportCommandProcessorDelegator d = new SocketTransportCommandProcessorDelegator();
        ServiceBindingDescriptor desc = mock(ServiceBindingDescriptor.class);
        OperationBindingDescriptor[] bindingDescriptors = new OperationBindingDescriptor[1];
        SocketOperationBindingDescriptor opDesc = new SocketOperationBindingDescriptor(opKey);
        opDesc.setOperationKey(opKey);
        bindingDescriptors[0] = opDesc;
        when(desc.getOperationBindings()).thenReturn(bindingDescriptors);
        when(desc.getServiceName()).thenReturn(opKey.getServiceName());
        when(desc.getServiceVersion()).thenReturn(opKey.getVersion());
        d.bind(desc);
        d.onDiscoStart();
        return d.createCommandResolver(command, tracer);
    }

    @Test
    public void testCreateCommandResolver() throws IOException {
        createCommandResolver(DefaultTimeConstraints.NO_CONSTRAINTS, tracer);
    }

    @Test
    public void createCommandResolver_NoTimeout() throws IOException {
        // resolve the command
        CommandResolver<SocketTransportCommand> cr = createCommandResolver(DefaultTimeConstraints.NO_CONSTRAINTS, tracer);
        Iterable<ExecutionCommand> executionCommands = cr.resolveExecutionCommands();

        // check the output
        ExecutionCommand executionCommand = executionCommands.iterator().next();
        TimeConstraints constraints = executionCommand.getTimeConstraints();
        assertNull(constraints.getExpiryTime());
    }

    @Test
    public void createCommandResolver_WithTimeout() throws IOException {
        // resolve the command
        CommandResolver<SocketTransportCommand> cr = createCommandResolver(DefaultTimeConstraints.fromTimeout(10000), tracer);
        Iterable<ExecutionCommand> executionCommands = cr.resolveExecutionCommands();

        // check the output
        ExecutionCommand executionCommand = executionCommands.iterator().next();
        TimeConstraints constraints = executionCommand.getTimeConstraints();
        assertNotNull(constraints.getExpiryTime());
    }

    @Test
    public void createCommandResolver_WithTimeoutAndOldRequestTime() throws IOException {
        requestTime = new Date(System.currentTimeMillis()-10001);
        // resolve the command
        CommandResolver<SocketTransportCommand> cr = createCommandResolver(DefaultTimeConstraints.fromTimeout(10000), tracer);
        Iterable<ExecutionCommand> executionCommands = cr.resolveExecutionCommands();

        // check the output
        ExecutionCommand executionCommand = executionCommands.iterator().next();
        TimeConstraints constraints = executionCommand.getTimeConstraints();
        assertTrue(constraints.getExpiryTime() < System.currentTimeMillis());
    }

}
