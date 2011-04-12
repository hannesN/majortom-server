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
package de.topicmapslab.majortom.server.cache;

import java.util.regex.Pattern;

/**
 * Cache class for MaJorToM server
 * 
 * @author Sven Krosse
 */
public interface ICache {

	/**
	 * clears the cache
	 * 
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public void clear() throws CacheException;
	
	/**
	 * clears the cache
	 * 
	 * @param topicMapId clears the cache of the given topic map id
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public void clear(String topicMapId) throws CacheException;

	/**
	 * Returns the value for the given key
	 * 
	 * @param key
	 *            the key
	 * @return the value or <code>null</code>
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public String get(String key) throws CacheException;

	/**
	 * Checks if the key is stored in the cache
	 * 
	 * @param key
	 *            the key
	 * @return <code>true</code> if there is a value for the key, <code>false</code> otherwise
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public boolean isCached(String key) throws CacheException;

	/**
	 * Cache the given value for the given key
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public void set(String key, String value) throws CacheException;

	/**
	 * Remove the value for the given key
	 * 
	 * @param key
	 *            the key
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public void remove(String key) throws CacheException;

	/**
	 * Remove all values with a key matching the given pattern
	 * 
	 * @param pattern
	 *            the pattern
	 * @throws CacheException
	 *             thrown if operation fails
	 */
	public void remove(Pattern pattern) throws CacheException;

}
