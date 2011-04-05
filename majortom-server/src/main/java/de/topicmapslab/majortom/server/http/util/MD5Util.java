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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class to create a MD5 checksum of a String
 * 
 * @author Hannes Niederhausen
 *
 */
public class MD5Util {

	/**
	 * Calculates the md5 sum of the given string
	 * 
	 * @param source the source string used to calculate
	 * @return the String representing the MD5 sum
	 * @throws NoSuchAlgorithmException  if the MD5 algorithm is not available
	 */
	public static final String calculateMD5(String source) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] bytes = digest.digest(source.getBytes());
		
		
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
			hexString.append(Integer.toHexString(0xFF & bytes[i]));
		}
		return hexString.toString();
	}
}
