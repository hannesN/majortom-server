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
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.topicmapslab.majortom.server.http.util.MD5Util;

/**
 * @author Hannes Niederhausen
 *
 */
@Entity(name="USER")
public class MTSUserDetail implements UserDetails {
	private static final long serialVersionUID = 6559868279801013282L;

	@Id
	private String username;
	
	private transient String plainPassword;
	
	private String password;

	private boolean enabled;
	
	private String apiKey;
	
	@ManyToMany(fetch=FetchType.EAGER, targetEntity=MTSGrantedAuthority.class)
	private List<GrantedAuthority> authorities;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return the plainPassword
	 */
	public String getPlainPassword() {
		return plainPassword;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		
		this.username = username;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param plainPassword the plainPassword to set
	 */
	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
		setPassword(MD5Util.calculateMD5(plainPassword));
	}
	
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}
	
	/**
	 * Generates an api key based in the username, the password and the given salt
	 * 
	 * @param salt
	 */
	public void generateApiKey(String salt) {
		String tmp = username+salt+password+salt;
		
		apiKey = MD5Util.calculateMD5(tmp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MTSUserDetail other = (MTSUserDetail) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	
	/**
	 * Helper method for the UI
	 * 
	 * @return the plainAuthorities
	 */
	public List<String> getPlainAuthorities() {
		ArrayList<String> tmp = new ArrayList<String>();
		if (authorities!=null) {
			for (GrantedAuthority a : getAuthorities()) {
				tmp.add(a.getAuthority());
			}
		}
		return tmp;
	}
	
	/**
	 *  Helper method for the UI
	 * 
	 * @param plainAuthorities the plainAuthorities to set
	 */
	public void setPlainAuthorities(List<String> plainAuthorities) {
		ArrayList<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
		for (String pa : plainAuthorities) {
			newAuthorities.add(new MTSGrantedAuthority(pa));
		}
		setAuthorities(newAuthorities);
	}
}
