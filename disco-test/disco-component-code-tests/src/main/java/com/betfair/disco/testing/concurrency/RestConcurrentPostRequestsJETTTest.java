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

package uk.co.exemel.disco.testing.concurrency;

import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestConcurrentPostRequestsJETTTest {


	private List<Thread> threads = new ArrayList<Thread>();
	private List<Executor> executors = new ArrayList<Executor>();
	private static final int OK_STATUS_CODE = 200;

	public List<Thread> getThreads() {
		return threads;
	}

	public void setThreads(List<Thread> threads) {
		this.threads = threads;
	}

	public List<Executor> getExecutors() {
		return executors;
	}

	public void setExecutors(List<Executor> executors) {
		this.executors = executors;
	}

	public RestConcurrentPostRequestsTestsResultBean executeTest(Integer numberOfThreads, Integer numberOfCallsPerThread, DiscoMessageProtocolRequestTypeEnum protocolRequestType, DiscoMessageContentTypeEnum responseContentType) throws InterruptedException{

		//Build required calls and executors, and thread them
		for (int i = 0; i < numberOfThreads; i++) {
			Executor executor = new Executor("executor"+i);
			executors.add(executor);
			Thread thread = new Thread(executor);
			threads.add(thread);
			executor.setNumberOfRequests(numberOfCallsPerThread);
			executor.setRequestProtocolTypeEnum(protocolRequestType);
			executor.buildCalls(responseContentType);
		}

		//Start the threads
		for (Thread thread: threads) {
			thread.start();
		}

		//Wait until all threads finished
		for (Thread thread: threads) {
			thread.join();
		}

		//Create maps to hold responses to assert
		Map<String, HttpResponseBean> expectedResponses = new LinkedHashMap<String, HttpResponseBean>();
		Map<String, HttpResponseBean> actualResponses = new LinkedHashMap<String, HttpResponseBean>();

		//Populate response maps
		for (Executor executor: executors) {
			Map<String, HttpResponseBean> executorExpectedResponses = executor.getExpectedResponses();
			expectedResponses.putAll(executorExpectedResponses);
			Map<String, HttpResponseBean> executorActualResponses = executor.getActualResponses();
			actualResponses.putAll(executorActualResponses);
		}

		//Put maps into bean and return
		RestConcurrentPostRequestsTestsResultBean returnBean = new RestConcurrentPostRequestsTestsResultBean();
		returnBean.setActualResponses(actualResponses);
		returnBean.setExpectedResponses(expectedResponses);
		return returnBean;

	}




	public static class Executor implements Runnable {

		public Executor(String identifier) {
			this.identifier = identifier;
		}

		private XMLHelpers xHelpers = new XMLHelpers();
		private DiscoManager discoManager = DiscoManager.getInstance();

		private String identifier;
		private int numberOfRequests;
		private DiscoMessageProtocolRequestTypeEnum requestProtocolTypeEnum;

		private Map<String, HttpResponseBean> expectedResponses = new LinkedHashMap<String, HttpResponseBean>();
		private Map<String, HttpResponseBean> actualResponses = new LinkedHashMap<String, HttpResponseBean>();
		private List<HttpCallBean> httpCallBeans = new ArrayList<HttpCallBean>();
		private Map<String, Timestamp> expectedRequestTimes = new LinkedHashMap<String, Timestamp>();

		public DiscoMessageProtocolRequestTypeEnum getRequestProtocolTypeEnum() {
			return requestProtocolTypeEnum;
		}

		public void setRequestProtocolTypeEnum(
				DiscoMessageProtocolRequestTypeEnum requestProtocolTypeEnum) {
			this.requestProtocolTypeEnum = requestProtocolTypeEnum;
		}

		public void run() {
			this.makeCalls();
		}

		public void buildCalls(DiscoMessageContentTypeEnum responseContentTypeEnum) {

			for (int i = 0; i < numberOfRequests+1; i++) {
				//Setup call beans
				HttpCallBean callBean = new HttpCallBean();
				callBean.setServiceName("baseline","discoBaseline");
				callBean.setVersion("v2");
				callBean.setOperationName("testComplexMutator","complex");

				Map<String, String> acceptProtocols = new HashMap<String,String>();
				switch (responseContentTypeEnum) {
				case JSON:
					acceptProtocols.put("application/json", "");
					break;
				case XML:
					acceptProtocols.put("application/xml", "");
					break;
				}
				callBean.setAcceptProtocols(acceptProtocols);

				String requestXmlString = "";
				requestXmlString = "<message><name>sum</name><value1> " + i + "</value1><value2>" + i + "</value2></message>";
				Document requestDocument = xHelpers.getXMLObjectFromString(requestXmlString);
				callBean.setRestPostQueryObjects(requestDocument);
				httpCallBeans.add(callBean);

				//Store expected responses
				HttpResponseBean responseBean = new HttpResponseBean();
				String responseXmlString = "<SimpleResponse><message>sum = " + i*2 + "</message></SimpleResponse>";
				Document responseBaseObject = xHelpers.getXMLObjectFromString(responseXmlString);
				Map<DiscoMessageProtocolRequestTypeEnum, Object> builtExpectedResponse = discoManager.convertResponseToRestTypes(responseBaseObject, callBean);
				switch (responseContentTypeEnum) {
				case XML:
					responseBean.setResponseObject(builtExpectedResponse.get(DiscoMessageProtocolRequestTypeEnum.RESTXML));
					break;
				case JSON:
					responseBean.setResponseObject(builtExpectedResponse.get(DiscoMessageProtocolRequestTypeEnum.RESTJSON));
					break;
				}
				responseBean.setHttpStatusCode(OK_STATUS_CODE);
				responseBean.setHttpStatusText("OK");

				expectedResponses.put(identifier + "Response " + i, responseBean);
			}
		}

		public void makeCalls() {
			//Make the calls
			int loopCounter = 0;
			for(HttpCallBean callBean: httpCallBeans) {
				Date time = new Date();
				//System.out.println("Making call: " + identifier + "-" + "Response " + loopCounter + " at: " + time.getTime()) ;
				expectedRequestTimes.put(identifier + "Response " + loopCounter, new Timestamp(time.getTime()));
				discoManager.makeRestDiscoHTTPCall(callBean, requestProtocolTypeEnum);
				loopCounter++;
			}


			//Get actual responses
			loopCounter=0;
			for (HttpCallBean httpCallBean: httpCallBeans) {
				HttpResponseBean responseBean = httpCallBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.REST);
				responseBean.clearResponseHeaders();
				actualResponses.put(identifier + "Response " + loopCounter, responseBean);
				loopCounter++;
			}

			//Set the expected response time
			for (String keyString: expectedResponses.keySet()) {
				HttpResponseBean responseBean = expectedResponses.get(keyString);
				Timestamp requestTime = expectedRequestTimes.get(keyString);
				responseBean.setRequestTime(requestTime);
				responseBean.setResponseTime(requestTime);
			}

		}

		public Map<String, HttpResponseBean> getActualResponses() {
			return actualResponses;
		}

		public void setActualResponses(Map<String, HttpResponseBean> actualResponses) {
			this.actualResponses = actualResponses;
		}

		public Map<String, HttpResponseBean> getExpectedResponses() {
			return expectedResponses;
		}

		public void setExpectedResponses(Map<String, HttpResponseBean> expectedResponses) {
			this.expectedResponses = expectedResponses;
		}

		public int getNumberOfRequests() {
			return numberOfRequests;
		}

		public void setNumberOfRequests(int numberOfRequests) {
			this.numberOfRequests = numberOfRequests;
		}
	}

	public static class RestConcurrentPostRequestsTestsResultBean {

		private Map<String, HttpResponseBean> expectedResponses = new LinkedHashMap<String, HttpResponseBean>();
		private Map<String, HttpResponseBean> actualResponses = new LinkedHashMap<String, HttpResponseBean>();

		public Map<String, HttpResponseBean> getActualResponses() {
			return actualResponses;
		}
		public void setActualResponses(Map<String, HttpResponseBean> actualResponses) {
			this.actualResponses = actualResponses;
		}
		public Map<String, HttpResponseBean> getExpectedResponses() {
			return expectedResponses;
		}
		public void setExpectedResponses(Map<String, HttpResponseBean> expectedResponses) {
			this.expectedResponses = expectedResponses;
		}

	}


}
