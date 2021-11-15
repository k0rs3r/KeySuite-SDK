package it.kdm.orchestratore.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.underscore.lodash.U;
import difflib.*;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.orchestratore.appBpm.utils.Helper;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.utils.KDMUtils;
import it.kdm.orchestratore.utils.ResourceUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static difflib.DiffRow.Tag.*;
import static difflib.DiffRowGenerator.wrapInTag;

public class ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final String BLANK_LINE = "--BLANK_LINE--";

    public int SURROUND_LINE = 4;
    public String INLINE_NEW_TAG = "b";
    public String INLINE_NEW_CLASS = "newline";
    public String INLINE_OLD_TAG = "s";
    public String INLINE_OLD_CLASS = "oldline";
    public int MAX_DELTAS = 3;

    public class ConfigurationException extends Exception {
        private List<LogEntry> logEntries;

        public ConfigurationException(List<LogEntry> logEntries){
            super(""+logEntries.size()+" messaggi");
            this.logEntries = new ArrayList<>(logEntries);
        }

        public ConfigurationException(Exception exc){
            super(exc.getMessage());
            logEntries = new ArrayList<>();
            logEntries.add(new LogEntry(Level.ERROR,exc.getMessage()));

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exc.printStackTrace(pw);

            logEntries.add(new LogEntry(Level.TRACE,sw.toString()));
        }

        public ConfigurationException(String exc){
            super(exc);
            logEntries = new ArrayList<>();
            logEntries.add(new LogEntry(Level.ERROR,exc));
        }

        public List<LogEntry> getLog(){
            return logEntries;
        }
    }

    public enum Level {
        ERROR,
        WARNING,
        INFO,
        TRACE
    }

    public static class LogEntry {
        Level level;

        public LogEntry(Level level, String message){
            this.level = level;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public Level getLevel() {
            return level;
        }

        String message;

        @Override
        public String toString(){
            return String.format("%s:%s",level,message);
        }
    }

    private static tuple tuple(Object first,Object second){
        tuple t = new tuple();
        t.first = first;
        t.second = second;
        return t;
    }

    private List<LogEntry> logEntries;

    private void logWarn(String message, String... args){
        String txt = args.length>0? String.format(message,args) : message;
        logger.warn(txt);
        logEntries.add(new LogEntry(Level.WARNING,txt));
    }

    private void logError(String message, String... args){
        String txt = args.length>0? String.format(message,args) : message;
        logger.error(txt);
        logEntries.add(new LogEntry(Level.ERROR,txt));
    }

    boolean hasErrors(){
        for( LogEntry le : logEntries )
            if (le.level.equals(Level.ERROR))
                return true;
        return false;
    }

    private void logInfo(String message, String... args){
        String txt = args.length>0? String.format(message,args) : message;
        logger.info(txt);
        logEntries.add(new LogEntry(Level.INFO,txt));
    }

    private void logTrace(String message, String... args){
        String txt = args.length>0? String.format(message,args) : message;
        logger.info(txt);
        logEntries.add(new LogEntry(Level.TRACE,txt));
    }

    static class tuple{
        Object first;
        Object second;

        tuple(){

        }

        tuple(Object first,Object second){
            this.first = first;
            this.second = second;
        }

        public Object getFirst() {
            return first;
        }

        public Object getSecond() {
            return second;
        }
    }

    private static final String WS_DOCER = "WSDocer";
    private static final String WS_PROTOCOLLAZIONE = "WSProtocollazione";
    private static final String WS_FASCICOLAZIONE = "WSFascicolazione";
    private static final String WS_REGISTRAZIONE = "WSRegistrazione";
    private static final String WS_FIRMA = "WSFirma";
    private static final String WS_PEC = "WSPEC";
    private static final String KS_SYSTEM = "system";
    private static final String KS_APP_BPM = "appBPM";
    private static final String KS_APP_DOC = "appDoc";
    private static final String KS_APP_HOME = "appHome";
    private static final String KS_BPM_SERVER = "bpmServer";
    private static final String KS_CUSTOM_APP = "customApp";
    private static final String KS_QUARTZ = "quartz";
    private static final String KS_SYSTEM_AOO = "systemAoo";
    private static final String DOCER_PROP_DATABASE = "docerPropDatabase";
    private static final String WS_SYSTEM = "docersystem";
    private static final String DOCER_PROP_JMS = "docerPropJms";
    private static final String DOCER_PROP_SOLR = "docerPropSolr";
    private static final String DOCER_PROP_FIRMA = "docerPropFirma";
    private static final String DOCER_PROP_FIRMA_ARUBA = "docerPropFirmaAruba";
    private static final String DOCER_PROP_PROTO = "docerPropProto";
    private static final String KS_CONFIG = "config";
    private static final String KS_CONFIG_AOO = "configAoo";
    //private static final String KS_CONFIG_SCHEMA = "configSchema";

    private static final String SOLR_SCHEMA = "solrSchema";
    private static final String SOLR_CONFIG = "solrConfig";
    private static final String SOLR_LOCAL = "solrLocal";

    private List<String> simpleTypes = Arrays.asList(StringUtils.split("string,int,decimal,date,dateTime,boolean",","));

    protected Map<String,Object> main_json = new LinkedHashMap<>();
    protected Map<String,Object> aoo_json = new LinkedHashMap<>();
    protected List<String> aoo_files = new ArrayList<>();
    protected Map<String,Entry> entries = new LinkedHashMap<>();
    //protected Map<String,Map<String,Map<String,Map>>> schemaConfig = new LinkedHashMap<>();
    protected ObjectMapper mapper = new ObjectMapper();

    protected final Map<String,File> files;
    protected final Collection<String> dirty_files;
    protected String codEnte;
    protected String codAoo;

    protected File configFile;
    protected File aooConfigFile;

    boolean opened = false;

    protected Pattern roleRe = Pattern.compile("([^(]+)\\s+\\(([^)]+)\\).*");
    protected Pattern hintRe = Pattern.compile("\\$\\{([^\\.\\}]+)\\.([^\\.\\}]+)\\.([^\\.\\}]+)\\}");
    private Map<String, PropertiesConfiguration> handlers = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> xmls = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> jsons = new LinkedHashMap<>();

    private File getDocerConfig(String service , String path){
        //String DOCER_CONFIG = "/opt/docer/configurations";
        String DOCER_CONFIG = System.getProperty("DOCER_CONFIG");
        File file = new File(DOCER_CONFIG,service+"/"+path);

        if (!file.exists()){
            InputStream is = this.getClass().getResourceAsStream("/docer/"+service+"/"+path);
            if (is==null)
                throw new RuntimeException("conf non presente");
            try {

                FileUtils.copyInputStreamToFile(is,file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logInfo("file '%s:%s' generato dalle risorse",service, file.getName());
        }

        return file;
    }

    private File getSolrConfig(String path, boolean init){
        String SOLR_CONFIG = System.getProperty("SOLR_CONFIG");

        if (SOLR_CONFIG==null)
            return null;

        File file = new File(SOLR_CONFIG,path);

        if (!file.exists()){
            if (init){
                InputStream is = this.getClass().getResourceAsStream("/solr/"+path);
                if (is!=null) {
                    try {
                        String fileString = StreamUtils.copyToString(is, Charset.defaultCharset());

                        fileString = fileString.replace("%COD_ENTE%", codEnte);
                        fileString = fileString.replace("%COD_AOO%", codAoo);

                        FileUtils.writeStringToFile(file, fileString);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                logInfo("file '%s' generato dalle risorse",file.getName());
                return file;
            }
            return null;
        }

        return file;
    }

    private File getConfig(String path){
        File file =  new File(Utils.getConfigHome(),path );

        if (!file.exists()){
            if (path.startsWith(codAoo+"-"))
                path = "aoo" + path.substring(codAoo.length());
            InputStream is;
            if (path.endsWith(".properties"))
                is = this.getClass().getResourceAsStream("/properties/"+path);
            else
                is = this.getClass().getResourceAsStream(path);
            if (is!=null)
            {
                try {
                    String fileString = StreamUtils.copyToString(is, Charset.defaultCharset());

                    fileString = fileString.replace("%COD_ENTE%",codEnte);
                    fileString = fileString.replace("%COD_AOO%",codAoo);
                    //fileString = fileString.replace("%ZOOKEEPER%","localhost:9983");
                    //fileString = fileString.replace("%DOCER%","http://localhost:8080");
                    fileString = fileString.replace("%KS_CONFIG%",Utils.getConfigHome().toString());

                    FileUtils.writeStringToFile(file,fileString);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return file;
    }

    File addFile(String fileId, File file, boolean local){
        files.put(fileId,file);
        if (local)
            aoo_files.add(fileId);

        //if (file.toString().endsWith(".properties"))
        //    loadProperties(fileId);

        return file;
    }

    public ConfigurationManager(String codEnte, String codAoo)  {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.codEnte = codEnte;
        this.codAoo = codAoo;

        files = new LinkedHashMap<>();
        aoo_files = new ArrayList<>();
        dirty_files = new HashSet<>();
        logEntries = new ArrayList<>();

        _initFiles();

        configFile = files.get(KS_CONFIG);
        aooConfigFile = files.get(KS_CONFIG_AOO);
    }

    public Map<String,Object> getConfig() throws IOException {

        _openConfigFiles();

        Map<String,Object> cMap = new LinkedHashMap<>();
        cMap.putAll(main_json);
        cMap.putAll(aoo_json);

        return cMap;
    }

    public Map<String,Object> getSchema() throws IOException {

        InputStream is;
        if (Session.getRequest()==null){
            is = this.getClass().getResourceAsStream("/configSchema.json");
        } else {
            try{
                is = ResourceUtils.getResourceAsStream(Session.getRequest(),Session.getUserInfoNoExc().getCodEnte(),null,"/configSchema.json");
            } catch( FileNotFoundException fnf){
                is = this.getClass().getResourceAsStream("/configSchema.json");
            }
        }


        //InputStream is = ResourceUtils.getResource(Session.getRequest(),null,null,"/configSchema.json");
        String schemaString = schemaString = StreamUtils.copyToString(is, Charset.defaultCharset());
        Map<String,Object> map = mapper.readValue(schemaString, new TypeReference<Map<String,Object>>() { });
        return  map;
    }

    public List<LogEntry> saveConfig(Map<String,Object> config, boolean pin) throws IOException , ConfigurationException {

        _openConfigFiles();

        Map<String,Object> main = new LinkedHashMap<>();
        Map<String,Object> aoo = new LinkedHashMap<>();
        for( String key : config.keySet() ){
            if (main_json.containsKey(key))
                main.put(key,config.get(key));
            else
                aoo.put(key,config.get(key));
        }
        String mainString = Helper.hashMapToJson(main,true);
        String aooString = Helper.hashMapToJson(aoo,true);

        if (pin){
            String timestamp = ""+ System.currentTimeMillis();
            FileUtils.copyFile(files.get(KS_CONFIG), new File(files.get(KS_CONFIG).toString()+"."+timestamp));
            FileUtils.copyFile(files.get(KS_CONFIG_AOO), new File(files.get(KS_CONFIG_AOO).toString()+"."+timestamp));
        }

        String txt = checkDiff(files.get(KS_CONFIG),mainString);

        if (txt!=null) {
            FileUtils.writeStringToFile(files.get(KS_CONFIG), mainString);
            logInfo("file '%s' modificato", files.get(KS_CONFIG).getName());
            logTrace(txt);
        }

        txt = checkDiff(files.get(KS_CONFIG_AOO),aooString);

        if (txt!=null) {
            FileUtils.writeStringToFile(files.get(KS_CONFIG_AOO), aooString);
            logInfo("file '%s' modificato", files.get(KS_CONFIG_AOO).getName());
            logTrace(txt);
        }
        return new ArrayList<>(logEntries);

    }

    public Collection<Long> getPinnedConfigs() throws IOException {

        Collection<File> versions = FileUtils.listFiles(
                Utils.getConfigHome(),
                new WildcardFileFilter(configFile.getName()+".*"), null);

        Collection<Long> timestamps = new HashSet<>();

        for ( File v : versions ){
            String timestamp = v.getName().split("\\.")[2];
            timestamps.add(Long.parseLong(timestamp));
        }

        List<Long> sorted = new ArrayList<>(timestamps);
        Collections.sort(sorted);

        return sorted;
    }

    public List<LogEntry> restoreConfig(Long timestamp) throws IOException {

        logEntries.clear();

        File vConfigFile = new File(configFile+"."+timestamp);
        File vAooConfigFile = new File(aooConfigFile+"."+timestamp);

        if (vConfigFile.exists() && vAooConfigFile.exists() ){
            FileUtils.copyFile(vConfigFile,configFile);
            logInfo("file '%s' ripristinato", configFile.getName());
            FileUtils.copyFile(vAooConfigFile,aooConfigFile);
            logInfo("file '%s' ripristinato", aooConfigFile.getName());
        } else {
            logError("versione '%s' non esiste",""+timestamp);
        }
        return new ArrayList<>(logEntries);
    }

    public List<LogEntry> deleteConfig(Long timestamp) throws IOException {
        logEntries.clear();

        File vConfigFile = new File(configFile+"."+timestamp);
        File vAooConfigFile = new File(aooConfigFile+"."+timestamp);

        FileUtils.deleteQuietly(vConfigFile);
        FileUtils.deleteQuietly(vAooConfigFile);

        logInfo("versione '%s' eliminata", ""+timestamp);

        return new ArrayList<>(logEntries);
    }

    private void collectErrors(){

        logEntries.clear();
        try {
            _openConfigFiles();
            if (main_json.size()>0){
                _export();
            } else {
                logError("non esiste configurazione corrente");
            }

            if (files.get(KS_SYSTEM).exists()){
                _import();
            } else {
                logError("non esiste configurazione corrente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logError(e.getMessage());
        }
    }

    public List<LogEntry> fixConfig(boolean preview) throws ConfigurationException{
        logEntries.clear();
        dirty_files.clear();
        try {
            //collectErrors();
            check();
            _writeFiles(preview);

        } catch (Exception e) {
            e.printStackTrace();
            //logError(e.getMessage());
            throw new ConfigurationException(e);
        }
        if (logEntries.size()==0)
            logInfo("nessun problema riscontrato");
        return new ArrayList<>(logEntries);
    }

    public List<LogEntry> initConfig() throws ConfigurationException {

        logEntries.clear();

        if (files.get(KS_CONFIG).exists() && !files.get(KS_SYSTEM).exists()) {

            try {
                _openConfigFiles();
                _export();
                if (!hasErrors()){
                    _writeFiles(false);
                } else {
                    //logError("configurazione non esportata");
                    throw new ConfigurationException(logEntries);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //logError(e.getMessage());
                throw new ConfigurationException(e);
            }

        } else {
            logInfo("configurazione non aggiornata");
        }

        return new ArrayList<>(logEntries);
    }

    public List<LogEntry> exportConfig(boolean preview) throws ConfigurationException{
        logEntries.clear();
        dirty_files.clear();
        try {
            _openConfigFiles();
            if (main_json.size()>0){
                _export();
                if (!hasErrors()) {
                    _writeFiles(preview);
                } else {
                    throw new ConfigurationException(logEntries);
                }
            } else {
                //logError("non esiste configurazione corrente");
                throw new ConfigurationException("non esiste configurazione corrente");
            }

        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            //logError(e.getMessage());
            throw new ConfigurationException(e);
        }

        return new ArrayList<>(logEntries);
    }

    public List<LogEntry> importConfig(boolean preview) throws ConfigurationException{
        logEntries.clear();
        dirty_files.clear();

        try {
            _openConfigFiles();
            if (files.get(KS_SYSTEM).exists()){

                main_json = new LinkedHashMap<>();
                aoo_json = new LinkedHashMap<>();

                InputStream is = this.getClass().getResourceAsStream("/custom-apps/default.json");

                if (is!=null) {
                    String fileString = StreamUtils.copyToString(is, Charset.defaultCharset());
                    Map defaultMap = Helper.jsonToHashMap(fileString);
                    aoo_json.put("apps", defaultMap);
                }

                _import();
                if (!hasErrors()) {
                    _writeFiles(preview);
                } else {
                    throw new ConfigurationException(logEntries);
                }
            } else {
                //logError("non esiste configurazione corrente");
                throw new ConfigurationException("non esiste configurazione corrente");
            }

        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logError(e.getMessage());
        }

        return new ArrayList<>(logEntries);
    }

    private void checkDocer() throws IOException {
        //TODO verifica campi base docer

        InputStream is = this.getClass().getResourceAsStream("/docer/WSDocer/configuration.xml");
        String config = StreamUtils.copyToString(is, Charset.defaultCharset());

        Map xconfig = (Map) U.fromXml(config);
        Map xactual = getXmlSection(WS_DOCER,"impianto","fields");

        Map<String, Object> configuration = (Map) xconfig.get("configuration");
        Map<String,Object> group = getXmlChild(configuration,"group",tuple("name","impianto"));
        Map<String,Object> xfields = getXmlChild(group,"section",tuple("name","fields"));

        for( String field : xfields.keySet()){
            if (!field.toString().startsWith("-") && !field.toString().startsWith("#")){

                Object xfield = xfields.get(field);
                if (xfield instanceof List){
                    xfield = ((List)xfield).get(0);
                    logWarn("field %s in impianto duplicato",field);
                }

                if ( "true".equals (((Map)xfield).get("-custom"))){
                    continue;
                }

                if (!xactual.containsKey(field)){
                    logWarn("field %s assente",field);
                    xactual.put(field, xfield);

                } else if (xactual.get(field) instanceof List){
                    logWarn("field %s duplicato",field);

                    xactual.put(field, ((List)xactual.get(field)).get(0) );

                }
            }
        }


        //TODO verifica pdf cartaceo e pdf etichetta
    }

    private void check() throws IOException {
        checkSolr();
        checkDocer();

        //TODO misura anti concorrenza
        //TODO gestire audit
        //TODO verifica presenza flussi e gestire configurazioni
    }

    private boolean checkSolrField(Map<String,Object> root, String name, String type, String indexed, String stored, String required, String multiValued){

        boolean modified = false;
        Map<String,Object> field = getXmlChild( (Map) root.get("schema") ,"field",tuple("name",name));

        if (field==null ||
            !type.equals(field.get("-type")) ||
            !stored.toString().equals(field.get("-stored")) ||
            !required.toString().equals(field.get("-required")) ||
            !multiValued.toString().equals(field.get("-multiValued")) ||
            !indexed.toString().equals(field.get("-indexed"))){
            modified = true;

            if (field==null){
                field = new LinkedHashMap<>();
                ((List)((Map) root.get("schema")).get("field")).add(field);
            }

            //<field name="roles" type="id" indexed="true" stored="true" required="false" multiValued="true" />
            field.put("-name",name);
            field.put("-type",type);
            field.put("-indexed",indexed.toString());
            field.put("-stored",stored.toString());
            field.put("-required",required.toString());
            field.put("-multiValued",multiValued.toString());

            logWarn("solr field '"+name+"' da aggiungere o correggere nello schema");
        }
        return modified;
    }

    private boolean checkSolrRule(List<Map<String,Object>> types, String typeName, String sectionName, String fieldName, String... rules){
        boolean modified = false;

        List<String> rule;
        if (rules==null || rules.length==0){
            rule = null;
        } else {
            rule = Arrays.asList(rules);
        }

        for ( Map<String,Object> type : types ){
            if (typeName.equals((String) type.get("name"))){
                Map<String,Object> section = (Map) type.get(sectionName);

                if (section==null){
                    if (rule==null)
                        return false;
                    section = new HashMap<>();
                    type.put(sectionName,section);
                }

                if (section.containsKey(fieldName)){

                    if (rule==null){
                        logWarn("regola "+fieldName+" da rimuovere da "+typeName+"/"+sectionName+" nel local.json");
                        section.remove(fieldName);
                        return true;
                    }

                    Object existingRule = section.get(fieldName);
                    if ( !(existingRule instanceof List) )
                        existingRule = Collections.singletonList(existingRule);

                    if (!existingRule.equals(rule)){
                        logWarn("regola "+fieldName+" errata in "+typeName+"/"+sectionName+" nel local.json");
                        modified = true;
                        section.put(fieldName,rule);
                    }
                } else {
                    if (rule==null)
                        return false;

                    logWarn("regola "+fieldName+" da aggiungere in "+typeName+"/"+sectionName+" nel local.json");
                    modified = true;
                    section.put(fieldName,rule);
                }
            }
        }
        return modified;
    }

    private void checkSolr() throws IOException {

        //schema.xml
        InputStream is = this.getClass().getResourceAsStream("/solr/schema-diff.xml");
        String schemaDiffString = StreamUtils.copyToString(is, Charset.defaultCharset());

        File schema = getSolrConfig("schema.xml",false);

        if (schema==null){
            logError("schema.xml not present");
            return;
        }

        Map<String,Object> xml = (Map) U.fromXml(FileUtils.readFileToString(schema));
        Map<String,Object> diffXml = (Map) U.fromXml(schemaDiffString);


        Object fields = ((Map)diffXml.get("schema")).get("field");
        if (!(fields instanceof List))
            fields = Collections.singletonList(fields);

        boolean modified = false;
        for( Map<String,Object> field : (List<Map<String,Object>>) fields ){
            modified = checkSolrField(xml,
                    (String) field.get("-name"),
                    (String) field.get("-type"),
                    (String) field.get("-indexed"),
                    (String) field.get("-stored"),
                    (String) field.get("-required"),
                    (String) field.get("-multiValued")) || modified;
        }

        files.put(SOLR_SCHEMA,schema);
        xmls.put(SOLR_SCHEMA,xml);

        if (modified){
            dirty_files.add(SOLR_SCHEMA);
        }

        //solrconfig.xml
        is = this.getClass().getResourceAsStream("/solr/solrconfig-diff.xml");
        String configDiffString = StreamUtils.copyToString(is, Charset.defaultCharset());

        File config = getSolrConfig("solrconfig.xml",false);

        if (config==null){
            logError("solrconfig.xml not present");
            return;
        }

        xml = (Map) U.fromXml(FileUtils.readFileToString(config));
        diffXml = (Map) U.fromXml(configDiffString);

        modified = false;
        for( String xtype : ((Map<String,Object>)diffXml.get("config")).keySet() ){

            Object items = ((Map<String,Object>)diffXml.get("config")).get(xtype);
            if (!(items instanceof List)) {
                items = Collections.singletonList(items);
            }

            for (Map item : (List<Map<String,Object>>) items){
                Map<String,Object> node = getXmlChild( ((Map)xml.get("config")),xtype,tuple("name",item.get("-name")));
                Map c = (Map)xml.get("config");
                if (node==null){
                    Object oldItems = (List<Map<String,Object>>) ((Map<String,Object>)xml.get("config")).get(xtype);
                    if (oldItems==null){
                        c.put(xtype,item);
                    } else if (oldItems instanceof List){
                        ((List)oldItems).add(item);
                    } else {
                        List x = new ArrayList<>();
                        x.add(oldItems);
                        x.add(item);
                        c.put(xtype,x);
                    }

                    modified = true;
                    logWarn("SOLR:item "+xtype+"/"+item.get("-name")+ " non presente");
                } else {
                    String _old = U.toXml(node);
                    String _new = U.toXml(item);

                    if (!_old.equals(_new)){
                        modified = true;
                        node.clear();
                        node.putAll(item);
                        logWarn("SOLR:item "+xtype+"/"+item.get("name")+ " cofigurato in modo sbagliato");
                    }
                }
            }

        }

        files.put(SOLR_CONFIG,config);
        xmls.put(SOLR_CONFIG,xml);

        if (modified){
            dirty_files.add(SOLR_CONFIG);
        }

        //local.json
        File localJson = getSolrConfig("local.json",false);

        if (localJson==null){
            logError("local.json not present");
            return;
        }

        String json = FileUtils.readFileToString(localJson);
        json = json.replaceAll("(/\\*.+\\*/)","");

        is = this.getClass().getResourceAsStream("/solr/local-diff.json");
        String jsonDiff = StreamUtils.copyToString(is, Charset.defaultCharset());

        Map<String,Object> localMap = Helper.jsonToHashMap(json);
        Map<String,Object> localDiffMap = Helper.jsonToHashMap(jsonDiff);

        List<Map<String,Object>> types = (List)localMap.get("types");
        List<Map<String,Object>> diffTypes = (List)localDiffMap.get("types");

        modified = false;
        for( Map<String,Object> type : diffTypes){

            for( String section : type.keySet()  ){
                if (type.get(section) instanceof Map){
                    Map<String,Object> jSection = (Map<String,Object>) type.get(section);
                    for ( String field : jSection.keySet() ){

                        Object rule = jSection.get(field);
                        String[] rules;

                        if (rule==null){
                            rules = null;
                        } else if (rule instanceof List){
                            rules = (String[]) ((List)rule).toArray(new String[0]);
                        } else {
                            rules = new String[] {rule.toString()};
                        }

                        modified = checkSolrRule(types, (String) type.get("name"),section,field,
                                rules) || modified;


                    }
                }



            }


        }

        files.put(SOLR_LOCAL,localJson);
        jsons.put(SOLR_LOCAL,localMap);

        if (modified){
            dirty_files.add(SOLR_LOCAL);
        }

    }

    private boolean _initFiles(){
        addFile(KS_CONFIG,getConfig("config.json"),false);
        addFile(KS_CONFIG_AOO,getConfig(codAoo+"-config.json"),false);
        addFile(KS_SYSTEM,getConfig("system.properties" ),false);
        addFile(KS_SYSTEM_AOO,getConfig(codAoo+"-system.properties" ),true);
        addFile(KS_APP_BPM,getConfig("AppBPM.properties" ),false);
        addFile(KS_APP_DOC,getConfig("AppDoc.properties" ),false);
        addFile(KS_APP_HOME,getConfig("AppHome.properties" ),false);
        addFile(KS_BPM_SERVER,getConfig("bpm-server-config.properties" ),false);
        addFile(KS_CUSTOM_APP,getConfig("custom-app.properties" ),false);
        addFile(KS_QUARTZ,getConfig("org.quartz.properties" ),false);

        addFile(WS_DOCER,getDocerConfig(WS_DOCER,"configuration.xml"),true);
        addFile(WS_FASCICOLAZIONE,getDocerConfig(WS_FASCICOLAZIONE,"configuration.xml"),true);
        addFile(WS_PROTOCOLLAZIONE,getDocerConfig(WS_PROTOCOLLAZIONE,"configuration.xml"),true);
        addFile(WS_REGISTRAZIONE,getDocerConfig(WS_REGISTRAZIONE,"configuration.xml"),true);
        addFile(WS_PEC,getDocerConfig(WS_PEC,"configuration.xml"),true);
        addFile(WS_FIRMA,getDocerConfig(WS_FIRMA,"configuration.xml"),true);
        addFile(WS_SYSTEM,getDocerConfig(WS_SYSTEM,"authproviders.xml"),true);

        addFile(DOCER_PROP_DATABASE,getDocerConfig("docersystem","database.properties"),false);
        addFile(DOCER_PROP_JMS,getDocerConfig("docersystem","jms.properties"),false);
        addFile(DOCER_PROP_FIRMA,getDocerConfig("WSFirma","configuration.properties"),false);
        addFile(DOCER_PROP_FIRMA_ARUBA,getDocerConfig("WSFirma","provider-aruba-config.properties"),false);
        addFile(DOCER_PROP_SOLR,getDocerConfig("WSDocer","solr.properties"),false);
        addFile(DOCER_PROP_PROTO,getDocerConfig("WSProtocollazione","provider-protocollazione-kdm.properties"),false);

        return files.get(KS_CONFIG).exists();
    }

    private void _openConfigFiles() throws IOException {

        if (opened)
            return;

        //File configFile = files.get(KS_CONFIG);
        //File aooConfigFile = files.get(KS_CONFIG_AOO);

        //File configSchema = addFile(KS_CONFIG_SCHEMA,getConfig("configSchema.json"),false);

        //configFile.delete();
        //configSchema.delete();

        //boolean result = false;

        if (configFile.exists()){
            String configString = FileUtils.readFileToString(configFile);
            main_json = Helper.jsonToHashMap(configString);
            //result = true;
        } else {
            main_json = new LinkedHashMap<>();
        }

        if (aooConfigFile.exists()){
            String configString = FileUtils.readFileToString(aooConfigFile);
            aoo_json = Helper.jsonToHashMap(configString);
        } else {
            aoo_json = new LinkedHashMap<>();

            //File hd = new File(Utils.getConfigHome(),String.format("custom-apps/%s/%s/header.json",codEnte,codAoo) );
            //File sm = new File(Utils.getConfigHome(),String.format("custom-apps/%s/%s/side-menu.json",codEnte,codAoo) );

            InputStream is = this.getClass().getResourceAsStream("/custom-apps/default.json");

            if (is!=null) {
                String fileString = StreamUtils.copyToString(is, Charset.defaultCharset());
                Map defaultMap = Helper.jsonToHashMap(fileString);
                aoo_json.put("apps", defaultMap);
            }

            /*List<Map<String,Object>> menuList = mapper.readValue(FileUtils.readFileToString(sm), new TypeReference<List<Map<String,Object>>>() { });

            List<Map<String,Object>> items = (List) headerMap.get("menuItems");
            Map<String,Object> apps = new LinkedHashMap<>();

            for( Map<String,Object> item : items ){
                String appName = (String) item.remove("appName");
                apps.put( appName, item );

                for( Map<String,Object> menuItem : menuList  ){
                    if (appName.equals(menuItem.get("appName"))){
                        item.put("tooltip", menuItem.get("tooltip") );
                        item.put("useJtree", menuItem.get("useJtree") );
                        item.put("sections",menuItem.get("sections"));
                    }
                }
            }*/


        }

        entries = new LinkedHashMap<>();

        Map<String,Object> map = getSchema();

        for ( String tabKey : map.keySet() ){
            Map<String,Map<String,Map>> tab = (Map<String,Map<String,Map>>) map.get(tabKey);

            for( Object v : tab.values() ) {
                if (v instanceof Map){
                    Map<String,Map> section = (Map) v;

                    for( Object v2 : section.values() ) {

                        if (v2 instanceof Map){
                            Map item = (Map) v2;
                            Entry e = new Entry(item);
                            if (Strings.isNotEmpty(e.getId()))
                                entries.put(e.getId(), e);
                        }

                    }
                }
            }
        }

        /*if (configSchema.exists()){
            String configString = FileUtils.readFileToString(configSchema);
            //List<Map> list = mapper.readValue(configString, new TypeReference<List<Map>>() { });
            Map<String,Map<String,Map<String,Map>>> map = mapper.readValue(configString, new TypeReference<Map<String,Map<String,Map>>>() { });
            for( Map<String,Map<String,Map>> tab : map.values() ){
                for( Map<String,Map> section : tab.values() ) {
                    for( Map item : section.values() ) {
                        Entry e = new Entry(item);
                        entries.put(e.getId(), e);
                    }
                }
            }
        }*/



        //_import("system",getConfig("system.properties"));
        //_import("WSDocer",getDocerConfig("WSDocer","configuration.xml"));

        //return result;

        opened = true;
    }

    private void onPropertyEntryRead(Entry entry,Object value){

        if (value instanceof Collection)
            value = (String) ((Collection) value).iterator().next();
        else if (value!=null)
            value = value.toString();

        if (entry.getPropertyName().equals("GROUP_ROLES") || entry.getPropertyName().equals("sicurezza.ruoli.globali")){
            List<String> l = Arrays.asList(value.toString().split(","));
            value = new LinkedHashMap<>();
            for( String role : l ){
                Matcher m = roleRe.matcher(role);
                if (m.find()){
                    ((Map)value).put(m.group(2),m.group(1));
                } else {
                    ((Map)value).put(role,role);
                }
            }
            entry.setTemplate(Collections.singletonMap("role","desc"));

        } else if (entry.isMultiValue()){
            if (value!=null)
                value = Arrays.asList(value.toString().split(","));
            else
                value = new ArrayList<>();
        }

        Map<String,Object> section = ensureSection(entry);
        if (!section.containsKey(entry.getName()))
            section.put(entry.getName(),value);
    }

    private Map<String, Object> getXmlSection(String fileId, String groupName, String sectionName) {
        Map<String, Object> xml = openXml(fileId);
        Map<String, Object> configuration = (Map) xml.get("configuration");
        Map<String,Object> group = getXmlChild(configuration,"group",tuple("name",groupName));
        return getXmlChild(group,"section",tuple("name",sectionName));
    }

    private void addXmlChild(Map<String,Object> parent,String nodeName,Object value ){
        Object children = parent.get(nodeName);
        if (children instanceof List){
            ((List)children).add(value);
        } else if (children!=null) {
            List newC = new ArrayList();
            newC.add(children);
            newC.add(value);
            parent.put(nodeName,newC);
        } else {
            parent.put(nodeName,value);
        }
    }

    private Map<String,Object> getXmlChild(Map<String,Object> parent, String nodeName, tuple... tuples){

        if (parent==null)
            return null;

        for( String key : parent.keySet() ){
            List<Map<String,Object>> children = null;
            List<Map<String,Object>> childrenT;

            Object node = parent.get(key);

            if ( node instanceof List){
                childrenT = (List) node;
            } else if (node instanceof Map) {
                childrenT = new ArrayList(Collections.singleton(node));
            } else {
                continue;
            }

            if (key.equals(nodeName)){
                children = childrenT;
            } else {
                children = new ArrayList<>();
                for( Map<String,Object> item : childrenT ){
                    item = (Map) item.get("#item");
                    if (item!=null){
                        Object x  = item.get(nodeName);
                        if (x instanceof List)
                            children.addAll((List)x);
                        else if (x instanceof Map)
                            children.add( (Map) x);
                    }
                }
            }

            if (children!=null){
                for(Map<String,Object> child : children ){

                    boolean flag = true;
                    for( int i=0; i<tuples.length; i++){
                        String att = (tuples[i].first.toString().startsWith("-") || tuples[i].first.toString().startsWith("#")) ? tuples[i].first.toString() : "-"+tuples[i].first;
                        String v = tuples[i].second.toString();

                        if (!v.equalsIgnoreCase( (String) child.get(att))){
                            flag = false;
                            break;
                        }
                    }
                    if (flag)
                        return child;
                }
            }
        }

        /*Object node = parent.get(nodeName);

        if (node==null)
            return null;

        List<Map<String,Object>> children;
        if ( !(node instanceof List)){
            children = new ArrayList(Collections.singleton(node));
        } else {
            children = (List) node;
        }*/


        return null;
    }

    private void importServizi(){

        Map<String,Object> section = ensureSection("servizi","protocollo");

        /* PROTOCOLLAZIONE */

        Map<String,Object> prot_xSection = getXmlSection(WS_PROTOCOLLAZIONE,"Configurazione","Providers");
        Map<String,Object> prot_xprovider = getXmlChild(prot_xSection,"provider",tuple("aoo",codAoo));

        if (prot_xprovider==null){
            logError("provider protocollazione non configurato per aoo %s",codAoo);
        } else {
            String cls = (String) prot_xprovider.get("#text");
            section.put("protocollazione",cls);
        }

        /* FASCICOLAZIONE */

        Map<String,Object> fasc_xsection = getXmlSection(WS_FASCICOLAZIONE,"Configurazione","Providers");
        Map<String,Object> fasc_xprovider = getXmlChild(fasc_xsection,"provider",tuple("aoo",codAoo));

        if (fasc_xprovider==null){
            logError("provider fascicolazione non configurato per aoo %s",codAoo);
        } else {
            String cls = (String)fasc_xprovider.get("#text");
            section.put("fascicolazione",cls);
        }

        /* FIRMA */

        Map<String,Object> firma_xsection = getXmlSection(WS_FIRMA,"Configurazione","Providers");
        Map<String,Object> firma_xprovider = getXmlChild(firma_xsection,"provider",tuple("aoo",codAoo));

        if (firma_xprovider==null){
            logError("provider firma non configurato per aoo %s",codAoo);
        } else {
            String cls = (String) firma_xprovider.get("#text");
            section.put("firma",cls);
        }

        Map<String,Object> pec_xsection = getXmlSection(WS_PEC,"Configurazione","Providers");
        Map<String,Object> pec_xprovider = getXmlChild(pec_xsection,"provider",tuple("aoo",codAoo));

        /* PEC */

        if (pec_xprovider==null){
            logError("provider firma non configurato per aoo %s",codAoo);
        } else {
            String cls = (String) pec_xprovider.get("#text");
            section.put("pec",cls);
        }

        /* TIMBRO */

        Map<String,Object> timbro_params = new LinkedHashMap<>();
        section.put("timbro",timbro_params);
        Map<String,Object> timbro_xsection = getXmlSection(WS_DOCER,"timbro","segnatura");

        if (timbro_xsection!=null){
            Map<String,Object> timbro_xprovider = getXmlChild(timbro_xsection,"provider",tuple("aoo",codAoo));

            if (timbro_xprovider==null){
                logError("provider timbro non configurato per aoo %s",codAoo);
            } else {
                String templ = (String) timbro_xprovider.getOrDefault("#text","template.xsl");
                timbro_params.put("template",templ);
            }

            Map <String,Object> parFromDocer = getXmlSection(WS_DOCER,"timbro","parametritimbro");
            timbro_params.put("format",parFromDocer.getOrDefault("format","jpg"));
            timbro_params.put("dpi",parFromDocer.getOrDefault("dpi",150));
            timbro_params.put("maxw",parFromDocer.getOrDefault("maxw",200));
            timbro_params.put("maxh",parFromDocer.getOrDefault("maxh",90));
            timbro_params.put("pag",parFromDocer.getOrDefault("pag",1));
            timbro_params.put("x",parFromDocer.getOrDefault("x",50));
            timbro_params.put("y",parFromDocer.getOrDefault("y",50));

        } else {
            logError("sezione timbro non presente");
        }

        /* REGISTRAZIONE */

        Map <String,Object> reg_xsection = getXmlSection(WS_REGISTRAZIONE,"Configurazione","Providers");
        Map<String,Object> registri =  ensureSection("servizi","registri");
        Map<String,Object> tipologie = ensureSection("documentale","tipologie");

        List<Map<String,Object>> l = (List<Map<String,Object>>) reg_xsection.get("provider");
        if (l!=null){
            for( Map<String,Object> xdoc : (List<Map<String,Object>>) reg_xsection.get("provider") ){

                if (codAoo.equals(xdoc.get("-aoo")))
                    registri.put( (String) xdoc.get("-registro"), new LinkedHashMap<>());
                //registriList.add( (String) xdoc.get("-name"));
            }
        }

        String defRegs = (String) findOrCreateEntry(KS_SYSTEM_AOO,"registri").getDefaultValue();

        List<String> registriList = new ArrayList<>();
        if (Strings.isNotEmpty(defRegs)){
            for ( String REG_ID : defRegs.split(",") ){
                if (!registri.containsKey(REG_ID)){
                    logError("registro %s non configurato",REG_ID);
                }else{
                    registriList.add(REG_ID);
                }
            }
        }

        for( String registro : registri.keySet() ){

            Map<String,Object> xprovider = getXmlChild(reg_xsection,"provider",tuple("aoo",codAoo),tuple("registro",registro));

            Map<String,Object> config = (Map) registri.getOrDefault(registro, new LinkedHashMap<>());
            registri.put(registro,config);

            String cls = (String) xprovider.get("#text");
            config.put("provider",cls);
            config.put("descrizione",registro);

            if (tipologie.keySet().contains(registro)){
                Map<String,Object> tip = (Map) tipologie.get(registro);
                if (!tip.containsKey("registro")){
                    tip.put("registro",registro);
                }
            }

            if (registriList.contains(registro)){
                List<String> acl_create = new ArrayList<>();
                acl_create.add(registro+"_REGISTRAZ");
                config.put("acl_create",acl_create);

                Entry entry = findOrCreateEntry(KS_SYSTEM_AOO,"SolrEffectiveRights.rule.registra-"+registro);

                String groups = String.format("@(%s)", Strings.join(acl_create,'|')).toUpperCase();
                entry.setProperty(groups);
            }

            //TODO verifica esistenza gruppo su solr
            //List<String> acl_read = (List<String>) config.get("acl_read");
            //List<String> acl_edit = (List<String>) config.get("acl_edit");
            //List<String> acl_full = (List<String>) config.get("acl_full");
        }
    }

    private void exportServizi(){

        Map<String,Object> section = ensureSection("servizi","protocollo");

        /* PROTOCOLLAZIONE */

        Map<String,Object> prot_xSection = getXmlSection(WS_PROTOCOLLAZIONE,"Configurazione","Providers");
        Map<String,Object> prot_xprovider = getXmlChild(prot_xSection,"provider",tuple("aoo",codAoo));



        if (prot_xprovider==null){
            prot_xprovider = new LinkedHashMap<>();
            prot_xprovider.put("-ente",codEnte);
            prot_xprovider.put("-aoo",codAoo);
            addXmlChild(prot_xSection,"provider",prot_xprovider);
        }

        String cls = (String) section.getOrDefault("protocollazione","it.kdm.docer.protocollazione.Provider");
        prot_xprovider.put("#text",cls);

        /* FASCICOLAZIONE */

        Map<String,Object> fasc_xsection = getXmlSection(WS_FASCICOLAZIONE,"Configurazione","Providers");
        Map<String,Object> fasc_xprovider = getXmlChild(fasc_xsection,"provider",tuple("aoo",codAoo));



        if (fasc_xprovider==null){
            fasc_xprovider = new LinkedHashMap<>();
            fasc_xprovider.put("-ente",codEnte);
            fasc_xprovider.put("-aoo",codAoo);
            addXmlChild(fasc_xsection,"provider",fasc_xprovider);
        }

        cls = (String) section.getOrDefault("fascicolazione","it.kdm.docer.fascicolazione.Provider");
        fasc_xprovider.put("#text",cls);
        fasc_xprovider.put("fascicolazioneInterna","true");

        /* FIRMA */

        Map<String,Object> firma_xsection = getXmlSection(WS_FIRMA,"Configurazione","Providers");
        Map<String,Object> firma_xprovider = getXmlChild(firma_xsection,"provider",tuple("aoo",codAoo));



        if (firma_xprovider==null){
            firma_xprovider = new LinkedHashMap<>();
            firma_xprovider.put("-ente",codEnte);
            firma_xprovider.put("-aoo",codAoo);
            addXmlChild(firma_xsection,"provider",firma_xprovider);
        }

        cls = (String) section.getOrDefault("firma","it.kdm.docer.firma.Provider");
        firma_xprovider.put("#text",cls);

        Map<String,Object> pec_xsection = getXmlSection(WS_PEC,"Configurazione","Providers");
        Map<String,Object> pec_xprovider = getXmlChild(pec_xsection,"provider",tuple("aoo",codAoo));

        /* PEC */



        if (pec_xprovider==null){
            pec_xprovider = new LinkedHashMap<>();
            pec_xprovider.put("-ente",codEnte);
            pec_xprovider.put("-aoo",codAoo);
            addXmlChild(pec_xsection,"provider",pec_xprovider);
        }

        cls = (String) section.getOrDefault("pec","it.kdm.docer.PEC.Provider");
        pec_xprovider.put("#text",cls);

        /* TIMBRO */

        Map<String,Object> timbro_params = (Map) section.getOrDefault("timbro",new LinkedHashMap<>());
        Map<String,Object> timbro_xsection = getXmlSection(WS_DOCER,"timbro","segnatura");

        if (timbro_xsection!=null){
            Map<String,Object> timbro_xprovider = getXmlChild(timbro_xsection,"provider",tuple("aoo",codAoo));

            timbro_params.put("format", timbro_params.getOrDefault("format","jpg"));
            timbro_params.put("dpi", timbro_params.getOrDefault("dpi",150));
            timbro_params.put("maxw", timbro_params.getOrDefault("maxw",200));
            timbro_params.put("maxh", timbro_params.getOrDefault("maxh",90));
            timbro_params.put("pag", timbro_params.getOrDefault("pag",1));
            timbro_params.put("x", timbro_params.getOrDefault("x",50));
            timbro_params.put("y", timbro_params.getOrDefault("y",50));

            String templ = (String) timbro_params.getOrDefault("template","template.xsl");

            if (timbro_xprovider==null){
                timbro_xprovider = new LinkedHashMap<>();
                timbro_xprovider.put("-ente",codEnte);
                timbro_xprovider.put("-aoo",codAoo);
                timbro_xprovider.put("-tipologia","DOCUMENTO");
                addXmlChild(timbro_xsection,"provider",timbro_xprovider);
            }

            timbro_xprovider.put("#text",templ);

            Map<String,Object> parametri = getXmlSection(WS_DOCER,"timbro","parametritimbro");

            if (parametri==null){
                parametri = new LinkedHashMap<>();
                addXmlChild(timbro_xsection,"provider",parametri);
            }
            parametri.put("format",timbro_params.get("format"));
            parametri.put("dpi",timbro_params.get("dpi"));
            parametri.put("maxw",timbro_params.get("maxw"));
            parametri.put("maxh",timbro_params.get("maxh"));
            parametri.put("pag",timbro_params.get("pag"));
            parametri.put("x",timbro_params.get("x"));
            parametri.put("y",timbro_params.get("y"));

        } else {
            logError("sezione timbro non presente");
        }

        /* REGISTRAZIONE */

        Map <String,Object> reg_xsection = getXmlSection(WS_REGISTRAZIONE,"Configurazione","Providers");
        Map<String,Object> registri =  ensureSection("servizi","registri");
        Map<String,Object> tipologie = ensureSection("documentale","tipologie");



        List<String> registriList = new ArrayList<>();
        //Map<String,List<String>> tip_regs = new LinkedHashMap<>();

        for( String t : tipologie.keySet()){
            Map<String,Object> tip = (Map) tipologie.get(t);

            if (registri.keySet().contains(t) && !tip.containsKey("registro"))
                tip.put("registro",t);

            if (tip.containsKey("registro")){
                String REG_ID = (String) tip.get("registro");
                if (REG_ID!=null){
                    registriList.add(REG_ID);
                    if (!registri.containsKey(REG_ID)){
                        registri.put(REG_ID,new LinkedHashMap<>());
                    }

                    Entry entry = findOrCreateEntry(KS_SYSTEM_AOO,"default.registro."+t);
                    entry.setIgnore(true);
                    entry.setProperty(REG_ID);
                }
            }
        }

        for( String registro : registri.keySet() ){
            //registro = registro.toUpperCase();
            Map<String,Object> xprovider = getXmlChild(reg_xsection,"provider",tuple("aoo",codAoo),tuple("registro",registro));
            if (xprovider==null){
                xprovider = new LinkedHashMap<>();
                xprovider.put("-ente",codEnte);
                xprovider.put("-aoo",codAoo);
                xprovider.put("-registro",registro);
                addXmlChild(reg_xsection,"provider",xprovider);
            }
            Map<String,Object> config = (Map) registri.getOrDefault(registro, new LinkedHashMap<>());

            cls = (String) config.getOrDefault("provider","it.kdm.docer.registrazione.Provider");

            xprovider.put("#text",cls);

            List<String> acl_create = (List<String>) config.get("acl_create");

            if (acl_create!=null && acl_create.size()>0){
                registriList.add(registro);

                Entry entry = findOrCreateEntry(KS_SYSTEM_AOO,"SolrEffectiveRights.rule.registra-"+registro);

                String groups = String.format("@(%s)", Strings.join(acl_create,'|')).toUpperCase();
                entry.setProperty(groups);
            }

            //TODO verifica esistenza gruppo su solr
            //List<String> acl_read = (List<String>) config.get("acl_read");
            //List<String> acl_edit = (List<String>) config.get("acl_edit");
            //List<String> acl_full = (List<String>) config.get("acl_full");
        }

        Entry entry = findOrCreateEntry(KS_SYSTEM_AOO,"registri");
        String sReg = StringUtils.join(registriList,",").toUpperCase();
        entry.setProperty(sReg);

    }

    private void importTipologie(){

        Map<String,Object> section = ensureSection("documentale","tipologie");

        Map<String,Object> xanagrafiche = getXmlSection(WS_DOCER,"form_dinamiche","anagrafiche");
        xanagrafiche = getXmlChild(xanagrafiche,"ente",tuple("id",codEnte));

        if (xanagrafiche==null){
            logError("ente %s non trovato nella sezione anagrafiche",codEnte);
            return;
        }

        Map<String,Object> xtipologie = getXmlSection(WS_DOCER,"form_dinamiche","documenti");
        Map<String,Object> ente = getXmlChild(xtipologie,"ente",tuple("id",codEnte));
        if (ente==null){
            logError("ente %s non trovato nella sezione documenti",codEnte);
            return;
        }
        xtipologie = ente;

        Map<String,Object> aoo = getXmlChild(xtipologie,"aoo",tuple("id", codAoo));

        if (aoo==null){
            logError("aoo %s non trovato nella sezione documenti",codAoo);
            return;
        }
        xtipologie = aoo;

        if (xtipologie!=null){

            List<Map<String,Object>> l = (List<Map<String,Object>>) xtipologie.get("documento");

            if (l!=null){
                for( Map<String,Object> xdoc : l ){
                    String TYPE_ID = (String) xdoc.get("-name");
                    Map m = new LinkedHashMap<>();
                    m.put("acl_create",Collections.singletonList("everyone"));
                    section.put( TYPE_ID, m);
                }
            }

        } else {
            logWarn("non ci sono tipologie definite");
            return;
        }

        Collection<String> optionals = new HashSet<>();

        String docs = (String) openProperties(KS_SYSTEM_AOO).getProperty("optionalDocTypes");
        String atts = (String) openProperties(KS_SYSTEM_AOO).getProperty("optionalAttTypes");

        if (Strings.isNotEmpty(docs)){
            optionals.addAll( Arrays.asList(docs.split(",")));
        }
        if (Strings.isNotEmpty(atts)){
            optionals.addAll( Arrays.asList(atts.split(",")));
        }

        for( String tip : optionals){
            if (!section.containsKey(tip))
                logWarn("la tipologia %s non è definita",tip);
        }

        Map xfields = getXmlSection(WS_DOCER,"impianto","fields");
        Map<String,Object> document_types = getXmlSection(WS_DOCER,"impianto","document_types");

        Set<String> allTypes = new HashSet();
        allTypes.addAll(simpleTypes);
        for ( String key : xanagrafiche.keySet() ){
            if (!key.startsWith("-") && !key.startsWith("#")){
                allTypes.add(key);
            }
        }

        for( String TYPE_ID : section.keySet() ) {
            //if (BASEPROFILE.equals(TYPE_ID))
            //    continue;

            Map<String, Object> tipologia = (Map) section.get(TYPE_ID);

            //String descrizione = (String) tipologia.getOrDefault("descrizione", TYPE_ID);
            //tipologia.put("descrizione", descrizione);


            /*List<String> related = (List<String>) tipologia.getOrDefault("related", new ArrayList<>());
            List<String> acl_read = (List<String>) tipologia.get("acl_read");
            List<String> acl_edit = (List<String>) tipologia.get("acl_edit");
            List<String> acl_full = (List<String>) tipologia.get("acl_full");
            List<String> acl_create = (List<String>) tipologia.get("acl_create");*/

            Map<String, Object> xtipologia = getXmlChild(xtipologie, "documento", tuple("name", TYPE_ID));

            String desc = (String) xtipologia.get("-description");
            if (Strings.isEmpty(desc))
                tipologia.put("descrizione",TYPE_ID);
            else
                tipologia.put("descrizione",desc);

            tipologia.put("acl_create",Collections.singletonList("everyone"));

            Map<String, Object> fields = new LinkedHashMap<>();
            tipologia.put("fields",fields);

            Map<String,Object> impianto = getXmlChild(document_types,"type",tuple("name",TYPE_ID));

            Set<String> keys;
            if (xtipologia!=null)
                keys = xtipologia.keySet();
            else if (impianto!=null)
                keys = impianto.keySet();
            else {
                logError("la tipologia %s non è definita", TYPE_ID);
                continue;
            }

            for( String field : keys ){
                if (!field.startsWith("-") && !field.startsWith("#")){
                    Map f = new LinkedHashMap();
                    fields.put(field.toString(),f);
                }
            }

            String defComps = (String) xtipologia.getOrDefault("-componenti","PRINCIPALE");
            List<String> componenti = Arrays.asList(defComps.split(","));

            tipologia.put("componenti", componenti);

            for (String fieldName : fields.keySet()) {
                Map<String, Object> field = (Map) fields.get(fieldName);
                Map<String, Object> baseField;

                if (xfields.get(fieldName) instanceof List){
                    baseField = (Map) ((List)xfields.get(fieldName)).get(0);
                    logWarn("field %s configurato più volte",fieldName);
                } else {
                    baseField = (Map) xfields.get(fieldName);
                }

                if (baseField != null) {
                    String base_type = ((String) baseField.getOrDefault("-type", "xs:string")).substring(3);
                    String base_multivalue = (String) baseField.getOrDefault("-multivalue", "false");
                    String base_displayName = (String) baseField.getOrDefault("-displayName", fieldName);
                    String base_format = (String) baseField.getOrDefault("-format", ".*");
                    String base_custom = (String) baseField.getOrDefault("-custom", "false");

                    if (!allTypes.contains(base_type)) {
                        logWarn("tipo % non valido",base_type);
                    }

                    if (!base_custom.equals("true")){
                        logWarn(TYPE_ID+":definizione non consistente di " + fieldName+ "(custom)");
                    }

                    field.put("type", base_type);
                    field.put("displayName", base_displayName);
                    field.put("format", base_format);
                    field.put("multivalue", base_multivalue);

                } else {
                    logError("campo non esistente:%s",fieldName);
                }
            } //fine ciclo campi
        }//fine ciclo tipologie
    }

    private void importAnagrafiche(){

        Map<String,Object> section = ensureSection("documentale","anagrafiche");

        /*Map<String,Object> xtipologie = getXmlSection(WS_DOCER,"form_dinamiche","anagrafiche");
        Map<String,Object> ente = getXmlChild(xtipologie,"ente",tuple("id",codEnte));
        if (ente==null){
            logError("ente %s non trovato in anagrafiche",codEnte);
            return;
        }
        xtipologie = ente;*/

        Map<String,Object> xanagrafiche = getXmlSection(WS_DOCER,"form_dinamiche","anagrafiche");
        xanagrafiche = getXmlChild(xanagrafiche,"ente",tuple("id",codEnte));

        if (xanagrafiche==null){
            logError("ente %s non trovato in anagrafiche",codEnte);
            return;
        }

        Collection<String> optionals = new HashSet<>();

        String anags = (String) openProperties(KS_SYSTEM_AOO).getProperty("optionalAnagTypes");

        if (Strings.isNotEmpty(anags)){
            optionals.addAll( Arrays.asList(anags.split(",")));
        }

        Set<String> allTypes = new HashSet();
        allTypes.addAll(simpleTypes);
        for ( String key : xanagrafiche.keySet() ){
            if (!key.startsWith("-") && !key.startsWith("#")){
                allTypes.add(key);
            }
        }

        for( String TYPE_ID : optionals ) {

            Map<String, Object> tipologia = new LinkedHashMap();
            tipologia.put("acl_create",Collections.singletonList("everyone"));
            section.put(TYPE_ID,tipologia);

            Map<String, Object> xanagrafica = (Map) xanagrafiche.get(TYPE_ID);

            String desc = (String) xanagrafica.get("-description");
            if (Strings.isEmpty(desc))
                tipologia.put("descrizione",TYPE_ID);
            else
                tipologia.put("descrizione",desc);

            xanagrafica = getXmlChild(xanagrafica, "aoo", tuple("id", codAoo));

            if (xanagrafica == null) {
                logError("la anagrafica %s non è definita per l'aoo %s",TYPE_ID,codAoo);
                continue;
            }

            Map<String, Object> fields = new LinkedHashMap<>();
            tipologia.put("fields",fields);

            Map<String,Object> anagrafica_types = getXmlSection(WS_DOCER,"impianto","anagrafica_types");
            Map<String,Object> impianto = getXmlChild(anagrafica_types,"type",tuple("name",TYPE_ID));

            Set<String> keys;
            if (xanagrafica!=null)
                keys = xanagrafica.keySet();
            else if (impianto!=null)
                keys = impianto.keySet();
            else {
                logError("la anagrafica %s non è definita", TYPE_ID);
                continue;
            }

            for( String field : keys ){
                if (!field.startsWith("-") && !field.startsWith("#")){
                    Map f = new LinkedHashMap();
                    fields.put(field.toString(),f);
                }
            }



            Map xfields = getXmlSection(WS_DOCER,"impianto","fields");

            for (String fieldName : fields.keySet()) {
                Map<String, Object> field = (Map) fields.get(fieldName);
                Map<String, Object> baseField;

                if (xfields.get(fieldName) instanceof List){
                    baseField = (Map) ((List)xfields.get(fieldName)).get(0);
                    logWarn("field %s configurato più volte",fieldName);
                } else {
                    baseField = (Map) xfields.get(fieldName);
                }

                if (baseField != null) {
                    String base_type = ((String) baseField.getOrDefault("-type", "xs:string")).substring(3);
                    String base_multivalue = (String) baseField.getOrDefault("-multivalue", "false");
                    String base_displayName = (String) baseField.getOrDefault("-displayName", fieldName);
                    String base_format = (String) baseField.getOrDefault("-format", ".*");

                    if (!allTypes.contains(base_type)) {
                        logWarn("tipo %s non valido",base_type);
                    }

                    field.put("type", base_type);
                    field.put("displayName", base_displayName);
                    field.put("format", base_format);
                    field.put("multivalue", base_multivalue);

                } else {
                    logError("campo non esistente:%s",fieldName);
                }
            } //fine ciclo campi

        }//fine ciclo tipologie
    }

    private void exportTipologie(){

        Map<String,Object> section = ensureSection("documentale","tipologie");

        //from new config file


        Map<String,Object> xtipologie = getXmlSection(WS_DOCER,"form_dinamiche","documenti");
        Map<String,Object> ente = getXmlChild(xtipologie,"ente",tuple("id",codEnte));
        if (ente==null){
            ente = new LinkedHashMap<>();
            ente.put("-id",codEnte);
            xtipologie.put("ente",ente);
        }
        xtipologie = ente;

        Map<String,Object> aoo = getXmlChild(xtipologie,"aoo",tuple("id", codAoo));

        if (aoo==null){
            aoo = new LinkedHashMap<>();
            aoo.put("-id",codAoo);
            xtipologie.put("aoo",aoo);
        }
        xtipologie = aoo;

        List<String> optionalDocTypes = new ArrayList<>();
        List<String> optionalAttTypes = new ArrayList<>();

        Set<String> allTypes = new HashSet(ensureSection("documentale","anagrafiche").keySet());
        allTypes.addAll(simpleTypes);

        Map<String, Object> xfields = getXmlSection(WS_DOCER, "impianto", "fields");
        Map document_types = getXmlSection(WS_DOCER,"impianto","document_types");

        for( String TYPE_ID : section.keySet() ) {

            Map<String, Object> tipologia = (Map) section.get(TYPE_ID);
            Map<String, Object> xtipologia = getXmlChild(xtipologie, "documento", tuple("name", TYPE_ID));

            Map impianto = getXmlChild(document_types,"type",tuple("name",TYPE_ID));

            if (xtipologia == null) {

                xtipologia = new LinkedHashMap<>();
                xtipologia.put("-viewBaseFieldProfile", "false");
                xtipologia.put("-providerTypeId", "documento");
                xtipologia.put("-name", TYPE_ID);
                xtipologia.put("-type", TYPE_ID);

                addXmlChild(xtipologie, "documento", xtipologia);
            }

            if (impianto == null){
                impianto = new LinkedHashMap<>();
                //impianto.put("-viewBaseFieldProfile", "false");
                impianto.put("-providerTypeId", "documento");
                impianto.put("-name", TYPE_ID);
                //impianto.put("-type", TYPE_ID);

                addXmlChild(document_types, "type", impianto);
            }


            /*if (impianto==null){
                Map xfields = getXmlSection(WS_DOCER,"impianto","fields");

                for( Object field : impianto.keySet()){
                    if (!field.toString().startsWith("-") && !field.toString().startsWith("#")){
                        Map xfield = getXmlChild(xfields, (String) field);
                        if (xfield==null){
                            throw new RuntimeException("campo non trovato:"+field);
                        }
                        xtipologia.put( (String) field,xfield);
                    }
                }
            }*/

            List<String> componenti = (List<String>) tipologia.getOrDefault("componenti", Arrays.asList("PRINCIPALE".split(",")));
            String descrizione = (String) tipologia.getOrDefault("descrizione",TYPE_ID);

            xtipologia.put("-description", descrizione);
            xtipologia.put("-componentTypes", StringUtils.join(componenti, ' '));
            xtipologia.put("-name", TYPE_ID);
            xtipologia.put("-type", TYPE_ID);

            impianto.put("-description",descrizione);

            //List<String> acl_read = (List<String>) tipologia.get("acl_read");
            //List<String> acl_edit = (List<String>) tipologia.get("acl_edit");
            //List<String> acl_full = (List<String>) tipologia.get("acl_full");
            List<String> acl_create = (List<String>) tipologia.get("acl_create");

            if (acl_create != null && acl_create.size() > 0) {
                if (componenti.contains("PRINCIPALE")) {
                    optionalDocTypes.add(TYPE_ID);
                }
                optionalAttTypes.add(TYPE_ID);
            }

            List<String> related = (List<String>) tipologia.getOrDefault("related", new ArrayList<>());

            Entry entry = findOrCreateEntry(KS_SYSTEM_AOO, "optionalAttTypes." + TYPE_ID);
            entry.setProperty(StringUtils.join(related,",").toUpperCase());

            Map<String, Object> fields = (Map) tipologia.getOrDefault("fields",new LinkedHashMap<>());

            for (String fieldName : fields.keySet()) {
                Map<String, Object> field = (Map) fields.get(fieldName);

                String type = (String) field.getOrDefault("type", "string");
                String displayName = (String) field.getOrDefault("displayName", fieldName);
                String format = (String) field.getOrDefault("format", ".*");
                String multivalue = (String) field.getOrDefault("multivalue", "false");

                if (!allTypes.contains(type)) {
                    logWarn("tipo non esistente:%s", type);
                }

                Map<String, Object> baseField;
                if (xfields.get(fieldName) instanceof List){
                    baseField = (Map) ((List)xfields.get(fieldName)).get(0);
                    logWarn("field %s configurato più volte",fieldName);
                } else {
                    baseField = (Map) xfields.get(fieldName);
                }

                if (baseField != null) {
                    String base_type = ((String) baseField.getOrDefault("-type", "xs:string"));
                    String base_multivalue = (String) baseField.getOrDefault("-multivalue", "false");
                    String base_custom = (String) baseField.getOrDefault("-custom", "false");

                    if (!base_type.equals("xs:"+type) || !(base_multivalue.equals(multivalue))) {
                        logError("definizione non consistente di " + fieldName + " in " + TYPE_ID);
                    }

                    if (!base_custom.equals("true")){
                        logError(TYPE_ID+":definizione non consistente di " + fieldName+ "(custom)");
                    }

                    if (!multivalue.equals(multivalue)){
                        logError(TYPE_ID+":definizione non consistente di " + fieldName+ "(multivalue)");
                    }

                } else {
                    baseField = new LinkedHashMap<>();
                    baseField.put("-multivalue", multivalue);
                    baseField.put("-type", "xs:" + type);
                    baseField.put("-custom", "true");
                    baseField.put("-self-closing", "true");

                    xfields.put(fieldName, baseField);
                }

                Map<String, Object> xfield = new LinkedHashMap<>();
                xtipologia.put(fieldName, xfield);
                xfield.put("-custom", "true");
                xfield.put("-displayName", displayName);
                xfield.put("-format", format);
                xfield.put("-multivalue", multivalue);
                xfield.put("-type", "xs:" + type);
                xfield.put("-self-closing", "true");

                xfield = new LinkedHashMap<>();
                impianto.put(fieldName, xfield);
                xfield.put("-self-closing", "true");
            } //fine ciclo campi

        }//fine ciclo tipologie

        openProperties(KS_SYSTEM_AOO).setProperty("optionalDocTypes",StringUtils.join(optionalDocTypes, ",").toUpperCase());
        openProperties(KS_SYSTEM_AOO).setProperty("optionalAttTypes",StringUtils.join(optionalAttTypes, ",").toUpperCase());
    }

    private void exportAnagrafiche(){

        Map<String,Object> section = ensureSection("documentale","anagrafiche");

        //from new config file


        Map<String,Object> xanagrafiche = getXmlSection(WS_DOCER,"form_dinamiche","anagrafiche");

        Map<String,Object> ente = getXmlChild(xanagrafiche,"ente",tuple("id",codEnte));
        if (ente==null){
            ente = new LinkedHashMap<>();
            ente.put("-id",codEnte);
            xanagrafiche.put("ente",ente);
        }
        xanagrafiche = ente;

        List<String> optionalAnagTypes = new ArrayList<>();

        Set<String> allTypes = new HashSet(ensureSection("documentale","anagrafiche").keySet());
        allTypes.addAll(simpleTypes);

        Map<String, Object> xfields = getXmlSection(WS_DOCER, "impianto", "fields");

        Map anagrafiche_types = getXmlSection(WS_DOCER,"impianto","anagrafiche_types");

        for( String TYPE_ID : section.keySet() ) {

            Map<String, Object> tipologia = (Map) section.get(TYPE_ID);

            String descrizione = (String) tipologia.getOrDefault("descrizione", TYPE_ID);

            //List<String> acl_read = (List<String>) tipologia.get("acl_read");
            //List<String> acl_edit = (List<String>) tipologia.get("acl_edit");
            //List<String> acl_full = (List<String>) tipologia.get("acl_full");
            List<String> acl_create = (List<String>) tipologia.get("acl_create");

            if (acl_create != null && acl_create.size() > 0) {
                optionalAnagTypes.add(TYPE_ID);
            }

            Map<String, Object> xanagrafica = (Map) xanagrafiche.get(TYPE_ID);

            if (xanagrafica == null) {
                xanagrafica = new LinkedHashMap<>();
                xanagrafica.put(TYPE_ID, xanagrafica);
            }

            xanagrafica.put("-description", descrizione);

            Map impianto = getXmlChild(anagrafiche_types,"type",tuple("name",TYPE_ID));
            if (impianto == null){
                impianto = new LinkedHashMap<>();
                impianto.put("-name", TYPE_ID);
                addXmlChild(anagrafiche_types, "type", impianto);
            }

            impianto.put("-displayName",descrizione);

            Map<String, Object> aoo = getXmlChild(xanagrafica, "aoo", tuple("id", codAoo));

            if (aoo == null) {
                aoo = new LinkedHashMap<>();
                aoo.put("-id", codAoo);
                xanagrafica.put("aoo",aoo);
            }
            xanagrafica = aoo;

            Map<String, Object> fields = (Map) tipologia.getOrDefault("fields",new LinkedHashMap<>());

            for (String fieldName : fields.keySet()) {
                Map<String, Object> field = (Map) fields.get(fieldName);

                String type = (String) field.getOrDefault("type", "string");
                String displayName = (String) field.getOrDefault("displayName", fieldName);
                String format = (String) field.getOrDefault("format", ".*");
                String multivalue = (String) field.getOrDefault("multivalue", "false");

                if (!allTypes.contains(type)) {
                    logWarn("tipo non esistente:%s", type);
                }

                Map<String, Object> baseField;
                if (xfields.get(fieldName) instanceof List){
                    baseField = (Map) ((List)xfields.get(fieldName)).get(0);
                    logWarn("field %s configurato più volte",fieldName);
                } else {
                    baseField = (Map) xfields.get(fieldName);
                }

                if (baseField != null) {
                    String base_type = (String) baseField.getOrDefault("-type", "xs:string");
                    String base_multivalue = (String) baseField.getOrDefault("-multivalue", "false");

                    if (!base_type.equals("xs:"+type) || !(base_multivalue.equals(multivalue))) {
                        logError("definizione non consistente di " + fieldName + " in " + TYPE_ID);
                    }

                    if (!multivalue.equals(multivalue)){
                        logError(TYPE_ID+":definizione non consistente di " + fieldName+ "(multivalue)");
                    }

                } else {
                    baseField = new LinkedHashMap<>();
                    baseField.put("-multivalue", multivalue);
                    baseField.put("-type", "xs:" + type);
                    baseField.put("-custom", "true");
                    baseField.put("-self-closing", "true");

                    xfields.put(fieldName, baseField);
                }

                Map<String, Object> xfield = new LinkedHashMap<>();
                xanagrafica.put(fieldName, xfield);
                xfield.put("-custom", "true");
                xfield.put("-displayName", displayName);
                xfield.put("-format", format);
                xfield.put("-multivalue", multivalue);
                xfield.put("-type", "xs:" + type);
                xfield.put("-self-closing", "true");

                xfield = new LinkedHashMap<>();
                impianto.put(fieldName, xfield);
                xfield.put("-self-closing", "true");
            } //fine ciclo campi
        }//fine ciclo anagrafiche

        openProperties(KS_SYSTEM_AOO).setProperty("optionalAAnagTypes",StringUtils.join(optionalAnagTypes, ",").toUpperCase());
    }

    private Entry findOrCreateEntry(String fileId,String name){
        Entry entry = entries.get(fileId+"."+name);

        if (entry==null)
            entry = newEntry(fileId,name);
        return entry;
    }

    private void onJsonEntryWrite(Entry entry) {

        if (entry.getIgnore())
            return;

        Map<String,Object> section = ensureSection(entry);

        Object value = section.getOrDefault(entry.getName(),entry.getDefaultValue());

        entry.setProperty(value);

        String pName = entry.getPropertyName();

        switch(pName){
            case "system.endPoints.docer":
                String location = (String) section.get("location");
                findOrCreateEntry(KS_SYSTEM,"sede."+location).setProperty(value);
                findOrCreateEntry(KS_SYSTEM,"documentale.docManager.server.host").setProperty(value);

                String authenticationurl = value + "/docersystem/services/AuthenticationService/login?username={0}&password={1}&codiceEnte={2}";
                findOrCreateEntry(DOCER_PROP_PROTO,"authenticationurl").setProperty(authenticationurl);
                break;

            case "system.endPoints.location":
                findOrCreateEntry(KS_SYSTEM,"sede").setProperty(value);
                findOrCreateEntry(DOCER_PROP_SOLR,"location").setProperty(value);
                break;

            case "system.endPoints.zookeeper":
                findOrCreateEntry(KS_SYSTEM,"zookeeper."+section.get("location")).setProperty(value);
                findOrCreateEntry(DOCER_PROP_SOLR,"ZkHost").setProperty(value);
                break;

            case "system.endPoints.bpm-server":
                String bpmRest = value + "/bpm-server/process/startProcessDocer/{0}/{1}";
                findOrCreateEntry(DOCER_PROP_PROTO,"bpmnRestToInvoke").setProperty(bpmRest);
                findOrCreateEntry(KS_SYSTEM,"server.rest").setProperty(value+"/bpm-server");


                String downloadUrl = value + "/KeySuiteRestUtils/rest-utils/getFile";
                findOrCreateEntry(KS_BPM_SERVER,"url.download.file").setProperty(downloadUrl);
                findOrCreateEntry(KS_BPM_SERVER,"bpm.receive.attach.link").setProperty(downloadUrl);

                findOrCreateEntry(KS_BPM_SERVER,"restutils.host").setProperty(value);
                break;

            case "system.endPoints.frontend":
                findOrCreateEntry(KS_APP_BPM,"DOCUMENTO.redirectUrlFormat").setProperty(value+"/AppDoc/viewProfile?docNum=${DOCNUM}");
                findOrCreateEntry(KS_APP_BPM,"DOCUMENTO.DOWNLOAD.redirectUrlFormat").setProperty(value+"/AppDoc/downloadDoc?docNum=${DOCNUM}");
                findOrCreateEntry(KS_APP_BPM,"DOCUMENTO.VERSION.redirectUrlFormat").setProperty(value+"/AppDoc/downloadVersion?docNum=${DOCNUM}&version=${VERSION}");
                findOrCreateEntry(KS_APP_BPM,"FASCICOLO.redirectUrlFormat").setProperty(value+"/AppDoc/loadContentTreeView$?p=${PATH}&sid=${CLASSIFICA}|${ANNO_FASCICOLO}|${PROGR_FASCICOLO}");
                break;

            case "solr.collection":
                break;

            case "protocollazione.entrata.processId":
                findOrCreateEntry(DOCER_PROP_PROTO,"protocollazione.entrata.processName").setProperty(KDMUtils.getProcessName(value.toString()));
                break;

            case "protocollazione.interna.processId":
                findOrCreateEntry(DOCER_PROP_PROTO,"protocollazione.interna.processName").setProperty(KDMUtils.getProcessName(value.toString()));
                break;

            case "protocollazione.uscita.processId":
                findOrCreateEntry(DOCER_PROP_PROTO,"protocollazione.uscita.processName").setProperty(KDMUtils.getProcessName(value.toString()));
                break;

            case "bpm.assegnazione.processId":
                findOrCreateEntry(KS_APP_DOC,"bpm.assegnazione.processName").setProperty(KDMUtils.getProcessName(value.toString()));
                break;

            case "bpm.inoltroProto.start.processId":
                findOrCreateEntry(KS_APP_DOC,"bpm.inoltroProto.start.processName").setProperty(KDMUtils.getProcessName(value.toString()));
                break;

            case "bpm.annullaProto.start.processId":
                String processName = KDMUtils.getProcessName(value.toString());
                findOrCreateEntry(KS_APP_DOC,"bpm.annullaProto.start.processName").setProperty(processName);
                break;

            case "system.filesystem.tmp":
                findOrCreateEntry(KS_BPM_SERVER,"portale.fileDir").setProperty(value+"/portale");
                findOrCreateEntry(KS_BPM_SERVER,"html2pdf.fileDir").setProperty(value+"/html2pdf");
                findOrCreateEntry(KS_BPM_SERVER,"mail.attachDirEmailPec").setProperty(value);
                findOrCreateEntry(KS_APP_BPM,"app.fileupload.path").setProperty(value);
                findOrCreateEntry(DOCER_PROP_FIRMA,"tmp.directory.input").setProperty(value+"/firma/input");
                findOrCreateEntry(DOCER_PROP_FIRMA,"tmp.directory.output").setProperty(value+"/firma/output");
                findOrCreateEntry(DOCER_PROP_FIRMA_ARUBA,"sourceFolder").setProperty(value+"/firma/input");
                findOrCreateEntry(DOCER_PROP_FIRMA_ARUBA,"destFolder").setProperty(value+"/firma/output");
                break;

            case "mail.sender.from":
                findOrCreateEntry(KS_BPM_SERVER,"mail.config.error.USER").setProperty(value);
                break;

            case "mail.sender.from.password":
                findOrCreateEntry(KS_BPM_SERVER,"mail.config.error.PWD").setProperty(value);
                break;

            case "mail.sender.endpoint":
                findOrCreateEntry(KS_BPM_SERVER,"mail.config.error.SMTP").setProperty(value);
                break;

            case "mail.sender.protocol":
                findOrCreateEntry(KS_BPM_SERVER,"mail.config.error.PROTOCOL").setProperty(value);
                break;

            case "mail.sender.port":
                findOrCreateEntry(KS_BPM_SERVER,"mail.config.error.PORT").setProperty(value);
                break;

            case "sicurezza.authentication.secret":
                findOrCreateEntry(KS_SYSTEM,"secretKey").setProperty(value);
                findOrCreateEntry(DOCER_PROP_SOLR,"secretKey").setProperty(value);
                break;

            case "jms.broker.url":
                findOrCreateEntry(KS_BPM_SERVER,"bpm.jms.url").setProperty(value);
                break;

            case "jms.broker.user":
                findOrCreateEntry(KS_BPM_SERVER,"bpm.jms.user").setProperty(value);
                break;

            case "jms.broker.password":
                findOrCreateEntry(KS_BPM_SERVER,"bpm.jms.pwd").setProperty(value);
                break;


            case "bpm.jms.codaBPMprotocollo":
                break;

            case "db.jndi":
                String jndiWithoutPrefix = value.toString();
                if (jndiWithoutPrefix.startsWith("java:"))
                    jndiWithoutPrefix = jndiWithoutPrefix.substring(5);
                findOrCreateEntry(KS_QUARTZ,"org.quartz.dataSource.managedDS.jndiURL").setProperty(value);
                findOrCreateEntry(KS_QUARTZ,"org.quartz.dataSource.notManagedDS.jndiURL").setProperty(value);
                break;

            case"GROUP_ROLES":
                assert (value instanceof Map);
                Map rMap = (Map) value;
                List<String> roles = new ArrayList<>();
                for( Object key : rMap.keySet() ){
                    String r = key.toString();
                    String d = (String) rMap.get(r);
                    if (d!=null && !d.equals(r))
                        r = d + " (" + r + ")";
                    roles.add(r);
                }
                value = roles;

                List<String> proto = (List) section.get("protocollazione");
                List<String> protoGroupRegex = new ArrayList<>();
                List<String> ruoli_protocollatori = new ArrayList<>();

                for(String p : proto){
                    if (rMap.containsKey(p)){
                        protoGroupRegex.add("[^ ]+_"+p);
                        ruoli_protocollatori.add("${COD_UO}_"+p);
                    } else {
                        protoGroupRegex.add(p);
                        ruoli_protocollatori.add(p);
                    }
                }

                entry.setProperty(roles);
                findOrCreateEntry(entry.getFileId(),"ruoli.protocollatori").setProperty(ruoli_protocollatori);
                findOrCreateEntry(entry.getFileId(),"protoGroupRegex").setProperty(StringUtils.join(protoGroupRegex,"|"));
                break;

            case "auth.tipo.autenticazione":

                String tipo = (String) value;
                String ldapUrl = (String) section.get("ldapUrl");
                if (Strings.isEmpty(tipo)){
                    if (Strings.isEmpty(ldapUrl))
                        tipo = "standard";
                    else
                        tipo = "ldap";
                }
                section.put("tipo",tipo);

                String defaultCodAoo = (String) section.get("aoo");
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".redirect.failed").setProperty(section.get("failedUrl"));
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".redirectLogout").setProperty(section.get("logoutUrl"));
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".utente").setProperty(section.get("utente"));
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".aoo").setProperty(section.get("aoo"));
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".ente").setProperty(section.get("ente"));

                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".ente.default").setProperty(codEnte);
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".aoo.default").setProperty(defaultCodAoo);
                findOrCreateEntry(KS_SYSTEM,"auth."+tipo+".enableLogout").setProperty(section.containsKey("logoutUrl"));

                //syncAuth(false);
                break;

            case "sicurezza.ruoli.globali":

                Map<String,String> globali = (Map<String,String>) value;

                for( String role : globali.keySet() ){
                    ensureRole(role,globali.get(role));
                }

            default:
                break;
        }


        /*if (KS_SYSTEM_AOO.equals(entry.getFileId())){
            Entry e2 = findOrCreateEntry(KS_SYSTEM,entry.getPropertyName());
            Map s2 = ensureSection(e2);
            if (!s2.containsKey(entry.getName()))
                s2.put(entry.getName(),value);
        }*/

    }

    private void ensureRole(String ROLE_ID, String desc){
        /*CacheGroup g = ActorsCache.getInstance().getGroupForGroupId(ROLE_ID);

        if (g==null){

        }*/
    }

    private void importAuth(){

        Map<String,Object> section =  ensureSection("system","authentication");

        Map<String, Object> xml = openXml(WS_SYSTEM);
        Map<String, Object> node = (Map) xml.get("configuration");
        node = (Map) node.get("auth-providers");
        node = (Map) node.get("standard");


        String cls = (String) node.get("class");
        String tipo = cls.contains("LDAP") ? "ldap" : "standard";
        //String secretKey = (String) node.get("secret");
        //String epr = (String) node.get("epr");
        String ldapUrl = "";

        if (tipo.equals("ldap")){
            Object enti = node.get("configuration");
            if (enti instanceof List)
                ldapUrl = (String) ((Map)((List)enti).get(0)).get("epr");
            else
                ldapUrl = (String) ((Map)enti).get("epr");
        }

        //section.put("secret",secretKey);
        section.put("tipo",tipo);
        section.put("ldapUrl",ldapUrl);

    }

    private void exportAuth(){

        Map<String,Object> section =  ensureSection("system","authentication");

        Map<String, Object> xml = openXml(WS_SYSTEM);
        Map<String, Object> node = (Map) xml.get("configuration");
        node = (Map) node.get("auth-providers");
        node = (Map) node.get("standard");




        String tipo = (String) section.get("tipo");
        String ldapUrl = (String) section.get("ldapUrl");
        String secretKey = (String) section.get("secret");
        String epr = (String) ensureSection("system","endPoints").get("docer") + "/WSDocer/services/DocerServices";

        node.put("secret",secretKey);
        node.put("epr",epr);

        if ("ldap".equals(tipo)){
            node.put("class","it.kdm.docer.core.authentication.providers.LDAPProvider");
            if (Strings.isEmpty(ldapUrl)){
                logError("ldapUrl non configurato per autenticazione ldap");
            }
        } else {
            ldapUrl = "";
            node.put("class","it.kdm.docer.core.authentication.providers.StandardProvider");
        }

        List<Map<String,Object>> enti = new ArrayList<>();
        Map<String,Object> empty = new LinkedHashMap<>();
        empty.put("ente", new LinkedHashMap<>());
        empty.put("prefix", new LinkedHashMap<>());
        if (Strings.isNotEmpty(ldapUrl))
            empty.put("epr",ldapUrl);
        else
            empty.put("epr",new LinkedHashMap<>());
        empty.put("ldap-user-dn-format", new LinkedHashMap<>() );

        enti.add(empty);

        Map<String,Object> ente = new LinkedHashMap<>();
        ente.putAll(empty);
        empty.put("ente",codEnte);
        enti.add(ente);

        node.put("configuration",enti);

    }

    /*private List<String> split(File file) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }*/

    private List<String> split(String content) {
        /*List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new StringReader(content));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;*/
        return Arrays.asList(content.split("\r?\n"));
    }


    public static String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        return newString;
    }

    private String getInlineDiffs(String oldLine, String newLine) {

        String pre = newLine.substring(0, newLine.length() - newLine.trim().length());

        oldLine = oldLine.trim();
        newLine = newLine.trim();

        //List<String> orig = (List<String>) delta.getOriginal().getLines();
        //List<String> rev = (List<String>) delta.getRevised().getLines();
        LinkedList<String> origList = new LinkedList<String>();
        for (Character character : oldLine.toCharArray()) {
            origList.add(character.toString());
        }
        LinkedList<String> revList = new LinkedList<String>();
        for (Character character : newLine.toCharArray()) {
            revList.add(character.toString());
        }
        List<Delta<String>> inlineDeltas = DiffUtils.diff(origList, revList).getDeltas();
        if (inlineDeltas.size()> MAX_DELTAS)
            return null;
        boolean hasDelete = false;
        boolean hasInsert = false;
        //if (inlineDeltas.size() < 3) {
        Collections.reverse(inlineDeltas);
        for (Delta<String> inlineDelta : inlineDeltas) {
            Chunk<String> inlineOrig = inlineDelta.getOriginal();
            Chunk<String> inlineRev = inlineDelta.getRevised();
            if (inlineDelta.getClass().equals(DeleteDelta.class)) {
                hasDelete = true;
                origList = wrapInTag(origList, inlineOrig.getPosition(), inlineOrig
                        .getPosition()
                        + inlineOrig.size() + 1, INLINE_OLD_TAG, INLINE_OLD_CLASS);
            } else if (inlineDelta.getClass().equals(InsertDelta.class)) {
                hasInsert = true;
                revList = wrapInTag(revList, inlineRev.getPosition(), inlineRev.getPosition()
                        + inlineRev.size() + 1, INLINE_NEW_TAG, INLINE_NEW_CLASS);
            } else if (inlineDelta.getClass().equals(ChangeDelta.class)) {
                hasDelete = true;
                hasInsert = true;
                origList = wrapInTag(origList, inlineOrig.getPosition(), inlineOrig
                        .getPosition()
                        + inlineOrig.size() + 1, INLINE_OLD_TAG, INLINE_OLD_CLASS);
                revList = wrapInTag(revList, inlineRev.getPosition(), inlineRev.getPosition()
                        + inlineRev.size() + 1, INLINE_NEW_TAG, INLINE_NEW_CLASS);
            }
        }
        StringBuilder origResult = new StringBuilder(), revResult = new StringBuilder();
        for (String character : origList) {
            origResult.append(character);
        }
        for (String character : revList) {
            revResult.append(character);
        }

        if (!hasInsert)
            return pre+origResult.toString();
        else if (!hasDelete)
            return pre+revResult.toString();
        else {
            oldLine = origResult.toString();
            newLine = revResult.toString();
            StringBuilder result = new StringBuilder();

            int o = 0;
            int n = 0;
            boolean isInlineNew = false;
            boolean isInlineOld = false;

            while(true){
                assert(!(isInlineNew && isInlineOld));

                if (o==oldLine.length() && n==newLine.length())
                    break;

                char oldChar = o>=oldLine.length() ? 0 : oldLine.charAt(o);
                char newChar = n>=newLine.length() ? 0 : newLine.charAt(n);

                boolean ending = false;

                if (isInlineNew){

                    while (true){
                        if (!ending && newChar=='<' && newLine.charAt(n)=='/')
                            ending = true;
                        if (ending && newChar=='>')
                            break;
                        newChar = newLine.charAt(n++);
                        result.append(newChar);
                    }
                    isInlineNew = false;

                } else if (isInlineOld){

                    while (true){
                        if (!ending && oldChar=='<' && oldLine.charAt(o)=='/')
                            ending = true;
                        if (ending && oldChar=='>')
                            break;
                        oldChar = oldLine.charAt(o++);
                        result.append(oldChar);
                    }
                    isInlineOld = false;

                } else {
                    if (oldChar == '<'){
                        isInlineOld = true;
                        result.append(oldChar);
                        o++;
                    } else if (newChar == '<'){
                        isInlineNew = true;
                        result.append(newChar);
                        n++;
                    } else if (oldChar==newChar){
                        result.append(oldChar);
                        o++;
                        n++;
                    } else {
                        assert(false);
                    }
                }
            }

            return pre+result.toString();

        }
    }

    private String wrapLine(String line,int size, Integer idx,DiffRow.Tag tag){
        String padding;

        if (idx==null)
            padding = StringUtils.leftPad("",size);
        else if (idx==-1)
            padding = StringUtils.leftPad("+",size);
        else
            padding = StringUtils.leftPad(""+(idx+1),size);

        String pre = line.substring(0,line.length()-line.trim().length());
        line = line.trim();

        StringBuilder sb = new StringBuilder(String.format("\n%s ",padding));
        sb.append(pre);

        if (tag.equals(EQUAL) || tag.equals(CHANGE)){
            sb.append(line);
        } else if (tag.equals(DELETE)){
            sb.append("<"+INLINE_OLD_TAG);
            if (Strings.isNotEmpty(INLINE_OLD_CLASS))
                sb.append(" class=\""+INLINE_OLD_CLASS+"\"");
            sb.append(">");
            sb.append(line);
            sb.append("</"+INLINE_OLD_TAG+">");
        } else {
            sb.append("<"+INLINE_NEW_TAG);
            if (Strings.isNotEmpty(INLINE_NEW_CLASS))
                sb.append(" class=\""+INLINE_NEW_CLASS+"\"");
            sb.append(">");
            sb.append(line);
            sb.append("</"+INLINE_NEW_TAG+">");
        }
        return sb.toString();

    }

    private String makeDiff(int size , int idx, DiffRow row, String oldLine, String newLine) {

        oldLine = oldLine.replace("<", "&lt;").replace(">", "&gt;");
        newLine = newLine.replace("<", "&lt;").replace(">", "&gt;");

        if (row.getTag().equals(CHANGE)) {

            String diff = getInlineDiffs(oldLine, newLine);
            if (diff != null)
                return wrapLine(diff, size, idx, row.getTag());
            else {
                return wrapLine(oldLine, size, idx, DELETE) +
                        wrapLine(newLine, size, null, INSERT);
            }
        } else {
            return wrapLine(row.getTag().equals(DELETE) ? oldLine : newLine, size, idx, row.getTag());
        }
    }

    private boolean rowIsEmpty(DiffRow row){
        //return row.getNewLine().equals(row.getOldLine()) && Strings.isEmpty(row.getNewLine());
        return Strings.isEmpty(row.getNewLine().trim()) && Strings.isEmpty(row.getOldLine().trim());
    }

    private boolean rowIsEqual(DiffRow row){

        String oldLine = row.getOldLine().trim().replaceAll("\\s+", " ");
        String newLine = row.getNewLine().trim().replaceAll("\\s+", " ");

        return row.getTag().equals(EQUAL) || newLine.equals(oldLine);
    }

    public String checkDiff(File file ,String str) throws IOException {
        if (!file.exists())
            return "{intero file aggiunto}";
        else if (Strings.isEmpty(str))
            return "{intero file rimosso}";

        boolean isXml = false;

        String old = FileUtils.readFileToString(file);

        if (file.getName().toLowerCase().endsWith(".xml")){
            isXml = true;
            old = U.toXml( (Map) U.fromXml(old));
            str = U.toXml( (Map) U.fromXml(str));
        }

        String diff = checkDiff(isXml,old,str);

        if (Strings.isEmpty(diff))
            return null;
        else
            return  file.getAbsolutePath()+diff;
    }

    public String checkDiff(Boolean isXml,String old,String str) throws IOException {



        DiffRowGenerator generator = new DiffRowGenerator.Builder()
                .ignoreWhiteSpaces(true)
                /*.InlineNewTag(INLINE_NEW_TAG)
                .InlineNewCssClass(INLINE_NEW_CLASS)
                .InlineOldTag(INLINE_OLD_TAG)
                .InlineOldCssClass(INLINE_OLD_CLASS)
                .showInlineDiffs(true)*/
                .columnWidth(Integer.MAX_VALUE)
                .defaultString(BLANK_LINE)
                .build();

        List<String> oldLines = split(old);
        List<String> newLines = split(str);

        List<DiffRow> rows = generator.generateDiffRows(oldLines, newLines);

        String diffTxt = "";
        int last = -1;

        List<tuple> cursors = new ArrayList<>();

        int oldIdx = 0;
        int newIdx = 0;
        for (int i=0 ; i<rows.size(); i++) {
            DiffRow row = rows.get(i);

            tuple tuple = new tuple(-1,-1);

            if (!row.getOldLine().equals(BLANK_LINE)){
                tuple.first = oldIdx++;
            }

            if (!row.getNewLine().equals(BLANK_LINE)){
                tuple.second = newIdx++;
            }

            cursors.add(tuple);

        }

        for (int i=0 ; i<rows.size(); i++) {

            DiffRow row = rows.get(i);

            if (rowIsEqual(row)){
                continue;
            }

            int current = i;

            //inizio dall'elemento precedente e vado all'indietro
            i--;
            for( int equals = 0 ; equals<SURROUND_LINE && i > last ; i-- ){

                row = rows.get(i);

                if (rowIsEqual(row) && !rowIsEmpty(row))
                    equals++;

            }
            //annullo l'ultimo decremento
            i++;

            //inizio dall'elemento successivo e vado avanti
            current++;
            for ( int equals = 0 ; equals<SURROUND_LINE && current < rows.size() ; current++ ){

                row = rows.get(current);

                if (rowIsEqual(row) && !rowIsEmpty(row))
                    equals++;
            }
            //annullo l'ultimo incremento
            current--;

            //metto i puntini solo se c'è almeno una riga in mezzo
            if ( i > (last+1) )
                diffTxt += "\n...";

            for ( last = current ; i<=last ; i++){
                row = rows.get(i);

                oldIdx = (int) cursors.get(i).first;
                newIdx = (int) cursors.get(i).second;

                String oldLine = oldIdx == -1 ? BLANK_LINE : oldLines.get(oldIdx);
                String newLine = newIdx == -1 ? BLANK_LINE : newLines.get(newIdx);

                if (!rowIsEmpty(row)) {
                    String d = makeDiff(("" + rows.size()).length(), oldIdx, rows.get(i), oldLine, newLine);
                    diffTxt += d.replace(BLANK_LINE,"");
                }

            }
        }

        if (Strings.isEmpty(diffTxt))
            return null;
        else{

            if ( last < (rows.size()-1) )
                diffTxt += "\n...";
            return diffTxt;
        }

    }

    private void _writeFiles(boolean preview) throws IOException, org.apache.commons.configuration2.ex.ConfigurationException {

        if (dirty_files.size()==0)
            logInfo("nessun file è cambiato");
        /*if (dirty_files.contains(KS_CONFIG_SCHEMA)){
            File schemaFile = files.get(KS_CONFIG_SCHEMA);

            Map<String,Object> schema = new LinkedHashMap<>();

            for( Entry e : entries.values()){
                Map<String,Object> tab = (Map) schema.getOrDefault(e.getTab(),new LinkedHashMap<>());
                schema.put(e.getTab(),tab);

                Map<String,Object> section = (Map) tab.getOrDefault(e.getSection(),new LinkedHashMap<>());
                tab.put(e.getSection(),section);

                section.put(e.getName(),e);
            }

            String schemaString = mapper.writeValueAsString(schema);

            if (checkDiff(schemaFile,schemaString)){
                FileUtils.writeStringToFile(schemaFile,schemaString);
                logInfo("file '%s' modificato",schemaFile.getName());
            }
        }*/

        if (dirty_files.contains(KS_CONFIG)) {
            File configFile = files.get(KS_CONFIG);
            String jsonString = mapper.writeValueAsString(main_json);
            String txt = checkDiff(configFile,jsonString);
            if (txt!=null) {
                if (!preview)
                    FileUtils.writeStringToFile(configFile, jsonString);
                logInfo("file '%s' modificato", configFile.getName());
                logTrace(txt);
            } else {
                logInfo("file '%s' immutato", configFile.getName());
            }
        }

        if (dirty_files.contains(KS_CONFIG_AOO)) {
            File aooConfigFile = files.get(KS_CONFIG_AOO);
            String aooJsonString = mapper.writeValueAsString(aoo_json);
            String txt = checkDiff(aooConfigFile,aooJsonString);
            if (txt!=null) {
                if (!preview)
                    FileUtils.writeStringToFile(aooConfigFile, aooJsonString);
                logInfo("file '%s' modificato",aooConfigFile.getName());
                logTrace(txt);
            } else {
                logInfo("file '%s' immutato", aooConfigFile.getName());
            }

            Map<String,Map<String,Object>> apps = (Map) aoo_json.get("apps");
            if (apps != null){

                Map<String,Object> header = new LinkedHashMap<>();
                List<Map<String,Object>> sidemenu = new ArrayList<>();

                for ( String app : apps.keySet() ){
                    Map<String,Object> item = apps.get(app);
                    item.put("appName",app);
                    List<Map<String,Object>> sections = (List) item.remove("sections");

                    if (sections!=null){
                        Map<String,Object> menu = new LinkedHashMap<>();

                        menu.put("appName",item.get("appName"));
                        menu.put("title",item.get("title"));
                        //menu.put("tooltip","");
                        menu.put("url",item.get("link"));
                        menu.put("useJtree",item.remove("useJtree"));
                        menu.put("sections",sections);

                        sidemenu.add(menu);
                    }
                }

                header.put("logo",ensureSection("ui","config").getOrDefault("logo",""));
                header.put("altTitle",ensureSection("ui","config").getOrDefault("title",""));
                header.put("menuItems", apps.values());

                String jHeader = mapper.writeValueAsString(header);
                File f1 = new File(Utils.getConfigHome(),String.format("custom-apps/%s/%s/header.json",codEnte,codAoo) );
                txt = checkDiff(f1,jHeader);
                if (txt!=null) {
                    if (!preview)
                        FileUtils.writeStringToFile(f1, jHeader);
                    logInfo("file '%s' modificato",f1.getName());
                    logTrace(txt);
                } else {
                    logInfo("file '%s' immutato", f1.getName());
                }

                String jMenu = mapper.writeValueAsString(sidemenu);
                File f2 = new File(Utils.getConfigHome(),String.format("custom-apps/%s/%s/side-menu.json",codEnte,codAoo) );

                txt = checkDiff(f2,jMenu);
                if (txt!=null) {
                    if (!preview)
                        FileUtils.writeStringToFile(f2, jMenu);
                    logInfo("file '%s' modificato",f2.getName());
                    logTrace(txt);
                } else {
                    logInfo("file '%s' immutato", f2.getName());
                }
            }

        }

        for( String fileId : handlers.keySet() ){
            if (dirty_files.contains(fileId)) {
                PropertiesConfiguration config = handlers.get(fileId);
                StringWriter sw = new StringWriter();
                config.write(sw);
                String xml = sw.toString();
                String txt = checkDiff(files.get(fileId),xml);
                if (txt!=null) {
                    if (!preview)
                        FileUtils.writeStringToFile(files.get(fileId), xml);
                    logInfo("file '%s' modificato", files.get(fileId).getName());
                    logTrace(txt);
                } else {
                    logInfo("file '%s' immutato", files.get(fileId).getName());
                }
            }
        }

        for( String fileId : xmls.keySet() ){
            if (dirty_files.contains(fileId)) {
                Map<String, Object> config = xmls.get(fileId);
                String xml = U.toXml(config);
                String txt = checkDiff(files.get(fileId),xml);
                if (txt!=null) {
                    if (!preview)
                        FileUtils.writeStringToFile(files.get(fileId), xml);
                    logInfo("file '%s:%s' modificato", fileId, files.get(fileId).getName());
                    logTrace(txt);
                } else {
                    logInfo("file '%s:%s' immutato", fileId, files.get(fileId).getName());
                }
            }
        }

        for( String fileId : jsons.keySet() ){
            if (dirty_files.contains(fileId)) {
                Map<String, Object> config = jsons.get(fileId);
                String json = Helper.hashMapToJson(config,true);
                String txt = checkDiff(files.get(fileId),json);
                if (txt!=null) {
                    if (!preview)
                        FileUtils.writeStringToFile(files.get(fileId), json);
                    logInfo("file '%s:%s' modificato", fileId, files.get(fileId).getName());
                } else {
                    logInfo("file '%s:%s' immutato", fileId, files.get(fileId).getName());
                }
            }
        }
    }

    private void _import(){
    //carica la configurazione dai vecchi file

        for( String fileId : files.keySet() ){
            if (files.get(fileId).toString().endsWith(".properties"))
                loadProperties(fileId);
        }

        importTipologie();
        importAnagrafiche();
        importServizi();
        importAuth();

        dirty_files.add(KS_CONFIG);
        dirty_files.add(KS_CONFIG_AOO);
        //dirty_files.add(KS_CONFIG_SCHEMA);
    }

    private void _export()  {
    //scrive la nuova configurazione verso i vecchi file

        List<Entry> list = new ArrayList<>(entries.values());

        for( Entry entry : list ){
            onJsonEntryWrite(entry);
        }

        dirty_files.add(KS_QUARTZ);
        dirty_files.add(KS_APP_BPM);
        dirty_files.add(KS_APP_DOC);
        dirty_files.add(KS_BPM_SERVER);
        dirty_files.add(DOCER_PROP_FIRMA_ARUBA);
        dirty_files.add(DOCER_PROP_FIRMA);
        dirty_files.add(DOCER_PROP_SOLR);

        exportTipologie();
        exportAnagrafiche();
        exportServizi();
        exportAuth();

        dirty_files.add(WS_DOCER);
        dirty_files.add(WS_PROTOCOLLAZIONE);
        dirty_files.add(WS_FASCICOLAZIONE);
        dirty_files.add(WS_FIRMA);
        dirty_files.add(WS_PEC);
        dirty_files.add(WS_REGISTRAZIONE);
        dirty_files.add(WS_SYSTEM);

    }

    private Map<String,Object> ensureSection(String tabName, String sectionName){
        Map jsontab = (Map) aoo_json.get(tabName);
        if (jsontab==null){
            jsontab = (Map) main_json.get(tabName);
        }
        if (jsontab==null){
            jsontab = new LinkedHashMap<>();
            aoo_json.put(tabName,jsontab);
        }

        Map jsonsection = (Map) jsontab.get(sectionName);
        if (jsonsection==null){
            jsonsection = new LinkedHashMap<>();
            jsontab.put(sectionName,jsonsection);
        }
        return jsonsection;
    }

    private Map<String,Object> ensureSection(Entry e){

        String tabName = e.getTab();
        String sectionName = e.getSection();
        Map<String,Object> json;

        if ( !aoo_files.contains(e.getFileId()))
            json = main_json;
        else
            json = aoo_json;

        Map jsontab = (Map) json.get(tabName);
        if (jsontab==null){
            jsontab = new LinkedHashMap<>();
            json.put(tabName,jsontab);
        }

        Map jsonsection = (Map) jsontab.get(sectionName);
        if (jsonsection==null){
            jsonsection = new LinkedHashMap<>();
            jsontab.put(sectionName,jsonsection);
        }
        return jsonsection;
    }

    private PropertiesConfiguration openProperties(String fileId){

        PropertiesConfiguration config = handlers.get(fileId);

        if (config==null){
            config = new PropertiesConfiguration();

            PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout();
            layout.setForceSingleLine(true);

            try {
                layout.load(config, new FileReader(files.get(fileId)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            config.setLayout(layout);
            handlers.put(fileId,config);
        }

        return config;
    }

    private Map<String, Object> openXml(String fileId) {

        Map<String, Object> object = xmls.get(fileId);

        if (object==null){
            String xml = null;
            try {
                xml = FileUtils.readFileToString(files.get(fileId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            object = (java.util.Map<String, Object>) U.fromXml(xml);
            xmls.put(fileId,object);
        }

        return object;
    }

    private void loadProperties(String fileId){

        PropertiesConfiguration config = openProperties(fileId);
        PropertiesConfigurationLayout layout = config.getLayout();

        Iterator<String> keys = config.getKeys();

        while(keys.hasNext()){
            String key = keys.next();
            String id = fileId + "." + key;
            Object value = config.getProperty(key);

            String help = layout.getComment(key) ;

            if (help!=null && help.contains("#IMPIANTO#"))
                break;

            Entry entry = findOrCreateEntry(fileId,key);

            if (!entry.getIgnore()) {
                onPropertyEntryRead(entry, value);
            }
        }
    }

    private Entry newEntry(String fileId, String key) {

        PropertiesConfiguration config = openProperties(fileId);

        PropertiesConfigurationLayout layout = config.getLayout();

        String help = layout.getComment(key);

        Entry entry = new Entry();
        String description = key;
        String propertyName = key;

        String tabName = fileId;
        String sectionName = "default";


        String entryKey = fileId + "." + key;

        Pattern pattern = Pattern.compile("(^[^.\\-]+)[.\\-].*");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            sectionName = matcher.group(1);
            key = key.substring(sectionName.length()+1);
            description = key;
        }

        //Map jsonsection = ensureSection(tabName,sectionName);

        //Entry entry = new Entry();
        entry.setHelp(help);
        entry.setType("property");
        entry.setDescription(description);
        entry.setName(key);
        entry.setPropertyName(propertyName);
        entry.setSection(sectionName);
        entry.setTab(tabName);
        entry.setFileId(fileId);
        entry.setTemplate(".*");
        entry.setId(entryKey);



        if (help!=null && help.contains("#LIST#"))
            entry.setTemplate(new ArrayList<>());

        entry.setIgnore(true);
        if (help!=null){
            Matcher m = hintRe.matcher(help);
            if (m.find()){
                entry.setIgnore(false);
                help = help.substring(0,help.indexOf("${"));
                entry.setTab(m.group(1));
                entry.setSection(m.group(2));
                entry.setName(m.group(3));
                entry.setHelp(help);
                layout.setComment(key,help);
            }
        }
        if ("SolrEffectiveRights".equals(entry.getSection())){
            entry.setIgnore(false);
            entry.setTab("sicurezza");
            entry.setSection("regole");
            entry.setName(StringUtils.removeStart(entry.getName(),"rule."));
        }

        if (help!=null && (help.contains("#IMPIANTO#") || help.contains("#SKIP#"))){
            entry.setIgnore(true);
        }
        entries.put(entryKey,entry);

        Object value = config.getProperty(propertyName);

        if (value instanceof Collection)
            value = (String) ((Collection) value).iterator().next();
        else if (value!=null)
            value = value.toString();

        entry.setDefaultValue(value);

        return entry;
    }

    public class Entry {

        public Object set(Object value){
            Map s = ensureSection(this);
            Object v = s.put(name,value);
            return v;
        }

        public void setProperty(Object value){

            if (value instanceof List)
                value = StringUtils.join( ((List)value),",");

            openProperties(this.getFileId()).setProperty(this.getPropertyName(),value);

            if (KS_SYSTEM_AOO.equals(this.getFileId())){

                if (openProperties(KS_SYSTEM).getProperty(this.getPropertyName())==null){
                    openProperties(KS_SYSTEM).setProperty(this.getPropertyName(),value);
                }
            }
        }

        public Object retrieveOrDefault(){
            return ensureSection(this).getOrDefault(name,defaultValue);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        String id;
        String description;
        String help;
        String tab;
        String section;
        Boolean ignore = false;
        Object defaultValue = null;

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Boolean getIgnore() {
            return ignore;
        }

        public void setIgnore(Boolean ignore) {
            this.ignore = ignore;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        String propertyName;
        String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        String type;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        String fileId;

        Object template;

        public String getTab() {
            return tab;
        }

        public void setTab(String tab) {
            this.tab = tab;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }





        public Entry(Map map){
            try {
                BeanUtils.populate(this,map);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Entry(){

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHelp() {
            return help;
        }

        public void setHelp(String help) {
            this.help = help;
        }



        public Object getTemplate() {
            return template;
        }

        public void setTemplate(Object template) {
            this.template = template;
        }



        public Boolean isMultiValue() {
            return template instanceof List;
        }

        public Object getValueTemplate(){
            Object tmpl = template;
            if (isMultiValue()){
                List list = (List) tmpl;
                if (list.size()>0)
                    tmpl = list.get(0);
                else
                    tmpl = null;
            }
            return tmpl;
        }

        public Boolean isNumber() {
            return getValueTemplate() instanceof Number;
        }

        public Boolean isMap(){
            return getValueTemplate() instanceof Map;
        }


    }
}
