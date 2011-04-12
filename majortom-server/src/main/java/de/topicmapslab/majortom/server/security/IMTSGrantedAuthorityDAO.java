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
public interface IMTSGrantedAuthorityDAO {

	/**
	 * 
	 * @return a list of persisted authorities
	 */
	public List<MTSGrantedAuthority> getAuthorities();
	
	/**
	 * Returns the authority with the given id.
	 * 
	 * @param id the id of the authority (name) 
	 * @return null or the {@link MTSGrantedAuthority} instance
	 */
	public MTSGrantedAuthority getAuthority(String id);
	
	
	/**
	 * persists the given authority
	 * @param ud the authority to persist
	 */
	public void persist(MTSGrantedAuthority authority);
	
	/**
	 * Removes the given authority from the persistence layer
	 * @param ud the authority to remove
	 */
	public void remove(MTSGrantedAuthority authority);
	
}
