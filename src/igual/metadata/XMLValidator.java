package igual.metadata;


import java.util.regex.Pattern;

import igual.services.StorageService;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * check if a xml file is valid using a xsd file
 * 
 * @author gcarrillo
 * @version 0.1
 */
public class XMLValidator {
	/**
	 * main method to validate a xml content
	 * 
	 * @param xmlFile
	 *            string with the content of a xml file
	 * @param xsdFile
	 *            path of the xsd file
	 * @return result "1" when validation is ok a message with the description
	 *         of the error
	 */
	public static String validate(String xmlFile, String xsdFile) {
		String result = "";
		try {
			// define the type of schema - we use W3C:
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			Schema schema = factory.newSchema(new StreamSource(xsdFile));
			Validator validator = schema.newValidator();

			// at last perform validation:
			validator.validate(new StreamSource(xmlFile));
			result = "1";
		} catch (SAXException ex) {
			// System.out.println(ex.getMessage());
			result = friendlyMessage(ex.getMessage());
			//parse response to a friendy way
			
		} catch (Exception ex) {
			// ex.printStackTrace();
			result = friendlyMessage(ex.getMessage());
		}
		return result;
	}

	/**
	 * friendlyMessage: change default xml messages to friendly messages
	 * 
	 * @param orgMessage original message
	 * 
	 * @return msg friendly message
	 */	
	
	private static String friendlyMessage(String orgMessage){
		String msg="";
	
		//cvc-enumeration-valid: Value 'teachers' is not facet-valid with respect to enumeration '[teacher, student, admin]'. It must be a value from the enumeration.
		String patt1 ="cvc-enumeration-valid: Value '(.*)' is not facet-valid with respect to enumeration '.*'. It must be a value from the enumeration.";
		//cvc-pattern-valid: Value 'Carrillo343' is not facet-valid with respect to pattern '[a-zA-Z\s]+' for type 'stringType'.
		String patt2 ="cvc-pattern-valid: Value '(.*)' is not facet-valid with respect to pattern '(.*)' for type '(\\w+)'.";
		//cvc-minLength-valid: Value '12345' with length = '5' is not facet-valid with respect to minLength '8' for type 'passwordType'.
		String patt3 ="cvc-minLength-valid: Value '(.*)' with length = '(\\w+)' is not facet-valid with respect to minLength '(\\w+)' for type '(\\w+)'.";
	//	cvc-datatype-valid.1.2.1: 'a' is not a valid value for 'integer'.
		String patt4 ="cvc-datatype-valid.1.2.1: '(.*)' is not a valid value for '(\\w+)'.";
		

		String pattern ="";
		String replaceRegex  ="";
		if (orgMessage.indexOf("cvc-enumeration-valid:")!=-1) {
			pattern = patt1;
			replaceRegex="Invalid value '$1'.  It must be a value from '$2'";
		}
		if (orgMessage.indexOf("cvc-pattern-valid:")!=-1){
			pattern = patt2;
			replaceRegex = "Value '$1' is not valid.";
		}
		if (orgMessage.indexOf("cvc-minLength-valid:")!=-1){
			pattern = patt3;
			replaceRegex = "Value '$1' is not valid, min length must be '$3'";
		}
		if (orgMessage.indexOf("cvc-datatype-valid.1.2.1:")!=-1){
			pattern = patt4;
			replaceRegex = "'$1' is not a valid value for '$2'.";
		}

		if (pattern.length()>0)
			msg = Pattern.compile(pattern).matcher(orgMessage).replaceAll(replaceRegex);
		else
			msg = orgMessage;
			
		
		return msg;		
	}
	
	public static void main(String[] args) {
		String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/usuarios/user_profile.xml";
		//String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/LO/IEEELOM_WIZARD.xml";
		
		//String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/adaptation/TAXONOMY_WIZARD.xml";
	//	String xmlFile="/home/gcarrillo/Descargas/laC.xml";
		//String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/objetivos/learning_objective.xml";
		//String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/cursos/course.xml";
		//String xmlFile="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/pruebas/social_info/social_information.xml";
		String valid = XMLValidator.validate(xmlFile,StorageService.getXSDFile("USER"));
		
		System.out.println(valid);
	}

}
