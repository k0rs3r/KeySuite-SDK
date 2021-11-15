package it.kdm.orchestratore.appBpm.properties;


import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;


public final class PropertiesReader {

    //TODO: Questa classe è duplicata

	private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);
	//private static ResourceBundle properties = ResourceBundle.getBundle("config");		

    private Properties properties   		= new Properties();
    private Properties global   			= new Properties();

	private Properties pbpm   			= new Properties();
	private static PropertiesReader instance = null;
	public static PropertiesReader getInstance() {
        if (instance == null) {
            instance = new PropertiesReader();
        }

        return instance;
    }

    private PropertiesReader() {
        try(InputStream app = getInputStreamApp();
            InputStream global = getInputStreamGlobal();
			InputStream bpm = getInputStreamBPM();
		) {

            properties.load(app);
            this.global.load(global);
			this.pbpm.load(bpm);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private InputStream getInputStreamApp() {
        return findResource("AppBPM.properties");
    }

    public InputStream getInputStreamGlobal() {
        return findResource("system.properties");
    }


	public InputStream getInputStreamBPM() {
		return findResource("bpm-server-config.properties");
	}



	private InputStream findResource(String name) {
		InputStream inputStream = null;
		
		ClassLoader.getSystemClassLoader();

		inputStream = getFromSystemProperty(name);
		if (inputStream == null )  {
			log.info("{} not set, try Class path", name);
			inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(name);
		} 
		if (inputStream == null) {
			log.info("Class retrieving failed, try Classloader path");
			inputStream = ClassLoader.getSystemResourceAsStream(name);
		}

        if (inputStream == null) {
            throw new IllegalStateException(String.format("%s not found on the classpath.", name));
        }

		return inputStream;
	}
	

	
	
	private InputStream getFromSystemProperty( String file) {
		File location = Utils.getConfigHome();
		if (!location.exists()) {
			return null;
		} 
		
		File myloc = new File(location, file);
		log.debug("looking in " + myloc.getAbsolutePath());

		try {
			return new FileInputStream(myloc);
		} catch (FileNotFoundException e) {
			log.error("Exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	/**
	 * Restituisce un messaggio leggendo la chiave specificata
	 * nel resource bundle dei testi (x.properties)
	 * @param key
	 * @return

	public static String getProperty(String key) {
		if (key==null) {
			return "";
		}
		String value = properties.getString(key);
		return value;
	}
	*/
	
	
	/**
	 *  GETTER
	 */
	public String getTestProperties(){
		//return properties.getProperty("name.app");
		return getProperty("name.app");
	}

	public String getAppError1(){
		//return properties.getProperty("app.error1");
		return getProperty("app.error1");
	}

	public String getAppError2(){
		//return properties.getProperty("app.error2");
		return getProperty("app.error2");
	}
	public String getSSOUSER(){
		//return properties.getProperty("sso_user.header");
		return getProperty("sso_user.header");
	}
	
	public String getFileUploadPath(){
		//return properties.getProperty("app.fileupload.path");
		return getProperty("app.fileupload.path");
	}
	
	public String getServerRestPath(){
		//return global.getProperty("server.rest");
		return getGlobalProperty("server.rest");
	}
	public String getAttachDir(){
		//return global.getProperty("attach.dir");
		return getGlobalProperty("attach.dir");
	}
	public String getGlobalProperty(String key){
		String res = "";
		try{
			res = ApplicationProperties.getInstance("system.properties").getPropertyByKey(key, "");
		}catch (Exception w){
			w.printStackTrace();
		}
		//return global.getProperty(key);
		return res;
	}
	public String getProperty(String key){
		String res = "";
		try{
			res = ApplicationProperties.getInstance("AppBPM.properties").getPropertyByKey(key, "");
		}catch (Exception w){
			w.printStackTrace();
		}
		//return properties.getProperty(key);
		return res;
	}
	public String getBpmProperty(String key){
		String res = "";
		try{
			res = ApplicationProperties.getInstance("bpm-server-config.properties").getPropertyByKey(key, "");
		}catch (Exception w){
			w.printStackTrace();
		}
		//return pbpm.getProperty(key);
		return res;
	}
}
