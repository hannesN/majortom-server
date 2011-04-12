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

import org.springframework.security.core.codec.Hex;

/**
 * Util class to create a MD5 checksum of a String.
 * 
 * parts of the code are copied from {@link Hex}
 * 
 * @author Hannes Niederhausen
 *
 */
public class MD5Util {

	private static final char[] HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
	
	/**
	 * Calculates the md5 sum of the given string
	 * 
	 * @param source the source string used to calculate
	 * @return the String representing the MD5 sum
	 * @throws NoSuchAlgorithmException  if the MD5 algorithm is not available
	 */
	public static final String calculateMD5(String source)  {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(source.getBytes());
			
			final int nBytes = bytes.length;
			char[] result = new char[2*nBytes];

			int j = 0;
			for (int i=0; i < nBytes; i++) {
			    // Char for top 4 bits
			    result[j++] = HEX[(0xF0 & bytes[i]) >>> 4 ];
			    // Bottom 4
			    result[j++] = HEX[(0x0F & bytes[i])];
			}

			return new String(result);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not calculate md5", e);
		}
	}
}
