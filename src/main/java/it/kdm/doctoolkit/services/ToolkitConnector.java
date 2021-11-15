package it.kdm.doctoolkit.services;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 02/07/14
 * Time: 10.37
 * To change this template use File | Settings | File Templates.
 */
public class ToolkitConnector {

//    private static Map<String,String> configMap = null;
//
//    public ToolkitConnector() {
//        configMap = new HashMap<String,String>();
//
//    }
//    public static void connect() throws DocerApiException{
//
//        String attBase = getLocalAddress();
//        remoteConnect(httBase);
//    }

    /*public static String makePassword(String username) {
        boolean securePassword = ToolkitConnector.isSecurePasswordEnabled();
        logger.info("attenzione toolkintconnector ha rigenerato la password ");
        if (securePassword==true) {

            return makeSecurePassword(username);
        }
        return username.toLowerCase();
    }*/

    public static String makeSecurePassword(String username){
        PropertyResolver prop = ToolkitConnector.loadConfigFile();

        String secret = prop.getProperty("secretKey");


        String pwd = username+secret;
        String result;

        try {
            byte[] bytesOfMessage = pwd.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            result = new String(Hex.encodeHex(thedigest));

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(ToolkitConnector.class);

    public static String getGlobalProperty(String propertyName){
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String propertyValue = conf.getProperty(propertyName);
        return propertyValue;
    }


    public static String getGlobalProperty(String propertyName,String defaultValue){
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String propertyValue = conf.getProperty(propertyName,defaultValue);
        return propertyValue;
    }


    public static String getHelpProperty(String propertyName){
        PropertyResolver conf = ToolkitConnector.loadConfigFile("help");
        String propertyValue = conf.getProperty(propertyName);
        return propertyValue;
    }

    public static String getLocalAddress(String servConfigName) {
        PropertyResolver prop = loadConfigFile();
        String httBase = prop.getProperty("documentale.docManager.server.host");
        httBase = httBase + prop.getProperty("documentale." + servConfigName + ".ws.endpoint");

        logger.info("getLocalAddress: " + httBase);
        return httBase;
    }

    public static Map<String, String> getListaSedi() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();

        HashMap<String, String> ret = new HashMap<>();

        String sede = getSedeLocale();

        //for(String name : conf.stringPropertyNames()) {
        //    if (name.startsWith("sede.")) {
        //        ret.put(name.substring(5), conf.getProperty(name));
        //    }
        //}
        ret.put( sede , conf.getProperty("sede."+sede));

        return ret;
    }

    public static String getSedeLocale() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String sedeLocale = conf.getProperty("sede","DOCAREA");
        return sedeLocale;
    }

    public static String getSysUser() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String sysUser = conf.getProperty("sysUser","admin");
        return sysUser;
    }

    public static boolean devModePassword() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String devMode = conf.getProperty("devModePassword");

        if ("true".equalsIgnoreCase(devMode))
            return true;

        return false;
    }

    public static Optional<String> getRootName() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        //TODO: Throws exception?
        return Optional.fromNullable(conf.getProperty("root.name"));
    }

    public static boolean isSecurePasswordEnabled() {
        PropertyResolver conf = ToolkitConnector.loadConfigFile();
        String disableSecurePassword = conf.getProperty("disableSecurePassword");

        if (disableSecurePassword!=null && "true".equalsIgnoreCase(disableSecurePassword))
            return false;

        return true;
    }
//    public static void connect(String serverIP) throws DocerApiException{
//        String pattern = "http://%s:%d/";
//        String httpBase = String.format(pattern,serverIP,8080);
//
//        remoteConnect(httpBase);
//    }

//    public static void connect(String serverIP, int serverPort) throws DocerApiException{
//        //syntax: documentale.docManager.server.host=http://192.168.0.95:8080/
//        String pattern = "http://%s:%d/";
//        String httpBase = String.format(pattern,serverIP,serverPort);
//
//        remoteConnect(httpBase);
//    }

