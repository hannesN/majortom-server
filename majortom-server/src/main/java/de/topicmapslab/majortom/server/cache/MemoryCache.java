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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * 
 * @author Sven Krosse
 */
public class MemoryCache implements ICache {

	final static Lock lock = new ReentrantLock(true);
	private final static Map<String, String> cache = new HashMap<String, String>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			cache.clear();
		} finally {
			lock.unlock();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String get(String key) throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			return cache.get(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCached(String key) throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			return cache.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(String key, String value) throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			cache.put(key, value);
		} finally {
			lock.unlock();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(String key) throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			cache.remove(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Pattern pattern) throws CacheException {
		while (!lock.tryLock()) {
			// VOID
		}

		try {
			for (String key : new HashSet<String>(cache.keySet())) {
				if (pattern.matcher(key).matches()) {
					cache.remove(key);
				}
			}
		} finally {
			lock.unlock();
		}
	}

}
