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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

/**
 * Redis cache implementation
 * 
 * @author Sven Krosse
 */
public class RedisCache implements ICache {

	private static final String IOERROR = "An I/O error occurred during operation of redis cache!";
	private Properties jedisProperties = new Properties();

	/**
	 * constructor
	 */
	public RedisCache() throws CacheException {
		InputStream stream;
		try {
			URL url = getClass().getResource("/jedis.properties");
			if (url == null) {
				throw new CacheException("Missing properties for redis cache!");
			}
			stream = url.openStream();
			jedisProperties.load(stream);
			stream.close();
		} catch (Exception e) {
			throw new CacheException("An I/O error occurred during initialization of redis cache!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() throws CacheException {
		try {
			Jedis jedis = openConnection();
			jedis.flushDB();
			jedis.disconnect();
		} catch (IOException e) {
			throw new CacheException(IOERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String get(String key) throws CacheException {
		try {
			Jedis jedis = openConnection();
			String value = jedis.get(key);
			jedis.disconnect();
			return value;
		} catch (IOException e) {
			throw new CacheException(IOERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCached(String key) throws CacheException {
		return get(key) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(String key, String value) throws CacheException {
		try {
			Jedis jedis = openConnection();
			jedis.set(key, value);
			jedis.disconnect();
		} catch (IOException e) {
			throw new CacheException(IOERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(String key) throws CacheException {
		try {
			Jedis jedis = openConnection();
			jedis.del(key);
			jedis.disconnect();
		} catch (IOException e) {
			throw new CacheException(IOERROR, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Pattern pattern) throws CacheException {
		try {
			Jedis jedis = openConnection();
			Set<String> keys = jedis.keys(pattern.pattern());
			if (!keys.isEmpty()) {
				jedis.del(keys.toArray(new String[keys.size()]));
			}
			jedis.disconnect();
		} catch (IOException e) {
			throw new CacheException(IOERROR, e);
		}
	}

	/**
	 * Internal method to get the redis connection
	 * 
	 * @return the redis connection
	 */
	Jedis openConnection() {
		Jedis jedis = new Jedis(jedisProperties.getProperty("hostname"));
		jedis.auth(jedisProperties.getProperty("password"));
		jedis.select(Integer.parseInt(jedisProperties.getProperty("storeId")));
		return jedis;
	}

}
