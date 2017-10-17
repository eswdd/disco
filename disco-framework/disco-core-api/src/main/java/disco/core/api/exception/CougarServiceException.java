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

package uk.co.exemel.disco.core.api.exception;

import java.util.logging.Level;

import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.api.fault.DiscoApplicationException;
import uk.co.exemel.disco.core.api.fault.Fault;

public class DiscoServiceException extends DiscoException {
	private DiscoApplicationException dae;

	public DiscoServiceException(ServerFaultCode fault, String message) {
		super(Level.FINE, fault, message);
        if (fault.getResponseCode() == null) {
            throw new IllegalArgumentException("Exception required for fault codes with a null response code");
        }
	}

	public DiscoServiceException(ServerFaultCode fault, String message, DiscoApplicationException dae) {
		super(Level.FINE, fault, message, dae);
        if (dae == null && fault.getResponseCode() == null) {
            throw new IllegalArgumentException("Exception required for fault codes with a null response code");
        }
		this.dae = dae;
	}

	public DiscoServiceException(ServerFaultCode fault, String message, Throwable t) {
		super(Level.FINE, fault, message, t);
        if (fault.getResponseCode() == null) {
            throw new IllegalArgumentException("Exception required for fault codes with a null response code");
        }
	}

	@Override
	public Fault getFault() {
		Fault fault = null;
    	if (dae != null) {
		    fault = new Fault(dae.getResponseCode().getFaultCode(), dae.getExceptionCode(), dae.getClass().getSimpleName(), dae);
    	} else {
    		fault = super.getFault();
    	}
    	return fault;
	}

	@Override
	public ResponseCode getResponseCode() {
    	if (dae != null) {
		    return dae.getResponseCode();
    	} else {
    		return super.getResponseCode();
    	}
	}
}
