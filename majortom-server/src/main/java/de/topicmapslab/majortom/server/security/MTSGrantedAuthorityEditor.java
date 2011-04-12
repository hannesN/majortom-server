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

import java.beans.PropertyEditorSupport;

/**
 * Editor to serialize and deserialize a list of {@link MTSGrantedAuthority} instances.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MTSGrantedAuthorityEditor extends PropertyEditorSupport {
	private final IMTSGrantedAuthorityDAO grantedAuthorityDAO;
	
	
	/**
	 * Constructor
	 * @param grantedAuthorityDAO
	 */
	public MTSGrantedAuthorityEditor(IMTSGrantedAuthorityDAO grantedAuthorityDAO) {
		super();
		this.grantedAuthorityDAO = grantedAuthorityDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		MTSGrantedAuthority a = grantedAuthorityDAO.getAuthority(text.trim());
		
		if (a==null)
			throw new IllegalArgumentException("Invalid authority");
		
		setValue(a);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		super.setValue(value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return super.getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText() {
		return ((MTSGrantedAuthority) getValue()).getAuthority();
	}
}
