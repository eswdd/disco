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

package uk.co.exemel.disco.disco.netutil.nio;

import uk.co.exemel.disco.netutil.nio.ClientHandshake;
import uk.co.exemel.disco.netutil.nio.NioLogger;
import uk.co.exemel.disco.netutil.nio.RequestResponseManager;
import uk.co.exemel.disco.netutil.nio.RequestResponseManagerImpl;
import uk.co.exemel.disco.netutil.nio.message.*;
import uk.co.exemel.disco.util.jmx.Exportable;
import uk.co.exemel.disco.util.jmx.JMXControl;
import org.apache.mina.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static uk.co.exemel.disco.netutil.nio.NioLogger.LoggingLevel.PROTOCOL;
import static uk.co.exemel.disco.netutil.nio.NioLogger.LoggingLevel.SESSION;

/**
 * DiscoProtocol for version 2 of the protocol
 */
@ManagedResource
public class DiscoProtocol2 extends IoFilterAdapter implements Exportable, IDiscoProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoProtocol2.class);

    private static final KeepAliveMessage KEEP_ALIVE = new KeepAliveMessage();

    public static final String PROTOCOL_VERSION_ATTR_NAME = "DiscoProtocol.sessionProtocolVersion";
    public static final String IS_SERVER_ATTR_NAME = "DiscoProtocol.isServer";

    public static final byte APPLICATION_PROTOCOL_VERSION_CLIENT_ONLY_RPC = 1;
    public static final byte APPLICATION_PROTOCOL_VERSION_BIDIRECTION_RPC = 2;
    public static final byte APPLICATION_PROTOCOL_VERSION_MIN_SUPPORTED = APPLICATION_PROTOCOL_VERSION_CLIENT_ONLY_RPC;
    public static final byte APPLICATION_PROTOCOL_VERSION_MAX_SUPPORTED = APPLICATION_PROTOCOL_VERSION_BIDIRECTION_RPC;
    public static final byte APPLICATION_PROTOCOL_VERSION_UNSUPPORTED = APPLICATION_PROTOCOL_VERSION_MIN_SUPPORTED - 1;

    // these allow tests to force us to particular versions of the protocol, even invalid ones
    private static byte maxServerProtocolVersion = APPLICATION_PROTOCOL_VERSION_MAX_SUPPORTED;
    private static byte maxClientProtocolVersion = APPLICATION_PROTOCOL_VERSION_MAX_SUPPORTED;
    private static byte minServerProtocolVersion = APPLICATION_PROTOCOL_VERSION_MIN_SUPPORTED;
    private static byte minClientProtocolVersion = APPLICATION_PROTOCOL_VERSION_MIN_SUPPORTED;

    public static void setMaxServerProtocolVersion(byte maxServerProtocolVersion) {
        DiscoProtocol2.maxServerProtocolVersion = maxServerProtocolVersion;
    }

    public static void setMaxClientProtocolVersion(byte maxClientProtocolVersion) {
        DiscoProtocol2.maxClientProtocolVersion = maxClientProtocolVersion;
    }

    public static void setMinServerProtocolVersion(byte minServerProtocolVersion) {
        DiscoProtocol2.minServerProtocolVersion = minServerProtocolVersion;
    }

    public static void setMinClientProtocolVersion(byte minClientProtocolVersion) {
        DiscoProtocol2.minClientProtocolVersion = minClientProtocolVersion;
    }

    private byte[] getServerAcceptableVersions() {
        byte[] ret = new byte[(maxServerProtocolVersion - minServerProtocolVersion) + 1];
        int ind = 0;
        for (byte i = maxServerProtocolVersion; i >= minServerProtocolVersion; i--) {
            ret[ind++] = i;
        }
        return ret;
    }

    private byte[] getClientAcceptableVersions() {
        byte[] ret = new byte[(maxClientProtocolVersion - minClientProtocolVersion) + 1];
        int ind = 0;
        for (byte i = minClientProtocolVersion; i <= maxClientProtocolVersion; i++) {
            ret[ind++] = i;
        }
        return ret;
    }

    private final NioLogger nioLogger;
    private boolean isServer;

    private volatile boolean isEnabled = false;

    private final int interval;
    private final int timeout;

    private final AtomicLong heartbeatsMissed = new AtomicLong();
    private final AtomicLong heartbeatsSent = new AtomicLong();
    private final AtomicLong sessionsCreated = new AtomicLong();

    private String lastSessionFrom = null;

    public DiscoProtocol2(boolean server, NioLogger nioLogger, int keepAliveInterval, int keepAliveTimeout) {
        this.isServer = server;
        this.nioLogger = nioLogger;
        this.interval = keepAliveInterval;
        this.timeout = keepAliveTimeout;
        export(nioLogger.getJmxControl());

    }

    public void closeSession(final IoSession ioSession) {
        WriteFuture future = ioSession.write(new DisconnectMessage());
        future.addListener(new IoFutureListener() {

            @Override
            public void operationComplete(IoFuture future) {
                nioLogger.log(NioLogger.LoggingLevel.SESSION, ioSession, "DiscoProtocol - Closing session after disconnection");
                future.getSession().close();

            }
        });
    }

    public void suspendSession(final IoSession ioSession) {
        final Byte protocolVersion = (Byte) ioSession.getAttribute(PROTOCOL_VERSION_ATTR_NAME);
        if (protocolVersion == null || protocolVersion.equals(APPLICATION_PROTOCOL_VERSION_CLIENT_ONLY_RPC)) {
            return; // We don't need to do this for clients using older version, as they don't understand this message
        }

        WriteFuture future = ioSession.write(new SuspendMessage());
        future.addListener(new IoFutureListener() {

            @Override
            public void operationComplete(IoFuture future) {
                nioLogger.log(NioLogger.LoggingLevel.SESSION, ioSession, "DiscoProtocol - Suspended session");
            }
        });

    }


    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        if (!isServer) {
            ClientHandshake clientHandshake = new ClientHandshake();
            session.setAttribute(ClientHandshake.HANDSHAKE, clientHandshake);
            session.write(new ConnectMessage(getClientAcceptableVersions()));
        }
        super.sessionOpened(nextFilter, session);
    }


    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        try {
            if (status == IdleStatus.WRITER_IDLE) {
                nioLogger.log(PROTOCOL, session, "DiscoProtocolCodecFilter: sending KEEP_ALIVE");
                session.write(KEEP_ALIVE);
                heartbeatsSent.incrementAndGet();
            } else {
                nioLogger.log(PROTOCOL, session, "DiscoProtocolCodecFilter: KEEP_ALIVE timeout closing session");
                session.close();
                heartbeatsMissed.incrementAndGet();
            }
        } finally {
            nextFilter.sessionIdle(session, status);
        }
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
        session.setIdleTime(IdleStatus.READER_IDLE, timeout);
        session.setIdleTime(IdleStatus.WRITER_IDLE, interval);
        nextFilter.sessionCreated(session);

        nioLogger.log(SESSION, session, "DiscoProtocolCodecFilter: Created session at %s from %s", session.getCreationTime(), session.getRemoteAddress());
        sessionsCreated.incrementAndGet();
        lastSessionFrom = session.getRemoteAddress().toString();
    }


    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof ProtocolMessage) {
            ProtocolMessage protocolMessage = (ProtocolMessage) message;
            switch (protocolMessage.getProtocolMessageType()) {
                case CONNECT:
                    if (isEnabled()) {
                        ConnectMessage connectMessage = (ConnectMessage) protocolMessage;
                        //As a server, ensure that we support a version the client also supports
                        byte protocolVersionToUse = APPLICATION_PROTOCOL_VERSION_UNSUPPORTED;
                        for (byte testVersion = maxServerProtocolVersion; testVersion >= minServerProtocolVersion; testVersion--) {
                            if (Arrays.binarySearch(connectMessage.getApplicationVersions(), testVersion) >= 0) {
                                protocolVersionToUse = testVersion;
                                break;
                            }
                        }
                        if (protocolVersionToUse >= minServerProtocolVersion) {
                            nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: ACCEPTing connection request with version %s", protocolVersionToUse);
                            session.setAttribute(PROTOCOL_VERSION_ATTR_NAME, protocolVersionToUse);
                            session.setAttribute(IS_SERVER_ATTR_NAME, true);
                            // this is used for all writes to the session after the initial handshaking
                            session.setAttribute(RequestResponseManager.SESSION_KEY, new RequestResponseManagerImpl(session, nioLogger, 0));
                            session.write(new AcceptMessage(protocolVersionToUse));
                        } else {
                            //we don't speak your language. goodbye
                            nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: REJECTing connection request with versions %s", getAsString(connectMessage.getApplicationVersions()));
                            LOG.info("REJECTing connection request from session " + session.getRemoteAddress() + " with versions " + getAsString(connectMessage.getApplicationVersions()));
                            session.write(new RejectMessage(RejectMessageReason.INCOMPATIBLE_VERSION, getServerAcceptableVersions()));
                            session.close();
                        }
                    } else {
                        nioLogger.log(PROTOCOL, session, "REJECTing connection request from session %s as service unavailable", session.getReadMessages());
                        LOG.info("REJECTing connection request from session " + session.getReadMessages() + " as service unavailable");
                        session.write(new RejectMessage(RejectMessageReason.SERVER_UNAVAILABLE, getServerAcceptableVersions()));
                        session.close();
                    }
                    break;
                case ACCEPT:
                    //Client Side - server has accepted our connection request
                    AcceptMessage acceptMessage = (AcceptMessage) protocolMessage;
                    if (acceptMessage.getAcceptedVersion() < minClientProtocolVersion || acceptMessage.getAcceptedVersion() > maxClientProtocolVersion) {
                        nioLogger.log(PROTOCOL, session, "Protocol version mismatch - client version is %s, server has accepted %s", maxClientProtocolVersion, acceptMessage.getAcceptedVersion());
                        session.close();
                        throw new IllegalStateException("Protocol version mismatch - client version is " + maxClientProtocolVersion + ", server has accepted " + acceptMessage.getAcceptedVersion());
                    }
                    nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: ACCEPT received for with version %s", acceptMessage.getAcceptedVersion());
                    session.setAttribute(IS_SERVER_ATTR_NAME, false);
                    session.setAttribute(PROTOCOL_VERSION_ATTR_NAME, acceptMessage.getAcceptedVersion());
                    session.setAttribute(RequestResponseManager.SESSION_KEY, new RequestResponseManagerImpl(session, nioLogger, 0));
                    ClientHandshake handshake = (ClientHandshake) session.getAttribute(ClientHandshake.HANDSHAKE);
                    if (handshake != null) {
                        handshake.accept();
                    }
                    break;
                case REJECT:
                    //Client Side - server has said foxtrot oscar
                    RejectMessage rejectMessage = (RejectMessage) protocolMessage;
                    nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: REJECT received: versions accepted are %s", getAsString(rejectMessage.getAcceptableVersions()));
                    ClientHandshake handshake2 = (ClientHandshake) session.getAttribute(ClientHandshake.HANDSHAKE);
                    if (handshake2 != null) {
                        handshake2.reject();
                    }
                    break;
                case KEEP_ALIVE:
                    //Both sides keep alive received, which is ignored
                    nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: KEEP_ALIVE received");
                    break;
                case DISCONNECT:
                    //Client Side - server doesn't love us anymore
                    session.setAttribute(ProtocolMessage.ProtocolMessageType.DISCONNECT.name());
                    nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: DISCONNECT received");
                    session.close();
                    break;
                case SUSPEND:
                    //Client Side - this session is about to be closed
                    session.setAttribute(ProtocolMessage.ProtocolMessageType.SUSPEND.name());
                    nioLogger.log(PROTOCOL, session, "DiscoProtocolDecoder: SUSPEND received");
                    break;
                case MESSAGE_REQUEST:
                case MESSAGE_RESPONSE:
                case EVENT:
                    super.messageReceived(nextFilter, session, message);
                    break;
                default:
                    LOG.error("Unknown message type " + protocolMessage.getProtocolMessageType() + " - Ignoring");

            }
        }
    }

    private String getAsString(byte[] versions) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (byte b : versions) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(b);
        }
        sb.append("}");
        return sb.toString();
    }

    @ManagedAttribute
    public void setEnabled(boolean healthy) {
        this.isEnabled = healthy;
    }

    @ManagedAttribute
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Exports this service as an MBean, if the JMXControl is available
     */
    @Override
    public void export(JMXControl jmxControl) {
        if (jmxControl != null) {
            jmxControl.registerMBean("DiSCO.socket.transport:name=wireProtocol", this);
        }
    }

    @ManagedAttribute
    public int getInterval() {
        return interval;
    }

    @ManagedAttribute
    public int getTimeout() {
        return timeout;
    }

    @ManagedAttribute
    public long getHeartbeatsMissed() {
        return heartbeatsMissed.get();
    }

    @ManagedAttribute
    public long getHeartbeatsSent() {
        return heartbeatsSent.get();
    }

    @ManagedAttribute
    public long getSessionsCreated() {
        return sessionsCreated.get();
    }

    @ManagedAttribute
    public String getLastSessionFrom() {
        return lastSessionFrom;
    }

}
