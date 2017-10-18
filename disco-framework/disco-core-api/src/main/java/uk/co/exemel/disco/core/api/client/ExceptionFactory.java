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

package uk.co.exemel.disco.core.api.client;

import uk.co.exemel.disco.api.ResponseCode;

import java.util.List;

/**
 * Interface describes a class who can instantiate an exception on either
 * the DiscoApplicationException or DiscoException hierarchy
 */
public interface ExceptionFactory {

    Exception parseException(ResponseCode responseCode, String prefix, String reason, List<String[]> params);
}
