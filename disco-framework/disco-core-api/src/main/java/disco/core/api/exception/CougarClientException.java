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

package uk.co.exemel.disco.core.api.exception;

import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.api.fault.DiscoApplicationException;
import uk.co.exemel.disco.core.api.fault.Fault;

import java.util.logging.Level;

public class DiscoClientException extends DiscoException {

	private DiscoApplicationException dae;
    private boolean confirmedAsDisco;

	public DiscoClientException(DiscoException ce) {
		this(ce.getServerFaultCode(), ce.getMessage(), ce.getCause());
	}

	public DiscoClientException(ServerFaultCode fault, String message) {
		this(fault, message, true);
	}

	public DiscoClientException(ServerFaultCode fault, String message, boolean confirmedAsDisco) {
		super(Level.FINE, fault, message);
        this.confirmedAsDisco = confirmedAsDisco;
	}

	public DiscoClientException(ServerFaultCode fault, String message, DiscoApplicationException dae) {
		super(Level.FINE, fault, message, dae);
		this.dae = dae;
        this.confirmedAsDisco = true;
	}

	public DiscoClientException(ServerFaultCode fault, String message, Throwable t) {
		this(fault, message, t, true);
	}

	public DiscoClientException(ServerFaultCode fault, String message, Throwable t, boolean confirmedAsDisco) {
		super(Level.FINE, fault, message, t);
        if (t instanceof DiscoApplicationException) {
            this.dae = (DiscoApplicationException) t;
        }
        this.confirmedAsDisco = confirmedAsDisco;
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

    @Override
    protected String additionalInfo() {
        return confirmedAsDisco ? null : "Server not confirmed to be a Disco";
    }

    public boolean isConfirmedAsDisco() {
        return confirmedAsDisco;
    }
}
