package com.betfair.cougar.modules.zipkin.impl;

import com.betfair.cougar.api.RequestUUID;
import com.betfair.cougar.modules.zipkin.api.ZipkinDataBuilder;
import com.betfair.cougar.modules.zipkin.api.ZipkinKeys;
import com.betfair.cougar.modules.zipkin.api.ZipkinRequestUUID;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@ManagedResource(description = "Zipkin tracing config", objectName = "Cougar:name=ZipkinManager")
public class ZipkinManager {

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 1000;

    private static final int HEX_RADIX = 16;

    // Fast - Pseudo-random used for sampling
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Can be arbitrarily slow (depends on the amount of entropy in the OS)
    // Used for long (complete 64-bit range) ID generation
    private static final ThreadLocal<SecureRandom> SECURE_RANDOM_TL = new ThreadLocal<SecureRandom>() {
        @Override
        public SecureRandom initialValue() {
            return new SecureRandom();
        }
    };

    private int samplingLevel = 0;

    static {
        SECURE_RANDOM_TL.set(new SecureRandom());
    }

    /**
     * Sampling strategy to determine whether a given request should be traced by Zipkin.
     *
     * @return true if the request should be traced by Zipkin.
     */
    public boolean shouldTrace() {
        // with short circuit so we don't go through the random generation process if the Zipkin tracing is disabled
        // (samplingLevel == 0)
        return samplingLevel > 0 && RANDOM.nextInt(MIN_LEVEL, MAX_LEVEL) < samplingLevel;
    }

    //TODO: In the future we may consider having a service that defers the decision of tracing or not to the next
    // underlying service, i.e. it doesn't create the Zipkin ids, but it also doesn't mark the request as do not sample.

    @ManagedAttribute
    public int getSamplingLevel() {
        return samplingLevel;
    }

    @ManagedAttribute
    public void setSamplingLevel(int samplingLevel) {
        if (samplingLevel >= MIN_LEVEL && samplingLevel <= MAX_LEVEL) {
            this.samplingLevel = samplingLevel;
        } else {
            throw new IllegalArgumentException("Sampling level " + samplingLevel + " is not in the range [" + MIN_LEVEL + ";" + MAX_LEVEL + "[");
        }
    }

    public ZipkinRequestUUID createNewZipkinRequestUUID(@Nonnull RequestUUID cougarUuid, @Nullable String traceId,
                                                        @Nullable String spanId, @Nullable String parentSpanId,
                                                        @Nullable String sampled, @Nullable String flags, int port) {
        Objects.requireNonNull(cougarUuid);

        if (Boolean.FALSE.equals(ZipkinKeys.sampledToBoolean(sampled))) {
            // short-circuit: if the request was already marked as not sampled, we don't even try to sample it now
            // otherwise, we don't care which sampled value we have (if it is true then the traceId/spanId should
            // also be != null)
            return new ZipkinRequestUUIDImpl(cougarUuid, null);
        }

        ZipkinDataBuilder zipkinDataBuilder;

        if (traceId != null && spanId != null) {
            // a request with the fields is always traceable so we always propagate the tracing to the following calls

            zipkinDataBuilder = new ZipkinDataImpl.Builder()
                    .traceId(hexUnsignedStringToLong(traceId))
                    .spanId(hexUnsignedStringToLong(spanId))
                    .parentSpanId(parentSpanId == null ? null : hexUnsignedStringToLong(parentSpanId))
                    .flags(flags == null ? null : Long.valueOf(flags))
                    .port((short) port);

        } else {

            if (shouldTrace()) {
                // starting point, we need to generate the ids if this request is to be sampled - we are the root
                // nevertheless, if there are any flags we get them so we can act on them and pass them on to the
                // underlying services

                zipkinDataBuilder = new ZipkinDataImpl.Builder()
                        .traceId(getRandomLong())
                        .spanId(getRandomLong())
                        .parentSpanId(null)
                        .flags(flags == null ? null : Long.valueOf(flags))
                        .port((short) port);

            } else {
                // otherwise leave them as null - this means Zipkin tracing will be disabled for this request
                zipkinDataBuilder = null;
            }

        }

        return new ZipkinRequestUUIDImpl(cougarUuid, zipkinDataBuilder);
    }

    public static long getRandomLong() {
        byte[] rndBytes = new byte[8];
        SECURE_RANDOM_TL.get().nextBytes(rndBytes);
        return ByteBuffer.wrap(rndBytes).getLong();
    }

    public static long hexUnsignedStringToLong(@Nonnull String hexValue) {
        // Long.parseLong receives signed longs, but Long.toHexString uses unsigned longs, so we need to use BigInteger
        // in order to parse the unsigned string created by Long.toHexString and then obtain the value without raising
        // an NumberFormatException caused by long overflow.
        return new BigInteger(hexValue, HEX_RADIX).longValue();
    }
}
