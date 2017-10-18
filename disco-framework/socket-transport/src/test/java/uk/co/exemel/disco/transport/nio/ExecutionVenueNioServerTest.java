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

package uk.co.exemel.disco.transport.nio;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.api.geolocation.GeoLocationDetails;
import uk.co.exemel.disco.api.security.IdentityChain;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.core.api.OperationBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceVersion;
import uk.co.exemel.disco.core.api.ev.*;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.security.IdentityResolverFactory;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.core.api.transcription.ParameterType;
import uk.co.exemel.disco.core.impl.DefaultTimeConstraints;
import uk.co.exemel.disco.core.impl.security.CommonNameCertInfoExtractor;
import uk.co.exemel.disco.core.impl.tracing.CompoundTracer;
import uk.co.exemel.disco.core.impl.transports.TransportRegistryImpl;
import uk.co.exemel.disco.netutil.nio.marshalling.DefaultExecutionContextResolverFactory;
import uk.co.exemel.disco.transport.api.RequestTimeResolver;
import uk.co.exemel.disco.transport.impl.DehydratedExecutionContextResolutionImpl;
import uk.co.exemel.disco.netutil.nio.marshalling.SocketRMIMarshaller;
import uk.co.exemel.disco.netutil.nio.DiscoProtocol;
import uk.co.exemel.disco.netutil.nio.NioLogger;
import uk.co.exemel.disco.netutil.nio.TlsNioConfig;
import uk.co.exemel.disco.netutil.nio.message.*;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectInput;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectOutput;
import uk.co.exemel.disco.netutil.nio.hessian.HessianObjectIOFactory;
import uk.co.exemel.disco.transport.api.protocol.socket.InvocationRequest;
import uk.co.exemel.disco.transport.api.protocol.socket.InvocationResponse;
import uk.co.exemel.disco.transport.api.protocol.socket.SocketBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.socket.SocketOperationBindingDescriptor;
import uk.co.exemel.disco.transport.socket.SocketTransportCommandProcessor;
import uk.co.exemel.disco.util.RequestUUIDImpl;
import uk.co.exemel.disco.util.UUIDGeneratorImpl;
import uk.co.exemel.disco.util.geolocation.GeoIPLocator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static uk.co.exemel.disco.netutil.nio.message.ProtocolMessage.ProtocolMessageType;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(value = Parameterized.class)
public class ExecutionVenueNioServerTest {

    private static class ByteArrayWrapper {
        private byte[] array;

        private ByteArrayWrapper(byte[] array) {
            this.array = array;
        }

        public byte[] getArray() {
            return array;
        }

        @Override
        public boolean equals(Object obj) {
            return Arrays.equals(array, ((ByteArrayWrapper) obj).array);
        }

        @Override
        public int hashCode() {
            return array != null ? Arrays.hashCode(array) : 0;
        }
    }

	@Parameters
	public static Collection<Object[]> data() {
        String[] addresses = new String[] { "127.0.0.1" /*, "::1" */};
        Set<ByteArrayWrapper> versionCombinations = new HashSet<ByteArrayWrapper>();
        addVersions(versionCombinations, new byte[] {}, DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MIN_SUPPORTED);
        for (ByteArrayWrapper b : versionCombinations) {
            System.out.println("Version combo: "+Arrays.toString(b.getArray()));
        }

        List<Object[]> ret = new ArrayList<Object[]>();
        for (String address : addresses) {
            for (ByteArrayWrapper versions : versionCombinations) {
                ret.add(new Object[] { address, versions.getArray() });
            }
        }


//		Object[][] data = new Object[][] { { "127.0.0.1", new byte[] { 1, 2 } }, { "127.0.0.1", new byte[] { 1 } }, { "127.0.0.1", new byte[] { 2 } }
//		                                   /*, { "::1", new byte[] { 1, 2 } }, { "::1", new byte[] { 1 } }, { "::1", new byte[] { 2 } } */ };
		return ret;
	}

    private static void addVersions(Set<ByteArrayWrapper> versionCombinations, byte[] prefix, byte nextVersion) {
        if (nextVersion > DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED) {
            return;
        }
        versionCombinations.add(new ByteArrayWrapper(new byte[] { nextVersion }));
        byte[] newPrefix = addToEnd(prefix, nextVersion);
        versionCombinations.add(new ByteArrayWrapper(newPrefix));
        for (byte b=(byte) (nextVersion+1); b<=DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED; b++) {
            addVersions(versionCombinations, newPrefix, b);
        }
    }

