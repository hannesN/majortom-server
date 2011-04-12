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
package de.topicmapslab.majortom.server.admin.model;

/**
 * Small class for data binding to create new topic map
 * 
 * @author Hannes Niederhausen
 *
 */
public class NewTopicMapData {

	private String baselocator;
	
	private boolean inmemory = true;
	
	private int initialCapacity = 16;
	
	private String filePath;
	
	

	/**
	 * @return the baselocator
	 */
	public String getBaselocator() {
		return baselocator;
	}

	/**
	 * @param baselocator the baselocator to set
	 */
	public void setBaselocator(String baselocator) {
		this.baselocator = baselocator;
	}

	/**
	 * @return the inmemory
	 */
	public boolean isInmemory() {
		return inmemory;
	}

	/**
	 * @param inmemory the inmemory to set
	 */
	public void setInmemory(boolean inmemory) {
		this.inmemory = inmemory;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * @param initialCapacity the initialCapacity to set
	 */
	public void setInitialCapacity(int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}
	
	/**
	 * @return the initialCapacity
	 */
	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "NewTopicMapData [baselocator=" + baselocator + ", inmemory=" + inmemory + "]";
	}
	
	
	
}
