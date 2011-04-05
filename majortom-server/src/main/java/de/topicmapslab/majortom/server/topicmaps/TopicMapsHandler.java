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
package de.topicmapslab.majortom.server.topicmaps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.format_estimator.FormatEstimator.Format;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.server.admin.util.IOUtil;
import de.topicmapslab.majortom.server.http.util.MD5Util;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.search.core.TopicMapIndexer;
import de.topicmapslab.search.util.ConstructIdentifier;
import de.topicmapslab.search.util.IdentifierType;
import de.topicmapslab.search.util.TopicMapIndexDirectory;

/**
 * Implementation of the {@link ITopicMapsHandler} interface using MaJorToM.
 * 
 * @author Hannes Niederhausen
 * 
 */
public class TopicMapsHandler {

	private static Logger logger = LoggerFactory
			.getLogger(TopicMapsHandler.class);

	private static TopicMapsHandler instance;

	private BidiMap topicMapMap;

	private TopicMapSystem dbTopicMapSystem;

	/**
	 * @return the instance
	 */
	public static TopicMapsHandler getInstance() {
		if (instance == null)
			instance = new TopicMapsHandler();
		return instance;
	}

	/**
	 * Constructor
	 */
	private TopicMapsHandler() {
		topicMapMap = new DualHashBidiMap();
	}

	/**
	 * Returns the id of the topic map with the given locator
	 * 
	 * @param baseLocator
	 *            the base locator to check
	 * @return the id of the topic map with the given base locator
	 * @throws UnknownTopicMapException
	 *             if no topic map with the given locator exists
	 */
	public String getTopicMapId(String baseLocator)
			throws UnknownTopicMapException {

		for (Entry<String, TopicMap> e : getTopicMapMap().entrySet()) {
			if (e.getValue().getLocator().toExternalForm().equals(baseLocator)) {
				return e.getKey();
			}
		}
		throw new UnknownTopicMapException();
	}

	/**
	 * Creates a topic map with the given locator and returns the id of the
	 * topic map.
	 * 
	 * <p>
	 * If a topic map with the given locator already exists its id will be
	 * returned.
	 * </p>
	 * 
	 * @param baseLocator
	 * @param inMemory
	 *            flag whether a in memory topic map is created
	 * @return the id of the topic map
	 * @throws ServletException
	 */
	public String createTopicMap(String baseLocator, boolean inMemory,
			int initialCapacity) throws ServletException {

		TopicMapSystem tms = null;

		if (inMemory) {
			tms = getMemoryTopicMapSystem(initialCapacity);
		} else {
			tms = getDBTopicMapSystem();
		}

		// try if topic map already exists
		TopicMap tm = tms.getTopicMap(baseLocator);

		if (tm == null) {
			try {
				tm = tms.createTopicMap(baseLocator);
			} catch (TopicMapExistsException e) {
				// should never happen
				logger.error("Topic Map with Base Locator: " + baseLocator
						+ " exists");
				throw new UnknownError();
			}

			// create id with md5 of baselocator
			String id = null;
			try {
				id = MD5Util.calculateMD5(baseLocator);

				// put the id to the map
				topicMapMap.put(id, tm);

				return id;
			} catch (NoSuchAlgorithmException e) {
				throw new ServletException(e);
			}
		}
		return (String) topicMapMap.getKey(tm);
	}

	/**
	 * Closes the topic map with the given id and frees resources
	 */
	public void closeTopicMap(String id) {
		TopicMap tm = (TopicMap) topicMapMap.get(id);
		if (tm == null)
			return;
		topicMapMap.remove(id);
		tm.close();
	}

	/**
	 * Removes the topic map with the given id
	 */
	public void removeTopicMap(String id) {
		TopicMap tm = (TopicMap) topicMapMap.get(id);
		if (tm == null)
			return;

		topicMapMap.remove(id);
		tm.remove();
	}

	/**
	 * Clears the topic map with the given id
	 */
	public void clearTopicMap(String id) {
		TopicMap tm = (TopicMap) topicMapMap.get(id);
		if (tm == null)
			return;
		tm.clear();
	}

