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

public abstract class AbstractCallMaker {

	/*
	 * Make an HTTP call to a local instance of the Disco container.
	 *   
	 * @param httpCallBean
	 * @param requestContentTypeEnum
	 * @return
	 */
	public abstract HttpResponseBean makeCall(HttpCallBean httpCallBean, DiscoMessageContentTypeEnum requestContentTypeEnum);
	
}
