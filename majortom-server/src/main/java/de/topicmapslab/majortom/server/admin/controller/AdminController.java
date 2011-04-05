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
/**
 * 
 */
package de.topicmapslab.majortom.server.admin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.server.admin.model.DatabaseConnectionData;
import de.topicmapslab.majortom.server.admin.model.NewTopicMapData;
import de.topicmapslab.majortom.server.admin.util.IOUtil;
import de.topicmapslab.majortom.server.cache.CacheHandler;
import de.topicmapslab.majortom.server.topicmaps.TopicMapsHandler;

/**
 * Controller for the admin pages.
 * 
 * @author Hannes Niederhausen
 * 
 */
@Controller
// @RequestMapping("/mt/admin")
public class AdminController {
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);

	private TopicMapsHandler tmh;

	// private Properties jedisProperties = new Properties();

	/**
	 * 
	 */
	public AdminController() {
		tmh = TopicMapsHandler.getInstance();

		// InputStream stream;
		// try {
		// stream = getClass().getResource("/jedis.properties").openStream();
		// // jedisProperties.load(stream);
		// stream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Returns the form for the database connection
	 */
	@RequestMapping("/configdb")
	public ModelAndView showDBConfig() {

		DatabaseConnectionData dbcd = IOUtil.loadDatabaseConnectionData();

		if (dbcd == null) {
			dbcd = new DatabaseConnectionData();
			dbcd.setDialect(SqlDialect.POSTGRESQL);
			logger.debug("Created new DatabaseConnectionData: " + dbcd.toString());
			System.err.println("Created new DatabaseConnectionData: " + dbcd.toString());
		} else {
			logger.debug("Loaded DatabaseConnectionData: " + dbcd.toString());
			System.err.println("Loaded DatabaseConnectionData: " + dbcd.toString());
		}

		return new ModelAndView("dbconnection", "command", dbcd);
	}

	/**
	 * Persist the new db configuration and redirects to "configdb"
	 * 
	 * @param dbcd
	 *            the new db config
	 * @param result
	 * @return
	 */
	@RequestMapping("/modifydb")
	public ModelAndView modifyDBConfig(@ModelAttribute("dbcd") DatabaseConnectionData dbcd, BindingResult result) {
		if (dbcd != null) {
			logger.debug("Got DatabaseConnectionData: " + dbcd.toString());
			System.err.println("Got DatabaseConnectionData: " + dbcd.toString());
		} else {
			System.err.println("Got DatabaseConnectionData: " + dbcd);
		}

		IOUtil.persistDatabaseConnectionData(dbcd);

		return showDBConfig();
	}

	/**
	 * Returns the view with the form for new topic maps
	 * 
	 * @return
	 */
	@RequestMapping("/newtopicmap")
	public ModelAndView newTopicMap() {
		return new ModelAndView("newtopicmap", "command", new NewTopicMapData());
	}

	/**
	 * Creates a new topic map and redirects to the topic maps list.
	 * 
	 * @param baselocator
	 * @param inmemory
	 * @param file
	 * @return redirect string
	 * @throws ServletException
	 */
	@RequestMapping("createtm")
	public ModelAndView newTopicMap(NewTopicMapData data, @RequestParam(value = "file", required = false) MultipartFile file) throws ServletException {

		String id = tmh.createTopicMap(data.getBaselocator(), data.isInmemory(), data.getInitialCapacity());

		if ((data.getFilePath() != null) && (data.getFilePath().length() > 0)) {
			tmh.loadFromLocalFile(id, data.getFilePath());
		} else if (file != null) {
			tmh.loadFromFileUpload(id, file);
		}

		// flush redis cache for queries
		// Jedis jedis = getJedis();

		if (CacheHandler.isCachingEnabled()) {
			logger.info("Deleting Cache with keys");
			CacheHandler.getCache().remove(Pattern.compile("tmql:" + id + "*"));
			// Set<String> keys = jedis.keys("tmql:" + id + "*");
			// if (!keys.isEmpty()) {
			// jedis.del(keys.toArray(new String[keys.size()]));
			// }
			//
			// // jedis.flushDB();
			// try {
			// jedis.disconnect();
			// } catch (IOException e) {
			// logger.error("Could not disconnect from jedis server", e);
			// }
		}

		return showTopicMaps();
	}

	/**
	 * Renders a list of created topic maps
	 * 
	 * @return the {@link ModelAndView} with the view param
	 */
	@RequestMapping("showtopicmaps")
	public ModelAndView showTopicMaps() {
		Map<String, String> resultMap = new HashMap<String, String>(tmh.getTopicMapMap().size());
		for (Entry<String, TopicMap> e : tmh.getTopicMapMap().entrySet()) {
			resultMap.put(e.getKey(), e.getValue().getLocator().toExternalForm());
		}

		return new ModelAndView("showtopicmaps", "entries", resultMap);
	}

	/**
	 * Removes the topic map with the given id
	 * 
	 * @param id
	 *            the id of the topic map to delete
	 * @return a redirect to showtopicmaps
	 */
	@RequestMapping("removetopicmap")
	public ModelAndView deleteTopicMap(String id) {
		tmh.closeTopicMap(id);

		return showTopicMaps();
	}

	/**
	 * Removes the topic map with the given id
	 * 
	 * @param id
	 *            the id of the topic map to delete
	 * @return a redirect to showtopicmaps
	 */
	@RequestMapping("cleartopicmap")
	public ModelAndView clearTopicMap(String id) {
		tmh.clearTopicMap(id);

		return showTopicMaps();
	}

	/**
	 * Removes the topic map with the given id
	 * 
	 * @param id
	 *            the id of the topic map to delete
	 * @return a redirect to showtopicmaps
	 */
	@RequestMapping("closetopicmap")
	public ModelAndView closeTopicMap(String id) {
		tmh.closeTopicMap(id);

		return showTopicMaps();
	}

	/**
	 * Clears the redis cache and returns an info message
	 * 
	 * @return
	 */
	@RequestMapping("clearcache")
	public ModelAndView clearCache() {
		try {
			/*
			 * checks if caching is enabled
			 */
			if (CacheHandler.isCachingEnabled()) {
				/*
				 * clear cache
				 */
				CacheHandler.getCache().clear();
			}
			// Jedis jedis = getJedis();
			// jedis.flushDB();
			// jedis.disconnect();
		} catch (Exception e) {
			return new ModelAndView("info", "msg", "Error clearing cache: " + e.getMessage());
		}

		return new ModelAndView("info", "msg", "Redis Cache cleared!");
	}

	/**
	 * Main page of the admin servlet
	 * 
	 * @return
	 */
	@RequestMapping({ "index", "/" })
	public ModelAndView index() {
		return new ModelAndView("index");
	}

	/**
	 * Returns the memory servlet
	 * 
	 * @return
	 */
	@RequestMapping("memory")
	public ModelAndView getMemory() {
		return new ModelAndView("memory");
	}

	/**
	 * Starts the garbage collector and returns to the memory page
	 * 
	 * @return redirect to "memory"
	 */
	@RequestMapping("garbagecollect")
	public ModelAndView collectGarbage() {
		System.gc();

		return getMemory();
	}

	// /**
	// * Returns the jedis and checks the connection before
	// *
	// * @return
	// */
	// private Jedis getJedis() {
	// Jedis jedis = new Jedis(jedisProperties.getProperty("hostname"));
	// jedis.auth(jedisProperties.getProperty("password"));
	// jedis.select(Integer.parseInt(jedisProperties.getProperty("storeId")));
	//
	// return jedis;
	// }
}
