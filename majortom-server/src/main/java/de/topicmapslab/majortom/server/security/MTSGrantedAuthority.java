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

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Hannes Niederhausen
 *
 */
@Entity(name="AUTHORITIES")
public class MTSGrantedAuthority implements GrantedAuthority {
	private static final long serialVersionUID = -6940956595857480163L;
	
	@Id
	private String authority;

	/**
	 * 
	 */
	public MTSGrantedAuthority() {
	}
	
	
	/**
	 * Constructor with an initial authority value
	 * @param authority
	 */
	public MTSGrantedAuthority(String authority) {
		super();
		this.authority = authority;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthority() {
		return authority;
	}
	
	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	

}
