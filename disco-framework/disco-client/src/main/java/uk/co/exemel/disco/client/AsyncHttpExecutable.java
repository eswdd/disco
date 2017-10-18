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

import uk.co.exemel.disco.client.api.ContextEmitter;
import uk.co.exemel.disco.core.api.client.TransportMetrics;
import uk.co.exemel.disco.core.api.ev.ExecutionObserver;
import uk.co.exemel.disco.core.api.ev.OperationDefinition;
import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.transport.api.protocol.http.HttpServiceBindingDescriptor;
import uk.co.exemel.disco.util.KeyStoreManagement;
import uk.co.exemel.disco.util.jmx.JMXControl;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.PoolingHttpDestination;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of client executable using async implementation of HTTP ReScript protocol.
 */
@ManagedResource
public class AsyncHttpExecutable extends AbstractHttpExecutable<Request> implements BeanNameAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientExecutable.class);

    private HttpClient client;

    private ExecutorService threadPool;
    private final ExecutorService responseThreadPool;
    private volatile boolean clientCreated;

    private String beanName;
    private JMXControl jmxControl;

    private JettyTransportMetrics metrics;
    private int maxConnectionsPerDestination;
    private int maxRequestsQueuedPerDestination;

    public AsyncHttpExecutable(HttpServiceBindingDescriptor bindingDescriptor, ContextEmitter emission, Tracer tracer,
                               ExecutorService threadPool, ExecutorService responseThreadPool) {
        super(bindingDescriptor, new JettyDiscoRequestFactory(emission), tracer);
        ((JettyDiscoRequestFactory)super.requestFactory).setExecutable(this);
        this.threadPool = threadPool;
        this.responseThreadPool = responseThreadPool;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void init() throws Exception {
        super.init();

        if (client == null) {
            client = new HttpClient(new SslContextFactory());
            client.setExecutor(new ExecutorThreadPool(threadPool));
            // configure timeout if set
            if (connectTimeout != -1) {
                client.setConnectTimeout(connectTimeout);
            }
            if (idleTimeout != -1) {
                client.setIdleTimeout(idleTimeout);
            }
            client.setMaxConnectionsPerDestination(maxConnectionsPerDestination);
            client.setMaxRequestsQueuedPerDestination(maxRequestsQueuedPerDestination);

            //Configure SSL - if relevant
            if (transportSSLEnabled) {
                KeyStoreManagement keyStore = KeyStoreManagement.getKeyStoreManagement(httpsKeystoreType, httpsKeystore, httpsKeyPassword);
                if (jmxControl != null && keyStore != null) {
                    jmxControl.registerMBean("DiSCO:name=AsyncHttpClientKeyStore,beanName="+beanName, keyStore);
                }
                KeyStoreManagement trustStore = KeyStoreManagement.getKeyStoreManagement(httpsTruststoreType, httpsTruststore, httpsTrustPassword);
                if (jmxControl != null) {
                    jmxControl.registerMBean("DiSCO:name=AsyncHttpClientTrustStore,beanName="+beanName, trustStore);
                }
                if (trustStore == null) {
                    throw new IllegalStateException("This configuration ostensibly supports TLS, yet doesn't provide valid truststore configuration");
                }

                final SslContextFactory sslContextFactory = client.getSslContextFactory();

                uk.co.exemel.disco.netutil.SslContextFactory factory = new uk.co.exemel.disco.netutil.SslContextFactory();
                factory.setTrustManagerFactoryKeyStore(trustStore.getKeyStore());
                if (keyStore != null) {
                    factory.setKeyManagerFactoryKeyStore(keyStore.getKeyStore());
                    factory.setKeyManagerFactoryKeyStorePassword(httpsKeyPassword);
                }
                SSLContext context = factory.newInstance();

                if (hostnameVerificationDisabled) {
                    context.getDefaultSSLParameters().setEndpointIdentificationAlgorithm(null);
                    LOGGER.warn("CRITICAL SECURITY CHECKS ARE DISABLED: server SSL certificate hostname " +
                            "verification is turned off.");
                }
                else {
                    context.getDefaultSSLParameters().setEndpointIdentificationAlgorithm("https");
                }

                sslContextFactory.setSslContext(context);
            }
            client.start();
            clientCreated = true;
        }

        metrics = new JettyTransportMetrics();

        if (jmxControl != null) {
            jmxControl.registerMBean("DiSCO:name=AsyncHttpClientExecutable,beanName=" + beanName, this);
        }
    }

    public void shutdown() throws Exception {
        if (clientCreated) {
            client.stop();
        }
    }

    public void setJmxControl(JMXControl jmxControl) {
        this.jmxControl = jmxControl;
    }

    public void setMaxConnectionsPerDestination(int maxConnectionsPerDestination) {
        this.maxConnectionsPerDestination = maxConnectionsPerDestination;
    }

    public void setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination) {
        this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
    }

    @Override
    protected void sendRequest(final Request request, final ExecutionObserver obs,
                               final OperationDefinition operationDefinition) {
        final String url = String.valueOf(request.getURI());
        final long startTime = System.currentTimeMillis();

        InputStreamResponseListener listener = new InputStreamResponseListener() {
            @Override
            public void onHeaders(final Response response) {
                super.onHeaders(response);

                // can only get this once, so let's makes sure..
                final InputStream inputStream = getInputStream();
                // this needs to be done in a seperate thread pool - to avoid deadlocks it needs to be the same size as or larger than the client pool
                responseThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processResponse(new DiscoHttpResponse() {
                                @Override
                                public InputStream getEntity() throws IOException {
                                    return inputStream;
                                }

                                @Override
                                public List<String> getContentEncoding() {
                                    return new ArrayList<>(response.getHeaders().getValuesList(HttpHeader.CONTENT_ENCODING.asString()));
                                }

                                @Override
                                public int getResponseStatus() {
                                    return response.getStatus();
                                }

                                @Override
                                public String getServerIdentity() {
                                    return response.getHeaders().get(HttpHeader.SERVER);
                                }

                                @Override
                                public long getResponseSize() {
                                    String s = response.getHeaders().get(HttpHeader.CONTENT_LENGTH);
                                    return s != null ? Long.parseLong(s) : -1;
                                }
                            }, obs, operationDefinition);
                        }
                        catch (Exception e) {
                            LOGGER.warn("DISCO: HTTP internal ERROR - URL [" + url + "] time [" + elapsed(startTime) + "mS]", e);
                            processException(obs, e, url);
                        }
                    }
                });
            }
        };
        request.onResponseFailure(new Response.FailureListener() {
            @Override
            public void onFailure(Response response, Throwable failure) {
                ServerFaultCode serverFaultCode = ServerFaultCode.RemoteDiscoCommunicationFailure;
                if (failure instanceof TimeoutException) {
                    failure = new DiscoFrameworkException("Read timed out", failure);
                    serverFaultCode = ServerFaultCode.Timeout;
                }
                LOGGER.warn("DISCO: HTTP communication ERROR - URL [" + url + "] time [" + elapsed(startTime) + "mS]", failure);
                processException(obs, failure, url, serverFaultCode);
            }
        }).onRequestFailure(new Request.FailureListener() {
            @Override
            public void onFailure(Request request, Throwable failure) {
                LOGGER.warn("DISCO: HTTP connection FAILED - URL [" + url + "] time [" + elapsed(startTime) + " mS]", failure);
                processException(obs, failure, url);
            }
        }).send(listener);
    }

    private long elapsed(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    protected boolean gzipHandledByTransport() {
        return true;
    }

    @Override
    public TransportMetrics getTransportMetrics() {
        return metrics;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

    //@VisibleForTesting
    HttpClient getClient() {
        return client;
    }

    final class JettyTransportMetrics implements TransportMetrics {
        private int port;
        private String host;
        private boolean https;

        JettyTransportMetrics() {
            try {
                URI uri = new URI(getRemoteAddress());
                https = "https".equalsIgnoreCase(uri.getScheme());
                port = uri.getPort() < 0 ? (https ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT) : uri.getPort();

                host = uri.getHost();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to determine address and port for service address '" + getRemoteAddress() + "'");//NOSONAR
            }
        }

        private PoolingHttpDestination getDestination() {
            return (PoolingHttpDestination) client.getDestination(https ? "https" : "http", host, port);
        }

        @Override
        public int getOpenConnections() {
            if (host != null) {
                return getDestination().getConnectionPool().getActiveConnections().size();
            }

            return 0;
        }

        @Override
        public int getMaximumConnections() {
            return maxConnectionsPerDestination;
        }

        @Override
        public int getFreeConnections() {
            if (host != null) {
                return getDestination().getConnectionPool().getIdleConnections().size();
            }
            return 0;
        }

        @Override
        public int getCurrentLoad() {
            return (getOpenConnections() - getFreeConnections()) * 100 / getMaximumConnections();
        }
    }
}
