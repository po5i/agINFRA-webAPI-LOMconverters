package igual.metadata.parsers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
/**
 * Read a LOM file and create a list of key-value pairs
 * 
 * @author gcarrillo
 * @version 0.1
 */

public class LOMParser {

	public static ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();
	/**
	 * analyze xml file and create a key-value pairs list
	 * @param sourceUrlString path of metadata file
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */	
	public static void parseToKeyValue(String sourceUrlString) throws MalformedURLException, IOException {
		keyValues.clear();
		/*if (sourceUrlString.indexOf(':') == -1)
			sourceUrlString = "file://" + sourceUrlString;*/

		Source source = new Source(new InputStreamReader(new FileInputStream(sourceUrlString), "UTF8"));  //new Source(new URL(sourceUrlString));

		List l = source.findAllElements("lom");
		Element element = null;
		String key = "";
		if (l.size() <= 0) {
			l = source.findAllElements("lom:lom"); // nextroom namespaces
													// lom with lom:lom
			if (l.size() <= 0) {
				// detects if lom-element not found
				System.out.println("Error with " + sourceUrlString);
			}
		}
		for (int i = 0; i < l.size(); i++) {
			element = (Element) l.get(i);
			key = element.getName();
			// check namespace
			int index = key.indexOf(":");
			if (index != -1) {
				// remove the namespace!
				key = key.substring(index + 1, key.length());
			}
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				analyseChildElement(child, key, sourceUrlString);
			}
		}
	}
	/**
	 * analyze a xml element to obtain key-value pair
	 * @param element
	 * @param parentkey
	 * @throws UnsupportedEncodingException 
	 */
	private static void analyseChildElement(Element element, String parentKey,
			String sourceUrlString) throws UnsupportedEncodingException {
		String name = element.getName();

		int index = name.indexOf(":");
		if (index != -1) {
			// remove the namespace
			name = name.substring(index + 1, name.length());
		}
		String key = parentKey += "." + name;
		//System.out.println("==========");
		//System.out.println(key);
		
		//is vcard?
		Boolean cdata_flag = false;
		if (element.getChildElements().size() == 1) {
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				if(child.getName().contains("cdata")){
					//System.out.println("hijo tiene cdata");
					cdata_flag = true;
				}
			}
		}

		if (element.getChildElements().size() == 0 || cdata_flag) {
			String attribute = null;		
			boolean saveKeyValue = true;
			key = key + ".type";
			//System.out.println("-----");
			//System.out.println(key);
			for (KeyValue tmpObject : keyValues) {

				if (tmpObject.getKey().equals(key)) {
					// check if the value is diferent
					
					//System.out.println(key + " / " +tmpObject.getValue() + " - " + element.getContent().toString());
					//System.out.println();
					if (tmpObject.getValue().equals(
							element.getContent().toString())) {

						saveKeyValue = false;
						break;

					}else{
						//add to the key the value to have avoid problems when update
						//po5i: comentado por problemas al formar el RDF para LOM
						//key = key + "." + element.getContent().toString().replace(' ', '_');
					}

				} else {
					if (tmpObject.getKey().indexOf(key) >= 0) {

						saveKeyValue = false;
						break;

					}
				}
			}
			if (saveKeyValue)			
			keyValues.add(new KeyValue(key, element
					.getContent().toString()));
			
			//System.out.println("**saved:"+key);
			//System.out.println("**elem::"+element.getContent().toString());
			
			if (name.equalsIgnoreCase("string"))
				attribute = element.getAttributeValue("language");
		} else {
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				analyseChildElement(child, key, sourceUrlString);
				//System.out.println("--analizar:"+child.getName());
			}
		}

	}
	

	/*public static void main(String[] args) throws Exception {

		String pathFile = "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/LO/lo.xml";

		// String pathFile =
		// "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/lesson.final.xml";
		//GeneralParser.parseToKeyValue(pathFile);
		LOMParser.parseToKeyValue(pathFile);
		System.out.println(keyValues.size());

		if (!LOMParser.keyValues.isEmpty()) {
			// get title and description
			try {

				for (KeyValue tmpObject : keyValues) {
					String key = tmpObject.getKey();
					System.out.println(key);
					if (key.indexOf("lessonid") != -1) {
						// keyValues.put("ID", e.getValue());
					}
					if (key.indexOf("description") != -1) {
						// System.out.println( e.getValue());
						// keyValues.put("description", e.getValue());
					}

				}
			} catch (Exception e) {
				System.out.println("ex");
				e.getStackTrace();
			}
			// put file content in a variable to store in lucene

		}
	}	*/
}
