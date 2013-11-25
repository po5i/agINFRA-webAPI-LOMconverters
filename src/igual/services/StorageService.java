package igual.services;

import java.io.File;
import java.io.IOException;
/**
 * Storage services available 
 * 
 * @author gcarrillo
 * @version 0.1
 */
public enum StorageService {
    LA,// learning activity
    LP, // learning path
    PROTOCOL,
    TAXONOMY,
    ADAPTATION,
    LESSON,
    COURSE,
    USER,
    SOCIAL_INFO,
    OBJECTIVE,
    LP_ACTIVITY,
    LS_USER_PROFILE,
    LS_SURVEY,
    USER_ACTIONS; //user learning style
    
    //static final String luceneFolder = "/user/hadoop/IndexFiles/";
    static final String luceneFolder = "/home/hadoop/IndexFiles/";
    static final String hdfsFolder = "/user/hadoop/repository/";
    static final String xsdFolder ="/home/igualproject/metadata/schemas/";
    static final String mahoutInputFolder = "/home/hadoop/mahout_input/";
    //static final String xsdFolder ="/home/gcarrillo/Documentos/CTI/IGUAL/metadata/schemas/";
  
	/**
	 * get name of the lucene folder for a specific service
	 * @param serviceName	name of the service
	 * 
	 * @return string with lucene index folder
	 */

    public static String getIndexFolder(String serviceName) {
        return luceneFolder + serviceName + "/";
    }
	/**
	 * get name of the HDFS folder for a specific service
	 * @param serviceName	name of the service
	 * 
	 * @return string with hdfs path
	 * 
	 */
    
    public static String getHDFSFolder(String serviceName) {
        return hdfsFolder + serviceName + "/";
    }    
    
	/**
	 * get name of the xsd file for a specific service
	 * @param serviceName	name of the service
	 * 
	 * @return string with xsd file path
	 */
    
    public static String getXSDFile(String serviceName) {
    
        return xsdFolder +  serviceName.toLowerCase() + ".xsd";
    }     
    
	/**
	 * get mahout input folder
	 * @return mahout folder path 
	 */
    public static String getMahoutInputFolder() {
        return mahoutInputFolder;
    }
}
