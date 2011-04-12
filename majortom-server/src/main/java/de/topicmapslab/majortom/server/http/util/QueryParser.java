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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topicmapslab.majortom.server.http.model.QueryExample;

/**
 * Parses the query
 * 
 * @author Hannes Niederhausen
 * 
 */
public class QueryParser {

	private static Logger logger = LoggerFactory.getLogger(QueryParser.class.getName());

	private static String FILENAME = "/disk/localhome/epg/webdir/epgexamples.txt";

	private static String FALLBACK = "queries.txt";

	private static List<QueryExample> examples = null;

	private static long lastModified = 0;

	/**
	 * Returns the examples stored in the query file
	 * 
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public static List<QueryExample> getExamples(HttpServletRequest req) throws IOException {
		File f = new File(FILENAME);

		if ((examples == null) || (f.lastModified() > lastModified)) {
			lastModified = f.lastModified();
			examples = new ArrayList<QueryExample>(10);
		}
		if (f.exists()) {
			loadFile(f);
		}

		f = new File(req.getServletContext().getRealPath(FALLBACK));
		loadFile(f);

		return examples;
	}

	private static void loadFile(File f) throws FileNotFoundException, IOException {
		logger.info("Loading File:" + f.getName());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		int counter = examples.size();
		while ((line = reader.readLine()) != null) {
			QueryExample q = new QueryExample();
			q.setValId(counter);
			// first line is the name
			q.setName(line);
			line = reader.readLine();
			// second line is the description
			q.setDescription(line);
			// next lines until line is either empty or null is the query
			StringBuilder query = new StringBuilder();
			line = reader.readLine();
			while (line != null && line.length() > 0) {
				query.append(line);
				query.append(" ");
				line = reader.readLine();
			}
			q.setQuery(query.toString());

			if (!examples.contains(q)) {
				examples.add(q);
				counter++;
			}
		}
	}

}
