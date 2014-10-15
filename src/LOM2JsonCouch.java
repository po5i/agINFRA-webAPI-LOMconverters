import igual.metadata.HandleGraph;
import igual.metadata.Misc;
import igual.metadata.XMLValidator;
import igual.metadata.parsers.GeneralParser;
import igual.metadata.parsers.KeyValue;
import igual.metadata.parsers.LOMParser;
import igual.metadata.parsers.LSParser;
import igual.metadata.parsers.SurveyParser;
import igual.services.StorageService;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;*/

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
//import WebAPI.repository.XMLResponse;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.io.FileUtils;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.vcard.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import java.util.StringTokenizer;



public class LOM2JsonCouch {

	/**
	 * agINFRA Social visualization components aux. tool
	 * LOM to Couchdb RDF/JSON conversion and storing
	 * @param args
	 * @throws FileNotFoundException 
	 * 
	 * CHECK:
	 * processed.txt file
	 * "debug" flags
	 */
	public static void main(String[] args) throws FileNotFoundException {

		System.out.println("Hello");
		
		//PARAMS
		if(args.length != 6)
	    {
	        System.out.println("Proper Arguments are: [datasets path] [uriGraph name] [local couchdb proxy hostname and directory (ej: localhost/aginfra)] [commit to couchdb] [limit] [offset]");
	        System.exit(0);
	    }
		
		String dspath = args[0];
		//String urlStore = args[1];
		String uriGraph = args[1];
		String localCouchdbProxy = args[2];
		String commit4store_arg = args[3];
		int limit = Integer.parseInt(args[4]);
		int offset = Integer.parseInt(args[5]);
		
		
		
		/*String dspath = "/home/carlos/workspace/WebAPI/ds/";
		//String urlStore = "http://4store.ipb.ac.rs:81";
		String uriGraph = "aginfra_eu";
		String localCouchdbProxy = "localhost/aginfra";
		String commit4store_arg = "false";*/
		
		boolean commit4store = false;
		if(commit4store_arg.equals("commit"))
			commit4store = true;
		
		//couchdb: http://wiki.apache.org/couchdb/Getting_started_with_Java
		//Session session = new Session("localhost",5984);
		//Database db = session.getDatabase("aginfra_datasets");
		
		
		String output = "0";// valor de retorno
		String status = "ERROR";
		String errorDescription = "";
		String tmpMetadataFile = "";
		// check if type is valid
		boolean bContinuar = true;
		//StorageService service;
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		HashMap fileDatasetMap = new HashMap(); 
		int counter = 0;
		
		
		
		//Fetch and download IPB metadata sets.
		//CouchDB via PHP local proxy
		//http://agro.ipb.ac.rs/agcouchdb/_design/datasets/_view/list?limit=10
		//http://localhost/ag_couch_proxy/proxy-IPB-datasets.php
		try{
			System.out.println("Connecting IPB CouchDB...");
			
			String url = "http://"+localCouchdbProxy+"/ag_couch_proxy/proxy-IPB-datasets.php?dspath="+dspath;
			WebResource webResource = Client.create().resource(url);
			//System.out.println(url);
			ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML,MediaType.WILDCARD).get(ClientResponse.class);
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			
			
			//String response_str = response.getEntity(String.class);	
			String response_str = getStringFromInputStream(response.getEntityInputStream());
			System.out.println(response_str);	//debug
			
			System.out.println("Finished IPB call");
			
			if(response_str.equals("")){
				errorDescription = "There are no new datasets available to download.";
			}
			
			
			
			
			
			
			System.out.println("Reading Dataset Map for new files...");
			
			//READ CSV
			//create BufferedReader to read csv file
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
           
            //read comma separated file line by line
            Scanner scanner = new Scanner(response_str);
            while (scanner.hasNextLine()) 
            {
                    lineNumber++;
                    String strLine = scanner.nextLine();
                   
                    //break comma separated line using ","
                    st = new StringTokenizer(strLine, ",");
                    
                    String datasetFile = "";
                    String datasetName = "";
                   
                    while(st.hasMoreTokens())
                    {
                            //display csv values
                            tokenNumber++;
                            //System.out.println("Line # " + lineNumber +", Token # " + tokenNumber + ", Token : "+ st.nextToken());
                            if(tokenNumber == 1)
                            	datasetFile = st.nextToken();
                            if(tokenNumber == 2)
                            	datasetName = st.nextToken();
                    }
                    
                    fileDatasetMap.put(datasetFile,datasetName);
                   
                    //reset token number
                    tokenNumber = 0;
                   
            }
            
            System.out.println("Finished Map reading");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		//foreach dataset.tar.gz **
		//Iterate 
		File root = new File(dspath);
		//root.mkdirs();
		Collection files = FileUtils.listFiles(root, null, false);
		
		
		System.out.println("Iterating all downloaded datasets tgz files...");
		int dsCount = 0;
		
		for (Iterator iterator = files.iterator(); iterator.hasNext();) {
			File dsFile = (File) iterator.next();
			String inputDataset = dsFile.getAbsolutePath();
			
			dsCount = dsCount + 1;
			System.out.println("  Processing "+dsCount+":"+inputDataset);	
			
			
			//Set the GraphID
			String graphID = (String) fileDatasetMap.get(dsFile.getName());
			System.out.println("    Graph:: "+graphID);
			
			
			
			//Uncompress the dataset and iterate throughout the files
			try {
				FileInputStream fin = new FileInputStream(inputDataset);
				BufferedInputStream in = new BufferedInputStream(fin);
				FileOutputStream out = new FileOutputStream(dspath+"/archive.tar");
				GzipCompressorInputStream gzIn;		
				gzIn = new GzipCompressorInputStream(in);		
				final byte[] buffer = new byte[1024];
				int n = 0;
				while (-1 != (n = gzIn.read(buffer))) {
				    out.write(buffer, 0, n);
				}
				out.close();
				gzIn.close();
				
				//read the tar
				File input = new File(dspath+"/archive.tar"); //getFile("ds/archive.tar");			
		        InputStream is = new FileInputStream(input);
		        ArchiveInputStream in1 = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
		        TarArchiveEntry entry = (TarArchiveEntry)in1.getNextEntry();
		        
		        while (entry != null) {// create a file with the same name as the tarEntry
		            File destPath = new File(dspath+"/extract/" + entry.getName());
		            if (entry.isDirectory()) {
		                destPath.mkdirs();
		            } else {
		                destPath.createNewFile();
		                OutputStream out1 = new FileOutputStream(destPath);
		                IOUtils.copy(in1, out1);
		                out1.close();
		            }
		            entry = (TarArchiveEntry)in1.getNextEntry();
		        }
		        
		        in1.close();
			} catch (Exception e) {
				e.printStackTrace();
				errorDescription = e.getMessage();
				System.out.println("    *Error extracting");
			}
			
			
			
			
			//Iterate on extracted files
			try{ 
				File root1 = new File(dspath+"/extract/");
				Collection files1 = FileUtils.listFiles(root1, null, true);
				new File(dspath+"/rdf").mkdir();
				new File(dspath+"/json").mkdir();
	
				for (Iterator iterator1 = files1.iterator(); iterator1.hasNext();) {
					File lomFile = (File) iterator1.next();
					String inputFile = lomFile.getAbsolutePath();
					
					//offset
					if(offset > counter){
						continue;
					}
	
					System.out.println("      Processing file:"+inputFile);
					
					if (bContinuar) {
						// save metadata stream in a local file
						tmpMetadataFile = inputFile;
						String valid = "1";
						//valid = XMLValidator.validate(tmpMetadataFile,
						//		StorageService.getXSDFile(storageType));
						boolean hasSource = false;
						if (tmpMetadataFile.length() > 0) {
							// TODO: metadata validation
							// valid = "1";
							if (valid.equalsIgnoreCase("1")) {
								// generate id for the new material
									
								output = graphID;
								// save metatada in rdf
								// obtain key-value pairs
								try {
									LOMParser.parseToKeyValue(tmpMetadataFile);
									if (!LOMParser.keyValues.isEmpty()) {
										keyValues = LOMParser.keyValues;
									}
								} catch (MalformedURLException e1) {
									e1.printStackTrace();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								
								
	
								if (!keyValues.isEmpty()) {
									
									int canSave = 1;
	
	
									if (canSave > 0) {							
								
										//4store
										//prepare RDF file (better)
										try {
											// HELP: http://www.roseindia.net/tutorials/rdf/generateRDF.shtml
											
											Model model = ModelFactory.createDefaultModel();

											//couchdb
											//Document newdoc = new Document();											
											
											
											for (KeyValue kv : keyValues)
											{
												//String s = uriGraph+"/ds/"+graphID+"/"+lomFile.getName();		//con el nombre del archivo
												String s = uriGraph;		//con nombre generico del DS
												//String p = URLEncoder.encode(kv.getKey(),"UTF-8");
												String p = kv.getKey().replace(".", "_");	//.replaceAll("[^\\w\\s\\.]","_");
												String v = kv.getValue();
												
												//obtener el autor del CDATA en variable v
												if(v.contains("CDATA"))
												{
													v = v.replace("<![CDATA[", "");
													v = v.replace("]]>", "");
													
													VCardEngine vcardEngine = new VCardEngine();
													VCard vcard = vcardEngine.parse(v);
													if(vcard.hasFN())
														v = vcard.getFN().getFormattedName();
													else if(vcard.hasN())
														v = vcard.getN().getFamilyName();
													else{
														//format string it can be parsed.
														
														StringBuffer sb;
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf("VERSION:"), "\n");
														v = sb.toString();
														
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf(" FN:")+1, "\n");
														v = sb.toString();
	
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf(" N:")+1, "\n");
														v = sb.toString();
														
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf("ORG:"), "\n");
														v = sb.toString();
														
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf("EMAIL:"), "\n");
														v = sb.toString();
														
														sb = new StringBuffer(v);													
														sb.insert(v.indexOf("END:"), "\n");
														v = sb.toString();
														
														vcard = vcardEngine.parse(v);
														if(vcard.hasFN())
															v = vcard.getFN().getFormattedName();
														else if(vcard.hasN())
															v = vcard.getN().getFamilyName();
														else{
															System.out.println(" ~ ~ Problem with:::"+v);
															System.out.println(" ~ ~ When Processing:"+inputFile);
														}
														
														//System.out.println(" ~ author is: "+v);
													}
														
												}
																								
												//generate the node
												Property lom_prop = model.createProperty(  p );	//"http://ltsc.ieee.org/xsd/LOM#" +
												model.createResource(s).addProperty(lom_prop, v);
												
												//couchdb
												//newdoc.put(p,v); // same as JSON: { foo: "baz"; }
											}
											
											//other node, patch with the dataset
											String s = uriGraph;		//con nombre generico del DS
											String p = "dataset";
											String v = graphID;
											Property lom_prop = model.createProperty(  p );	//"http://ltsc.ieee.org/xsd/LOM#" +
											model.createResource(s).addProperty(lom_prop, v);
											//couchdb
											//newdoc.put(p,v); // same as JSON: { foo: "baz"; }
											
											
											//couchdb
											//db.saveDocument(newdoc); // auto-generated id given by the database
											
											FileOutputStream fop = null;
											File rdfFile = new File(dspath+"/json/"+lomFile.getName().replace(".xml", ".json"));
											fop = new FileOutputStream(rdfFile);
											
											//deprecated
											model.write(fop,"RDF/JSON");	//guardar todo el RDF en un solo JSON 
											counter++;
											
											//check limit
											if(limit > 0 && counter > limit){
												break;
											}
											
											
											
										} catch (Exception e) {
											e.printStackTrace();
										}
										//break;	//debug (only one file)
										
									}
								} else {
									output = "0";
									errorDescription = "Could not handle metadata to key-value";
								}
								
							} else {
								errorDescription = "XML Validation:" + valid;
							}
						} else {
							errorDescription = "Could not handle metadata file";
						}
	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorDescription = e.getMessage();
				System.out.println("    Some error:: ");
			}
			
