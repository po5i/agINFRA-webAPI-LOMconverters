package igual.metadata.parsers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;

public class LSParser {

	public static ArrayList<KeyValue> keyValues = new ArrayList<KeyValue>();

	/**
	 * analyze xml file and create a key-value pairs list
	 * 
	 * @param sourceUrlString
	 *            path of metadata file
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
				
				if (key.equalsIgnoreCase("learning_styles_profile"))
				{
					String qid = element.getAttributeValue("questionary_id");
					keyValues.add(new KeyValue("learning_styles_profile.qid", qid));
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
	 * 
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
			String id = null;
			boolean saveKeyValue = true;
			String value = "";
			// check if the key is not part of a current key

			if (name.equalsIgnoreCase("question")) {
				id = element.getAttributeValue("id");
				if (id != null) {
					key = key + "." + id;
					value = element.getAttributeValue("selected_ans");

				}
			}
		
			else if (key.indexOf("profile")>0) {
				
				value = element.getAttributeValue("value");
				
			}			

			for (KeyValue tmpObject : keyValues) {
				if (tmpObject.getKey().indexOf(key) >= 0) {
					saveKeyValue = false;
					break;
				}
			}

			if (saveKeyValue)
				keyValues.add(new KeyValue(key, value));

		} else {
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				analyseChildElement(child, key);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		// String pathFile =
		// "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/lecciones/lesson1.xml";
		String pathFile = "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/learning_style/profile.xml";

		LSParser.parseToKeyValue(pathFile);
		System.out.println(keyValues.size());

		if (!LSParser.keyValues.isEmpty()) {
			// get title and description
			try {

				for (KeyValue tmpObject : keyValues) {
					String key = tmpObject.getKey();
					String value = tmpObject.getValue();
					System.out.println(key + " : " + value);
					

				}
			} catch (Exception e) {
				System.out.println("ex");
				e.getStackTrace();
			}
			// put file content in a variable to store in lucene

		}
	}
}
