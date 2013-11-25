package igual.metadata.parsers;

/**
 *  Key-value pair to store metadata field
 * 
 * @author gcarrillo
 * @version 0.1
 */
public class KeyValue {
	public String key;
	public String value;

	/**
	 * Constructor: set a key-value pair
	 * @param sKey
	 *            string with key name
	 * @param sValue
	 *            string with value for key
	 * 
	 */
	public KeyValue(String sKey, String sValue) {
		key = sKey;
		value = sValue;
	}

	/**
	 * @param sKey string with key name
	 */
	 
	public void setKey(String sKey) {
		key = sKey;
	}
	
	/**
	 * @param sValue string with value for key
	 */
	
	public void setValue(String sValue) {
		value = sValue;
	}

	/**
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}
}