			//put graph to Couchdb via REST
			if(commit4store){
				String url = "http://"+localCouchdbProxy+"/ag_couch_proxy/local-json2couchdb.php?dspath="+dspath;
				WebResource webResource = Client.create().resource(url);
				ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML,MediaType.WILDCARD).get(ClientResponse.class);
				if (response.getStatus() != 200) {
				   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}
				
				//String response_str = response.getEntity(String.class);	//I don't know why this does not work when running in shell
				String response_str = getStringFromInputStream(response.getEntityInputStream());
				System.out.println(response_str);	
				
				//Borrar todo lo de ds/extract/, rdf y el archive.tar para liberar espacio
				try {
					FileUtils.deleteDirectory(new File(dspath+"/extract/"));
					FileUtils.deleteDirectory(new File(dspath+"/rdf/"));
					FileUtils.deleteDirectory(new File(dspath+"/json/"));
					FileUtils.deleteQuietly(new File(dspath+"/archive.tar"));
					FileUtils.deleteQuietly(new File(dsFile.getAbsolutePath()));	//delete dataset
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			
			
			//break;	//debug (only one dataset)
			
			//check limit (again - after couchdb insert)
			if(limit > 0 && counter > limit){
				break;
			}

		}

			

		

		// prepare response
		if (output.contentEquals("0"))
			status = "ERROR: " + errorDescription;
		else
			status = "OK";
		
		System.out.println(status);
	}
	
	
	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line+"\n");
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	

}