	/**
	 * Returns the topic map with the given id.
	 * 
	 * @param id
	 *            the id of the topic map
	 * @return the {@link TopicMap}
	 * @throws IllegalArgumentException
	 *             if there is no topic map for the id
	 */
	public TopicMap getTopicMap(String id) throws IllegalArgumentException {

		TopicMap tm = (TopicMap) topicMapMap.get(id);
		if (tm != null)
			return tm;

		throw new IllegalArgumentException("No Topic Map for the given id: "
				+ id);

	}

	/**
	 * Returns an unmodifiable map containing all topic maps and their id
	 * 
	 * @return the map with the created topic maps
	 */
	@SuppressWarnings("unchecked")
	public Map<String, TopicMap> getTopicMapMap() {
		return Collections.unmodifiableMap(topicMapMap);
	}

	/**
	 * Deserializes the file and stores the data in the topic map with the given
	 * id
	 * 
	 * @param id
	 *            id of the target topic map
	 * @param file
	 *            the multipart file
	 */
	public void loadFromFileUpload(String id, final MultipartFile file) {

		logger.info("Start Loading file: " + file.getOriginalFilename());

		final TopicMap topicMap = (TopicMap) topicMapMap.get(id);
		if (topicMap == null)
			return;

		if (file.getOriginalFilename().length() == 0)
			return;

		if (((ITopicMap) topicMap).getStore() instanceof InMemoryTopicMapStore) {
			InMemoryTopicMapStore store = (InMemoryTopicMapStore) ((ITopicMap) topicMap)
					.getStore();

			try {
				if (file.getOriginalFilename().toLowerCase().endsWith("xtm")) {
					Importer.importStream(store, file.getInputStream(),
							topicMap.getLocator().toExternalForm(), Format.XTM);
				} else if (file.getOriginalFilename().toLowerCase()
						.endsWith("ctm")) {
					Importer.importStream(store, file.getInputStream(),
							topicMap.getLocator().toExternalForm(), Format.CTM);
				} else {
					throw new IllegalArgumentException(
							"Only XTM or CTM files allowed.");
				}
				logger.info("Finished Loading file: "
						+ file.getOriginalFilename());
				logger.info("Start removing duplicates");
				((ITopicMap) topicMap).removeDuplicates();
				logger.info("Finished removing duplicates");

				// indexing stuff
				indexTopicMap(topicMap);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Only XTM or CTM files allowed.", e);
			}
			return;
		}

		try {
			TopicMapReader reader = null;
			if (file.getOriginalFilename().toLowerCase().endsWith("xtm")) {
				reader = new XTMTopicMapReader(topicMap, file.getInputStream(),
						topicMap.getLocator().toExternalForm());
			} else if (file.getOriginalFilename().toLowerCase().endsWith("ctm")) {
				reader = new CTMTopicMapReader(topicMap, file.getInputStream(),
						topicMap.getLocator().toExternalForm());
			} else {
				throw new IllegalArgumentException(
						"Only XTM or CTM files allowed.");
			}

			reader.read();
			logger.info("Start removing duplicates");
			((ITopicMap) topicMap).removeDuplicates();
			logger.info("Finished removing duplicates");
			logger.info("Finished Loading file: " + file.getOriginalFilename());
		} catch (IOException e) {
			logger.error("Could not load topic map", e);
		}
	}

