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

package uk.co.exemel.disco;

import uk.co.exemel.disco.core.api.exception.DiscoFrameworkException;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test for @See DiscoServerVersion
 */
public class DiscoVersionTest {

    @Test
    public void test() {
        DiscoVersion.init("/version/DiscoVersionHappy.properties");
        String v = DiscoVersion.getVersion();
        assertEquals("Versions do not agree", "2.3.4", v);
    }

    @Test
    public void testMajorMinor() {
        DiscoVersion.init("/version/DiscoVersionHappy.properties");
        String v = DiscoVersion.getMajorMinorVersion();
        assertEquals("Versions do not agree", "2.3", v);
    }

    @Test
    public void testMajorMinorWithSnapshot() {
        DiscoVersion.init("/version/DiscoVersionSnapshot.properties");
        String v = DiscoVersion.getMajorMinorVersion();
        assertEquals("Versions do not agree", "2.3", v);
    }

    @Test(expected = DiscoFrameworkException.class)
    public void testMajorMinorNoPropertiesFile() {
        DiscoVersion.init("/version/wibble.properties");
        DiscoVersion.getMajorMinorVersion();
    }


}
