/**
 * Copyright (c) 2009, Magus Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package igual.metadata;

import igual.metadata.parsers.KeyValue;
import igual.metadata.parsers.LOMParser;
import igual.services.StorageService;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

//import com.sun.jersey.multipart.FormDataParam;
//import com.sun.tools.xjc.outline.ElementOutline;

/**
 * Example of handling to an existing graph
 * 
 * @author Dan Hanley - dan.hanley @ magus.co.uk
 */
public class HandleGraph {

	private String storeUrl;
	private String graphUri;
	private String message;

	public String getMessage() {
		return message;
	}

	public HandleGraph() {
		storeUrl = "http://localhost:8000";
		graphUri = "http://aginfra.eu/RDFLOM";
	}

	public HandleGraph(String uriGraph) {
		storeUrl = "http://localhost:8000";
		graphUri = "http://aginfra.eu/" + uriGraph;
	}

	public HandleGraph(String urlStore, String uriGraph) {
		storeUrl = urlStore;
		graphUri = uriGraph;
	}

	/**
	 * Read the contents of a file
	 * 
	 * @param aFile
	 * @return string
	 */
	static public String getContents(File aFile) {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return contents.toString();
	}

	/**
	 * AppendTriple: append triple to a graph
	 * 
	 * @param Index_DIR
	 * @param keyValues  arraylist with key values 
	 * 
	 * @return responseCode 1 on success, 0 otherwise
	 */
	
	public int AppendTriple(String Index_DIR, ArrayList<KeyValue> keyValues) {
		Store store;
		int responseCode = 0;

		try {

			store = new Store(storeUrl);

			// po5i: loop the Map
			String turtle = "";
			for (KeyValue tmpObject : keyValues) {
				String s = Index_DIR;
				String p = tmpObject.getKey();
				String o = tmpObject.getValue();

				turtle = "<" + graphUri + "/ID#" + s + "> <" + graphUri
						+ "/KEY#" + URLEncoder.encode(p, "UTF-8") + "> \"" + o
						+ "\" .\n";

				String response = store.append(graphUri, turtle,
						Store.InputFormat.TURTLE);
				responseCode = Integer.parseInt(response.substring(0, 3));
			}
			return 1;

		} catch (MalformedURLException e) {
			message = e.getMessage();
			return 0;
		} catch (IOException e) {
			message = e.getMessage();
			return 0;
		}
	}

