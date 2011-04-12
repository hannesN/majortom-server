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
/**
 * 
 */
package de.topicmapslab.majortom.server.http.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * View which gets an already rendered json string.
 * 
 * @author Hannes Niederhausen
 * 
 */
public class JSONView extends AbstractView {

	private String jsonString;
	private String callback;

	/**
	 * Constructor
	 * @param jsonString result json String
	 */
	public JSONView(String jsonString) {
		this.jsonString = jsonString;
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param jsonString the json string
	 * @param callback a callback function name for JSONP
	 */
	public JSONView(String jsonString, String callback) {
		super();
		this.jsonString = jsonString;
		this.callback = callback;
	}



	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setCharacterEncoding("UTF-8");
		
		if (callback!=null) {
			response.setContentType("application/javascript");
			response.getWriter().write(callback);
			response.getWriter().write("(");
			response.getWriter().write(jsonString);
			response.getWriter().write(");");
		} else {
			response.setContentType("application/json");
			response.getWriter().write(jsonString);
		}
	}
}
