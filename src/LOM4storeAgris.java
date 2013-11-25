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

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

import static jenajsonld.JenaJSONLD.JSONLD ;
import org.apache.jena.riot.RDFDataMgr ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.sparql.lib.DatasetLib ;
import com.hp.hpl.jena.sparql.sse.SSE ;


@SuppressWarnings("unused")
public class LOM4storeAgris {

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
	        System.out.println("Proper Arguments are: [Dataset origin full path trailing slash] [URL store] [URI graph base] [Local Couchdb proxy IP] [commit to 4store]");
	        System.out.println("Example: java -jar xxxxx.jar /home/carlos/Desktop/AGRIS/RDF_Output/ http://localhost:81 http://agris.fao.org/ localhost commit");
	        System.exit(0);
	    }
		
		String dspath = args[0];
		String urlStore = args[1];
		String uriGraph = args[2];
		String localCouchdbProxy = args[3];
		String commit4store_arg = args[4];
		
		boolean commit4store = false;
		/*if(commit4store_arg.equals("commit"))
			commit4store = true;*/
		
		/*String dspath = "/home/carlos/workspace/WebAPI/ds/";
		//String inputDataset = "/home/carlos/workspace/WebAPI/ds/4-1363720526-oai_lom.tar.gz";
		//String graphID = "Organic.Edunet";
		//String urlStore = "http://localhost:81";
		String urlStore = "http://4store.ipb.ac.rs:81";
		String uriGraph = "http://aginfra.eu";*/
		
		
		
		String output = "0";// valor de retorno
		String status = "ERROR";
		String errorDescription = "";
		String tmpMetadataFile = "";
		// check if type is valid
		boolean bContinuar = true;
		//StorageService service;
		ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
		HashMap fileDatasetMap = new HashMap(); 
		
		
		
		
			
			//Iterate on extracted files
			try{
				File root1 = new File(dspath);
				Collection files1 = FileUtils.listFiles(root1, null, true);
				//new File("ds/rdf").mkdir();
	
				for (Iterator iterator1 = files1.iterator(); iterator1.hasNext();) {
					File lomFile = (File) iterator1.next();
					String inputFile = lomFile.getAbsolutePath();
	
					System.out.println("      Processing:"+inputFile);	//debug
					/*
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
												String s = uriGraph+"/ds/"+graphID+"/"+lomFile.getName();
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
											File rdfFile = new File("ds/rdf/"+lomFile.getName().replace(".xml", ".rdf"));
											fop = new FileOutputStream(rdfFile);
											
											//model.write(System.out);
											model.write(fop);	*/
											
											//4store
											if(commit4store){
												HandleGraph graph = new HandleGraph(urlStore,uriGraph);		
												int result = graph.AppendGraph(lomFile.getAbsolutePath());	//returns 0-1
											}
											
											try
											{
												//XML to JSON
												// create an empty model
										        Model model = ModelFactory.createDefaultModel();
												InputStream in = FileManager.get().open( inputFile );
										        
										        
										        // read the RDF/XML file
										        model.read(in, "");
										        
										        Property foaf_name = ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name");
									            Property identifier = ResourceFactory.createProperty("http://purl.org/dc/terms/identifier");
										        
									            
										        // each bibo:Article
										        StmtIterator iter_articles = model.listStatements((Resource)null,identifier,(RDFNode)null);
										        while (iter_articles.hasNext()) {										        	
										            Statement stmt      = iter_articles.nextStatement();  // get next statement
										            Resource  subject   = stmt.getSubject();     // get the subject
										            Property  predicate = stmt.getPredicate();   // get the predicate
										            RDFNode   object    = stmt.getObject();      // get the object
										            
										            //System.out.println("        Processing: "+subject.toString());
										            
										            //create model
										            Model model_article = ModelFactory.createDefaultModel();
										            // 24MB limit
											        File outfile = new File("/home/carlos/Desktop/AGRIS/RDF_JSON/"+object.toString()+".json");
											        FileOutputStream fop = new FileOutputStream(outfile);
											        if (!outfile.exists()) {
											        	outfile.createNewFile();
													}
											        
										            
										            //each triple for the bibo:Article
										            StmtIterator iter = model.listStatements(subject,(Property)null,(RDFNode)null);
										            while (iter.hasNext()) {											            
										            	Statement stmt3      = iter.nextStatement();  // get next statement
										            	Resource  a_subject   = stmt3.getSubject();     // get the subject
											            Property  a_predicate = stmt3.getPredicate();   // get the predicate
											            RDFNode   a_object    = stmt3.getObject();      // get the object
	
											            

										            	//Print the triple
										            	/*System.out.print(a_subject.toString());
											            System.out.print(" " + a_predicate.toString() + " ");
											            if (a_object instanceof Resource) {
											               System.out.print(a_object.toString());
											            } else {
											                // object is a literal
											                System.out.print(" \"" + a_object.toString() + "\"");
											            }
											            System.out.println(" .");*/
											            
											            //add to the model
											            if(!a_predicate.toString().equals("http://purl.org/dc/terms/creator")){
											            	model_article.add(stmt3);
											            }
											            
											            
											            // Get the creator if object is resource
											            if (a_object instanceof Resource && a_predicate.toString().equals("http://purl.org/dc/terms/creator")) {
												            StmtIterator iter_authors = model.listStatements((Resource)a_object,foaf_name,(RDFNode)null);
												            //each author
												            while (iter_authors.hasNext()) {
												            	Statement stmt2      = iter_authors.nextStatement();  // get next statement
													            
												            	Resource  subject2   = stmt2.getSubject();     // get the subject
													            Property  predicate2 = stmt2.getPredicate();   // get the predicate
													            RDFNode   object2    = stmt2.getObject();      // get the object
													            
													            //Print the author triple
													            /*System.out.print(" >>> "+subject2.toString());
													            System.out.print(" " + predicate2.toString() + " ");
													            if (object2 instanceof Resource) {
													               System.out.print(object2.toString());
													            } else {
													                // object is a literal
													                System.out.print(" \"" + object2.toString() + "\"");
													            }
													            System.out.println(" .");*/
													            
													            //add to the model
													            model_article.add(ResourceFactory.createStatement(a_subject, a_predicate, object2));
												            }
											            }
												            
										            }
										            //model_article.write(System.out,"RDF/JSON");
										            model_article.write(fop,"RDF/JSON");
										            //RDFDataMgr.write(fop, model, JSONLD) ;
										            //break;	//solo procesar un Article
										        }
										        
										        //break;	//solo procesar 1 archivo
										        //model.write(fop,"RDF/JSON");	//guardar todo el RDF en un solo JSON 
											} catch (Exception e) {
												e.printStackTrace();
												System.out.println(">> JENA Exception");
												//break;
											}
											     
											
											/*
										} catch (Exception e) {
											e.printStackTrace();
										}
										//break;	//debug
										
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
						
					}*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


		// prepare response
		if (output.contentEquals("0"))
			status = "FINISHED: " + errorDescription;
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
