/*
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

package uk.co.exemel.disco.test.socket.tester.client;

import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.client.api.CompoundContextEmitter;
import uk.co.exemel.disco.client.api.ContextEmitter;
import uk.co.exemel.disco.client.socket.ClientConnectedObjectManager;
import uk.co.exemel.disco.client.socket.ExecutionVenueNioClient;
import uk.co.exemel.disco.client.socket.jmx.ClientSocketTransportInfo;
import uk.co.exemel.disco.client.socket.resolver.DNSBasedAddressResolver;
import uk.co.exemel.disco.client.socket.resolver.NetworkAddressResolver;
import uk.co.exemel.disco.core.api.ev.OperationDefinition;
import uk.co.exemel.disco.core.api.ev.WaitingObserver;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.core.impl.DefaultTimeConstraints;
import uk.co.exemel.disco.core.impl.tracing.CompoundTracer;
import uk.co.exemel.disco.marshalling.api.socket.RemotableMethodInvocationMarshaller;
import uk.co.exemel.disco.netutil.nio.NioConfig;
import uk.co.exemel.disco.netutil.nio.NioLogger;
import uk.co.exemel.disco.netutil.nio.TlsNioConfig;
import uk.co.exemel.disco.netutil.nio.hessian.HessianObjectIOFactory;
import uk.co.exemel.disco.netutil.nio.marshalling.SocketRMIMarshaller;
import uk.co.exemel.disco.test.socket.tester.common.ClientAuthRequirement;
import uk.co.exemel.disco.test.socket.tester.common.SslRequirement;
import uk.co.exemel.disco.transport.api.protocol.DiscoObjectIOFactory;
import uk.co.exemel.disco.util.JMXReportingThreadPoolExecutor;
import org.springframework.core.io.ClassPathResource;

import javax.management.MBeanServerFactory;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

/**
*
*/
public class ClientInstance {
    private final ClientConnectedObjectManager connectedObjectManager;
    private final ExecutionVenueNioClient client;
    private final JMXReportingThreadPoolExecutor ioExecutorService;
    private final JMXReportingThreadPoolExecutor reconnectExecutor;

    public ClientInstance(String loggingLevel, int port, SslRequirement sslRequirement, ClientAuthRequirement clientAuthRequirement) throws ExecutionException, InterruptedException {
        NioLogger logger = new NioLogger(loggingLevel);
        NioConfig config;
        if (sslRequirement == SslRequirement.None) {
            config = new NioConfig();
        }
        else {
            TlsNioConfig tlsNioConfig = new TlsNioConfig();
            tlsNioConfig.setSupportsTls(sslRequirement == SslRequirement.Supports || sslRequirement == SslRequirement.Requires);
            tlsNioConfig.setRequiresTls(sslRequirement == SslRequirement.Requires);
            tlsNioConfig.setTruststore(new ClassPathResource("disco_server_ca.jks"));
            tlsNioConfig.setTruststoreType("JKS");
            tlsNioConfig.setTruststorePassword("password");
            tlsNioConfig.setMbeanServer(MBeanServerFactory.createMBeanServer());
            if (clientAuthRequirement != ClientAuthRequirement.None) {
                tlsNioConfig.setKeystore(new ClassPathResource("disco_client_cert.jks"));
                tlsNioConfig.setKeystoreType("JKS");
                tlsNioConfig.setKeystorePassword("password");
                tlsNioConfig.setWantClientAuth(true);
                if(clientAuthRequirement == ClientAuthRequirement.Needs) {
                    tlsNioConfig.setNeedClientAuth(true);
                }
            }
            config = tlsNioConfig;
        }
        config.setNioLogger(logger);
        config.setKeepAlive(true);
        config.setKeepAliveTimeout(5000);
        config.setKeepAliveInterval(1000);
        config.setReuseAddress(true);
        config.setMaxWriteQueueSize(0);
        config.setRecvBufferSize(524288);
        config.setRpcTimeoutGranularityMillis(100);
        config.setRpcTimeoutMillis(0);
        config.setSendBufferSize(524288);
        config.setTcpNoDelay(true);
        config.setWorkerTimeout(0);
        config.setUseDirectBuffersInMina(false);

        DiscoObjectIOFactory objectIoFactory = new HessianObjectIOFactory(true);
        connectedObjectManager = new ClientConnectedObjectManager();
        connectedObjectManager.setNioLogger(logger);
        connectedObjectManager.setMaxDeltaQueue(100);
        connectedObjectManager.setMaxInitialPopulationWait(1000);
        connectedObjectManager.setMissingDeltaTimeout(1000);
        connectedObjectManager.setNumProcessingThreads(2);
        connectedObjectManager.setPullerAwaitTimeout(1000);
        ClientSocketTransportInfo clientSocketTransportInfo = new ClientSocketTransportInfo("fakeBeanName", connectedObjectManager);
        String addressList = "localhost:"+port;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();
        BlockingQueue<Runnable> reconnectQueue = new LinkedBlockingDeque<>();
        ioExecutorService = new JMXReportingThreadPoolExecutor(1,5,5000, TimeUnit.MILLISECONDS,workQueue);
        reconnectExecutor = new JMXReportingThreadPoolExecutor(1,5,5000, TimeUnit.MILLISECONDS,reconnectQueue);
        NetworkAddressResolver addressResolver = new DNSBasedAddressResolver();
        Tracer tracer = new CompoundTracer();
        connectedObjectManager.start();
        client = new ExecutionVenueNioClient(logger, config, objectIoFactory, connectedObjectManager, clientSocketTransportInfo, addressList, ioExecutorService, reconnectExecutor, 5000, 5000, 86400000, addressResolver, tracer);
        RemotableMethodInvocationMarshaller marshaller = new SocketRMIMarshaller();
        client.setMarshaller(marshaller);
        client.setContextEmitter(new CompoundContextEmitter<>(new ArrayList<ContextEmitter<Map<String,String>, Object>>()));
        FutureTask<Boolean> startFuture = client.start();
        try {
            System.out.println("STARTED: "+startFuture.get(5,TimeUnit.SECONDS));
        }
        catch (TimeoutException te) {
            System.out.println("FAILED TO CONNECT ON START, expect failures");
        }
    }

    public void shutdown() throws ExecutionException, InterruptedException {
        connectedObjectManager.stop();
        FutureTask<Boolean> stopFuture = client.stop();
        ioExecutorService.shutdown();
        reconnectExecutor.shutdown();
        System.out.println("STOPPED: " + stopFuture.get());
    }

    public WaitingObserver execute(ExecutionContext ctx, OperationDefinition opdef, Object[] params) {
        WaitingObserver observer = new WaitingObserver();
        client.execute(ctx,opdef,params,observer, DefaultTimeConstraints.NO_CONSTRAINTS);
        return observer;
    }
}
