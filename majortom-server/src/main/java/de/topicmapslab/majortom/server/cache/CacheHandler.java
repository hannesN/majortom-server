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

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CacheHandler {

	final static Lock lock = new ReentrantLock(true);
	private static Class<? extends ICache> cacheClazz;
	private static boolean enabled = false;

	/**
	 * Returns the cache instance.
	 * 
	 * @return the instance or <code>null</code>
	 * @throws CacheException
	 */
	public static ICache getCache() throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (cacheClazz == null) {
				newCache();
			}
			/*
			 * caching disabled
			 */
			if (!enabled) {
				return null;
			}
			try {
				return cacheClazz.newInstance();
			} catch (Exception e) {
				throw new CacheException("Cannot instantiate cache!", e);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Checks if caching is enabled
	 * 
	 * @return <code>true</code> if caching is enabled, <code>false</code> otherwise.
	 */
	public static boolean isCachingEnabled() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (cacheClazz == null) {
				newCache();
			}
			return enabled;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Internal method to initialize the cache
	 */
	@SuppressWarnings("unchecked")
	private static void newCache() {
		InputStream stream;
		try {
			URL url = CacheHandler.class.getResource("/cache.properties");
			if (url == null) {
				enabled = false;
				return;
			}
			stream = url.openStream();
			Properties property = new Properties();
			property.load(stream);
			stream.close();

			Object o = property.get("class");
			if (o == null) {
				enabled = false;
				return;
			}
			Class<?> clazz = Class.forName(o.toString());
			if (!ICache.class.isAssignableFrom(clazz)) {
				throw new CacheException("The class property is invalid. Should be set to a class extends ICache!");
			}
			cacheClazz = (Class<? extends ICache>) clazz;
			enabled = true;
		} catch (Exception e) {
			throw new CacheException("An I/O error occurred during initialization of redis cache!", e);
		}
	}
}
