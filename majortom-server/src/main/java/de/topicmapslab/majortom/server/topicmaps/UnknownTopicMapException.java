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
package de.topicmapslab.majortom.server.topicmaps;

/**
 * Exception thrown by the getTopicMapId method in the Topic maps Handler
 * 
 * @author Hannes Niederhausen
 *
 */
public class UnknownTopicMapException extends Exception {

	private static final long serialVersionUID = -8653543721625846495L;

	/**
	 * 
	 */
	public UnknownTopicMapException() {
	}

	/**
	 * @param message
	 */
	public UnknownTopicMapException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnknownTopicMapException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnknownTopicMapException(String message, Throwable cause) {
		super(message, cause);
	}

}
