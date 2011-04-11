/*******************************************************************************
 * Copyright 2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.server.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.server.admin.model.DatabaseConnectionData;
import de.topicmapslab.majortom.server.admin.model.IDatabaseConnectionDataDAO;
import de.topicmapslab.majortom.server.admin.model.NewTopicMapData;
import de.topicmapslab.majortom.server.cache.CacheHandler;
import de.topicmapslab.majortom.server.security.IMTSGrantedAuthorityDAO;
import de.topicmapslab.majortom.server.security.IMTSUserDetailDAO;
import de.topicmapslab.majortom.server.security.MTSGrantedAuthority;
import de.topicmapslab.majortom.server.security.MTSGrantedAuthorityEditor;
import de.topicmapslab.majortom.server.security.MTSUserDetail;
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

	@Autowired
	private IMTSUserDetailDAO userDetailDAO;
	
	@Autowired
	private IMTSGrantedAuthorityDAO grantedAuthorityDAO;
	
	@Autowired
	private IDatabaseConnectionDataDAO databaseConnectionDataDAO;
	
	private TopicMapsHandler tmh;

	// private Properties jedisProperties = new Properties();

	/**
	 * 
	 */
	public AdminController() {
		tmh = TopicMapsHandler.getInstance();
	}

	/**
	 * Registers the property editor for the authority class
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MTSGrantedAuthority.class, "authorities", new MTSGrantedAuthorityEditor(grantedAuthorityDAO));
//		binder.registerCustomEditor(List.class, "authorities", new MTSGrantedAuthorityListEditor());
	}
	
	/**
	 * Returns the form for the database connection
	 */
	@RequestMapping("/configdb")
	public ModelAndView showDBConfig(String info) {
		DatabaseConnectionData dbcd = null;
		List<DatabaseConnectionData> connections = databaseConnectionDataDAO.getConnections();
		if (connections.isEmpty()) {
			dbcd = new DatabaseConnectionData();
			dbcd.setDialect(SqlDialect.POSTGRESQL);
			
		} else {
			dbcd = connections.get(0);
		}

		
		ModelAndView mv = new ModelAndView("dbconnection", "command", dbcd);
		if (info!=null)
			mv.addObject("info", info);
		return mv;
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
		
		databaseConnectionDataDAO.persist(dbcd);
		
		return showDBConfig("Database Connection Saved");
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
	public ModelAndView newTopicMap(NewTopicMapData data, @RequestParam(value = "file", required = false) MultipartFile file)
			throws ServletException {

		String id = tmh.createTopicMap(data.getBaselocator(), data.isInmemory(), data.getInitialCapacity());

		if ((data.getFilePath() != null) && (data.getFilePath().length() > 0)) {
			tmh.loadFromLocalFile(id, data.getFilePath());
		} else if (file != null) {
			tmh.loadFromFileUpload(id, file);
		}

		if (CacheHandler.isCachingEnabled()) {
			logger.info("Deleting Cache with keys");
			CacheHandler.getCache().remove(Pattern.compile("tmql:" + id + "*"));
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
	 * Renders a list of users
	 * 
	 * @return the {@link ModelAndView} with the view param
	 */
	@RequestMapping("showusers")
	public ModelAndView showUsers() {
		return new ModelAndView("showusers", "entries", userDetailDAO.getUsers());
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

	/**
	 * Returns the form for the database connection
	 */
	@RequestMapping("/edituser")
	public ModelAndView showUserConfig(String username) {
		MTSUserDetail user = null;
		if (username != null) {
			user = userDetailDAO.getUser(username);
		}
		if (user == null) {
			user = new MTSUserDetail();
		}

		ModelAndView modelAndView = new ModelAndView("user", "command", user);
		
		List<String> items = new ArrayList<String>();
		List<MTSGrantedAuthority> authorities = grantedAuthorityDAO.getAuthorities();
		
		for (MTSGrantedAuthority a : authorities) {
			items.add(a.getAuthority());
		}
		
		modelAndView.addObject("items", authorities);
		modelAndView.addObject("items2", items);
		
		return modelAndView;
	}
	
	/**
	 * Persist the new db configuration and redirects to "configdb"
	 * 
	 * @param dbcd
	 *            the new db config
	 * @param result
	 * @return
	 */
	@RequestMapping("/modifyuser")
	public ModelAndView modifyUserConfig(@ModelAttribute("user") MTSUserDetail user, BindingResult result) {
		if (user != null) {
			if (user.getApiKey()==null)
				user.generateApiKey(Long.toString(System.currentTimeMillis()));
			userDetailDAO.persist(user);

		}

		return showUsers();
	}
	
	/**
	 * Removes the user with the given id
	 * 
	 * @param username the username
	 * @return
	 */
	@RequestMapping("/deleteuser")
	public ModelAndView deleteUserConfig(String username) {
		userDetailDAO.remove(username);

		return showUsers();
	}
	
	/**
	 * Generates an apikey for the user with the given id
	 * 
	 * @param username the username
	 * @return
	 */
	@RequestMapping("/generateapikey")
	public ModelAndView generateAPIKey(String username) {
		
		MTSUserDetail user = userDetailDAO.getUser(username);
		
		user.generateApiKey(Long.toString(System.currentTimeMillis()));

		userDetailDAO.persist(user);
		
		return showUsers();
	}
}
