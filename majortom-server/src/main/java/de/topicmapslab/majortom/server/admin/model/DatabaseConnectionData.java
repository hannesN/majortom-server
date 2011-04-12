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
package de.topicmapslab.majortom.server.admin.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;

/**
 * Model for a database connection used for the database administration an the db backend for the topic maps handler.
 * 
 * @author Hannes Niederhausen
 *
 */
@Entity(name="DBCONNECTION")
public class DatabaseConnectionData {

	@Id
	private String id;
	
	private String host;
	
	private String name;
	
	private String username;
	
	private String password;
	
	private transient SqlDialect dialect;

	/**
	 * 
	 */
	public DatabaseConnectionData() {
		this.id = "default";
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		if (host==null)
			return "";
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		if (name==null)
			return "";
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		if (username==null)
			return "";
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		if (password==null)
			return "";
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the dialect
	 */
	public SqlDialect getDialect() {
		if (dialect==null)
			return SqlDialect.POSTGRESQL;
		return dialect;
	}

	/**
	 * @param dialect the dialect to set
	 */
	public void setDialect(SqlDialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DatabaseConnectionData [host=" + host + ", name=" + name + ", username=" + username + ", password=" + password
				+ ", dialect=" + dialect + "]";
	} 
	
	
	
}
