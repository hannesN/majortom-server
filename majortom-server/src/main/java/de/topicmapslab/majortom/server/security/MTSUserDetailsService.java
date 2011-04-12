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

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private IMTSGrantedAuthorityDAO grantedAuthorityDAO;
	/**
	 * 
	 */
	public MTSUserDetailsService(IMTSUserDetailDAO userDetailDAO, IMTSGrantedAuthorityDAO grantedAuthorityDAO) {
		super();
		this.userDetailDAO = userDetailDAO;
		this.grantedAuthorityDAO = grantedAuthorityDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		if (userDetailDAO.getUsers().size()==0) {
			init();
		}
		
		MTSUserDetail ud = userDetailDAO.getUser(username);
		if (ud==null)
			throw new UsernameNotFoundException("No user: "+username);
		
		return ud;
	}

	/**
	 * 
	 */
	private void init() {
		// init roles
		MTSGrantedAuthority user = new MTSGrantedAuthority("ROLE_USER");
		grantedAuthorityDAO.persist(user);
		
		MTSGrantedAuthority admin = new MTSGrantedAuthority("ROLE_ADMIN");
		grantedAuthorityDAO.persist(admin);
		
		
		MTSUserDetail ud = new MTSUserDetail();
		ud.setEnabled(true);
		ud.setPlainPassword("test");
		ud.setUsername("hans");
		ud.generateApiKey(System.currentTimeMillis() + "");

		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(3);
		authorities.add(user);
		ud.setAuthorities(authorities);
		userDetailDAO.persist(ud);

		ud = new MTSUserDetail();
		ud.setEnabled(true);
		ud.setPlainPassword("sEcReT");
		ud.setUsername("admin");

		authorities = new ArrayList<GrantedAuthority>(3);
		authorities.add(admin);
		ud.setAuthorities(authorities);
		userDetailDAO.persist(ud);
	}

}
