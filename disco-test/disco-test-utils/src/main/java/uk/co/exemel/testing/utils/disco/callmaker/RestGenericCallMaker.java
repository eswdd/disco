/*
 * Copyright 2013, The Sporting Exchange Limited
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

package uk.co.exemel.testing.utils.disco.callmaker;


import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import org.apache.http.client.methods.HttpUriRequest;

public class RestGenericCallMaker extends AbstractCallMaker {

	private DiscoHelpers discoHelpers = new DiscoHelpers();
	
	public DiscoHelpers getDiscoHelpers() {
		return discoHelpers;
	}

	public void setDiscoHelpers(DiscoHelpers discoHelpers) {
		this.discoHelpers = discoHelpers;
	}

	/* (non-Javadoc)
	 * @see com.betfair.testing.utils.disco.callmaker.AbstractCallMaker#makeCall(com.betfair.testing.utils.disco.beans.HttpCallBean, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum)
	 */
	@Override
	public HttpResponseBean makeCall(HttpCallBean httpCallBean, DiscoMessageContentTypeEnum responseContentTypeEnum) {

		DiscoMessageProtocolRequestTypeEnum protocolRequestType = DiscoMessageProtocolRequestTypeEnum.REST;
		DiscoMessageContentTypeEnum requestContentTypeEnum = DiscoMessageContentTypeEnum.OTHER;
				
		HttpUriRequest method = discoHelpers.getRestMethod(httpCallBean, protocolRequestType);
		HttpResponseBean responseBean = discoHelpers.makeRestDiscoHTTPCall(httpCallBean, method, protocolRequestType, responseContentTypeEnum, requestContentTypeEnum);
		
		return responseBean;

	}

}
