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

package uk.co.exemel.disco.client.exception;

import uk.co.exemel.disco.api.ResponseCode;
import uk.co.exemel.disco.core.api.client.ExceptionFactory;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.fault.DiscoFault;
import uk.co.exemel.disco.marshalling.api.databinding.FaultUnMarshaller;
import uk.co.exemel.disco.transport.api.protocol.http.ResponseCodeMapper;

import java.io.InputStream;

/**
 * Transforms an HTTP error into a DiscoServiceException
 *
 */
public class HTTPErrorToDiscoExceptionTransformer extends AbstractExceptionTransformer {

    public HTTPErrorToDiscoExceptionTransformer(FaultUnMarshaller faultUnMarshaller) {
        super(faultUnMarshaller);
    }

    public Exception convert(final InputStream inputStream, final ExceptionFactory exceptionFactory, final int httpStatusCode) {
        DiscoFault fault = getFaultFromInputStream(inputStream);
        ResponseCode responseCode = ResponseCodeMapper.getResponseCodeFromHttpCode(httpStatusCode, fault);
        return exceptionFactory.parseException(responseCode, fault.getErrorCode(),
                fault.getFaultCode() + " fault received from remote server: "+ ServerFaultCode.getByDetailCode(fault.getErrorCode()), fault.getDetail().getFaultMessages());
	}
}
