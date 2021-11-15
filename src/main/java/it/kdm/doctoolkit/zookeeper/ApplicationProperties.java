package it.kdm.doctoolkit.zookeeper;

import it.kdm.orchestratore.utils.ResourceUtils;
import org.springframework.core.env.PropertyResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ApplicationProperties {

    private static PropertyResolver env = null;

    public static void setEnv(PropertyResolver env){
        synchronized (properties){
            ApplicationProperties.env = env;
        }
    }

    public static PropertyResolver getEnv(){
        return ApplicationProperties.env;
    }

    /*public static Properties getEnv(){
        return env;
    }*/

    public interface PropFile{
        String getPropertyByKey(String key,String def);
        PropertyResolver getProp();
    }

    public static void clear(){
        properties.clear();
    }

    public static void clearCache(){
    }

    final static String APPDOC = "AppDoc.properties";
    final static String APPBPM = "AppBPM.properties";
    final static String APPHOME = "AppHome.properties";
    final static String BPM_SERVER = "bpm-server-config.properties";
    final static String DOCSYNCSERVICE = "docsyncservice.properties";
    final static String CUSTOM_APP = "custom-app.properties";
    final static String SYSTEM = "system.properties";

    static final String[] OrderedList = {
            APPDOC,
            APPBPM,
            APPHOME,
            BPM_SERVER,
            DOCSYNCSERVICE,
            CUSTOM_APP,
            SYSTEM
    } ;

    //private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);

    public static final String CONFIG_URI = "KEYSUITE_CONFIG";
    public static final String CONFIG_HOME = System.getProperty(CONFIG_URI,new File(System.getProperty("user.home"),"bpm-config" ).toString());
    public static final File CONFIG_HOME_FILE = new File(CONFIG_HOME);

    //public final static File configHome;

    //static {
        //System.setProperty(CONFIG_URI,System.getProperty(CONFIG_URI,new File(System.getProperty("user.home"),"bpm-config" ).toString()));
        //configHome = new File(System.getProperty(CONFIG_URI));
    //}

    private static HashMap<String, PropFile> properties= new HashMap<>();
    //private static String configUri; // = ResourceCache.getGlobalProperty(CONFIG_URI,"/root/bpm-config/");



    public static String get(String key){
        return get(key,null);
    }

    public static String get(String key, String defaultValue){

        if (env!=null)
            return env.getProperty(key,defaultValue);

        for ( String file : OrderedList ){
            String v = getInstance(file).getPropertyByKey(key,null);
            if (v != null)
                return v;
        }

        String value = System.getProperty(key);
        if (value!=null)
            return value;
        value = System.getenv(key);
        if (value!=null)
            return value;
        return defaultValue;
    }

    public static PropFile getInstance(String name) {

        if (env!=null)
            return new PropFile() {
                @Override
                public String getPropertyByKey(String key, String def) {
                    return env.getProperty(key,def);
                }

                @Override
                public PropertyResolver getProp() {
                    return env;
                }
            };

        PropFile prop = properties.get(name);

        if(prop == null){
            Properties p =new Properties();
            try {
                InputStream is = ResourceUtils.getResourceNoExc(name);
                if (is!=null){
                    p.load(is);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            prop = new PropFile() {
                @Override
                public String getPropertyByKey(String key, String def) {
                    return p.getProperty(key,def);
                }

                @Override
                public PropertyResolver getProp() {
                    return new PropertyResolver() {
                        @Override
                        public boolean containsProperty(String key) {
                            return p.containsKey(key);
                        }

                        @Override
                        public String getProperty(String key) {
                            return p.getProperty(key);
                        }

                        @Override
                        public String getProperty(String key, String defaultValue) {
                            return p.getProperty(key,defaultValue);
                        }

                        @Override
                        public <T> T getProperty(String key, Class<T> targetType) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getRequiredProperty(String key) throws IllegalStateException {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String resolvePlaceholders(String text) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
            properties.put(name, prop );
        }
        return prop;
    }
}
