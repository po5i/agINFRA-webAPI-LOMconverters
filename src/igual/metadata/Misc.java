package igual.metadata;

//import igual.services.query.Taxonomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Misc {
	/**
	 * changeLAMetadataFile:  change LA metadata file to add technical location and identifier
	 * 
	 * @param tmpMetadataFile  metadata file path
	 * @param identifier LA id
	 * @param hasSource if the learning activity has source file
	 * 
	 */			
	public static void changeLAMetadataFile(String tmpMetadataFile,
			String identifier, boolean hasSource) {

		String newMetadataFile = "/tmp/LA" + System.currentTimeMillis()
				+ ".xml";
		FileWriter fichero = null;
		PrintWriter pw = null;
		File archivo = new File(tmpMetadataFile);
		String location_bin = "http://200.126.23.124:8080/WebAPI/getLA.jsp?id="
				+ identifier + "&amp;source=0";
		String location_src = "http://200.126.23.124:8080/WebAPI/getLA.jsp?id="
				+ identifier + "&amp;source=1";

		try {
			fichero = new FileWriter(newMetadataFile);
			BufferedReader in = new BufferedReader(new FileReader(archivo));
			pw = new PrintWriter(fichero);
			String data = "";
			data = in.readLine();
			String spaces = "";
			boolean secId = false;
			boolean secTec = false;
			while (data != null) {
				if (data.indexOf("<identifier>") != -1) {
					secId = true;
				}
				int s = data.indexOf("<entry>");
				if (s != -1 && secId) {

					secId = false;
					for (int i = 0; i < s; i++)
						spaces += " ";
					data = spaces + "<entry>" + identifier + "</entry>";

					System.out.println(data);
				}
				if (data.indexOf("<technical>") != -1) {
					secTec = true;
				}
				s = data.indexOf("<bin_location>");
				spaces = "";
				if (s != -1 && secTec) {
					for (int i = 0; i < s; i++)
						spaces += " ";
					data = spaces + "<bin_location>" + location_bin
							+ "</bin_location>";
			
				}
				s = data.indexOf("<source_location>");
				spaces = "";
				if (s != -1 && secTec) {
					if (hasSource) {
						for (int i = 0; i < s; i++)
							spaces += " ";
						data = spaces + "<source_location>" + location_src
								+ "</source_location>";
					}
				
				}
				pw.println(data);

				data = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			System.out.println("unable to find file");
		} finally {
			try {
				// close out file
				if (null != fichero) {
					fichero.close();
					// replace original file
					Process proc = Runtime.getRuntime().exec(
							"cp " + newMetadataFile + " " + tmpMetadataFile);
					
					//proc = Runtime.getRuntime().exec(
						//	"rm " + newMetadataFile );					
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	/**
	 * changeProtocolMetadataFile:  change protocol metadata file to add identifier
	 * 
	 * @param tmpMetadataFile  metadata file path
	 * @param identifier protocol id
	 * 
	 */			
	public static void changeProtocolMetadataFile(String tmpMetadataFile,
			String identifier) {

		String newMetadataFile = "/tmp/PRO" + System.currentTimeMillis()
				+ ".xml";
		FileWriter fichero = null;
		PrintWriter pw = null;
		File archivo = new File(tmpMetadataFile);


		try {
			fichero = new FileWriter(newMetadataFile);
			BufferedReader in = new BufferedReader(new FileReader(archivo));
			pw = new PrintWriter(fichero);
			String data = "";
			data = in.readLine();
			String spaces = "";
			boolean secId = false;
			
			while (data != null) {
				if (data.indexOf("<identifier>") != -1) {
					secId = true;
				}
				int s = data.indexOf("<entry>");
				if (s != -1 && secId) {

					secId = false;
					for (int i = 0; i < s; i++)
						spaces += " ";
					data = spaces + "<entry>" + identifier + "</entry>";

				
				}

				pw.println(data);

				data = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			System.out.println("unable to find file");
		} finally {
			try {
				// close out file
				if (null != fichero) {
					fichero.close();
					// replace original file
					Process proc = Runtime.getRuntime().exec(
							"cp " + newMetadataFile + " " + tmpMetadataFile);
					
					//proc = Runtime.getRuntime().exec(
						//	"rm " + newMetadataFile );					
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
		
	/**
	 * changeMetadataFile:  change  metadata file to add ID
	 * 
	 * @param entity entity type: COURSE, LESSON, etc
	 * @param tmpMetadataFile  metadata file path
	 * @param identifier object id
	 * 
	 */	
	public static void changeMetadataFile(String entity, String tmpMetadataFile,
			String identifier) {

		String newMetadataFile = "/tmp/GEN" + System.currentTimeMillis()
				+ ".xml";
		FileWriter fichero = null;
		PrintWriter pw = null;
		File archivo = new File(tmpMetadataFile);


		try {
			fichero = new FileWriter(newMetadataFile);
			BufferedReader in = new BufferedReader(new FileReader(archivo));
			pw = new PrintWriter(fichero);
			String data = "";
			data = in.readLine();
			String spaces = "";
			boolean secId = false;
			String stringID = entity.toLowerCase()+"ID";
			while (data != null) {
				int s = data.indexOf("<"+ stringID + ">");
				if (s != -1) {
					for (int i = 0; i < s; i++)
						spaces += " ";
					data = spaces + "<" + stringID +">" + identifier + "</"+stringID+">";
				}
				
				pw.println(data);
				data = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			System.out.println("unable to find file");
		} finally {
			try {
				// close out file
				if (null != fichero) {
					fichero.close();
					// replace original file
					Process proc = Runtime.getRuntime().exec(
							"cp " + newMetadataFile + " " + tmpMetadataFile);
					
					//proc = Runtime.getRuntime().exec(
						//	"rm " + newMetadataFile );					
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	/**
	 * changeSurveyMetadataFile:  change survey metadata file to add ID
	 * 
	 * @param tmpMetadataFile  metadata file path
	 * @param identifier object id
	 * 
	 */	
	public static void changeSurveyMetadataFile(String tmpMetadataFile,
			String identifier) {
		String newMetadataFile = "/tmp/SURVEY" + System.currentTimeMillis()
				+ ".xml";
		FileWriter fichero = null;
		PrintWriter pw = null;
		File archivo = new File(tmpMetadataFile);


		try {
			fichero = new FileWriter(newMetadataFile);
			BufferedReader in = new BufferedReader(new FileReader(archivo));
			pw = new PrintWriter(fichero);
			String data = "";
			data = in.readLine();
			String spaces = "";
			boolean secId = false;

			while (data != null) {
				int s = data.indexOf("<questionary>");
				if (s != -1) {
					for (int i = 0; i < s; i++)
						spaces += " ";
					data = spaces + "<questionary id=\"" + identifier+ "\">";
				}
				
				pw.println(data);
				data = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			System.out.println("unable to find file");
		} finally {
			try {
				// close out file
				if (null != fichero) {
					fichero.close();
					// replace original file
					Process proc = Runtime.getRuntime().exec(
							"cp " + newMetadataFile + " " + tmpMetadataFile);
					
					//proc = Runtime.getRuntime().exec(
						//	"rm " + newMetadataFile );					
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}		
	}
	
	/**
	 * isNumeric:  verify if a string is a number
	 * 
	 * @param str string value to verify
	 * 
	 * @return true if string is a numnber, false otherwise
	 */	
    public static boolean isNumeric(String str){
	    try {
	    	Integer.parseInt(str);
	    	return true;
	    } catch (NumberFormatException nfe){
	    	return false;
	    }
    }
    
	
	/**
	 * isValidWord:  verify if a string is a stop word
	 * 
	 * @param word string value to verify
	 * @param language language of the word
	 * 
	 * @return response true if string is not a stop word, false otherwise
	 */	
    
    public static boolean isValidWord(String word, String language){
    	boolean response = true;
//    	String enStopWords="a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
  //  	String[] enStopWordsL = enStopWords.split(",");
    	String[] enStopWordsL ={"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"};
    	
    //	String esStopWords = "de,de,la,que,el,en,y,a,los,del,se,las,por,un,para,con,no,una,su,al,es,lo,como,más,pero,sus,le,ya,o,fue,este,ha,sí,porque,esta,son,entre,está,cuando,muy,sin,sobre,ser,tiene,también,me,hasta,hay,donde,han,quien,están,estado,desde,todo,nos,durante,estados,todos,uno,les,ni,contra,otros,fueron,ese,eso,había,ante,ellos,e,esto,mí,antes,algunos,qué,unos,yo,otro,otras,otra,él,tanto,esa,estos,mucho,quienes,nada,muchos,cual,sea,poco,ella,estar,haber,estas,estaba,estamos,algunas,algo,nosotros,mi,mis,tú,te,ti,tu,tus,ellas,nosotras,vosotros,vosotras,os,mío,mía,míos,mías,tuyo,tuya,tuyos,tuyas,suyo,suya,suyos,suyas,nuestro,nuestra,nuestros,nuestras,vuestro,vuestra,vuestros,vuestras,esos,esas,estoy,estás,está,estamos,estáis,están,esté,estés,estemos,estéis,estén,estaré,estarás,estará,estaremos,estaréis,estarán,estaría,estarías,estaríamos,estaríais,estarían,estaba,estabas,estábamos,estabais,estaban,estuve,estuviste,estuvo,estuvimos,estuvisteis,estuvieron,estuviera,estuvieras,estuviéramos,estuvierais,estuvieran,estuviese,estuvieses,estuviésemos,estuvieseis,estuviesen,estando,estado,estada,estados,estadas,estad,he,has,ha,hemos,habéis,han,haya,hayas,hayamos,hayáis,hayan,habré,habrás,habrá,habremos,habréis,habrán,habría,habrías,habríamos,habríais,habrían,había,habías,habíamos,habíais,habían,hube,hubiste,hubo,hubimos,hubisteis,hubieron,hubiera,hubieras,hubiéramos,hubierais,hubieran,hubiese,hubieses,hubiésemos,hubieseis,hubiesen,habiendo,habido,habida,habidos,habidas,soy,eres,es,somos,sois,son,sea,seas,seamos,seáis,sean,seré,serás,será,seremos,seréis,serán,sería,serías,seríamos,seríais,serían,era,eras,éramos,erais,eran,fui,fuiste,fue,fuimos,fuisteis,fueron,fuera,fueras,fuéramos,fuerais,fueran,fuese,fueses,fuésemos,fueseis,fuesen,siendo,sido,tengo,tienes,tiene,tenemos,tenéis,tienen,tenga,tengas,tengamos,tengáis,tengan,tendré,tendrás,tendrá,tendremos,tendréis,tendrán,tendría,tendrías,tendríamos,tendríais,tendrían,tenía,tenías,teníamos,teníais,tenían,tuve,tuviste,tuvo,tuvimos,tuvisteis,tuvieron,tuviera,tuvieras,tuviéramos,tuvierais,tuvieran,tuviese,tuvieses,tuviésemos,tuvieseis,tuviesen,teniendo,tenido,tenida,tenidos,tenidas,tened";
    	String[] esStopWordsL={"de","la","que","el","en","y","a","los","del","se","las","por","un","para","con","no","una","su","al","es","lo","como","más","pero","sus","le","ya","o","fue","este","ha","sí","porque","esta","son","entre","está","cuando","muy","sin","sobre","ser","tiene","también","me","hasta","hay","donde","han","quien","están","estado","desde","todo","nos","durante","estados","todos","uno","les","ni","contra","otros","fueron","ese","eso","había","ante","ellos","e","esto","mí","antes","algunos","qué","unos","yo","otro","otras","otra","él","tanto","esa","estos","mucho","quienes","nada","muchos","cual","sea","poco","ella","estar","haber","estas","estaba","estamos","algunas","algo","nosotros","mi","mis","tú","te","ti","tu","tus","ellas","nosotras","vosotros","vosotras","os","mío","mía","míos","mías","tuyo","tuya","tuyos","tuyas","suyo","suya","suyos","suyas","nuestro","nuestra","nuestros","nuestras","vuestro","vuestra","vuestros","vuestras","esos","esas","estoy","estás","está","estamos","estáis","están","esté","estés","estemos","estéis","estén","estaré","estarás","estará","estaremos","estaréis","estarán","estaría","estarías","estaríamos","estaríais","estarían","estaba","estabas","estábamos","estabais","estaban","estuve","estuviste","estuvo","estuvimos","estuvisteis","estuvieron","estuviera","estuvieras","estuviéramos","estuvierais","estuvieran","estuviese","estuvieses","estuviésemos","estuvieseis","estuviesen","estando","estado","estada","estados","estadas","estad","he","has","ha","hemos","habéis","han","haya","hayas","hayamos","hayáis","hayan","habré","habrás","habrá","habremos","habréis","habrán","habría","habrías","habríamos","habríais","habrían","había","habías","habíamos","habíais","habían","hube","hubiste","hubo","hubimos","hubisteis","hubieron","hubiera","hubieras","hubiéramos","hubierais","hubieran","hubiese","hubieses","hubiésemos","hubieseis","hubiesen","habiendo","habido","habida","habidos","habidas","soy","eres","es","somos","sois","son","sea","seas","seamos","seáis","sean","seré","serás","será","seremos","seréis","serán","sería","serías","seríamos","seríais","serían","era","eras","éramos","erais","eran","fui","fuiste","fue","fuimos","fuisteis","fueron","fuera","fueras","fuéramos","fuerais","fueran","fuese","fueses","fuésemos","fueseis","fuesen","siendo","sido","tengo","tienes","tiene","tenemos","tenéis","tienen","tenga","tengas","tengamos","tengáis","tengan","tendré","tendrás","tendrá","tendremos","tendréis","tendrán","tendría","tendrías","tendríamos","tendríais","tendrían","tenía","tenías","teníamos","teníais","tenían","tuve","tuviste","tuvo","tuvimos","tuvisteis","tuvieron","tuviera","tuvieras","tuviéramos","tuvierais","tuvieran","tuviese","tuvieses","tuviésemos","tuvieseis","tuviesen","teniendo","tenido","tenida","tenidos","tenidas","tened"};
    	//String[] esStopWordsL = esStopWords.split(",");
    	
    	//String ptStopWords = "de,a,o,que,e,do,da,em,um,para,é,com,não,uma,os,no,se,na,por,mais,as,dos,como,mas,foi,ao,ele,das,tem,à,seu,sua,ou,ser,quando,muito,há,nos,já,está,eu,também,só,pelo,pela,até,isso,ela,entre,era,depois,sem,mesmo,aos,ter,seus,quem,nas,me,esse,eles,estão,você,tinha,foram,essa,num,nem,suas,meu,às,minha,têm,numa,pelos,elas,havia,seja,qual,será,nós,tenho,lhe,deles,essas,esses,pelas,este,fosse,dele,tu,te,vocês,vos,lhes,meus,minhas,teu,tua,teus,tuas,nosso,nossa,nossos,nossas,dela,delas,esta,estes,estas,aquele,aquela,aqueles,aquelas,isto,aquilo,estou,está,estamos,estão,estive,esteve,estivemos,estiveram,estava,estávamos,estavam,estivera,estivéramos,esteja,estejamos,estejam,estivesse,estivéssemos,estivessem,estiver,estivermos,estiverem,hei,há,havemos,hão,houve,houvemos,houveram,houvera,houvéramos,haja,hajamos,hajam,houvesse,houvéssemos,houvessem,houver,houvermos,houverem,houverei,houverá,houveremos,houverão,houveria,houveríamos,houveriam,sou,somos,são,era,éramos,eram,fui,foi,fomos,foram,fora,fôramos,seja,sejamos,sejam,fosse,fôssemos,fossem,for,formos,forem,serei,será,seremos,serão,seria,seríamos,seriam,tenho,tem,temos,tém,tinha,tínhamos,tinham,tive,teve,tivemos,tiveram,tivera,tivéramos,tenha,tenhamos,tenham,tivesse,tivéssemos,tivessem,tiver,tivermos,tiverem,terei,terá,teremos,terão,teria,teríamos,teriam";
    	//String[] ptStopWordsL = ptStopWords.split(",");
    	String[] ptStopWordsL ={"de","a","o","que","e","do","da","em","um","para","é","com","não","uma","os","no","se","na","por","mais","as","dos","como","mas","foi","ao","ele","das","tem","à","seu","sua","ou","ser","quando","muito","há","nos","já","está","eu","também","só","pelo","pela","até","isso","ela","entre","era","depois","sem","mesmo","aos","ter","seus","quem","nas","me","esse","eles","estão","você","tinha","foram","essa","num","nem","suas","meu","às","minha","têm","numa","pelos","elas","havia","seja","qual","será","nós","tenho","lhe","deles","essas","esses","pelas","este","fosse","dele","tu","te","vocês","vos","lhes","meus","minhas","teu","tua","teus","tuas","nosso","nossa","nossos","nossas","dela","delas","esta","estes","estas","aquele","aquela","aqueles","aquelas","isto","aquilo","estou","está","estamos","estão","estive","esteve","estivemos","estiveram","estava","estávamos","estavam","estivera","estivéramos","esteja","estejamos","estejam","estivesse","estivéssemos","estivessem","estiver","estivermos","estiverem","hei","há","havemos","hão","houve","houvemos","houveram","houvera","houvéramos","haja","hajamos","hajam","houvesse","houvéssemos","houvessem","houver","houvermos","houverem","houverei","houverá","houveremos","houverão","houveria","houveríamos","houveriam","sou","somos","são","era","éramos","eram","fui","foi","fomos","foram","fora","fôramos","seja","sejamos","sejam","fosse","fôssemos","fossem","for","formos","forem","serei","será","seremos","serão","seria","seríamos","seriam","tenho","tem","temos","tém","tinha","tínhamos","tinham","tive","teve","tivemos","tiveram","tivera","tivéramos","tenha","tenhamos","tenham","tivesse","tivéssemos","tivessem","tiver","tivermos","tiverem","terei","terá","teremos","terão","teria","teríamos","teriam"};
    	if (language.equalsIgnoreCase("es")){
    		int index = Arrays.binarySearch(esStopWordsL, word);
    		if (index>=0) response = false;
    		/*
    		if (word.equalsIgnoreCase("el") || word.equalsIgnoreCase("la") || word.equalsIgnoreCase("los") || word.equalsIgnoreCase("las")
    				|| word.equalsIgnoreCase("en") || word.equalsIgnoreCase("es") || word.equalsIgnoreCase("cual"))
    			response = false;*/
    	}
    	if (language.equalsIgnoreCase("en")){
    		int index = Arrays.binarySearch(enStopWordsL, word);
    		if (index>=0) response = false;
    	/*	if (word.equalsIgnoreCase("the") || word.equalsIgnoreCase("and") || word.equalsIgnoreCase("or") || word.equalsIgnoreCase("is")
    				 || word.equalsIgnoreCase("at")  || word.equalsIgnoreCase("which")  || word.equalsIgnoreCase("on"))
    			response = false;*/
    	}
    	if (language.equalsIgnoreCase("pt")){
    		int index = Arrays.binarySearch(ptStopWordsL, word);
    		if (index>=0) response = false;
    	}
    	return response;
    	
    }
    
	/**
	 * getRecomendationType:  get recomendation type
	 * 
	 * @return type number of recomendation
	 */	
    /*
    public static String getRecomendationType(){
    	String type ="";
    	//get recomendation value from db
    	IgualDB db = new IgualDB();
    	int lastR = 0;
    	String lastValue = db.getValueForKey("last_recomendation_algorithm");
    	if (lastValue.length()>0){
    		try{
    			lastR = Integer.parseInt(lastValue);
    		}catch (NumberFormatException nfe){
    			
    		}
    		
    	}
    	if (lastR == 0) type = "1";
    	if (lastR == 1) type = "2";
    	if (lastR == 2) type = "3";
    	if (lastR == 3) type = "1";
    	return type;
    }*/
    
	/**
	 * setRecomendationType:  save last recommendation type used
	 * 
	 * @param value new value for recommendation
	 * 
	 * @return result 1 on success, 0 otherwise
	 */	
    /*public static int setRecomendationType(String value){
    	int result =0;
    	IgualDB db = new IgualDB();
    	result = db.setValueForKey("last_recomendation_algorithm", value);
    	
    	return result;
    }*/

    
	public static void main(String[] args) {
//		int rec = Misc.setRecomendationType("3");
		boolean valid = Misc.isValidWord("is", "en");
		if (valid) System.out.println("Valida");
		else  System.out.println("stop word");
	//	System.out.println(rec);
	}
	
	/**
	 * deleteFolderAndcontent:  delete a folder
	 * 
	 * @param dir directory path
	 * 
	 * @return childrenDeleted true on success
	 */	
	public static boolean deleteFolderAndcontent(File dir) {  
	    File[] children = dir.listFiles();  
	    boolean childrenDeleted = true;  
	    for (int i = 0; children != null && i < children.length; i++) {  
	        File child = children[i];  
	        if (child.isDirectory()) {  
	            childrenDeleted = Misc.deleteFolderAndcontent(child) && childrenDeleted;  
	        }  
	        if (child.exists()) {  
	            childrenDeleted = child.delete() && childrenDeleted;  
	        }  
	    }  
	    dir.delete();
	    return childrenDeleted;  
	}  
}
