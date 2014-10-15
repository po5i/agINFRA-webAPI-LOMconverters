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

import com.hp.hpl.jena.rdf.model.*;
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



public class LOMfiles24store {

	/**
	 * agINFRA Social visualization components aux. tool
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		System.out.println("Hello");
		
		//PARAMS
		if(args.length != 5)
	    {
	        System.out.println("Proper Arguments are: [Dataset files full path] [URL store] [URI graph base] [Destination directory] [commit to 4store]");
	        System.out.println("Example: java -jar xxxxx.jar /home/carlos/Desktop/agINFRA-workflow/workflow/loms/bioe/ http://localhost:81 http://laclo.laflor /home/carlos/Desktop/agINFRA-workflow/workflow/loms/bioerdf/ false");
	        System.exit(0);
	    }
		
		String dspath = args[0];
		String urlStore = args[1];
		String uriGraph = args[2];
		String destination = args[3];
		String commit4store_arg = args[4];
		
		/*String dspath = "/home/carlos/workspace/WebAPI/ds/";
		String urlStore = "http://4store.ipb.ac.rs:81";
		String uriGraph = "http://aginfra.eu";
		String localCouchdbProxy = "localhost";
		String commit4store_arg = "no";*/
		
		boolean commit4store = false;
		if(commit4store_arg.equals("commit"))
			commit4store = true;
		
	
		
		
		String output = "0";// valor de retorno
		String status = "ERROR";
		String errorDescription = "";
		String tmpMetadataFile = "";
		// check if type is valid
		boolean bContinuar = true;
		//StorageService service;
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		HashMap fileDatasetMap = new HashMap(); 
		
		
		/*
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
			
			
			//String response_str = response.getEntity(String.class);	//I don't know why this does not work when running in shell
			String response_str = getStringFromInputStream(response.getEntityInputStream());
			//System.out.println(response_str);	//debug
			
			System.out.println("Finished IPB call");
			
			
			
			System.out.println("Reading Dataset Map...");
			
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
		*/
		
		
		
		/*
		//foreach dataset.tar.gz **
		//Iterate 
		File root = new File("ds/");
		Collection files = FileUtils.listFiles(root, null, false);
		
		//mini db processed files		
		ArrayList processed = new ArrayList();
        try {
        	BufferedReader br = new BufferedReader(new FileReader("processed.txt"));
            String line;
        	while((line = br.readLine()) != null) {
        		processed.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Iterating all downloaded datasets tgz files...");
		int dsCount = 0;
		
		for (Iterator iterator = files.iterator(); iterator.hasNext();) {
			File dsFile = (File) iterator.next();
			String inputDataset = dsFile.getAbsolutePath();
			
			dsCount = dsCount + 1;
			System.out.println("  Processing "+dsCount+":"+inputDataset);	//debug
			
			//po5i: mini db processed files			
			if(processed.contains(inputDataset)){
				System.out.println("    >>Already processed... skipping... ");
				continue;
			}
			else
			{
				processed.add(inputDataset);
				try {
					FileWriter fileWritter = new FileWriter("processed.txt",true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    	        bufferWritter.write(inputDataset+"\n");
	    	        bufferWritter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//Set the GraphID
			String graphID = (String) fileDatasetMap.get(dsFile.getName());
			System.out.println("    Graph:: "+graphID);
						
			
			
			//Uncompress the dataset and iterate throughout the files
			try {
				FileInputStream fin = new FileInputStream(inputDataset);
				BufferedInputStream in = new BufferedInputStream(fin);
				FileOutputStream out = new FileOutputStream("ds/archive.tar");
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
				File input = new File("ds/archive.tar"); //getFile("ds/archive.tar");			
		        InputStream is = new FileInputStream(input);
		        ArchiveInputStream in1 = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
		        TarArchiveEntry entry = (TarArchiveEntry)in1.getNextEntry();
		        
		        while (entry != null) {// create a file with the same name as the tarEntry
		            File destPath = new File("ds/extract/" + entry.getName());
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
			}*/
			
			
			
			
			//Iterate on extracted files
			try{ 
				File root1 = new File(dspath);
				Collection files1 = FileUtils.listFiles(root1, null, true);
				//new File(dspath+"../rdf").mkdir();
	
				for (Iterator iterator1 = files1.iterator(); iterator1.hasNext();) {
					File lomFile = (File) iterator1.next();
					String inputFile = lomFile.getAbsolutePath();
	
					//System.out.println("      Processing:"+inputFile);	//debug
					
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
									
								//output = graphID;
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
										//save to rdf this triple (slow)
										//HandleGraph graph = new HandleGraph(urlStore,uriGraph);		
										//result = graph.AppendTriple(graphID, keyValues);
										
										
										
										
										//4store
										//prepare RDF file (better)
										try {
											// HELP: http://www.roseindia.net/tutorials/rdf/generateRDF.shtml
											
											Model model = ModelFactory.createDefaultModel();
											
											for (KeyValue kv : keyValues)
											{
												String s = uriGraph+"/"+lomFile.getName();
												//String p = URLEncoder.encode(kv.getKey(),"UTF-8");
												String p = kv.getKey().replaceAll("[^\\w\\s\\.]","_");
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
															System.out.println(" ~ ~ When Processing:"+inputFile);	//debug
														}
														
														//System.out.println(" ~ author is: "+v);
													}
														
												}
												
												//System.out.println("p: "+p+"\t\t\t v: "+v);
												
												Property lom_prop = model.createProperty( "http://ltsc.ieee.org/xsd/LOM#" + p );
												Resource node = model.createResource(s).addProperty(lom_prop, v);
											}		
											
											FileOutputStream fop = null;
											File rdfFile = new File(destination+lomFile.getName().replace(".xml", ".rdf"));
											fop = new FileOutputStream(rdfFile);
											
											//model.write(System.out);
											model.write(fop);
											
											//4store
											if(commit4store){
												HandleGraph graph = new HandleGraph(urlStore,uriGraph);		
												int result = graph.AppendGraph(rdfFile.getAbsolutePath());	//returns 0-1
											}
											
											
										} catch (Exception e) {
											e.printStackTrace();
										}										
										//break;	//debug
																			
									}
									output = "1";
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
			}
			
			/*//break;	//debug
			
			//Borrar todo lo de ds/extract/, rdf y el archive.tar para liberar espacio
			try {
				FileUtils.deleteDirectory(new File("ds/extract/"));
				FileUtils.deleteDirectory(new File("ds/rdf/"));
				FileUtils.deleteQuietly(new File("ds/archive.tar"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
*/
			
		
		
		
		

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
