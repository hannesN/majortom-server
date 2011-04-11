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

import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Hannes Niederhausen
 *
 */
public class MTSUserDetailsService implements UserDetailsService {
	private static final long serialVersionUID = -7779033884352226969L;
	private IMTSUserDetailDAO userDetailDAO;

	/**
	 * 
	 */
	public MTSUserDetailsService(IMTSUserDetailDAO userDetailDAO) {
		super();
		this.userDetailDAO = userDetailDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		MTSUserDetail ud = userDetailDAO.getUser(username);
		if (ud==null) {
			if ("admin".equals(username)) {
				ud = new MTSUserDetail();
				ud.setEnabled(true);
				ud.setPlainPassword("test");
				ud.setUsername("hans");
				ud.generateApiKey(System.currentTimeMillis()+"");
				
				ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(3);
				authorities.add(new MTSGrantedAuthority("ROLE_USER"));
				ud.setAuthorities(authorities);
				userDetailDAO.persist(ud);
				
				ud = new MTSUserDetail();
				ud.setEnabled(true);
				ud.setPlainPassword("sEcReT");
				ud.setUsername(username);
				
				authorities = new ArrayList<GrantedAuthority>(3);
				authorities.add(new MTSGrantedAuthority("ROLE_ADMIN"));
				ud.setAuthorities(authorities);
				userDetailDAO.persist(ud);
			} else {
				throw new UsernameNotFoundException("Invalid User: "+username);
			}
		}
		
		return ud;
	}

}
