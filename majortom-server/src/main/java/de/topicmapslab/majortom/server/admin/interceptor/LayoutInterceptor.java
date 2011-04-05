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
package de.topicmapslab.majortom.server.admin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author niederhausen
 *
 */
public class LayoutInterceptor  extends HandlerInterceptorAdapter {
	private static final String NO_LAYOUT = "noLayout:";

	private String layoutView;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		super.postHandle(request, response, handler, modelAndView);
		if (modelAndView==null)
			return;
		
		String originalView = modelAndView.getViewName();

		if (originalView != null) {
			includeLayout(modelAndView, originalView);
		}
	}

	private void includeLayout(ModelAndView modelAndView, String originalView) {
		boolean noLayout = originalView.startsWith(NO_LAYOUT);

		String realViewName = (noLayout) ? originalView.substring(NO_LAYOUT.length()) : originalView;

		if (noLayout) {
			modelAndView.setViewName(realViewName);
		} else {
			modelAndView.addObject("view", realViewName);
			modelAndView.setViewName(layoutView);
		}
	}

	/**
	 * Returns the name of the layout view
	 * @return
	 */
	public String getLayoutView() {
		return layoutView;
	}

	/**
	 * Sets the name of the layout view
	 * @param layoutView
	 */
	public void setLayoutView(String layoutView) {
		this.layoutView = layoutView;
	}
}

