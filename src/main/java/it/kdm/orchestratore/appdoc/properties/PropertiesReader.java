package it.kdm.orchestratore.appdoc.properties;


import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import java.io.*;
import java.util.Properties;

// Using an Enum is the most secure Singleton Pattern implementation
public final class PropertiesReader {

    //TODO: Questa classe è duplicata

    private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);
    //private static ResourceBundle properties = ResourceBundle.getBundle("config");

    private Properties properties   		= new Properties();
    private Properties global   			= new Properties();
    private Properties docsync   			= new Properties();

    private static PropertiesReader instance = null;
    public static PropertiesReader getInstance() {
        if (instance == null) {
            instance = new PropertiesReader();
        }

        return instance;
    }

    private PropertiesReader() {
        try(InputStream app = getInputStreamApp();
            InputStream docsyncres = getInputStreamDocSync();
            InputStream global = getInputStreamGlobal()) {

            properties.load(app);
            this.global.load(global);
            docsync.load(docsyncres);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private InputStream getInputStreamApp() {
        return findResource("AppDoc.properties");
    }

    private InputStream getInputStreamDocSync() {
        return findResource("docsyncservice.properties");
    }
    public InputStream getInputStreamGlobal() {
        return findResource("system.properties");
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

        return getProperty("name.app");
        //return properties.getProperty("name.app");
    }

    public String getAppError1(){
        return getProperty("app.error1");
        //return properties.getProperty("app.error1");
    }

    public String getAppError2(){
        return getProperty("app.error2");
        //return properties.getProperty("app.error2");
    }
    public String getSSOUSER(){
        return getProperty("sso_user.header");
        //return properties.getProperty("sso_user.header");
    }

    public String getFileUploadPath(){
        return getProperty("app.fileupload.path");
        //return properties.getProperty("app.fileupload.path");
    }

    public String getServerRestPath(){
        return getGlobalProperty("server.rest");
        //return global.getProperty("server.rest");
    }
    public String getAttachDir(){
        return getGlobalProperty("attach.dir");
//        return global.getProperty("attach.dir");
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

    public String getDocSyncProperty(String key){
        String res = "";
        try{
            res = ApplicationProperties.getInstance("docsyncservice.properties").getPropertyByKey(key, "");
        }catch (Exception w){
            w.printStackTrace();
        }
        //return properties.getProperty(key);
        return res;
    }

    public PropertyResolver getDocSyncProperties(){
//        return docsync;
        //return ToolkitConnector.loadConfigFile("docsyncservice");
        PropertyResolver res = null;
        try{
            res = ApplicationProperties.getInstance("docsyncservice.properties").getProp();
        }catch (Exception w){
            w.printStackTrace();
        }
        //return properties.getProperty(key);
        return res;
    }
    public String getProperty(String key){
        String res = "";
        try{
            res = ApplicationProperties.getInstance("AppDoc.properties").getPropertyByKey(key, "");
        }catch (Exception w){
            w.printStackTrace();
        }
        //return properties.getProperty(key);
        return res;
    }
}