//    public static void connectObject(ICIFSObject obj) throws DocerApiException{
//        //syntax: documentale.docManager.server.host=http://192.168.0.95:8080/
//
//        try {
//            Properties prop = loadConfigFile();
//            String sede = obj.getLocationIP();
//
//            //se il parametro non è settato, setta il default in locale
//            if (sede == null || "".equals(sede))
//                sede = prop.getProperty("documentale.docManager.server.host");
//
//            String httpBase = getLocationIP(sede);
//
//            remoteConnect(httpBase);
//
//        } catch (Exception e) {
//            throw new DocerApiException(e);
//        }
//    }

    public static String getServiceAddress(String sede, String servConfigName){
        //syntax: documentale.docManager.server.host=http://192.168.0.95:8080/
        PropertyResolver prop = loadConfigFile();
        String httBase="";

        if ("".equals(sede)) {
            httBase = prop.getProperty("documentale.docManager.server.host")+prop.getProperty("documentale." + servConfigName + ".ws.endpoint");
        } else {
            String sedeLocation = prop.getProperty("sede." + sede.toUpperCase());

            //se il parametro non è valido setta il default in locale
            if (sedeLocation == null)
                throw new RuntimeException("Sede: " + sede.toUpperCase() + " non configurata correttamente.");

            httBase = sedeLocation+prop.getProperty("documentale." + servConfigName + ".ws.endpoint");
        }

        logger.info("getLocationIP: " + httBase);

        return httBase;
    }

    public static String getLocationIP(String sede) {
        //syntax: documentale.docManager.server.host=http://192.168.0.95:8080/
        PropertyResolver prop = loadConfigFile();

        if ("".equals(sede))
            return prop.getProperty("documentale.docManager.server.host");

        String sedeLocation = prop.getProperty("sede." + sede.toUpperCase());

        //se il parametro non è valido setta il default in locale
        if (sedeLocation == null)
            throw new RuntimeException("Sede: " + sede.toUpperCase() + " non configurata correttamente.");
            //sedeLocation = ""; //se non trova la configurazione switcha in locale (da verificare se utile)


        return sedeLocation;
    }

    public static String getGoogleApiKey() {
        PropertyResolver prop = loadConfigFile();

        return prop.getProperty("googleApiKey","");
    }
//    public static void remoteConnect(String baseUrl) throws DocerApiException{
//
//        //syntax: documentale.docManager.server.host=http://192.168.0.95:8080/
//
//        try {
//
//            Properties prop = loadConfigFile();
//
//            String authService 	= baseUrl+prop.getProperty("documentale.auth.ws.endpoint");
//            String docerService = baseUrl+prop.getProperty("documentale.docer.ws.endpoint");
//            String fascService = baseUrl+prop.getProperty("documentale.fasc.ws.endpoint");
//            String protService = baseUrl+prop.getProperty("documentale.prot.ws.endpoint");
//            String regisService = baseUrl+prop.getProperty("documentale.regis.ws.endpoint");
//            String tracerService = baseUrl+prop.getProperty("documentale.tracer.ws.endpoint");
//
//            AuthenticationService.setWSURL(authService);
//            DocerService.setWSURL(docerService);
//            ServizioFascicolazione.setWSURL(fascService);
//            ServizioProtocollazione.setWSURL(protService);
//            ServizioRegistrazione.setWSURL(regisService);
//            TracerService.setWSURL(tracerService);
//        }
//        catch(Exception e) {
//            throw new DocerApiException(e);
//        }
//    }

    public static Optional<String> extractSedeFromPath(String path) {


        String regex = ToolkitConnector.getGlobalProperty("parse-sede-regex");

        if (Strings.isNullOrEmpty(regex))
            return Optional.absent();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            Optional<String> optional = Optional.of(matcher.group(1));
            return optional;
        }

        return Optional.absent();

//        //TODO SOLO PER PIROLA (DA SISTEMARE PER IL PRODOTTO)
//        if (scriptSede == null) {
//            File confDir = getConfigHome();
//            File sedeMvel = new File(confDir, "sede.mvel");
//            String script = FileUtils.readFileToString(sedeMvel);
//            scriptSede = MVEL.compileExpression(script);
//        }
//
//        HashMap<String, Object> env = new HashMap<>();
//        env.put("path", path);
//        return (Optional<String>) MVEL.executeExpression(scriptSede, env);
    }

    public static String extractSedeFromToken(String token) {
        String sede="";

        try {
            sede = Utils.extractTokenKey(token, "app");

            if ("".equalsIgnoreCase(sede)) {
                sede = ToolkitConnector.getSedeLocale();
            }
        } catch(Exception e) {}

        return sede;
    }

    public static PropertyResolver loadConfigFile() {
        try {
            PropertyResolver prop = ApplicationProperties.getInstance("system.properties").getProp();
//            String FrontendProperty = "system.properties";
//            File pathDirectory = new File(Utils.getConfigHome(), FrontendProperty);
//            try (InputStream inputStream = new FileInputStream(pathDirectory)) {
//                Properties prop = new Properties();
//                prop.load(inputStream);
//                return prop;
//            }
            return prop;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PropertyResolver loadConfigFile(String type) {
        try {
            String FrontendProperty = String.format( "%s.properties", type);
            PropertyResolver prop = ApplicationProperties.getInstance(FrontendProperty).getProp();
//            File pathDirectory = new File(Utils.getConfigHome(), FrontendProperty);
//            try (InputStream inputStream = new FileInputStream(pathDirectory)) {
//                Properties prop = new Properties();
//                prop.load(inputStream);
//                return prop;
//            }
            return prop;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
