package igual.metadata.parsers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;

public class SurveyParser {

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

			/*	if (key.equalsIgnoreCase("questionary")) {
					String qid = element.getAttributeValue("id");
					keyValues.add(new KeyValue("questionary.id", qid));
				}
*/
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
		String key = parentKey + "." + name;

		if (element.getChildElements().size() == 0) {

			String id = null;
			boolean saveKeyValue = true;
			String value = "";

			// check if the key is not part of a current key

			if (parentKey.equalsIgnoreCase("questionary")) {
				value = element.getAttributeValue("min");
				keyValues.add(new KeyValue(key + ".min", value));
				value = element.getAttributeValue("max");
				keyValues.add(new KeyValue(key + ".max", value));
				saveKeyValue = false;
			}

			if (parentKey.indexOf("answers.answer") >= 0) {
				value = element.getAttributeValue("points");
			}

			for (KeyValue tmpObject : keyValues) {
				if (tmpObject.getKey().indexOf(key) >= 0) {
					saveKeyValue = false;
					break;
				}
			}
			if (parentKey.indexOf("question.answers.answer") >= 0) {
				saveKeyValue = false;
			}
			if (key.indexOf("answer.") == 0) {
				saveKeyValue = false;
			}

			if (saveKeyValue) {
				keyValues.add(new KeyValue(key, value));
			}
		} else {

			if (name.equalsIgnoreCase("question")) {
				String value = element.getAttributeValue("q_id");
				key = key + "." + value;

			}
			if (name.equalsIgnoreCase("answer")) {
				String value = element.getAttributeValue("qa_id");
				key = key + "." + value;

			}
			List<Element> children = element.getChildElements();
			for (Element child : children) {
				analyseChildElement(child, key);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		// String pathFile =
		// "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/lecciones/lesson1.xml";
		String pathFile = "/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/ls_survey/ls_questionary.xml";

		SurveyParser.parseToKeyValue(pathFile);
		System.out.println(keyValues.size());

		if (!SurveyParser.keyValues.isEmpty()) {
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
