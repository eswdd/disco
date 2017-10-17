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
import uk.co.exemel.disco.api.security.IdentityToken;
import uk.co.exemel.disco.api.security.IdentityTokenResolver;
import uk.co.exemel.disco.client.exception.ExceptionTransformer;
import uk.co.exemel.disco.client.query.QueryStringGeneratorFactory;
import uk.co.exemel.disco.core.api.OperationBindingDescriptor;
import uk.co.exemel.disco.core.api.client.AbstractClientTransport;
import uk.co.exemel.disco.core.api.client.ExceptionFactory;
import uk.co.exemel.disco.core.api.client.TransportMetrics;
import uk.co.exemel.disco.core.api.ev.*;
import uk.co.exemel.disco.core.api.exception.*;
import uk.co.exemel.disco.core.api.tracing.Tracer;
import uk.co.exemel.disco.core.api.transcription.EnumDerialisationException;
import uk.co.exemel.disco.core.api.transcription.EnumUtils;
import uk.co.exemel.disco.core.api.transcription.Parameter;
import uk.co.exemel.disco.core.impl.tracing.TracingEndObserver;
import uk.co.exemel.disco.marshalling.api.databinding.DataBindingFactory;
import uk.co.exemel.disco.transport.api.protocol.http.HttpServiceBindingDescriptor;
import uk.co.exemel.disco.transport.api.protocol.http.rescript.RescriptOperationBindingDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

/**
 * Base executable which holds the logic of interacting with Execution Venue.
 * Note this marshals the request and un-marshals the response using the provided Transformable
 */
