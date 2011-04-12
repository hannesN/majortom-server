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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hannes Niederhausen
 *
 */
@Repository
@Transactional
public class MTSUserDetailDAO implements IMTSUserDetailDAO {

	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	public MTSUserDetailDAO(SessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MTSUserDetail> getUsers() {
		return currentSession().createQuery("from "+MTSUserDetail.class.getName()).list();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MTSUserDetail getUserByAPIKey(String apikey) {
		List<?> res = currentSession().createQuery("from "+MTSUserDetail.class.getName()+" as ud where ud.apiKey='"+apikey+"'").list();
		
		if (res.isEmpty())
			return null;
		
		return (MTSUserDetail) res.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MTSUserDetail getUser(String id) {
		return (MTSUserDetail) currentSession().get(MTSUserDetail.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void persist(MTSUserDetail ud) {
		currentSession().saveOrUpdate(ud);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(MTSUserDetail ud) {
		currentSession().delete(ud);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(String id) {
		
		currentSession().delete(getUser(id));
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}
}
