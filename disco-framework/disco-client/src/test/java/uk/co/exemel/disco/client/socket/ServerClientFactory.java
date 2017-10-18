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

package uk.co.exemel.disco.client.socket;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import uk.co.exemel.disco.api.export.Protocol;
import uk.co.exemel.disco.client.api.CompoundContextEmitter;
import uk.co.exemel.disco.client.socket.resolver.DNSBasedAddressResolver;
import uk.co.exemel.disco.core.api.OperationBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceBindingDescriptor;
import uk.co.exemel.disco.core.api.ServiceVersion;
import uk.co.exemel.disco.core.api.ev.ExecutionTimingRecorder;
import uk.co.exemel.disco.core.api.ev.TimeConstraints;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.impl.security.CommonNameCertInfoExtractor;
import uk.co.exemel.disco.core.impl.tracing.CompoundTracer;
import uk.co.exemel.disco.netutil.nio.marshalling.DefaultExecutionContextResolverFactory;
import uk.co.exemel.disco.transport.api.DehydratedExecutionContextResolution;
import uk.co.exemel.disco.transport.api.RequestTimeResolver;
import uk.co.exemel.disco.transport.impl.DehydratedExecutionContextResolutionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.exemel.disco.netutil.nio.DiscoProtocol;
import uk.co.exemel.disco.netutil.nio.NioLogger;
import uk.co.exemel.disco.netutil.nio.TlsNioConfig;
import uk.co.exemel.disco.netutil.nio.marshalling.SocketRMIMarshaller;
import uk.co.exemel.disco.transport.api.protocol.socket.SocketBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.socket.SocketOperationBindingDescriptor;
import uk.co.exemel.disco.transport.nio.IoSessionManager;
import uk.co.exemel.disco.transport.socket.SocketTransportCommandProcessor;
import uk.co.exemel.disco.util.geolocation.GeoIPLocator;
import uk.co.exemel.disco.util.JMXReportingThreadPoolExecutor;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.security.IdentityResolver;
import uk.co.exemel.disco.core.api.ev.Executable;
import uk.co.exemel.disco.core.api.ev.ExecutionObserver;
import uk.co.exemel.disco.core.api.ev.ExecutionPostProcessor;
import uk.co.exemel.disco.core.api.ev.ExecutionPreProcessor;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.core.api.ev.ExecutionVenue;
import uk.co.exemel.disco.core.api.ev.OperationDefinition;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.security.IdentityResolverFactory;
import uk.co.exemel.disco.netutil.nio.NioConfig;
import uk.co.exemel.disco.netutil.nio.hessian.HessianObjectIOFactory;
import uk.co.exemel.disco.transport.nio.ExecutionVenueNioServer;
import uk.co.exemel.disco.transport.nio.ExecutionVenueServerHandler;

import static org.mockito.Mockito.mock;

/**
 * This class is used as a stub to facilitate NIO unit testing
 */
