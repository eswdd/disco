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
<#include "common.ftl">
<#include "interfaceParser.ftl">
<#assign service = doc.@name>
// Generated from syncServiceExecutableResolver.ftl
package ${package}.${majorVersion};

import ${package}.${majorVersion}.co.*;
import ${package}.${majorVersion}.to.*;
import ${package}.${majorVersion}.enumerations.*;
import ${package}.${majorVersion}.exception.*;
import uk.co.exemel.disco.core.api.ev.ConnectedResponse;
import uk.co.exemel.disco.api.ExecutionContext;
import uk.co.exemel.disco.api.RequestContext;
import uk.co.exemel.disco.api.fault.DiscoApplicationException;
import uk.co.exemel.disco.core.api.exception.DiscoException;
import uk.co.exemel.disco.core.api.exception.DiscoClientException;
import uk.co.exemel.disco.core.api.exception.DiscoServiceException;
import uk.co.exemel.disco.core.api.exception.ServerFaultCode;
import uk.co.exemel.disco.core.api.ev.Executable;
import uk.co.exemel.disco.core.api.ev.ExecutableResolver;
import uk.co.exemel.disco.core.api.ev.ExecutionObserver;
import uk.co.exemel.disco.core.api.ev.ExecutionResult;
import uk.co.exemel.disco.core.api.ev.ExecutionVenue;
import uk.co.exemel.disco.core.api.ev.OperationKey;
import uk.co.exemel.disco.core.api.ev.TimeConstraints;
import uk.co.exemel.disco.core.impl.ev.ServiceExceptionHandlingObserver;

import java.util.*;

/**
 * Resolves Executables for all operations in the synchronous  ${service}Service.
 */
@SuppressWarnings("all")
public class ${service}SyncServiceExecutableResolver implements ExecutableResolver {<#t>

	private Map<OperationKey, Executable> executableMap = new HashMap<OperationKey, Executable>();
	private ${service}Service service;

	public ${service}SyncServiceExecutableResolver() {
	  	<#list doc.operation as operation>
            <#if (operation.@connected[0]!"false")?lower_case?trim=="true"><#t>
                <#assign returnType = "ConnectedResponse"><#t>
            <#else>
                <#assign parsedOperation = parseOperation(operation, interface)>
                <#if parsedOperation.responseParam.isEnumType>
                    <#assign returnType=parsedOperation.responseParam.paramType.javaType>
                <#else>
                    <#assign returnType = translateTypes2(operation.parameters.simpleResponse.@type, doc, false, "CO", false, false)><#t>
                </#if>
            </#if>
            <#assign method = operation.@name><#t>
            <#assign call="service." + method + "((RequestContext)ctx"><#t>
            <#assign argPos = 0>
            <#list operation.parameters.request.parameter as parameter>
                <#assign param=parameter.@name>
                <#assign paramType=parameter.@type>
                <#if parameter.extensions.style??>
                  <#assign style = parameter.extensions.style>
                </#if>
                <#assign paramCapFirst=parameter.@name?cap_first><#t>
                <#assign isEnumType=parameter.validValues[0]??><#t>
                <#if isEnumType>
                    <#assign javaType = "${method?cap_first}${paramCapFirst}Enum"><#t>
                <#else>
                    <#assign javaType = translateTypes(paramType)><#t>
                </#if><#t>
                <#assign call=call + ", (" + javaType + ")args["  + argPos + "]"><#t>
                <#assign argPos = argPos + 1><#t>
            </#list><#t>
            <#assign call=call + ", timeConstraints);"><#t>
	  	executableMap.put(${service}ServiceDefinition.${method}Key,
            new Executable() {
                @Override
                public void execute(ExecutionContext ctx, OperationKey key,
                        Object[] args, ExecutionObserver observer,
                        ExecutionVenue executionVenue, TimeConstraints timeConstraints) {

                ServiceExceptionHandlingObserver exceptionHandlingObserver = new ServiceExceptionHandlingObserver(observer);

                try {
                    <#if returnType!="void">
                    ${returnType} result = ${call}
                    observer.onResult(new ExecutionResult(result));
                    <#else>
                    ${call}
                    exceptionHandlingObserver.onResult(new ExecutionResult());
                    </#if>
                } catch (DiscoException ce) {
                    exceptionHandlingObserver.onResult(new ExecutionResult(ce));
                <#list operation.parameters.exceptions.exception as exception>
                } catch (${exception.@type} ex) {
                    exceptionHandlingObserver.onResult(new ExecutionResult((DiscoApplicationException)ex));
                </#list>
                };
            }
        });

		</#list>
        <#list doc.event as event>
        <#assign method = "subscribeTo" + event.@name><#t>

        executableMap.put(${service}ServiceDefinition.${method}OperationKey, 
            new Executable() {
                @Override
                public void execute(ExecutionContext ctx, OperationKey key,
							Object[] args, ExecutionObserver observer,
							ExecutionVenue executionVenue, TimeConstraints timeConstraints) {
		  			service.${method}(ctx, args, observer);

            }
        });
        </#list><#t>
	}

	public void setService(${service}Service service) {
    	this.service = service;
	}

	@Override
	public Executable resolveExecutable(OperationKey operationKey, ExecutionVenue ev) {
		return executableMap.get(operationKey);
	}
  

}