@ManagedResource
public abstract class AbstractHttpExecutable<HR> extends AbstractClientTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpExecutable.class);

    private static final String REMOTE_ADDRESS_DESCRIPTION = "Remote Service Address";
    private static final String SERVICE_CONTEXT_PATH_DESCRIPTION = "Service Context Path";

    protected static final int MAX_TOTAL_CONNECTIONS = 128;
    protected static final String CONTENT_TYPE = "application/json";
    private static final String UNMARSHALL_ENCODING = "utf-8";
    protected static final String DEFAULT_REQUEST_UUID_HEADER = "X-UUID";
    protected static final String DEFAULT_REQUEST_UUID_PARENTS_HEADER = "X-UUID-PARENTS";
    protected static final int DEFAULT_HTTPS_PORT = 443;
    protected static final int DEFAULT_HTTP_PORT = 80;

    private final Map<OperationKey, OperationBindingDescriptor> descriptorMap =
            new HashMap<OperationKey, OperationBindingDescriptor>();
    private final HttpServiceBindingDescriptor serviceBindingDescriptor;
    private final MessageBuilder messageBuilder = new MessageBuilder();

    private AtomicReference<String> remoteAddressRef = new AtomicReference<String>("NOT ASSIGNED");
    private DataBindingFactory dataBindingFactory;
    private IdentityTokenResolver<HR, HR, X509Certificate[]> identityTokenResolver;
    private QueryStringGeneratorFactory queryStringGeneratorFactory;
    private ExceptionTransformer exceptionTransformer;
    private ExceptionFactory exceptionFactory;
    protected DiscoRequestFactory<HR> requestFactory;
    private Tracer tracer;

    protected boolean transportSSLEnabled;
    protected boolean hostnameVerificationDisabled;
    private boolean gzipCompressionEnabled = true;
    protected boolean hardFailEnumDeserialisation;

    //Path to the keystore
    protected Resource httpsKeystore;
    //Password for that keystore
    protected String httpsKeyPassword;
    protected String httpsKeystoreType = KeyStore.getDefaultType();


    //The trust store fields are only necessary for 2 way SSL
    //Path to the trusted certificate store
    protected Resource httpsTruststore;
    //Trust store's password
    protected String httpsTrustPassword;
    protected String httpsTruststoreType = KeyStore.getDefaultType();

    // http timeout, -1 = not set
    protected int connectTimeout = -1;
    protected int idleTimeout = -1;


    protected AbstractHttpExecutable(final HttpServiceBindingDescriptor bindingDescriptor,
                                  DiscoRequestFactory<HR> requestFactory,
                                  Tracer tracer) {
        this.serviceBindingDescriptor = bindingDescriptor;
        this.requestFactory = requestFactory;
        this.tracer = tracer;
    }

    protected int extractPortFromAddress() throws IOException {
        String remoteAddress = remoteAddressRef.get();
        if (remoteAddress == null || remoteAddress.equals("NOT ASSIGNED")) {
            throw new IllegalArgumentException("Remote address not set!");
        }
        URL url = new URL(remoteAddress);
        return url.getPort();
    }


    public void init() throws Exception {
        final OperationBindingDescriptor[] operationBindings = serviceBindingDescriptor.getOperationBindings();

        for (OperationBindingDescriptor binding : operationBindings) {
            descriptorMap.put(binding.getOperationKey(), binding);
        }

        requestFactory.setGzipCompressionEnabled(gzipCompressionEnabled);
    }


    @Override
    public void execute(final ExecutionContext ctx, final OperationKey key, final Object[] args,
                        final ExecutionObserver obs, final ExecutionVenue executionVenue, final TimeConstraints timeConstraints) {

        final ClientCallContext callContext = CallContextFactory.createSubContext(ctx);

        tracer.startCall(ctx.getRequestUUID(), callContext.getRequestUUID(), key);

        final OperationDefinition operationDefinition = executionVenue.getOperationDefinition(key);

        final Parameter[] parameters = operationDefinition.getParameters();
        final RescriptOperationBindingDescriptor operationBinding =
                (RescriptOperationBindingDescriptor) descriptorMap.get(key.getLocalKey());

        final Message message = messageBuilder.build(args, parameters, operationBinding);
        String contextPath = serviceBindingDescriptor.getServiceContextPath();
        String remoteAddress = remoteAddressRef.get();
        if (remoteAddress.endsWith("/") && contextPath.startsWith("/")) {
            contextPath = contextPath.substring(1);
        }
        final String uri = remoteAddress + contextPath +"v"+ serviceBindingDescriptor.getServiceVersion().getMajor()
                + operationBinding.getURI();
        final String queryString = queryStringGeneratorFactory.getQueryStringGenerator()
                .generate(message.getQueryParmMap());
        final String httpMethod = operationBinding.getHttpMethod();

        // create http request

        HR request = requestFactory.create(uri + queryString, httpMethod, message,
                dataBindingFactory.getMarshaller(), CONTENT_TYPE, callContext, timeConstraints);

        Exception exception = null;
        Object result = null;
        InputStream inputStream = null;

        if (identityTokenResolver != null && getIdentityResolver() != null &&
                identityTokenResolver.isRewriteSupported()) {
            final List<IdentityToken> identityTokens = getIdentityResolver().tokenise(ctx.getIdentity());
            identityTokenResolver.rewrite(identityTokens, request);
            if (LOGGER.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (IdentityToken it: identityTokens) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(it.getName()).append("=").append(it.getValue());
                }
                LOGGER.debug("Rewrote tokens " + sb + " to http request");
            }
        }

        // Send Request


        sendRequest(request, new TracingEndObserver(tracer, obs, ctx.getRequestUUID(), callContext.getRequestUUID(), key), operationDefinition);

    }

    protected interface DiscoHttpResponse {

        InputStream getEntity() throws IOException;

        List<String> getContentEncoding();

        int getResponseStatus();

        String getServerIdentity();

        long getResponseSize();
    }

    protected void processResponse(final DiscoHttpResponse response,
                                   final ExecutionObserver obs,
                                   final OperationDefinition definition) {
        Exception exception = null;
        Object result = null;
        InputStream inputStream = null;
        final RescriptOperationBindingDescriptor operationBinding = (RescriptOperationBindingDescriptor)
                descriptorMap.get(definition.getOperationKey().getLocalKey());
        try {
            if (response.getEntity() == null) {
                exception = new DiscoClientException(ServerFaultCode.RemoteDiscoCommunicationFailure,
                        "No response returned by server");
            } else {
                inputStream = response.getEntity();
                if (gzipCompressionEnabled && !gzipHandledByTransport()) {
                    List<String> codecs = response.getContentEncoding();
                    for (String codecName : codecs) {
                        if ("gzip".equals(codecName) || "x-gzip".equals(codecName)) {
                            inputStream = new GZIPInputStream(inputStream);
                        }
                    }
                }
                EnumUtils.setHardFailureForThisThread(hardFailEnumDeserialisation);
                if (response.getResponseStatus() == HttpStatus.SC_OK) {
                    if (!operationBinding.voidReturnType()) {
                        try {
                            result = dataBindingFactory.getUnMarshaller().unmarshall(inputStream,
                                    definition.getReturnType(), UNMARSHALL_ENCODING, true);
                        }
                        catch (Exception e2) {
                            throw clientException(response, e2);
                        }
                    }
                } else if (response.getResponseStatus() == HttpStatus.SC_NOT_FOUND) {
                    // IN this case, we know there will be no exception body, so just stick on a new Exception
                    exception = new DiscoClientException(ServerFaultCode.NoSuchService,
                            "The server did not recognise the URL.");
                } else {
                    try {
                        exception = exceptionTransformer.convert(inputStream, exceptionFactory,
                                response.getResponseStatus());
                    } catch (Exception e2) {
                        throw clientException(response, e2);
                    }
                }
            }
        } catch (final Exception e) {
            if (e instanceof DiscoClientException) {
                exception = e;
            }
            else if (e instanceof EnumDerialisationException) {
                exception = new DiscoClientException(DiscoMarshallingException.unmarshallingException("json","Enum failure",e,true));
            }
            else if (e instanceof DiscoException) {
                exception = new DiscoClientException((DiscoException)e);
            }
            else {
                exception = new DiscoClientException(ServerFaultCode.RemoteDiscoCommunicationFailure,
                        "Exception occurred in Client: " + e.getMessage(), e);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        if (exception == null) {
            obs.onResult(new ClientExecutionResult(result, response.getResponseSize()));
        } else {
            obs.onResult(new ClientExecutionResult(exception, response.getResponseSize()));
        }
        // end request callback
    }

    private DiscoClientException clientException(DiscoHttpResponse response, Exception e) {
        if (e instanceof DiscoClientException) {
            return (DiscoClientException)e;
        }
        if (e instanceof DiscoException) {
            return new DiscoClientException(((DiscoException)e).getServerFaultCode(),e.getMessage(),e, !isDefinitelyDiscoResponse(response));
        }
        ServerFaultCode serverFaultCode = ServerFaultCode.RemoteDiscoCommunicationFailure;
        String message = "Unknown error communicating with remote Disco service";
        switch (response.getResponseStatus()) {
            case HttpStatus.SC_NOT_FOUND:
                serverFaultCode = ServerFaultCode.NoSuchOperation;
                message = "Service not found";
        }
        return new DiscoClientException(serverFaultCode, message, e, !isDefinitelyDiscoResponse(response));
    }

    protected void processException(ExecutionObserver obs, Throwable t, String url) {
        ServerFaultCode serverFaultCode = ServerFaultCode.RemoteDiscoCommunicationFailure;
        if (t instanceof SocketTimeoutException) {
            serverFaultCode = ServerFaultCode.Timeout;
        }
        processException(obs, t, url, serverFaultCode);
    }

    protected void processException(ExecutionObserver obs, Throwable t, String url, ServerFaultCode serverFaultCode) {
        Exception exception = new DiscoClientException(serverFaultCode,
                "Exception occurred in Client: " + t.getMessage()+": "+url, t);
        obs.onResult(new ClientExecutionResult(exception, 0));
    }

    protected boolean gzipHandledByTransport() {
        return false;
    }

    protected abstract void sendRequest(HR request, ExecutionObserver obs,
                                        OperationDefinition operationDefinition);


    /**
     * has the responding server identified itself as Disco
     *
     * Note that due to legacy servers/network infrastructure a 'false negative' is possible.
     * in other words although a 'true' response confirms that the server has identified itself Disco,
     * a 'false' does not confirm that a server IS NOT Disco. The disco header could have been stripped out
     * by network infrastructure, or the server could still be a legacy Disco which does not provide this header.
     *
     * @param response http response
     * @return boolean has the responding server identified itself as Disco
     */
    private boolean isDefinitelyDiscoResponse(DiscoHttpResponse response) {
        String ident = response.getServerIdentity();
        if (ident != null && ident.contains("Disco 2")) {
            return true;
        }
        return false;
    }

    @ManagedAttribute(description = REMOTE_ADDRESS_DESCRIPTION)
    public void setRemoteAddress(final String host) {
        this.remoteAddressRef.set(host);
    }

    @ManagedAttribute(description = REMOTE_ADDRESS_DESCRIPTION)
    public String getRemoteAddress() {
        return remoteAddressRef.get();
    }

    @ManagedAttribute(description = SERVICE_CONTEXT_PATH_DESCRIPTION)
    public String getServiceContextPath() {
        if (serviceBindingDescriptor != null) {
            return serviceBindingDescriptor.getServiceContextPath();
        } else {
            return "";
        }
    }

    /**
     * Return some information about the current transport.
     *
     * @return the transport metrics
     */
    public abstract TransportMetrics getTransportMetrics();

    public void setDataBindingFactory(DataBindingFactory dataBindingFactory) {
        this.dataBindingFactory = dataBindingFactory;
    }

    public void setQueryStringGeneratorFactory(QueryStringGeneratorFactory queryStringGeneratorFactory) {
        this.queryStringGeneratorFactory = queryStringGeneratorFactory;
    }

    public void setIdentityTokenResolver(IdentityTokenResolver<HR, HR, X509Certificate[]> identityTokenResolver) {
        this.identityTokenResolver = identityTokenResolver;
    }

    public void setExceptionTransformer(ExceptionTransformer exceptionTransformer) {
        this.exceptionTransformer = exceptionTransformer;
    }

    public void setExceptionFactory(ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public void setTransportSSLEnabled(boolean transportSSLEnabled) {
        this.transportSSLEnabled = transportSSLEnabled;
    }

    public void setHttpsKeyPassword(String httpsKeyPassword) {
        this.httpsKeyPassword = httpsKeyPassword;
    }

    public void setHttpsKeystore(Resource httpsKeystore) {
        this.httpsKeystore = httpsKeystore;
    }

    public void setHttpsKeystoreType(String httpsKeystoreType) {
        this.httpsKeystoreType = httpsKeystoreType;
    }

    public void setHttpsTrustPassword(String httpsTrustPassword) {
        this.httpsTrustPassword = httpsTrustPassword;
    }

    public void setHttpsTruststore(Resource httpsTruststore) {
        this.httpsTruststore = httpsTruststore;
    }

    public void setHttpsTruststoreType(String httpsTruststoreType) {
        this.httpsTruststoreType = httpsTruststoreType;
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setHostnameVerificationDisabled(final boolean hostnameVerificationDisabled) {
        this.hostnameVerificationDisabled = hostnameVerificationDisabled;
    }

    public void setRequestFactory(DiscoRequestFactory<HR> requestFactory) {
        this.requestFactory = requestFactory;
    }

    @ManagedAttribute
    public int getIdleTimeout() {
        return idleTimeout;
    }

    @ManagedAttribute
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @ManagedAttribute
    public String getHttpsTruststoreType() {
        return httpsTruststoreType;
    }

    @ManagedAttribute
    public Resource getHttpsTruststore() {
        return httpsTruststore;
    }

    @ManagedAttribute
    public String getHttpsKeystoreType() {
        return httpsKeystoreType;
    }

    @ManagedAttribute
    public Resource getHttpsKeystore() {
        return httpsKeystore;
    }

    @ManagedAttribute
    public boolean isHostnameVerificationDisabled() {
        return hostnameVerificationDisabled;
    }

    @ManagedAttribute
    public boolean isTransportSSLEnabled() {
        return transportSSLEnabled;
    }

    @ManagedAttribute
    public boolean isHardFailEnumDeserialisation() {
        return hardFailEnumDeserialisation;
    }

    public void setHardFailEnumDeserialisation(boolean hardFailEnumDeserialisation) {
        this.hardFailEnumDeserialisation = hardFailEnumDeserialisation;
    }

    @ManagedAttribute
    public boolean isGzipCompressionEnabled() {
        return gzipCompressionEnabled;
    }

    @ManagedAttribute
    public void setGzipCompressionEnabled(boolean gzipCompressionEnabled) {
        this.gzipCompressionEnabled = gzipCompressionEnabled;
    }

}
