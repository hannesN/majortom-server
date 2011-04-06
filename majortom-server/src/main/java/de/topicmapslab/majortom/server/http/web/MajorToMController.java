/*******************************************************************************
 * Copyright 2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.server.http.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapix.io.XTM2TopicMapWriter;
import org.tmapix.io.XTMVersion;

import de.topicmapslab.beru.core.TopicMapSearcher;
import de.topicmapslab.beru.result.Facet;
import de.topicmapslab.beru.result.FacetedResult;
import de.topicmapslab.beru.util.ConstructIdentifier;
import de.topicmapslab.beru.util.IdentifierType;
import de.topicmapslab.beru.util.TopicMapIndexDirectory;
import de.topicmapslab.ctm.writer.core.CTMTopicMapWriter;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.server.cache.CacheHandler;
import de.topicmapslab.majortom.server.http.util.JSONView;
import de.topicmapslab.majortom.server.http.util.MD5Util;
import de.topicmapslab.majortom.server.http.util.QueryParser;
import de.topicmapslab.majortom.server.topicmaps.TopicMapsHandler;
import de.topicmapslab.majortom.server.topicmaps.UnknownTopicMapException;
import de.topicmapslab.sesame.simpleinterface.SPARQLResultFormat;
import de.topicmapslab.sesame.simpleinterface.TMConnector;
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.path.components.processor.runtime.TmqlRuntime2007;
import de.topicmapslab.tmql4j.query.IQuery;

/**
 * Controller for the REST interface
 * 
 * @author Hannes Niederhausen
 * 
 */
@Controller
public class MajorToMController extends AbstractMajorToMController {

	private Logger logger = LoggerFactory.getLogger(MajorToMController.class.getName());

	/**
	 * Constructor
	 */
	public MajorToMController() {
		tmh = TopicMapsHandler.getInstance();
		runtime = TMQLRuntimeFactory.newFactory().newRuntime(TmqlRuntime2007.TMQL_2007);
	}

	/**
	 * Returns all topics from the topic map
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/topics/{id}")
	public JSONView getTopics(@PathVariable String id, String apikey) {
		
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		
		try {
			String redisKey = "topics:" + id;
			String resString = null;
			if (CacheHandler.isCachingEnabled()) {
				resString = CacheHandler.getCache().get(redisKey);
			}

			if (resString == null) {
				TopicMap tm = tmh.getTopicMap(id);

				String queryString = "// tm:subject USE JTMQR";

				IQuery query = runtime.run(tm, queryString);

				resString = (String) query.getResults().get(0, 0);
				if (CacheHandler.isCachingEnabled()) {
					CacheHandler.getCache().set(redisKey, resString);
				}
			}
			return new JSONView(getResultJSON(0, "OK", resString));
		} catch (Exception e) {
			return new JSONView(getResultJSON(1, e.getMessage(), null, null));
		}
	}


	/**
	 * Returns the id of the topic map
	 */
	@RequestMapping("resolvetm")
	public JSONView getTopicMapId(String bl, String apikey) {
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		
		try {
			String id = tmh.getTopicMapId(bl);

			return new JSONView(getResultJSON(0, "OK", "\"" + id + "\""));
		} catch (UnknownTopicMapException e) {
			return new JSONView(getResultJSON(1, "Unknown Base Locator"));
		}

	}

	/**
	 * Writes the given topic map to xtm
	 */
	@RequestMapping(value = "/xtm/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public void toXTM(@PathVariable String id, String query, String serverid, String callback, String apikey, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		if (!isValidKey(apikey)) {
			invalidApiKeyResult().render(null, req, resp);
			return;
		}

		TopicMap topicMap = tmh.getTopicMap(id);
		
