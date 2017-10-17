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

package uk.co.exemel.disco.marshalling.api.databinding;

import uk.co.exemel.disco.core.api.fault.DiscoFault;

import java.io.InputStream;

/**
 * Interface to describe a marshaller to hydrate a DiscoFault from the input stream
 */
public interface FaultUnMarshaller {

    DiscoFault unMarshallFault(InputStream inputStream, String encoding);
}