    private static byte[] addToEnd(byte[] arr, byte toAdd) {
        byte[] newArr = new byte[arr.length+1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[newArr.length-1] = toAdd;
        return newArr;
    }

    public static final String THE_ITALIAN_JOB = "you were only supposed to blow the ruddy doors off";

	private static final OperationKey KEY = new OperationKey(new ServiceVersion("v1.0"), "UnitTestService",
	        "myUnitTestMethod");

	private static final Parameter[] OP_PARAMS = new Parameter[] {
	        new Parameter("pass", new ParameterType(Boolean.class, null), true),
	        new Parameter("echoMe", new ParameterType(String.class, null), true) };

    private static final ParameterType RETURN_PARAM_TYPE = new ParameterType(String.class, null);

    private static final TimeConstraints TIME_CONSTRAINTS = DefaultTimeConstraints.NO_CONSTRAINTS;

	public static final OperationDefinition OPERATION_DEFINITION = new OperationDefinition() {
		@Override
		public OperationKey getOperationKey() {
			return KEY;
		}
		@Override
		public Parameter[] getParameters() {
			return OP_PARAMS;
		}
		@Override
		public ParameterType getReturnType() {
			return RETURN_PARAM_TYPE;
		}
	};


    private String address;
    TlsNioConfig cfg;
    private ExecutionVenueNioServer server;
    private Executor executor;
    private Tracer tracer;
    private SocketRMIMarshaller marshaller;
    private ExecutionVenue ev;
    private SocketTransportCommandProcessor cmdProcessor;
    private byte[] clientConnectVersions;

	private HessianObjectIOFactory ioFactory;


    public ExecutionVenueNioServerTest(Object address, Object clientConnectVersions) {
        this.address = (String)address;
        this.clientConnectVersions = (byte[]) clientConnectVersions;
    }

    @BeforeClass
    public static void setupStatic() {
        RequestUUIDImpl.setGenerator(new UUIDGeneratorImpl());
    }

    @Before
    public void startDummyEchoSocketServer() throws IOException {

    	ioFactory = new HessianObjectIOFactory(false);

        tracer = new CompoundTracer();

		cfg = new TlsNioConfig();
        final NioLogger logger = new NioLogger("ALL");
        cfg.setNioLogger(logger);

		cfg.setListenAddress(address);
		cfg.setListenPort(0);
		cfg.setReuseAddress(true);
		cfg.setTcpNoDelay(true);
        cfg.setKeepAliveInterval(Integer.MAX_VALUE);
        cfg.setKeepAliveTimeout(Integer.MAX_VALUE);

		server = new ExecutionVenueNioServer();
		server.setNioConfig(cfg);


        cmdProcessor = new SocketTransportCommandProcessor();
        cmdProcessor.setIdentityResolverFactory(new IdentityResolverFactory());

        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                Thread t = new Thread(command);
                t.start();
            }
        };

        DehydratedExecutionContextResolutionImpl contextResolution = new DehydratedExecutionContextResolutionImpl();
        contextResolution.registerFactory(new DefaultExecutionContextResolverFactory(mock(GeoIPLocator.class), mock(RequestTimeResolver.class)));
        contextResolution.init(false);
        marshaller = new SocketRMIMarshaller(new CommonNameCertInfoExtractor(), contextResolution);
        IdentityResolverFactory identityResolverFactory = new IdentityResolverFactory();
        identityResolverFactory.setIdentityResolver(mock(IdentityResolver.class));


