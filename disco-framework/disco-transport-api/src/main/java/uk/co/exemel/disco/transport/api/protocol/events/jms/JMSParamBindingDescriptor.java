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

package uk.co.exemel.disco.transport.api.protocol.events.jms;

public class JMSParamBindingDescriptor {
    public enum ParamSource {
        BODY
    }

    private final String name;
    private final ParamSource source;

    /**
     * @param name name of the parameter
     * @param source specifies the source of the parameter value
     */
    public JMSParamBindingDescriptor(final String name, final ParamSource source) {
        this.source = source;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ParamSource getSource() {
        return source;
    }


}
