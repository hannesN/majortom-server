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
package de.topicmapslab.majortom.server.security;

import java.util.List;

/**
 * Interface for a Data Access Object for the user detail class.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IMTSUserDetailDAO {

	/**
	 * 
	 * @return a list of persisted users
	 */
	public List<MTSUserDetail> getUsers();
	
	/**
	 * Returns the user with the given id.
	 * 
	 * @param id the id of the user
	 * @return null or the user instance
	 */
	public MTSUserDetail getUser(String id);
	
	/**
	 * Returns the user with the given apikey.
	 * 
	 * @param apikey the apikey of the user
	 * @return null or the user instance
	 */
	public MTSUserDetail getUserByAPIKey(String apikey);
	
	/**
	 * persists the given user detail
	 * @param ud the user detail to persist
	 */
	public void persist(MTSUserDetail ud);
	
	/**
	 * Removes the given user from the persistence layer
	 * @param ud the user detail to remove
	 */
	public void remove(MTSUserDetail ud);
	
	/**
	 * Removes the user with the given id from the persistence layer
	 * @param id the id of the user detail to remove
	 */
	public void remove(String id);
	
}
