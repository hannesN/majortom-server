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
public class DatabaseConnectionDataDAO implements IDatabaseConnectionDataDAO {

	private SessionFactory sessionFactory;
	
	
	/**
	 * Constructor
	 * @param sessionFactory the sessionFactory
	 */
	public DatabaseConnectionDataDAO(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DatabaseConnectionData> getConnections() {
		return currentSession().createQuery("from "+DatabaseConnectionData.class.getName()).list();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseConnectionData getConnection(String id) {
		return (DatabaseConnectionData) currentSession().get(DatabaseConnectionData.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(DatabaseConnectionData dbconnection) {
		currentSession().saveOrUpdate(dbconnection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(DatabaseConnectionData dbconnection) {
		currentSession().delete(dbconnection);
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}
}