		resp.setContentType("application/x-tm+xtm;version=2.1");
		XTM2TopicMapWriter writer = new XTM2TopicMapWriter(resp.getOutputStream(), topicMap.getLocator().toExternalForm(), XTMVersion.XTM_2_1);
		writer.write(topicMap);
	}
	
	/**
	 * Writes the given topic map to ctm
	 */
	@RequestMapping(value = "/ctm/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public void toCTM(@PathVariable String id, String query, String serverid, String callback, String apikey, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {

		if (!isValidKey(apikey)) {
			invalidApiKeyResult().render(null, req, resp);
			return;
		}
		
		TopicMap topicMap = tmh.getTopicMap(id);
		
		
		resp.setContentType("application/x-tm+ctm");
		CTMTopicMapWriter writer = new CTMTopicMapWriter(resp.getOutputStream(), topicMap.getLocator().toExternalForm());
		writer.write(topicMap);
	}

	

	/**
	 * Retrieves a TMQL query, appends USE JTMQR and executes the query.
	 */
	@RequestMapping(value = "/tmql/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public JSONView getTMQL(@PathVariable String id, String query, String serverid, String callback, String apikey, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		
		try {
			String queryString = extractBodyContent(req);

			if (queryString.length() == 0) {
				queryString = query;
			}

			// get md5sum for key
			String md5 = MD5Util.calculateMD5(queryString);

			String redisKey = "tmql:" + id + ":" + md5;

			String resString = null;
			if (CacheHandler.isCachingEnabled()) {
				resString = CacheHandler.getCache().get(redisKey);
			}

			Meta meta = new Meta(queryString);
			meta.setCached(true);
			if (resString == null) {

				TopicMap topicMap = tmh.getTopicMap(id);

				// setting baselocator as default prefix
				String defaultPrefix = topicMap.getLocator().toExternalForm();
				if (!defaultPrefix.endsWith("/")) {
					defaultPrefix += "/";
				}
				// runtime.getLanguageContext().getPrefixHandler().setDefaultPrefix(defaultPrefix);

				long start = System.currentTimeMillis();

				logger.info("Executing Query: '" + queryString);
				IQuery tmqlQuery = runtime.run(topicMap, queryString);

				long duration = System.currentTimeMillis() - start;

				meta.setDuration(duration);
				meta.setCached(false);

				logger.info("Query: '" + queryString + "' Duration: " + duration);
				logger.info("Query Result: '" + tmqlQuery.getResults().toString());

				resString = tmqlQuery.getResults().toJTMQR();

				if (CacheHandler.isCachingEnabled()) {
					CacheHandler.getCache().set(redisKey, resString);
				}
			}

			return new JSONView(getResultJSON(0, "OK", resString, meta), callback);
		} catch (Exception e) {
			logger.error("An exception was thrown: ", e);
			return new JSONView(getResultJSON(1, e.getMessage()), callback);
		}
	}

	/**
	 * Returns index {@link ModelAndView}
	 * 
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = "topicmaps", method = { RequestMethod.GET, RequestMethod.POST })
	public JSONView getTopicMaps(String callback, String apikey, HttpServletRequest req) throws ServletException {
		
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		
		try {
			StringBuilder jsonResult = new StringBuilder();

			jsonResult.append("{");
			jsonResult.append("\"topicmaps\" : [");

			Iterator<Entry<String, TopicMap>> it = tmh.getTopicMapMap().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, TopicMap> e = it.next();
				jsonResult.append(" { \"id\" : ");
				jsonResult.append("\"");
				jsonResult.append(e.getKey());
				jsonResult.append("\", \"locator\" : ");
				jsonResult.append("\"");
				jsonResult.append(e.getValue().getLocator().toExternalForm());
				jsonResult.append("\" }");

				if (it.hasNext()) {
					jsonResult.append(", ");
				}
			}
			jsonResult.append("] }");

			return new JSONView(getResultJSON(0, "OK", jsonResult.toString()), callback);
		} catch (Exception e) {
			logger.error("An Exception occured: ", e);
			return new JSONView(getResultJSON(1, e.getMessage()), callback);
		}
	}

	/**
	 * Returns index {@link ModelAndView}
	 * 
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = { "index", "", "/" })
	public ModelAndView index(HttpServletRequest req) throws ServletException {
		Map<String, String> resultMap = new HashMap<String, String>(tmh.getTopicMapMap().size());
		for (Entry<String, TopicMap> e : tmh.getTopicMapMap().entrySet()) {
			resultMap.put(e.getKey(), e.getValue().getLocator().toExternalForm());
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("entries", resultMap);
		try {
			model.put("queries", QueryParser.getExamples(req));
		} catch (IOException e1) {
			logger.error("Error while reading queries.", e1);
			model.put("queries", Collections.emptyList());
		}

		return new ModelAndView("index", "model", model);
	}

	/**
	 * Returns all topics from the topic map
	 * 
	 * @param id
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = "/sparql/{id}", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD })
	public Object getSparql(@PathVariable String id, String query, String apikey, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		
		try {
			TopicMap tm = tmh.getTopicMap(id);
			TopicMapSystem tms = ((ITopicMap) tm).getTopicMapSystem();

			TMConnector connector = new TMConnector(tms);

			String acceptType = req.getHeader("Accept");

			// switch result type:
			String contentType = null;
			String format = null;
			if (acceptType.contains("application/sparql-results+xml")) {
				contentType = "application/sparql-results+xml";
				format = SPARQLResultFormat.XML;
			} else if (acceptType.contains("application/sparql-results+json")) {
				contentType = "application/sparql-results+json";
				format = SPARQLResultFormat.JSON;
			} else {
				// fallback is html
				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				connector.executeSPARQL(tm.getLocator().toExternalForm(), query, SPARQLResultFormat.HTML, bos);

				return new ModelAndView("sparqlresult", "result", new String(bos.toByteArray(), "UTF-8"));
			}
			resp.setContentType(contentType);
			connector.executeSPARQL(tm.getLocator().toExternalForm(), query, format, resp.getOutputStream());

			return null;

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Returns all topics from the topic map
	 * 
	 * @param id
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = "/beru/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public JSONView getLucene(@PathVariable String id, String query, String apikey, HttpServletResponse resp) throws ServletException {
		try {
			if (!isValidKey(apikey))
				return invalidApiKeyResult();
			TopicMap tm = tmh.getTopicMap(id);

			TopicMapSearcher s = new TopicMapSearcher(new TopicMapIndexDirectory(new ConstructIdentifier(IdentifierType.TOPIC_MAP_LOCATOR,
					tm.getLocator().toExternalForm()), "/tmp/index/", "facets/"));
			Set<FacetedResult> result = s.searchWithFacets(query);

			StringBuilder searchBuilder = new StringBuilder();
			searchBuilder.append("[");
			for (FacetedResult r : result) {

				if (searchBuilder.length() > 1) {
					searchBuilder.append(", ");
				}

				searchBuilder.append("{");
				searchBuilder.append("\"name\" : \"");
				searchBuilder.append(r.getResultName());
				searchBuilder.append("\", \"identifier\" : \"");
				searchBuilder.append(r.getResultIdentifier().getEncodedURI());
				searchBuilder.append("\", \"score\" : \"");
				searchBuilder.append(r.getResultScore());
				searchBuilder.append("\", \"is_topic_type\" : \"");
				searchBuilder.append(Boolean.toString(r.isTopicType()));
				searchBuilder.append("\", \"facets\" : [");

				int i = 0;
				for (Facet f : r.getFacets()) {
					if (i > 0) {
						searchBuilder.append(", ");
					}
					searchBuilder.append("{");
					searchBuilder.append("\"class_identifier\" : \"");
					searchBuilder.append(f.getKlassIdentifier().getFieldName());
					searchBuilder.append(":");
					searchBuilder.append(f.getKlassIdentifier().getEncodedURI());
					searchBuilder.append("\", \"value\" : \"");
					searchBuilder.append(StringEscapeUtils.escapeJavaScript(f.getValue()));
					searchBuilder.append("\"");
					searchBuilder.append("}");
					i++;
				}
				searchBuilder.append("]");
				searchBuilder.append("}");
			}
			searchBuilder.append("]");

			String data = searchBuilder.toString();

			return new JSONView(getResultJSON(0, "OK", data));

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Clears the  cache and returns an info message
	 * 
	 * @return
	 */
	@RequestMapping(value = "/clearcache/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public JSONView clearCache(@PathVariable String id, String query, String apikey, HttpServletResponse resp) throws ServletException {
		if (!isValidKey(apikey))
			return invalidApiKeyResult();
		try {
			/*
			 * checks if caching is enabled
			 */
			if (CacheHandler.isCachingEnabled()) {
				/*
				 * clear cache
				 */
				CacheHandler.getCache().clear(id);
			}
		} catch (Exception e) {
			return new JSONView(getResultJSON(1, "Error clearing cache: " + e.getMessage()));
		}

		return new JSONView(getResultJSON(0, "OK"));
	}

}
