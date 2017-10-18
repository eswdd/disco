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

package uk.co.exemel.disco.core.api;

import org.springframework.context.ApplicationContext;

/**
 * A factory that creates and starts disco application server. Can be used to start disco application server as a
 * stand-alone java application or to start it as an embedded component, e.g. from a servlet-based web application.
 * <p/>
 * Examples:
 * To start disco as a standalone app just call:
 * new DiscoSpringCtxFactoryImpl().create(null);
 * <p/>
 * To integrate disco with existing servlet-based web application, create custom ServletContextListener and call:
 * WebApplicationContext parentCtx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
 * new DiscoSpringCtxFactoryImpl().create(parentCtx);
 */
public interface DiscoSpringCtxFactory {

    /**
     * Creates and starts disco application server without using a parent context.
     *
     * @return Disco spring context.
     */
    ApplicationContext create();

    /**
     * Creates and starts disco application server.
     *
     * @param parentCtx Parent spring context. Useful when starting disco as an embedded component, e.g. from a servlet-based
     *                  web application. If null then parent context is not set.
     * @return Disco spring context.
     */
    ApplicationContext create(ApplicationContext parentCtx);
}
