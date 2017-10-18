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

import uk.co.exemel.disco.logging.DiscoLoggingUtils;
import uk.co.exemel.disco.logging.LogDefinition;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

import java.util.TimeZone;

import static org.mockito.Mockito.mock;


public abstract class DiscoUtilTestCase extends TestCase {
	public  static final String SEPARATOR = System.getProperty("line.separator");

    private Class classUnderTest;
    protected Logger traceLogger;

    protected DiscoUtilTestCase(Class classUnderTest) {
        super();
        this.classUnderTest = classUnderTest;
    }

	protected DiscoUtilTestCase() {
		super();
	}

	public DiscoUtilTestCase(String name) {
		super(name);
	}

    @Before
    public void setUp() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		LogDefinition hnd = new LogDefinition();
        hnd.setLogName("");
        hnd.register();

        traceLogger = mock(Logger.class);
        DiscoLoggingUtils.setTraceLogger(traceLogger);
    }

    @After
    public void tearDown() {
        DiscoLoggingUtils.setTraceLogger(null);
    }
/*
	public List<LogRecord> getMessageLog() {
        if (classUnderTest != null) {
            return ((MockCapturingLogger)DiscoLoggingUtils.getLogger(classUnderTest)).getLogRecords();
        } else {

		    return ((MockCapturingLogger)DiscoLoggingUtils.getLogger("")).getLogRecords();
        }
	}*/
}
