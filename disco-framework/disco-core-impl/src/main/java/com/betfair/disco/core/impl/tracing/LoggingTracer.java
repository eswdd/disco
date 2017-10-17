/*
 * Copyright 2014, Simon Matić Langford
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

package uk.co.exemel.disco.core.impl.tracing;

import uk.co.exemel.disco.api.RequestUUID;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.logging.DiscoLoggingUtils;

/**
 * Simple tracer implementation which writes trace messages to a trace log.
 */
public class LoggingTracer extends AbstractTracer {

    @Override
    public void start(RequestUUID uuid, OperationKey operation) {
        // no-op
    }

    @Override
    public void trace(RequestUUID uuid, String msg) {
        DiscoLoggingUtils.getTraceLogger().info(uuid.toDiscoLogString()+": "+ msg);
    }

    @Override
    public void trace(RequestUUID uuid, String msg, Object arg1) {
        DiscoLoggingUtils.getTraceLogger().info(uuid.toDiscoLogString()+": "+ msg, arg1);
    }

    @Override
    public void trace(RequestUUID uuid, String msg, Object arg1, Object arg2) {
        DiscoLoggingUtils.getTraceLogger().info(uuid.toDiscoLogString()+": "+ msg, arg1, arg2);
    }

    @Override
    public void trace(RequestUUID uuid, String msg, Object arg1, Object arg2, Object arg3) {
        DiscoLoggingUtils.getTraceLogger().info(uuid.toDiscoLogString()+": "+ msg, arg1, arg2, arg3);
    }

    @Override
    public void trace(RequestUUID uuid, String msg, Object... args) {
        DiscoLoggingUtils.getTraceLogger().info(uuid.toDiscoLogString()+": "+ msg, args);
    }

    @Override
    public void end(RequestUUID uuid) {
        // no-op
    }

    @Override
    public void startCall(RequestUUID uuid, RequestUUID subUuid, OperationKey key) {
        // parent uuid could be null if we're embedded in a non-disco app and they've not set
        // uuid in the execution context
        if (uuid != null) {
            trace(uuid,"Making request to %s with uuid %s",key,subUuid.toDiscoLogString());
        }
    }

    @Override
    public void endCall(RequestUUID uuid, RequestUUID subUuid, OperationKey key) {
        // parent uuid could be null if we're embedded in a non-disco app and they've not set
        // uuid in the execution context
        if (uuid != null) {
            trace(uuid,"Received response from %s with uuid %s",key,subUuid.toDiscoLogString());
        }
    }
}