        ev = new ExecutionVenue() {
            @Override
            public void registerOperation(String ns, OperationDefinition def, Executable executable, ExecutionTimingRecorder recorder, long maxExecutionTime) {
            }

            @Override
            public OperationDefinition getOperationDefinition(OperationKey key) {
                return OPERATION_DEFINITION;
            }

            @Override
            public Set<OperationKey> getOperationKeys() {
                return null;
            }

            @Override
            public void execute(ExecutionContext ctx, OperationKey key, Object[] args, ExecutionObserver observer, TimeConstraints clientExpiryTime) {
                if ((Boolean)args[0]) {
                    observer.onResult(new ExecutionResult(args[1]));
                } else {
                    observer.onResult(new ExecutionResult(new DiscoServiceException(ServerFaultCode.FrameworkError, THE_ITALIAN_JOB)));
                }
            }

            @Override
            public void execute(final ExecutionContext ctx, final OperationKey key, final Object[] args, final ExecutionObserver observer, final Executor executor, final TimeConstraints clientExpiryTime) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        execute(ctx, key, args, observer, clientExpiryTime);
                    }
                });
            }

            @Override
            public void setPreProcessors(List<ExecutionPreProcessor> preProcessorList) {
            }

            @Override
            public void setPostProcessors(List<ExecutionPostProcessor> preProcessorList) {
            }
        };

        cmdProcessor.setExecutor(executor);
        cmdProcessor.setMarshaller(marshaller);
        cmdProcessor.setExecutionVenue(ev);
        cmdProcessor.setTracer(tracer);
        ServiceBindingDescriptor desc = new SocketBindingDescriptor() {
            @Override
            public OperationBindingDescriptor[] getOperationBindings() {
                return new OperationBindingDescriptor[] { new SocketOperationBindingDescriptor(KEY) };
            }

            @Override
            public ServiceVersion getServiceVersion() {
                return KEY.getVersion();
            }

            @Override
            public String getServiceName() {
                return KEY.getServiceName();
            }

            @Override
            public Protocol getServiceProtocol() {
                return Protocol.SOCKET;
            }
        };
        cmdProcessor.bind(desc);
        cmdProcessor.onDiscoStart();


        ExecutionVenueServerHandler handler = new ExecutionVenueServerHandler(new NioLogger("NONE"), cmdProcessor, new HessianObjectIOFactory(false));
        server.setServerHandler(handler);
        server.setSocketAcceptorProcessors(1);
        server.setServerExecutor(Executors.newCachedThreadPool());
        server.setTransportRegistry(new TransportRegistryImpl());
        server.start();
        server.setHealthState(true);
        final IoSessionManager sessionManager = new IoSessionManager();
        sessionManager.setNioLogger(logger);
        sessionManager.setMaxTimeToWaitForRequestCompletion(5000);
        server.setSessionManager(sessionManager);
    }

    @After
    public void stopDummyEchoSocketServer() throws IOException {
        server.stop();
    }



    @Test
    public void testSocketRequest()  throws Exception {
        String expectedResult = "sweet";
        InvocationResponse response = makeSocketRequest(123, true, expectedResult);

        if (!response.isSuccess()) {
            response.getException().printStackTrace();
        }
        assertTrue(response.isSuccess());
        assertEquals(expectedResult, response.getResult());
    }


    @Test
    public void testSocketRequestThrowingException() throws IOException {
        InvocationResponse response = makeSocketRequest(2, false, "");

        assertFalse(response.isSuccess());
        DiscoException exception = response.getException();

        assertNotNull(exception);
        assertEquals("Server fault received from remote server: FrameworkError(DSC-0002)", exception.getMessage());
        assertEquals(THE_ITALIAN_JOB, exception.getCause().getMessage());
    }

    private Object readMessageFromInputStream(InputStream stream, byte communicationVersion) throws IOException {

        DataInputStream dis = new DataInputStream(stream);
        int messageLen = dis.readInt();

        //Read the message type
        ProtocolMessageType pm = ProtocolMessageType.getMessageByMessageType(dis.readByte());

        switch (pm) {
            case  CONNECT:
                int len = dis.readInt();
                byte[] bytes = new byte[len];
                dis.read(bytes);
                return new ConnectMessage(bytes);
            case REJECT:
                RejectMessageReason reason = RejectMessageReason.getByReasonCode(dis.readByte());
                byte versionCount = dis.readByte();
                byte[] versions = new byte[versionCount];
                return new RejectMessage(reason, versions);
            case ACCEPT:
                byte acceptedVersion = dis.readByte();
                return new AcceptMessage(acceptedVersion);
            case MESSAGE_RESPONSE:
            case MESSAGE: // used for v1 clients
                byte[] messageBody2 = new byte[messageLen - 9];
                long correlationId2 = dis.readLong();
                dis.read(messageBody2);
                return new ResponseMessage(correlationId2, messageBody2);
        }

        return null;
    }

    private void writeMessageToOutputStream(Object message, OutputStream stream, byte communicationVersion) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream s = new DataOutputStream(baos);

        if (message instanceof RequestMessage) {
            RequestMessage messageBody = (RequestMessage) message;
            s.writeInt(messageBody.getPayload().length + 9);
            if (communicationVersion == DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_CLIENT_ONLY_RPC) {
                s.writeByte(ProtocolMessageType.MESSAGE.getMessageType());
            }
            else {
                s.writeByte(ProtocolMessageType.MESSAGE_REQUEST.getMessageType());
            }
            s.writeLong(messageBody.getCorrelationId());
            s.write(messageBody.getPayload());
        }
        else if (message instanceof ConnectMessage) {
            ConnectMessage cm = (ConnectMessage) message;
            s.writeInt(cm.getApplicationVersions().length+2);
            s.write(cm.getProtocolMessageType().getMessageType());
            s.write((byte) cm.getApplicationVersions().length);
            s.write(cm.getApplicationVersions());
        }
        s.flush();
        stream.write(baos.toByteArray());
        stream.flush();

    }

    @Test
    public void testBadHandshake() throws IOException {
        Socket connectedClient = new Socket(server.getBoundAddress(), server.getBoundPort());
        OutputStream output = connectedClient.getOutputStream();
        InputStream input = connectedClient.getInputStream();

        byte communicationVersion = DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MAX_SUPPORTED + 1;

        //We, a nonsense client, only support version 3 of the protocol
        writeMessageToOutputStream(new ConnectMessage(new byte[] {communicationVersion } ), output, communicationVersion);
        ProtocolMessage message = (ProtocolMessage) readMessageFromInputStream(input, communicationVersion);

        assertEquals("Incorrect application protocol version was accepted", ProtocolMessageType.REJECT, message.getProtocolMessageType());
    }

    private InvocationResponse makeSocketRequest(long correlationId, boolean pass, String echoMe) throws IOException {
        Socket connectedClient = new Socket(server.getBoundAddress(), server.getBoundPort());
        OutputStream output = connectedClient.getOutputStream();
        InputStream input = connectedClient.getInputStream();

        byte communicationVersion = DiscoProtocol.TRANSPORT_PROTOCOL_VERSION_MIN_SUPPORTED; // handshake is set in stone

        //start with handshake
        writeMessageToOutputStream(new ConnectMessage(clientConnectVersions ), output, communicationVersion);
        ProtocolMessage message = (ProtocolMessage) readMessageFromInputStream(input, communicationVersion);

        assertEquals("Handshake was incorrect", ProtocolMessageType.ACCEPT, message.getProtocolMessageType());
        communicationVersion = ((AcceptMessage) message).getAcceptedVersion();

        //Now on to the message providing we handshook correctly

        //Construct the byte stream to be sent, starts with correlation id, then the marshalled request
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiscoObjectOutput out = ioFactory.newDiscoObjectOutput(baos, communicationVersion);

        Object[] args = { pass, echoMe};

        marshaller.writeInvocationRequest(createRequest(args), out,null, null,communicationVersion);
        out.flush();

        final byte[] bytes = baos.toByteArray();

        writeMessageToOutputStream(new RequestMessage(correlationId, bytes), output, communicationVersion);

        ResponseMessage response = (ResponseMessage) readMessageFromInputStream(input, communicationVersion);
        ByteArrayInputStream bais = new ByteArrayInputStream(response.getPayload());
        DiscoObjectInput dis = ioFactory.newDiscoObjectInput(bais, communicationVersion);

		assertEquals(correlationId, response.getCorrelationId());
		return marshaller.readInvocationResponse(RETURN_PARAM_TYPE, dis);
    }



    public InvocationRequest createRequest(final Object[] args) {
        return new InvocationRequest() {
            @Override
            public Object[] getArgs() {
                return args;
            }

            @Override
            public ExecutionContext getExecutionContext() {
                return new ExecutionContext() {
                    @Override
                    public GeoLocationDetails getLocation() {
                        return new GeoLocationDetails() {
                            @Override
                            public String getRemoteAddr() {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }

                            @Override
                            public String getInferredCountry() {
                                return null;
                            }

                            @Override
                            public List<String> getResolvedAddresses() {
                                return Collections.singletonList("5.1.8.6");
                            }

                            @Override
                            public String getCountry() {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }

                            @Override
                            public boolean isLowConfidenceGeoLocation() {
                                return false;  //To change body of implemented methods use File | Settings | File Templates.
                            }

                            @Override
                            public String getLocation() {
                                return null;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        };
                    }

                    @Override
                    public IdentityChain getIdentity() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public RequestUUID getRequestUUID() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public Date getReceivedTime() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public Date getRequestTime() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public boolean traceLoggingEnabled() {
                        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
            }

            @Override
            public OperationKey getOperationKey() {
                return KEY;
            }

            @Override
            public Parameter[] getParameters() {
                return OP_PARAMS;
            }

            @Override
            public TimeConstraints getTimeConstraints() {
                return TIME_CONSTRAINTS;
            }
        };
    }
}
