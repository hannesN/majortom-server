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
package de.topicmapslab.majortom.server.admin.model;

import java.util.List;

/**
 * Interface for a Data Access Object for {@link DatabaseConnectionData}.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IDatabaseConnectionDataDAO {
	
	/**
	 * 
	 * @return a list of persisted authorities
	 */
	public List<DatabaseConnectionData> getConnections();
	
	/**
	 * Returns the authority with the given id.
	 * 
	 * @param id the id of the dbconnection  
	 * @return null or the {@link DatabaseConnectionData} instance
	 */
	public DatabaseConnectionData getConnection(String id);
	
	
	/**
	 * persists the given authority
	 * @param dbconnection the connection to persist
	 */
	public void persist(DatabaseConnectionData dbconnection);
	
	/**
	 * Removes the given authority from the persistence layer
	 * @param dbconnection the connection to remove
	 */
	public void remove(DatabaseConnectionData dbconnection);
	
}
