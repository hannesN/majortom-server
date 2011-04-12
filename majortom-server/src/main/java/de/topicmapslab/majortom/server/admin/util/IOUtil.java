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
package de.topicmapslab.majortom.server.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.server.admin.model.DatabaseConnectionData;

/**
 * This util class provides methods to persist and load 
 * persisted preferences.
 * 
 * @author Hannes Niederhausen
 * 
 */
public class IOUtil {
	/**
	 * Filename for the db properties 
	 */
	private static final String DB_PROP_FILENAME = System.getProperty("user.home")+"/.majortom-server/db.properties";
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(IOUtil.class);
	
	
	/**
	 * Tries to load the database connection from the bundle data files directory. 
	 * 
	 * If this fails the return value is <code>null</code>, 
	 * else a new {@link DatabaseConnectionData} object containing the data is returned.
	 * 
	 * @return <code>null</code> if no property file exists, the loaded data else
	 */
	public static final DatabaseConnectionData loadDatabaseConnectionData() {

		try {
			File engineFile = new File(DB_PROP_FILENAME);
			
			if (!engineFile.exists()) {
				logger.info("No database connection data found");
				return null;
			}
			
			Properties properties = new Properties();
			
			properties.load(new FileInputStream(engineFile));
			
			
			DatabaseConnectionData data = new DatabaseConnectionData();
			data.setHost(properties.getProperty(JdbcTopicMapStoreProperty.DATABASE_HOST));
			data.setUsername(properties.getProperty(JdbcTopicMapStoreProperty.DATABASE_USER));
			data.setPassword(properties.getProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD));
			data.setName(properties.getProperty(JdbcTopicMapStoreProperty.DATABASE_NAME));
			// get dialect:
			data.setDialect(SqlDialect.valueOf(properties.getProperty(JdbcTopicMapStoreProperty.SQL_DIALECT)));
			
			return data;
		} catch (Exception e) {
			logger.error("Could not load database connection data", e);
			return null;
		}
	}

	/**
	 * Writes the content of the data into a properties file stored in the bundles data file directory.
	 * 
	 * Old data will be overwritten.
	 * 
	 * @param data the data to persist
	 */
	public static final void persistDatabaseConnectionData(DatabaseConnectionData data) {
		try {
			
			// check if the majortom folder exists and if not create it
			File f = new File(System.getProperty("user.home")+"/.majortom-server");
			if (!f.exists()) {
				f.mkdir();
			}
			
			File engineFile = new File(DB_PROP_FILENAME);
			
			Properties properties = new Properties();
			
			properties.setProperty(JdbcTopicMapStoreProperty.DATABASE_HOST, data.getHost());
			properties.setProperty(JdbcTopicMapStoreProperty.DATABASE_USER, data.getUsername());
			properties.setProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD, data.getPassword());
			properties.setProperty(JdbcTopicMapStoreProperty.DATABASE_NAME, data.getName());
			properties.setProperty(JdbcTopicMapStoreProperty.SQL_DIALECT, SqlDialect.POSTGRESQL.name());
			
			properties.store(new FileOutputStream(engineFile), "Database Connections for the server");
			
		} catch (Exception e) {
			logger.error("Could not persist database connection data", e);
		}
	}

}
