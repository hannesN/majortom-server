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
package de.topicmapslab.majortom.server.topicmaps;

import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.web.multipart.MultipartFile;
import org.tmapi.core.TopicMap;

/**
 * Interface for the Topic Maps Handler
 * 
 * @author Hannes Niederhausen
 *
 */
public interface ITopicMapHandler {

	/**
	 * Returns the id of the topic map with the given locator
	 * 
	 * @param baseLocator
	 *            the base locator to check
	 * @return the id of the topic map with the given base locator
	 * @throws UnknownTopicMapException
	 *             if no topic map with the given locator exists
	 */
	public abstract String getTopicMapId(String baseLocator) throws UnknownTopicMapException;

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
	public abstract String createTopicMap(String baseLocator, boolean inMemory, int initialCapacity) throws ServletException;

	/**
	 * Closes the topic map with the given id and frees resources
	 */
	public abstract void closeTopicMap(String id);

	/**
	 * Removes the topic map with the given id
	 */
	public abstract void removeTopicMap(String id);

	/**
	 * Clears the topic map with the given id
	 */
	public abstract void clearTopicMap(String id);

	/**
	 * Returns the topic map with the given id.
	 * 
	 * @param id
	 *            the id of the topic map
	 * @return the {@link TopicMap}
	 * @throws IllegalArgumentException
	 *             if there is no topic map for the id
	 */
	public abstract TopicMap getTopicMap(String id) throws IllegalArgumentException;

	/**
	 * Returns an unmodifiable map containing all topic maps and their id
	 * 
	 * @return the map with the created topic maps
	 */
	public abstract Map<String, TopicMap> getTopicMapMap();

	/**
	 * Deserializes the file and stores the data in the topic map with the given
	 * id
	 * 
	 * @param id
	 *            id of the target topic map
	 * @param file
	 *            the multipart file
	 */
	public abstract void loadFromFileUpload(String id, final MultipartFile file);

	/**
	 * Deserializes the local file and stores the data in the topic map with the
	 * given id
	 * 
	 * @param id
	 *            id of the target topic map
	 * @param filename
	 *            the name of the local file
	 */
	public abstract void loadFromLocalFile(String id, final String filename);

}