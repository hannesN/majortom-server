/*******************************************************************************
 * Copyright 2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.server.http.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.topicmapslab.majortom.server.http.util.JSONView;
import de.topicmapslab.majortom.server.security.IMTSUserDetailDAO;
import de.topicmapslab.majortom.server.security.MTSUserDetail;
import de.topicmapslab.majortom.server.topicmaps.ITopicMapHandler;
import de.topicmapslab.tmql4j.components.processor.runtime.ITMQLRuntime;

/**
 * @author niederhausen
 *
 */
public class AbstractMajorToMController {
	@Autowired
	private IMTSUserDetailDAO userDetailDAO;
	protected ITMQLRuntime runtime;
	@Autowired
	protected ITopicMapHandler tmh;

	/**
	 * 
	 */
	public AbstractMajorToMController() {
		super();
	}

	protected String extractBodyContent(HttpServletRequest req) throws IOException, UnsupportedEncodingException {
		InputStream is = req.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		int c = 0;
	
		// read query
		while ((c = is.read()) != -1) {
			bos.write(c);
		}
		// save query
		String res = new String(bos.toByteArray(), "UTF-8");
	
		// clean up
		is.close();
		bos.close();
		return res;
	}

	protected String getResultJSON(int code, String msg) {
		return getResultJSON(code, msg, null, null);
	}

	protected String getResultJSON(int code, String msg, String data) {
		return getResultJSON(code, msg, data, null);
	}

	protected String getResultJSON(int code, String msg, String data, Meta meta) {
		StringBuilder b = new StringBuilder();
	
		b.append("{");
	
		b.append("\"code\" : ");
		b.append("\"");
		b.append(code);
		b.append("\"");
	
		b.append(", \"msg\" : ");
		b.append("\"");
		b.append(msg);
		b.append("\"");
	
		if (data != null) {
			b.append(", \"data\" : ");
			b.append(data);
		}
	
		if (meta != null) {
			b.append(", \"meta\" : {");
			b.append("\"duration\" : \"");
			b.append(Long.toString(meta.getDuration()));
			b.append("\", \"cached\" : \"");
			b.append(Boolean.toString(meta.isCached()));
			b.append("\", \"query\" : \"");
			b.append(StringEscapeUtils.escapeJavaScript(meta.getQuery()));
			b.append("\"}");
		}
	
		b.append("}");
	
		return b.toString();
	}

	/**
	 * Checks if a user with the given apikey exists
	 * @param apikey
	 * @return 
	 */
	public boolean isValidKey(String apikey) {
		MTSUserDetail r = userDetailDAO.getUserByAPIKey(apikey);
		
		return r!=null;
	}
	
	/**
	 * @param userDetailDAO the userDetailDAO to set
	 */
	public void setUserDetailDAO(IMTSUserDetailDAO userDetailDAO) {
		this.userDetailDAO = userDetailDAO;
	}
	
	/**
	 * @param tmh the tmh to set
	 */
	public void setTmh(ITopicMapHandler tmh) {
		this.tmh = tmh;
	}
	
	/**
	 * Returns a new {@link JSONView} with the error message: "Invalid API Key"
	 * @return
	 */
	public JSONView invalidApiKeyResult() {
		return new JSONView(getResultJSON(1, "Invalid api key"));
	}
	
	class Meta {
		long duration;
		boolean cached;
		private String query;

		/**
		 * Constructor
		 * 
		 * @param duration
		 * @param cached
		 */
		public Meta(String query) {
			super();
			this.query = query;
		}

		/**
		 * @return the duration
		 */
		public long getDuration() {
			return duration;
		}

		/**
		 * @return the cached
		 */
		public boolean isCached() {
			return cached;
		}

		/**
		 * @return the query
		 */
		public String getQuery() {
			return query;
		}

		/**
		 * @param cached
		 *            the cached to set
		 */
		public void setCached(boolean cached) {
			this.cached = cached;
		}

		/**
		 * @param duration
		 *            the duration to set
		 */
		public void setDuration(long duration) {
			this.duration = duration;
		}
	}

}