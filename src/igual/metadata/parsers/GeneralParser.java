package igual.metadata.parsers;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;

/**
 * Read a xml file and create a list of key-value pairs
 * 
 * @author gcarrillo
 * @version 0.1
 */

public class GeneralParser {
	
	public static ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();

	/**
	 * analyze xml file and create a key-value pairs list
	 * @param sourceUrlString path of metadata file
	 */
	public static void parseToKeyValue(String sourceUrlString) {
		try {
			keyValues.clear();
			if (sourceUrlString.indexOf(':') == -1)
				sourceUrlString = "file://" + sourceUrlString;
			
			Source source = new Source(new URL(sourceUrlString));

			List l = source.findAllElements();
			Element element = null;
			String key = "";
			if (l.size() <= 0) {
				// System.out.println("Error with " + sourceUrlString);

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
					analyseChildElement(child, key);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
/**
 * analyze a xml element to obtain key-value pair
 * @param element
 * @param parentkey
 */
	private static void analyseChildElement(Element element, String parentKey) {
		String name = element.getName();

		int index = name.indexOf(":");

		if (index != -1) {
			// remove the namespace
			name = name.substring(index + 1, name.length());
		}
		String key = parentKey += "." + name;

		if (element.getChildElements().size() == 0) {
			String attribute = null;
			boolean saveKeyValue = true;
			if (name.equalsIgnoreCase("content")) {
				keyValues.add(new KeyValue(key + ".id", element.getContent()
						.toString()));

				attribute = element.getAttributeValue("type");
				if (attribute != null)
					keyValues.add(new KeyValue(key + ".type", attribute));

			} else {
				// check if the key is not part of a current key

				for (KeyValue tmpObject : keyValues) {

					if (tmpObject.getKey().equals(key)) {
						// check if the value is diferent
						if (tmpObject.getValue().equals(
								element.getContent().toString())) {

							saveKeyValue = false;
							break;

						}else{
							//add to the key the value to have avoid problems when update
							key = key + "." +element.getContent().toString();
						}

					} else {
						if (tmpObject.getKey().indexOf(key) >= 0) {

							saveKeyValue = false;
							break;

						}
					}
				}
				if (saveKeyValue)
					keyValues.add(new KeyValue(key, element.getContent()
							.toString()));
			}

		} else {
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				analyseChildElement(child, key);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		//String pathFile = "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/lecciones/lesson1.xml";
		String pathFile = "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/learning_style/profile.xml";
		
		GeneralParser.parseToKeyValue(pathFile);
		System.out.println(keyValues.size());

		if (!GeneralParser.keyValues.isEmpty()) {
			// get title and description
			try {

				for (KeyValue tmpObject : keyValues) {
					String key = tmpObject.getKey();
					String value = tmpObject.getValue();
					System.out.println(key + " : "+value);
				/*	if (key.indexOf("lessonid") != -1) {
						// keyValues.put("ID", e.getValue());
					}
					if (key.indexOf("description") != -1) {
						// System.out.println( e.getValue());
						// keyValues.put("description", e.getValue());
					}*/

				}
			} catch (Exception e) {
				System.out.println("ex");
				e.getStackTrace();
			}
			// put file content in a variable to store in lucene

		}
	}
}
