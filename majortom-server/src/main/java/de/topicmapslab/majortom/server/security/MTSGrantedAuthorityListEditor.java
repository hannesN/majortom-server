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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Editor to serialize and deserialize a list of {@link MTSGrantedAuthority} instances.
 * 
 * @author Hannes Niederhausen
 *
 */
public class MTSGrantedAuthorityListEditor extends PropertyEditorSupport {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String[] roles = text.split(",");
		
		List<MTSGrantedAuthority> authorities = new ArrayList<MTSGrantedAuthority>(roles.length);
		for (String r : roles) {
			authorities.add(new MTSGrantedAuthority(r.trim()));
		}
		
		setValue(authorities);
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
		if (getValue() instanceof Collection<?>) {
			List<?> values = (List<?>) getValue();
			System.out.println(getValue());
			StringBuilder b = new StringBuilder();

			Iterator<?> it = values.iterator();
			while (it.hasNext()) {
				MTSGrantedAuthority a = (MTSGrantedAuthority) it.next();
				b.append(a.getAuthority());
				if (it.hasNext())
					b.append(", ");
			}

			return b.toString();
		}
		return getValue().toString();
	}
}