	/**
	 * AppendGraph: Append rdf string to a graph in the store
	 * 
	 * @param fullpath
	 *            (of the graph)
	 * @return 1 on success, 0 on failure
	 */
	public int AppendGraph(String fullpath) {
		Store store;

		try {

			store = new Store(storeUrl);

			// po5i: load the file from the path
			File testFile = new File(fullpath);
			String exampleRDFGraph = getContents(testFile);

			String response = store.append(graphUri, exampleRDFGraph,
					Store.InputFormat.XML);
			message = response;
			//System.out.println(response);
			int responseCode = Integer.parseInt(response.substring(0, 3));

			if (responseCode == 200)
				return 1;
			else
				return 0;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * QueryGraph: query a graph
	 * 
	 * @param sparql
	 *            sparql query
	 * @return response result of the query
	 */
	public String QueryGraph(String sparql) {
		Store store;

		try {
			store = new Store(storeUrl);

			String response = store.query(sparql,
					Store.OutputFormat.TAB_SEPARATED);
			return response;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * updateField: Append rdf string to a graph in the store
	 * 
	 * @param fullpath
	 *            (of the graph)
	 * @return 1 on success, 0 on failure
	 */
	public int updateField(String ID, String field, String newValue) {
		int result = 0;
		int errores = 0;
		String idUrl = "<" + graphUri + "/ID#" + ID + ">";

		try {

			String predicate = "<" + graphUri + "/KEY#"
					+ URLEncoder.encode(field, "UTF-8") + ">";
			String object = "\"" + newValue + "\"";

			Store store = new Store(storeUrl);
			// delete old data
			String sqlDelete = "DELETE { GRAPH <" + graphUri + "> { " + idUrl
					+ " " + predicate + " ?o } } WHERE { " + idUrl + " "
					+ predicate + " ?o }";
			String response1 = store.update(sqlDelete);
	
			message = response1;

			// insert new one
			if (response1.indexOf("error") >= 0) {
				errores++;
			} else {
				String updateql2 = "INSERT DATA { GRAPH <" + graphUri + "> { "
						+ idUrl + " " + predicate + " " + object + " } } ";
				response1 = store.update(updateql2);
				if (response1.indexOf("error") >= 0) {
					errores++;
					message = response1;
				} else {
					result = 1;
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			message = e.getMessage();
		}

		return result;
	}

	/**
	 * deleteTriple: delete triple in a graph
	 * 
	 * @param identifier triple id
	 *           
	 * @return response 1 on success, 0 on failure
	 */
	
	public int deleteTriple(String identifier) {
		int response = 1;
		Store store;
		boolean continueAdd = true;
		try {
			String idUrl = "<" + graphUri + "/ID#" + identifier + ">";
			store = new Store(storeUrl);
			// delete old data
			String sqlDelete = "DELETE { GRAPH <" + graphUri
					+ "> { " + idUrl + " ?p  ?o } } WHERE { " + idUrl + " ?p ?o }";
			String response1 = store.update(sqlDelete);
			message = response1;

			// insert new one
			if (response1.indexOf("error") >= 0) {
				response = 0;
			} 		
			
			
		} catch (MalformedURLException e) {
			
			message = e.getMessage();
			return 0;
		} catch (IOException e) {
			message = e.getMessage();
			return 0;
		}
		return response;
	}
	/**
	 * update a graph using key-value pairs
	 * 
	 * @param Index_DIR
	 *            identifier to query the graph
	 * @param keyValues
	 *            values for update
	 * @return 1 if the updated was completed or 0 if an error ocurred
	 */

	public int updateTriple(String Index_DIR, ArrayList<KeyValue> keyValues) {
		Store store;
		boolean continueAdd = true;
		try {
			String idUrl = "<" + graphUri + "/ID#" + Index_DIR + ">";
			store = new Store(storeUrl);
			String predicate = "";
			String object = "";
			int errores = 0;
			String turtle;
			// search all current values for that identifier

			for (KeyValue tmpObject : keyValues) {
				String p = tmpObject.getKey();
				String o = tmpObject.getValue();
				predicate = "<" + graphUri + "/KEY#"
						+ URLEncoder.encode(p, "UTF-8") + ">";
				object = "\"" + o + "\"";
				// search current value for that key
				String sparqlQuery = "SELECT ?o FROM <" + graphUri
						+ "> WHERE { " + idUrl + " " + predicate + " ?o } ";

				String result = store.query(sparqlQuery,
						Store.OutputFormat.TAB_SEPARATED);

				// interpretar la respuesta
				int i = 0;
				String old_object;
				for (String triple : result.split("\n")) {

					if (!(triple.indexOf("?o") == 0 || triple.indexOf("#") == 0)) {
						// split by tabs

						old_object = triple.trim();

						i++;

						if (!object.equals(old_object)) {
							// delete old data
							String sqlDelete = "DELETE { GRAPH <" + graphUri
									+ "> { " + idUrl + " " + predicate
									+ " ?o } } WHERE { " + idUrl + " "
									+ predicate + " ?o }";
							String response1 = store.update(sqlDelete);
							message = response1;

							// insert new one
							if (response1.indexOf("error") >= 0) {
								errores++;
							} else {
								String updateql2 = "INSERT DATA { GRAPH <"
										+ graphUri + "> { " + idUrl + " "
										+ predicate + " " + object + " } } ";
								response1 = store.update(updateql2);
							}
						}
					}

				}
				if (i == 0) {// is new

					String updateql2 = "INSERT DATA { GRAPH <" + graphUri
							+ "> { " + idUrl + " " + predicate + " " + object
							+ " } } ";
					String response = store.update(updateql2);
				}
			}

			// delete values not present in the current values
			String sparqlQuery = "SELECT ?p FROM <" + graphUri + "> WHERE { "
					+ idUrl + "?p ?o } ";

			String result = store.query(sparqlQuery,
					Store.OutputFormat.TAB_SEPARATED);

			// interpretar la respuesta

			String old_key;
			for (String triple : result.split("\n")) {
				if (!(triple.indexOf("?p") == 0 || triple.indexOf("#") == 0)) {
					// split by tabs
					int existe = 0;
					old_key = triple.trim();
					// search in current list
					for (KeyValue tmpObject : keyValues) {
						String p = tmpObject.getKey();

						predicate = "<" + graphUri + "/KEY#"
								+ URLEncoder.encode(p, "UTF-8") + ">";

						if (old_key.equalsIgnoreCase(predicate))
							existe++;

					}

					if (existe == 0) {
						// delete
						String sqlDelete = "DELETE { GRAPH <" + graphUri
								+ "> { " + idUrl + " " + old_key
								+ " ?o } } WHERE { " + idUrl + " " + old_key
								+ " ?o }";
						String response1 = store.update(sqlDelete);
						message = response1;
					}
				}
			}

			if (errores > 1)
				return 0;
			else
				return 1;

		} catch (MalformedURLException e) {
			
			message = e.getMessage();
			return 0;
		} catch (IOException e) {
			message = e.getMessage();
			return 0;
		}
	}

	/**
	 * delete triples in a graph
	 * 
	 * @param Index_DIR
	 *            identifier to query the graph
	 * @param keyValues
	 *            values to delete
	 * @return 1 if the updated was completed or 0 if an error ocurred
	 */

	public int deleteAllTriples(String Index_DIR) {
		Store store;
		boolean continueAdd = true;
		try {
			String idUrl = "<" + graphUri + "/ID#" + Index_DIR + ">";
			store = new Store(storeUrl);
			// search all current values for that identifier

			String sparqlQuery = "SELECT ?p ?o FROM <" + graphUri
					+ "> WHERE { " + idUrl + " ?p ?o } ";

			String result = store.query(sparqlQuery,
					Store.OutputFormat.TAB_SEPARATED);

			// interpretar la respuesta
			int i = 0;
			String predicate, object;
			for (String triple : result.split("\n")) {
				if (!(triple.indexOf("?p") == 0 || triple.indexOf("#") == 0)) {
					// split by tabs
					int j = 1;

					predicate = "";
					object = "";
					for (String component : triple.split("\t")) {

						if (j == 1)
							predicate = component.trim();
						if (j == 2)
							object = component.trim();
						j++;
					}
					i++;
					// delete graph
					String sqlDelete = "DELETE { GRAPH <" + graphUri + "> { "
							+ idUrl + " " + predicate + " ?o } } WHERE { "
							+ idUrl + " " + predicate + " ?o }";

					String response1 = store.update(sqlDelete);

					if (response1.indexOf("error") >= 0) {
						return 0;
					}

				}
			}

			return 1;

		} catch (MalformedURLException e) {
			
			message = e.getMessage();
			return 0;
		} catch (IOException e) {
			
			message = e.getMessage();
			return 0;
		}
	}

	/**
	 * delete a graph
	 * 
	 * @param storeName
	 *            name of the graph
	 * 
	 * @return 1 if delete was completed or 0 if an error occurred
	 */

	public int deleteGraph(String storeName) {
		Store store;
		boolean continueAdd = true;
		try {
			String urlStorage = "<http://igualproject.org/" + storeName + ">";
			store = new Store(storeUrl);

			String sqlDelete = "DELETE { GRAPH " + urlStorage + " { "
					+ "?s ?p ?o } } WHERE { " + "?s ?p ?o }";

			String response1 = store.update(sqlDelete);
			return 1;

		} catch (MalformedURLException e) {
			message = e.getMessage();
			return 0;
		} catch (IOException e) {
			message = e.getMessage();
			return 0;
		}
	}

	
	/**
	 * getSubject: get subject given predicate and key
	 * 
	 * @param key     predicate
	 * @param value   value of object
	 *           
	 * @return subject 
	 */
	
	public String getSubject(String key, String value) {
		String subject = "";
		String result = QueryGraph("SELECT ?s  FROM <" + graphUri
				+ "> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"" + key
				+ "\")) . FILTER(REGEX(str(?o),\"" + value + "\")) }");

		for (String triple : result.split("\n")) {
			if (!(triple.indexOf("?s") == 0 || triple.indexOf("#") == 0)) {
				subject = triple.trim();

			}
		}

		return subject;
	}

	/**
	 * getObject: get object given subject and predicate
	 * 
	 * @param subject value of subject
	 * @param key   value of predicate
	 *           
	 * @return object 
	 */
	
	public String getObject(String subject, String key) {
		String object = "";
		String result = QueryGraph("SELECT ?o  FROM <" + graphUri
				+ "> WHERE { " + subject + " ?p ?o   . FILTER(REGEX(str(?p),\""
				+ key + "\"))}");

		for (String triple : result.split("\n")) {
			if (!(triple.indexOf("?o") == 0 || triple.indexOf("#") == 0)) {
				object = triple.trim().substring(1, triple.length() - 1);

			}
		}
		return object;
	}
	
	/**
	 * queryList: query graph to get list of elements
	 * 
	 * @return resultList arraylist of results
	 */
	/*public ArrayList<QueryResult> queryList() {
		ArrayList<QueryResult> resultList = new ArrayList<QueryResult>();
		// query graph to get list
		String elementID = "";
		String oldElementID = "";
		QueryResult qResult = null;
		int num = 0;
		String strResult = QueryGraph("SELECT ?s ?p ?o FROM <" + graphUri
				+ "> WHERE { ?s ?p ?o } ORDER BY ?s");
		int i = 0;
		String predicate = "";
		String object = "";
		for (String triple : strResult.split("\n")) {

			if (!(triple.indexOf("?s") == 0 || triple.indexOf("#") == 0)) {
				// split by tabs

				int j = 1;
				predicate = "";
				object = "";
				for (String component : triple.split("\t")) {
					if (j == 1)
						elementID = component.trim();
					if (j == 2)
						predicate = component.trim();
					if (j == 3)
						object = component.trim();
					j++;
				}

				if (!elementID.equals(oldElementID)) {
					num++;
					// add to the list
					if (i > 0)
						resultList.add(qResult);
					// create new object
					qResult = new QueryResult();

				}

				// add fields
				if (predicate.indexOf("title") != -1)
					qResult.setTitle(object.substring(1, object.length() - 1));

				if (predicate.indexOf("description") != -1)
					qResult.setDescription(object.substring(1,
							object.length() - 1));

				qResult.setID(elementID.substring(elementID.indexOf('#') + 1,
						elementID.length() - 1));
				oldElementID = elementID;
				i++;
			}

		}
		if (resultList.size() != num)
			resultList.add(qResult);
		return resultList;
	}
	*/
	/**
	 * Testing main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Append triple test

		System.out.println("Start...");
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		// Create a hash map
		HashMap hm = new HashMap();
		// Put elements to the map;
	/*	String id ="1_10_14_5";
		keyValues.add(new KeyValue("ID",id));
		keyValues.add(new KeyValue("LP","1"));
		keyValues.add(new KeyValue("lesson","10"));
		keyValues.add(new KeyValue("objective","14"));
		keyValues.add(new KeyValue("LA","5"));
		keyValues.add(new KeyValue("status","created"));
		keyValues.add(new KeyValue("LP_status_ACT","1_0"));*/
	/*	
		String id ="1";
		keyValues.add(new KeyValue("ID",id));
		keyValues.add(new KeyValue("language","en"));
		keyValues.add(new KeyValue("course","2"));
		keyValues.add(new KeyValue("lesson","1"));
		keyValues.add(new KeyValue("userID","2"));
		keyValues.add(new KeyValue("state","active"));
		keyValues.add(new KeyValue("objectives_count","2"));
		keyValues.add(new KeyValue("current_lesson","10"));
		keyValues.add(new KeyValue("current_objective","13"));
		keyValues.add(new KeyValue("creationTime","2012-07-12 12:11:00"));
		keyValues.add(new KeyValue("finishTime",""));
		*/
		
		/*	keyValues.add(new KeyValue("title","es"));
		keyValues.add(new KeyValue("description","Spanish"));
		String id ="es";
		keyValues.add(new KeyValue("title","en"));
		keyValues.add(new KeyValue("description","English"));
		String id ="en";*/
		keyValues.add(new KeyValue("title","pt"));
		keyValues.add(new KeyValue("description","Portuguese"));
		String id ="pt";
		HandleGraph graph = new HandleGraph("LP");
		// graph.AppendTriple("444446", hm);
		// graph.updateTriple("444446", hm);
		// graph.AppendTriple("444446", hm);
		
	//graph.AppendTriple(id, keyValues);
		System.out.println("Done!.");

		// Append file test

		/*
		 * System.out.println("Start...");
		 * 
		 * HandleGraph graph = new HandleGraph();
		 * graph.AppendGraph("/home/po5i/workspace/Test4store/lom-rdf1.rdf");
		 * 
		 * System.out.println("Done!.");
		 */

		// Query test
		HandleGraph graph2 = new HandleGraph("TAXONOMY");
		//graph2.deleteGraph("SOCIAL_INFO");
	//	graph2.updateField("1_10_13_4", "status", "FINISHED");
		/*
		 * ArrayList<QueryResult> result = new ArrayList<QueryResult>();
		 * 
		 * result = graph2.queryList(); for (QueryResult tmpObject : result) {
		 * String id = tmpObject.getID();
		 * 
		 * System.out.println("ID:"+id+ " NAme:"
		 * +tmpObject.getName()+"Descr"+tmpObject.getDescription());
		 * 
		 * }
		 */
		

		System.out.println("Test a sparql query: Start...");
		//graph2.deleteGraph("LP_ACTIVITY");
	//	graph2.deleteTriple("16");
		String subject = "<http://igualproject.org/LA/ID#179>";
		String sparqlXml =
				graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LP_ACTIVITY> WHERE { ?s ?p ?o } order by ?s LIMIT 200");
		//	graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/OBJECTIVE> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"activities\")) . FILTER(REGEX(str(?o),\"^1$\")) } order by ?o LIMIT 2000");
	//	graph2.QueryGraph("SELECT ?s FROM <http://igualproject.org/OBJECTIVE> WHERE { ?s ?p \"1\" . FILTER(REGEX(str(?p),\"activities\"))   } order by ?s");
	//	graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LS_USER_PROFILE> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"learning_styles_profile.profile\")) } order by ?s");
//		graph2.QueryGraph("SELECT ?p ?o FROM <http://igualproject.org/TAXONOMY> WHERE {<http://igualproject.org/TAXONOMY/ID#8> ?p ?o } ");
		
	//	graph2.QueryGraph("SELECT  ?p ?o FROM <http://igualproject.org/LA> WHERE { " + subject + " ?p ?o } order by ?p");
		//graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LA> WHERE { ?s ?p ?o } order by ?s");
//		graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/SOCIAL_INFO> WHERE { ?s ?p ?o . FILTER(REGEX(str(?s),\"#LA_\")) } order by ?s");
		//graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LESSON_OBJECTIVE> WHERE { ?s ?p ?o } order by ?s ?p");
		//graph2.QueryGraph("SELECT ?p ?o FROM <http://igualproject.org/LESSON> WHERE { <http://igualproject.org/LESSON/ID#1> ?p ?o }");
		
		// graph2.QueryGraph("SELECT  ?o FROM <http://igualproject.org/LESSON_OBJECTIVE> WHERE { <http://igualproject.org/LESSON_OBJECTIVE/ID#1> ?p ?o . FILTER(REGEX(str(?p),\"bin_location\"))} ");
		//		graph2.QueryGraph("SELECT ?o FROM <http://igualproject.org/SOCIAL_INFO> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"action\")) . FILTER(REGEX(str(?s),\"COURSE_17_14_\"))} ");				
//		graph2.QueryGraph("SELECT ?s ?o FROM <http://igualproject.org/SOCIAL_INFO> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"userid\")) } ");				

		// graph2.getObject("<http://igualproject.org/LA/ID#18>",
		// "bin_location");
		// String sparqlTAB =
		// graph2.QueryGraph("SELECT  ?s  FROM <http://igualproject.org/SOCIAL_INFO> WHERE { ?s ?p ?o . FILTER(REGEX(str(?o),\"rate\")) . FILTER(REGEX(str(?s),\"COURSE_1\")) } ORDER BY ?s");
		// graph2.QueryGraph("SELECT ?p (COUNT(?p) as ?pCount) FROM <http://igualproject.org/SOCIAL_INFO> WHERE { ?s ?p ?o . FILTER(REGEX(str(?s),\"COURSE_1\"))  } GROUP BY ?p");
		 //graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/USER> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"approved\")) . FILTER(REGEX(str(?o),\"1\")) }");
	//	graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LA> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"language\")) . FILTER(REGEX(str(?o),\"\")) }");
		// graph2.QueryGraph("SELECT ?s FROM <http://igualproject.org/USER> WHERE { ?s ?p \"0\" . FILTER(REGEX(str(?p),\"approved\")) }");
		// String sparqlTAB =
	//	graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/LA> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"title\")) } order by ?s");
		// String sparqlTAB = graph2
		// .QueryGraph("SELECT ?p ?o FROM <http://igualproject.org/SOCIAL_INFO> WHERE { <http://igualproject.org/SOCIAL_INFO/ID#1> ?p ?o  }");
		// String sparqlTAB =
	//	 graph2.QueryGraph("SELECT ?s ?p ?o FROM <http://igualproject.org/USER> WHERE { ?s ?p ?o . FILTER(REGEX(str(?p),\"username\")) . FILTER(REGEX(str(?o),\"test\")) }");
		// SELECT * FROM <http://igualproject.org/lomRdf> WHERE { ?id
		// <http://igualproject.org/lomRdf/KEY#lom.general.description.string>
		// ?value . FILTER(REGEX(?value,\"Protocolo\")) }
		// HandleGraph graph1 = new HandleGraph("USER");
		// String sparqlTAB = graph1.getSubject("username", "gcarrillo");
		// String sparqlTAB =
		// graph1.getObject("<http://igualproject.org/USER/ID#1>", "password");
		System.out.println(sparqlXml);
		// LOMParser.parseToKeyValue("/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/LO/la1.xml");
		// graph2.updateTriple("20", LOMParser.keyValues);
		//queryByIdService("OBJECTIVE", "3,6");
		String activityID = "41";
		String strResult = graph.		

		QueryGraph("SELECT ?s FROM <http://igualproject.org/ADAPTATION> WHERE { ?s ?p ?o . FILTER(REGEX(str(?s),\"#" + activityID +"_\")) } order by ?p");
		
		for (String triple1 : strResult.split("\n")) {

			if (!(triple1.indexOf("?s") == 0 || triple1.indexOf("#") == 0)) {
				String courseID = triple1.substring(
						triple1.indexOf("#") + 1, triple1.length() - 1);
				System.out.println("Courso:" + courseID);
				break;
			}
		}
		
		
		HandleGraph graph3 = new HandleGraph("LS_USER_PROFILE");
	
		
		
	}
	

}