public class ServerClientFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(ServerClientFactory.class);

    public static final int COMMAND_STOP_SERVER = 1;
    public static final int COMMAND_SLEEP_60S = 2;
    public static final int COMMAND_ECHO_ARG2 = 100;
    public static final int COMMAND_FRAMEWORK_ERROR = 999;

	public static ExecutionVenueNioServer createServer(byte serverVersion, TlsNioConfig cfg) {
        DiscoProtocol.setMinServerProtocolVersion(serverVersion);
        DiscoProtocol.setMaxServerProtocolVersion(serverVersion);
		final ExecutionVenueNioServer server = new ExecutionVenueNioServer();
		server.setNioConfig(cfg);


	    SocketTransportCommandProcessor cmdProcessor = new SocketTransportCommandProcessor();
        cmdProcessor.setTracer(new CompoundTracer());
        cmdProcessor.setIdentityResolverFactory(new IdentityResolverFactory());

		Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                Thread t = new Thread(command);
                t.start();
            }
        };


        DehydratedExecutionContextResolutionImpl contextResolution = new DehydratedExecutionContextResolutionImpl();
        contextResolution.registerFactory(new DefaultExecutionContextResolverFactory(mock(GeoIPLocator.class),mock(RequestTimeResolver.class)));
        contextResolution.init(false);
        SocketRMIMarshaller marshaller = new SocketRMIMarshaller(new CommonNameCertInfoExtractor(), contextResolution);
        IdentityResolverFactory identityResolverFactory = new IdentityResolverFactory();
        identityResolverFactory.setIdentityResolver(mock(IdentityResolver.class));



        ExecutionVenue ev = new ExecutionVenue() {
            @Override
            public void registerOperation(String namespace, OperationDefinition def, Executable executable, ExecutionTimingRecorder recorder, long maxExecutionTime) {
            }

            @Override
            public OperationDefinition getOperationDefinition(OperationKey key) {
                return AbstractClientTest.OPERATION_DEFINITION;
            }

            @Override
            public Set<OperationKey> getOperationKeys() {
                return null;
            }

            @Override
            public void execute(ExecutionContext ctx, OperationKey key, Object[] args, ExecutionObserver observer, TimeConstraints timeConstraints) {
                switch (Integer.parseInt(args[1].toString())) {
            	case COMMAND_STOP_SERVER:
                    LOGGER.info("Stopping server");
            		server.stop();
            		break;
                case COMMAND_SLEEP_60S:
                    LOGGER.info("Sleeping for 60s");
                    try { Thread.sleep(60000L); } catch (Exception e) {}
                case COMMAND_ECHO_ARG2:
                	observer.onResult(new ExecutionResult(args[2]));
                    break;
                case COMMAND_FRAMEWORK_ERROR:
                	observer.onResult(new ExecutionResult(new DiscoServiceException(ServerFaultCode.FrameworkError, AbstractClientTest.BANG)));
                    break;
            }

            }

            public void execute(final ExecutionContext ctx, final OperationKey key, final Object[] args, final ExecutionObserver observer, Executor executor, final TimeConstraints timeConstraints) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        execute(ctx, key, args, observer, timeConstraints);
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
        ServiceBindingDescriptor desc = new SocketBindingDescriptor() {
            @Override
            public OperationBindingDescriptor[] getOperationBindings() {
                return new OperationBindingDescriptor[] { new SocketOperationBindingDescriptor(AbstractClientTest.OPERATION_DEFINITION.getOperationKey()) };
            }

            @Override
            public ServiceVersion getServiceVersion() {
                return AbstractClientTest.OPERATION_DEFINITION.getOperationKey().getVersion();
            }

            @Override
            public String getServiceName() {
                return AbstractClientTest.OPERATION_DEFINITION.getOperationKey().getServiceName();
            }

            @Override
            public Protocol getServiceProtocol() {
                return Protocol.SOCKET;
            }
        };
        cmdProcessor.bind(desc);
        cmdProcessor.onDiscoStart();


        final NioLogger nioLogger = new NioLogger("ALL");
        ExecutionVenueServerHandler handler = new ExecutionVenueServerHandler(nioLogger, cmdProcessor, new HessianObjectIOFactory(false));
        server.setServerHandler(handler);

        IoSessionManager sessionManager = new IoSessionManager();
        sessionManager.setNioLogger(nioLogger);
        sessionManager.setMaxTimeToWaitForRequestCompletion(5000);
        server.setSessionManager(sessionManager);

		return server;
	}

	public static TlsNioConfig getDefaultConfig() {
        TlsNioConfig cfg = new TlsNioConfig();
        cfg.setNioLogger(new NioLogger("ALL"));

		cfg.setReuseAddress(true);
		cfg.setTcpNoDelay(true);

		return cfg;
	}

	public static ExecutionVenueNioServer createServer(String host, int port, byte serverVersion) {
		return createServer(host, port, serverVersion, getDefaultConfig());
	}

	public static ExecutionVenueNioServer createServer(String host, int port, byte serverVersion, TlsNioConfig cfg) {
		cfg.setListenAddress(host);
		cfg.setListenPort(port);

		return createServer(serverVersion, cfg);
	}


	public static ExecutionVenueNioClient createClient (String connectionString, NioConfig cfg) {
        DehydratedExecutionContextResolution contextResolution = mock(DehydratedExecutionContextResolution.class);
        SocketRMIMarshaller marshaller = new SocketRMIMarshaller(new CommonNameCertInfoExtractor(), contextResolution);
        IdentityResolverFactory factory = new IdentityResolverFactory();
        factory.setIdentityResolver(mock(IdentityResolver.class));

        NioLogger logger = new NioLogger("ALL");
		ExecutionVenueNioClient client = new ExecutionVenueNioClient(logger,  cfg, new HessianObjectIOFactory(true), new ClientConnectedObjectManager(), null, connectionString,
                new JMXReportingThreadPoolExecutor(30, 60, 0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()), new JMXReportingThreadPoolExecutor(30, 60, 0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()),
                new DNSBasedAddressResolver(), new CompoundTracer());
        client.setMarshaller(marshaller);
        client.setContextEmitter(new CompoundContextEmitter<Map<String, String>, Object>(Collections.EMPTY_LIST));

        return client;
	}


	public static ExecutionVenueNioClient createClient (String connectionString) {
        return createClient(connectionString, getDefaultConfig());

	}

}
