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

package uk.co.exemel.disco.util.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class LimitedByteCountingInputStream extends ByteCountingInputStream {

    private long maxBytes;

    public LimitedByteCountingInputStream(InputStream in, long maxBytes) {
        super(in);
        this.maxBytes = maxBytes;
    }

    @Override
    protected void incrementCount(long increment) throws IOException {
        super.incrementCount(increment);
        if (getCount() > maxBytes) {
            throw new IOException("Max bytes violated: "+increment+">"+maxBytes);
        }
    }
}