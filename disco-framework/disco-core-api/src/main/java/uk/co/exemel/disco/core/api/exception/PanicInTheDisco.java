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

package uk.co.exemel.disco.core.api.exception;

import java.util.logging.Level;




/**
 * Fatal exception - disco cannot start, or if started is in such a state that correct processing may not proceed - eg. out of memory
 *
 */
@SuppressWarnings("serial")
public class PanicInTheDisco extends DiscoException {
	private static final Level LOG_LEVEL = Level.SEVERE;
	public PanicInTheDisco(String cause, Exception e) {
		super(LOG_LEVEL, ServerFaultCode.StartupError, cause, e);
	}

	public PanicInTheDisco(String cause) {
		super(LOG_LEVEL, ServerFaultCode.StartupError, cause);
	}

	public PanicInTheDisco(Exception e) {
		super(LOG_LEVEL, ServerFaultCode.StartupError, e);
	}


}