	/**
	 * Deserializes the local file and stores the data in the topic map with the
	 * given id
	 * 
	 * @param id
	 *            id of the target topic map
	 * @param filename
	 *            the name of the local file
	 */
	public void loadFromLocalFile(String id, final String filename) {
		logger.info("Start Loading file: " + filename);
		final TopicMap topicMap = (TopicMap) topicMapMap.get(id);
		if (topicMap == null)
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					File f = new File(filename);
					if (f.isDirectory()) {

						File[] files = f.listFiles();
						Arrays.sort(files, new Comparator<File>() {
							@Override
							public int compare(final File o1, final File o2) {
								return o1.getName().compareToIgnoreCase(
										o2.getName());
							}

						});
						logger.info("Found " + files.length + "Files:");
						for (File currFile : files) {
							logger.info(currFile.getName());
						}
						for (File currFile : files) {
							String currName = currFile.getName();
							if ((currName.endsWith("xtm"))
									|| (currName.endsWith("ctm"))) {

								loadFile(currFile, topicMap);
							}
						}
					} else {
						loadFile(f, topicMap);
					}
					logger.info("Start removing duplicates");
					((ITopicMap) topicMap).removeDuplicates();
					logger.info("Finished removing duplicates");

					// indexing stuff
					indexTopicMap(topicMap);
				} catch (Exception e) {
					logger.error("Could not load topic map", e);
				}
			}

			private void loadFile(final File file, final TopicMap topicMap)
					throws Exception {
				String fileName = file.getName();
				logger.info("Start Loading file: " + fileName);
				TopicMapReader reader = null;

				if (((ITopicMap) topicMap).getStore() instanceof InMemoryTopicMapStore) {
					InMemoryTopicMapStore store = (InMemoryTopicMapStore) ((ITopicMap) topicMap)
							.getStore();

					try {
						if (fileName.toLowerCase().endsWith("xtm")) {
							Importer.importFile(store, file, topicMap
									.getLocator().toExternalForm());
						} else {
							throw new IllegalArgumentException(
									"Only XTM or CTM files allowed.");
						}
					} catch (Exception e) {
						throw new IllegalArgumentException(
								"Only XTM or CTM files allowed.");
					}
					logger.info("Finished Loading file: " + fileName);
					return;
				}

				if (fileName.toLowerCase().endsWith("xtm")) {
					reader = new XTMTopicMapReader(topicMap,
							new FileInputStream(file), topicMap.getLocator()
									.toExternalForm());
				} else if (fileName.toLowerCase().endsWith("ctm")) {
					reader = new CTMTopicMapReader(topicMap,
							new FileInputStream(file), topicMap.getLocator()
									.toExternalForm());
				} else {
					throw new IllegalArgumentException(
							"Only XTM or CTM files allowed.");
				}

				reader.read();
				logger.info("Finished Loading file: " + fileName);
			}
		}).start();
	}

	private TopicMapSystem getMemoryTopicMapSystem(int initialCapacity) {
		try {
			TopicMapSystemFactoryImpl tmsFac = new TopicMapSystemFactoryImpl();
			tmsFac.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
			tmsFac.setProperty(
					TopicMapStoreProperty.INITIAL_COLLECTION_CAPACITY,
					initialCapacity);
			tmsFac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
					InMemoryTopicMapStore.class.getName());

			TopicMapSystem inMemorySystem = tmsFac.newTopicMapSystem();
			return inMemorySystem;
		} catch (Exception e) {
			logger.error("Could not create topic map", e);
		}

		return null;
	}

	private TopicMapSystem getDBTopicMapSystem() {

		if (dbTopicMapSystem == null) {
			de.topicmapslab.majortom.server.admin.model.DatabaseConnectionData data = IOUtil
					.loadDatabaseConnectionData();
			if (data == null) {
				throw new UnsupportedOperationException(
						"Could not find a database configuration. Please configure the connection using the admin interface");
			}

			try {
				TopicMapSystemFactoryImpl fac = new TopicMapSystemFactoryImpl();
				fac.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
						"de.topicmapslab.majortom.database.store.JdbcTopicMapStore");
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_HOST,
						data.getHost());
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_USER,
						data.getUsername());
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD,
						data.getPassword());
				fac.setProperty(JdbcTopicMapStoreProperty.DATABASE_NAME,
						data.getName());
				fac.setProperty(JdbcTopicMapStoreProperty.SQL_DIALECT, data
						.getDialect().name());

				fac.setFeature(
						FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION,
						false);
				fac.setFeature(FeatureStrings.SUPPORT_HISTORY, true);

				dbTopicMapSystem = fac.newTopicMapSystem();

			} catch (Exception e) {
				logger.error("Could not create topic map", e);
			}
		}
		return dbTopicMapSystem;
	}

	private void indexTopicMap(TopicMap topicMap) {
		try {
			logger.info("Start indexing");
			TopicMapIndexer idx = new TopicMapIndexer(
					new TopicMapIndexDirectory(new ConstructIdentifier(
							IdentifierType.TOPIC_MAP_LOCATOR, topicMap
									.getLocator().toExternalForm()),
							"/tmp/index/", "facets/"));
			idx.setCreateFacets(true);
			idx.setCreateOccurrenceFacets(true);
			idx.setCreateNameFacets(false);
			idx.setCreateRoleFacets(true);
			idx.setAllowOverwrite(true);
			idx.index(topicMap);
			logger.info("Finished indexing");
		} catch (Exception e) {
			logger.error("Could not create fulltext index", e);

		}
	}

}
