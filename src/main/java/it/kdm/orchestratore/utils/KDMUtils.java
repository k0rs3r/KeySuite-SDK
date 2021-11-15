package it.kdm.orchestratore.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import it.kdm.orchestratore.appBpm.utils.Helper;
import it.kdm.orchestratore.appdoc.utils.URLHelper;
import it.kdm.orchestratore.entity.object.InstancesObject;
import it.kdm.orchestratore.entity.object.ProcessConfigurationObject;
import it.kdm.orchestratore.exception.KDMException;
import it.kdm.orchestratore.session.*;
import keysuite.docer.client.ClientUtils;
import keysuite.docer.client.DocerBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KDMUtils  {

    //public final static String RSX_FOLDER = new File( Utils.getConfigHome(), "resources" ).toString();
    private static final Logger logger = LoggerFactory.getLogger(KDMUtils.class);
	public static Boolean TaskStatusIsActive(String status){
		//Created, Ready, Reserved, InProgress, Suspended, Completed, Failed, Error, Exited, Obsolete

		if ("Ready".equals(status) ||
				"Reserved".equals(status) ||
				"InProgress".equals(status) ||
				"Suspended".equals(status) ||
				"Completed".equals(status))
			return true;
		else
			return false;
	}

	public static String getProperty(String property, String def){
		return ApplicationProperties.get(property,def);
	}

	public static String getProperty(String property){
		return ApplicationProperties.get(property);
	}

	public static String getRoleClasses(){
		String classes = "";

		try {
			List<String> tokens = Session.getUserInfo().getUserTokens();

			for( String token : tokens ){
				classes += ".secureblock." + token + ",";
			}
			classes += ".unsecureblock";
			return classes;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static String getExtraHtml(String key){
		String path = ToolkitConnector.getGlobalProperty(key);
		if (!Strings.isNullOrEmpty(path)){
			try {
				return FileUtils.readFileToString(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "<!-- no extra html -->";
	}

	public static String checkActor(Map<String,Object> workItem, String field)
	{
		Object value = workItem.get(field);

		if (value == null || "".equals(value))
			return null;

		List actors = new ArrayList();

		if (value instanceof Collection) {
			actors.addAll( (Collection) value );
		} else {
			actors.add(value);
		}

		for( int i=0; i<actors.size(); i++){
			value = actors.get(i);

			if (value instanceof Map)
			{
				String id = (String) ((Map)value).get("identity");
				if (id==null)
					id = (String) ((Map)value).get("USER_ID");
				else if (id==null)
					id = (String) ((Map)value).get("GROUP_ID");
				else if (id==null)
					id = (String) ((Map)value).get("ACTOR_ID");

				if (id!=null)
					actors.set(i,id );
				else
					actors.set(i,"" );
			}
			else if (value instanceof String)
			{
				String token = (String) value;
				int idx = token.indexOf("|uid:") + 5;
				if (idx > 5)
					actors.set(i,token.substring(idx, token.indexOf("|",idx)));
			}
		}

		value = StringUtils.join(actors, System.getProperty("org.jbpm.ht.user.separator", ",") );

		workItem.put(field,value);

		return (String) value;
	}
	// classe di utilità dove mettere tutti i metodi statici che verranno usati nel progetto

	public static void cleanCookies(HttpServletRequest request, HttpServletResponse response) {

		if (request.getSession()!=null) {
			request.getSession().invalidate();
		}

		Cookie[] cookies = request.getCookies();

		if (cookies==null)
			return;

		for(int i = 0; i< cookies.length ; ++i){
			if (!cookies[i].getName().equalsIgnoreCase("GUID") && !cookies[i].getName().equalsIgnoreCase("SSO_USER")
					&& !cookies[i].getName().equalsIgnoreCase("DISABLE_LOGOUT")&& !cookies[i].getName().equalsIgnoreCase("REDIRECT_LOGOUT")	) {
				cookies[i].setValue(null);
				cookies[i].setMaxAge(0);
				response.addCookie(cookies[i]);
			}
		}
	}

	public static RestTemplate prepareRestTemplate(RestTemplate restTemplate){

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT , false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(mapper);
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(messageConverter);
		restTemplate.setMessageConverters(messageConverters);

		return restTemplate;
	}

	public static void printMyStackTrace(Logger log, StackTraceElement[] stackElements){
		for (int lcv = 0; lcv < stackElements.length; lcv++) {
			log.error(stackElements[lcv].toString());
		}

	}
	public static String debugPrintTimeStamp(String text) {
		java.util.Date date= new java.util.Date();
		System.out.print(text);
		System.out.println(new Timestamp(date.getTime()));

		return text + new Timestamp(date.getTime()).toString();
	}

    public static ModelAndView finalizeModelAndView(ModelAndView model) throws IOException, TemplateException {
	    return finalizeModelAndView(model,Session.getRequest());
    }

    public static String finalizeModelAndView(String viewName) throws IOException, TemplateException {
        return finalizeModelAndView(viewName,Session.getRequest());
    }

	@SuppressWarnings("unchecked")
	public static <T> T finalizeModelAndView(Object model, HttpServletRequest request) throws IOException, TemplateException {
		//****************************************************************************************************
		//Per attivare questa modalità, aggiugere la classe 'jClass' e l'attributo target-data='<placeHolder>'
		//all'elemento html che dovrà essere gestito con la modalità di chiamata ajax
		//Settando il Content-Type a 'fragment/html' la richiesta sarà switchata sulla view del frammento
		//Settando l'header 'form-view' con valore '<nome-view>' la richiesta utilizzerà la view specificata
		//****************************************************************************************************

		request.setAttribute("requestPath", request.getRequestURI());
		request.setAttribute("context", request.getContextPath());
		request.setAttribute("ActorsCache", ActorsCache.getInstance());

		T res = null;
		if (model instanceof String) {

			res =  (T) finalizeModelAndView_((String) model, request);

		} else if  (model instanceof ModelAndView) {

			((ModelAndView) model).addObject("dateFormat", ToolkitConnector.getGlobalProperty("ui.dateFormat","dd-MMM-yyyy HH:mm:ss"));
			res = (T) finalizeModelAndView_((ModelAndView) model, request) ;
		}

		Long currentTimeIn =request.getAttribute("currentTimeIn") != null ?  (Long) request.getAttribute("currentTimeIn") :null;
		if(currentTimeIn != null) {
			long currentTimeOut = System.currentTimeMillis();
			logger.info("Tempo esecuzione "+ request.getRequestURL() + request.getQueryString() + " duration: "+ (currentTimeOut-currentTimeIn));
		}

		return res;
	}

	public static String getDeploymentId( String codEnte ){
		return "default-per-pinstance";
	}

	private static final Pattern procVer = Pattern.compile("^(.*[^0-9\\.])([0-9][0-9\\.]+)$");

	public static String getSortedVersion( String version ){
		String[] parts = version.split("\\.");
		for( int i=0; i<parts.length; i++){
			parts[i] = StringUtils.leftPad(parts[i],i+2,'0');
		}
		return StringUtils.join(parts,".");
	}

	public static String getProcessVersion( String processId ){

		Matcher m = procVer.matcher(processId);
		if (m.find())
			return m.group(2);
		else
			return null;
	}

	public static String getProcessName( String processId ){
		Matcher m = procVer.matcher(processId);
		if (m.find())
			return m.group(1);
		else
			return null;
	}

	public static String getPreviousVersion( String version ){
		int idx = version.lastIndexOf(".");

		String precVer = null;

		try {
			if (idx >= 0) {
				int num = Integer.parseInt(version.substring(idx + 1));
				if (num > 0)
					precVer = version.substring(0, idx + 1) + (num - 1);
			}
		} catch (Exception e) {
			//logger.error("impossibile impostare settings da versione precedente", e);
		}
		return precVer;
	}

	private static String finalizeModelAndView_(String model, HttpServletRequest request) throws IOException, TemplateException {
		ModelAndView s2model = new ModelAndView();
		s2model.setViewName(model);
		return finalizeModelAndView_(s2model, request).getViewName();
	}

	public static String getErrorMessage(MessageSource messageSource,Exception e) {
		//String errorMsg = "Eccezione generica rilevata contattare l'amministratore";
		String lblKey = "label.error.generic";
		String msg="";

		if (e instanceof DocerApiException) {
			lblKey = String.format("label.error.%s",((DocerApiException)e).errorCode);
			msg=((DocerApiException)e).getMessage();
		}

		if (e instanceof KDMException) {
			lblKey = String.format("label.error.%s",((KDMException)e).getErrorCode());
			msg=((KDMException)e).getMessage();
		}

		String language;

		try {
			UserInfo uInfo = Session.getUserInfo();
			language = uInfo.getLanguage();
		} catch (Exception e1) {
			language = "it";
		}

		String errorMsg = messageSource.getMessage(lblKey, new Object[]{msg} , "Codice Errore: "+lblKey, new Locale(language));

		return errorMsg;
	}

    public static void setErrorStatus(HttpServletResponse response){
	    setErrorStatus(Session.getRequest(),response);
    }

	public static void setErrorStatus(HttpServletRequest request,HttpServletResponse response){

		String contentType = request.getContentType();
		if (StringUtils.isNotEmpty(contentType)) {
			if (contentType.equals("fragment/html")) {
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}
		if(request.getHeader("accept").contains("application/json"))
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

	}

	private static ModelAndView finalizeModelAndView_(ModelAndView model, HttpServletRequest request) throws IOException, TemplateException {
		//gestione dello switch della view
		String viewName = request.getHeader("form-view");
		if (StringUtils.isEmpty(viewName)) {
			viewName = model.getViewName();
		}

		if (viewName!=null && (viewName.startsWith("ftl:") || viewName.toLowerCase().endsWith(".ftl")) ){
			String template = viewName.startsWith("ftl:") ? viewName.substring(4) : viewName;
			String markup = KDMUtils.ftlHandler(template,model.getModel());

			if ("print".equals(request.getAttribute("wt")))
				viewName = "ftl.print";
			else
				viewName = "ftl";
			model.addObject("ftl",markup);
		}

		//gestione dello switch della visualizzazione a frammento
		String contentType = request.getContentType();
		if (StringUtils.isNotEmpty(contentType)) {
			if (contentType.equals("fragment/html")) {
				viewName = viewName.concat(".fragment");
			}
		}
		if (request.getHeader("accept").contains("application/json"))
			viewName = viewName.concat(".fragment");

		model.setViewName(viewName);
		return model;
	}

	public static String ftlHandler( String template, String name, Map<String, ?> settings, Configuration cfg ) throws TemplateException, IOException {

		Writer out = new StringWriter();

		Template t = new Template(name, new StringReader(template), cfg);
		t.process(settings, out);
		return out.toString();

	}



	static Configuration cfg = null;

	public static Configuration getFTLConfiguration(){
		if (cfg==null){
			try{
				/*String basePath = ToolkitConnector.getGlobalProperty("resources.folder",RSX_FOLDER);

				final File templates = new File(basePath,"templates");
				final File reports = new File(basePath,"reports");

				if (!templates.exists())
					templates.mkdir();

				if (!reports.exists())
					reports.mkdir();*/

				cfg = new Configuration(Configuration.VERSION_2_3_0);

				/*MultiTemplateLoader ml = new MultiTemplateLoader(
						new TemplateLoader[]{
								//new FileTemplateLoader( reports ),
								//new FileTemplateLoader( templates ),
								new ResourceTemplateLoader( "reports/", cfg),
								new ResourceTemplateLoader( "templates/", cfg)
						}
				);

				cfg.setTemplateLoader(ml);*/

				cfg.setTemplateLoader(new ResourceTemplateLoader( "templates/", cfg));

				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			} catch ( Exception e ){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return cfg;
	}

	public static Object getBean(String bean){
		return WebApplicationContextUtils.getRequiredWebApplicationContext(Session.getRequest().getSession().getServletContext()).getBean(bean);
	}

	public static <T> T getBean(Class<T> aClass){
		return WebApplicationContextUtils.getRequiredWebApplicationContext(Session.getRequest().getSession().getServletContext()).getBean(aClass);
	}

	public static Template getFTLTemplate(String template) throws TemplateException, IOException{
		Template t;

		if (template.length()<=255 && template.matches("^[/\\w\\-. ]+$")){
			if (!template.toLowerCase().endsWith(".ftl"))
				template+=".ftl";
			Locale locale = (Locale) Session.getRequest().getAttribute("KS_LOCALE");
			t = getFTLConfiguration().getTemplate(template, locale );
		} else {
			t = new Template("temp"+template.hashCode(), new StringReader(template), getFTLConfiguration());
		}

		return t;
	}

	public static String ftlHandler( String template, Map settings) throws TemplateException, IOException {

		return ftlHandler(getFTLTemplate(template),settings);
	}

	public static String resolveMessages(String outString){
		if (outString.contains("$[")){
			HttpServletRequest req = Session.getRequest();

			final Map<String,String> properties = (Map) req.getAttribute("properties");
			final MessageSource messageSource = getBean(MessageSource.class);
			UserInfo ui = Session.getUserInfoNoExc();

			final String lang  = (String) Session.getRequest().getAttribute("KS_LANG");
			final Locale locale = (Locale) Session.getRequest().getAttribute("KS_LOCALE");

			StrSubstitutor substr = new StrSubstitutor( new StrLookup() {
				@Override
				public String lookup(String key) {

					if (key.startsWith("#noparse"))
						return "$"+key+"]";

					String[] parts = key.split("(?<!\\\\),");
					if (parts.length>1) {
						for (String part : parts) {
							if (part.startsWith(lang + ":")) {
								String msg = part.substring(part.indexOf(":") + 1);
								return msg;
							}
						}
						return parts[0].substring(parts[0].indexOf(":") + 1);
					}

					if (key.startsWith("name:")){
						key = key.substring(5);
						DocerBean bean = ClientUtils.getItem(key.substring(5),null);
						if (bean!=null)
							return bean.getName();
						else
							return key;
					}

					String def = ui.isAdmin() ? String.format("<span class='mnotfound'>[%s]</span>",key) : String.format("[%s]",key) ;

					if (key.contains(":")){
						def = key.split(":")[1];
						key = key.split(":")[0];
					}

					if (!Strings.isNullOrEmpty(System.getProperty("label:"+key)))
						return System.getProperty("label:"+key);
					else if (properties!=null&& properties.containsKey(key))
						return properties.get(key);
					else if (messageSource!=null)
						return messageSource.getMessage( key, null, def, locale);
					else
						return def;

				}
			} ,"$[","]",'$');
			outString = substr.replace(outString);
			//outString = outString.replace("$#noparse","$[");

		}
		return outString;
	}

	public static String fixContext(String context){
		if (Strings.isNullOrEmpty(context)){
			return "";
		} else if (!context.startsWith("/")){
			context=  "/" + context;
		}
		if (context.endsWith("/"))
			context = context.substring(0,context.length()-1);
		return context;
	}

	//public static String resVer = ""+Math.abs((KDMUtils.getWarVersion()+"."+ KDMUtils.getWarTimestamp()).hashCode());
	public static String resFolder = "/resources";

	public static Map<String,Object> buildRenderModel(Map<String,Object> settings){
		settings = new LinkedHashMap(settings);

		//settings.put("resVer",resVer);
		//settings.put("resources",resFolder);
		settings.put("context",fixContext( (String) settings.get("context")));
		settings.put("utils", new TemplateUtils() );
		settings.put("$", new TemplateUtils() );

		if (!settings.containsKey("baseUrl")){
			settings.put("baseUrl",Session.getRequest().getServletPath());
		}
		return settings;
	}

	public static String ftlHandler( Template t, Map settings) throws TemplateException, IOException {

		settings = buildRenderModel(settings);

		Writer out = new StringWriter();
		try {
			t.process(settings, out);
		} catch (Exception e) {
			HttpServletRequest request = Session.getRequest();
			if (request!=null)
				request.setAttribute("exceptionModel",settings);
			throw e;
		}
		String outString = out.toString();

		outString = resolveMessages(outString);

		return outString;
	}

	/*public static InputStream findResource(String path) throws FileNotFoundException {
		return ResourceUtils.getResource(path);
	}*/

	/*public static InputStream findResource2(String path){

		boolean expand = "true".equals( Session.getRequest().getParameter("export"));

		String basePath = ToolkitConnector.getGlobalProperty("resources.folder", Utils.getConfigHome().toString()+"/resources/");

		File home = new File(basePath);
		File check = new File(home,path);
		if (!check.exists()){
			InputStream is = KDMUtils.class.getClassLoader().getResourceAsStream(path);

			if (is!=null && expand){
				try{
					FileUtils.copyInputStreamToFile(is, check);

					if (path.toLowerCase().endsWith(".ftl")){
						TemplateLoader tl = getFTLConfiguration().getTemplateLoader();
						if (tl instanceof MultiTemplateLoader){
							((MultiTemplateLoader)tl).setSticky(false);
						}
					}
				} catch (Exception io){
					io.printStackTrace();
				} finally {
					is = KDMUtils.class.getClassLoader().getResourceAsStream(path);
				}
			}

			return is;
		} else {
			try {
				return new FileInputStream(check);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}*/

	public static Properties getMultiLinePropertiesFile(InputStream pfile) throws IOException {
		List<String> lines = IOUtils.readLines(pfile);

		Properties p = new Properties();

		for( int i=0; i<lines.size(); i++ ){

			String line = lines.get(i);

			if (i==0 && line.startsWith("!include ")){

				String qt = line.substring(9);
				InputStream incFile = ResourceUtils.getResourceAsStream(String.format("reports/%s.properties",qt));
				p = getMultiLinePropertiesFile(incFile);
				continue;
			}

			/* salto la prima linea perchè non continua nulla e l'ultima perchè non deve continuare nulla */
			if (i==0 || i>=(lines.size()-1))
				continue;

			/* la linea corrente non inizia per spazio o è vuota */
			if (!line.matches("^[\\t\\s]+.*$|^$"))
				continue;

			/* la linea successiva non inizia per spazio o è vuota */
			if (!lines.get(i+1).matches("^[\\t\\s]+.*$|^$"))
				continue;

			/* la linea precedente non è multiline */
			if (!lines.get(i-1).matches("^.*\\\\\\s*$"))
				continue;

			/* la linea successiva è già multiline */
			if (lines.get(i+1).matches("^.*\\\\\\s*$"))
				continue;

			/* la linea successiva è un commento */
			if (lines.get(i+1).matches("^\\s*#.*"))
				continue;

			lines.set(i, line+"\\n\\");


			/*if (i>0 && i<(lines.size()-1)

					&& line.matches("^[\\t\\s]+.*$|^$")


					&& lines.get(i-1).matches("^.*\\\\\\s*$")


					&& !lines.get(i+1).matches("^.*\\\\\\s*$")


					&& !lines.get(i+1).matches("^\\s*#.*")

				)*/
			/* diventa multiline */
			//lines.set(i, line+"\\n\\");
		}

		p.load(new StringReader(StringUtils.join(lines,"\n").replace("\\n+","\\n")+"\n"));
		return p;
	}

	public static <T> void handleDateTreatment( T object){

		String DATE_FORMAT_STRING ="dd-MMM-yyyy HH:mm:ss";

		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_STRING);

		if (object instanceof InstancesObject) {

			if (StringUtils.isNotEmpty(((InstancesObject) object).getEndDate())) {
				Long endDate = Long.valueOf(((InstancesObject) object).getEndDate());
				((InstancesObject) object).setEndDate(fmt.format(new Date(endDate)));
			}

			if (StringUtils.isNotEmpty(((InstancesObject) object).getStartDate())) {
				Long startDate = Long.valueOf(((InstancesObject) object).getStartDate());
				((InstancesObject) object).setStartDate(fmt.format(new Date(startDate)));
			}

			if (StringUtils.isNotEmpty(((InstancesObject) object).getLastModificationDate())) {
				Long lastMDate = Long.valueOf(((InstancesObject) object).getLastModificationDate());
				((InstancesObject) object).setLastModificationDate(fmt.format(new Date(lastMDate)));
			}

			if (StringUtils.isNotEmpty(((InstancesObject) object).getLastReadDate())) {
				Long lastRDate = Long.valueOf(((InstancesObject) object).getLastReadDate());
				((InstancesObject) object).setLastReadDate(fmt.format(new Date(lastRDate)));
			}

		}


	}

	public static String buildPassword( String username, String hash) {

		return username;

	}
	public static String replace(String input, Pattern regex, StringReplacerCallback callback, MessageSource messageSource, Locale locale) {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(input);
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(resultString, callback.replace(regexMatcher, messageSource,locale));
		}
		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}

	public static String translateMessages(String template, String pattern, MessageSource messageSource, Locale locale){

		pattern = pattern==null? "label(\\.[^\\s\\W]+)+" : pattern;

		Pattern regex = Pattern.compile(pattern);

		return KDMUtils.replace(template, regex, new SpringMessageReplacer() {
			@Override
			public String replace(Matcher m, MessageSource messageSource, Locale locale) {
				return messageSource.getMessage(m.group(), null, m.group(), locale);
			}

		},  messageSource, locale);
	}

	public static String getISO8601StringForCurrentDate() {
		Date now = new Date();
		return getISO8601StringForDate(now);
	}

	public static String getISO8601StringForDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static void mergeSettings(Map<String, Object> winner, Map<String, Object> looser){
		//merge configuration
		Map<String, Object> actual = (Map<String, Object>) looser.get("configuration");
		Map<String, Object> old = (Map<String, Object>) winner.get("configuration");
		if (actual!=null && old!=null)
			actual.putAll(old);

		//merge bufferDefaults
		actual = (Map<String, Object>) looser.get("bufferDefaults");
		old = (Map<String, Object>) winner.get("bufferDefaults");
		if (actual!=null && old!=null)
			actual.putAll(old);
	}

	public static String getStartRole(String ente, String aoo,  Map<String,Object> settingMap){
		return getRoleFromConfig("startRole",ente,aoo,settingMap);
	}

	private static String getRoleFromConfig(String role, String ente, String aoo, Map<String,Object> settingMap) {

		Map<String, Object> configuration = (Map<String, Object>) settingMap.get("configuration");

		String roleValue = (String) configuration.get(role);

		if (!Strings.isNullOrEmpty(roleValue)) { //runnable

			String sysGroup = ToolkitConnector.getGlobalProperty("adminGroups");

			if (roleValue.equals("INSTANCE_AOO") || roleValue.equals("{INSTANCE_AOO}"))
				roleValue = aoo;
			else if (roleValue.equals("INSTANCE_ENTE") || roleValue.equals("{INSTANCE_ENTE}"))
				roleValue = ente;
			else {
				Object bufferRole = ((Map) settingMap.get("bufferDefaults")).get(roleValue);
				roleValue = sysGroup;

				if (bufferRole instanceof Map)
					roleValue = (String) ((Map) bufferRole).get("identity");
				else if (bufferRole != null)
					roleValue = bufferRole.toString();
			}
		}
		return roleValue;
	}

	public static void setupSettings(String ente, String aoo, Map<String,Object> settingMap) throws Exception{
		if(settingMap.containsKey("configuration"))
		{
			Map<String,Object> configuration = (Map<String, Object>) settingMap.get("configuration");

			configuration.put("ente",ente);
			configuration.put("aoo",aoo);
			//prendiamo quello del designer
			if (!configuration.containsKey("respUo") || "".equals(configuration.get("respUo").toString()))
				configuration.put("respUo",aoo);
		}
	}

	public static void setupProcessConfiguration(String ente, String aoo, Map<String,Object> settingMap, ProcessConfigurationObject processConfigurationObject) throws Exception{

		processConfigurationObject.setEnte(ente);
		processConfigurationObject.setAoo(aoo);

		String sysGroup = ToolkitConnector.getGlobalProperty("adminGroups");
		processConfigurationObject.setViewRole(sysGroup);
		processConfigurationObject.setStartRole(sysGroup);
		processConfigurationObject.setConfigRole(sysGroup);
		processConfigurationObject.setPrefer(true);
		processConfigurationObject.setRunnable(true);
		processConfigurationObject.setCloneRole(sysGroup);
		processConfigurationObject.setCategory("default");

		if(settingMap.containsKey("configuration"))
		{
			setupSettings(ente,aoo,settingMap);

			Map<String,Object> configuration = (Map<String, Object>) settingMap.get("configuration");

			Boolean runnable = (Boolean) configuration.get("runnable");
			String category = (String) configuration.get("category");

			String startRole = getStartRole(ente,aoo,settingMap);
			String respUo = getRoleFromConfig("respUo",ente,aoo,settingMap);
			String respRole = getRoleFromConfig("Administrators",ente,aoo,settingMap);

			if (!Strings.isNullOrEmpty(respRole))
				processConfigurationObject.setConfigRole(respRole);

			if (!Strings.isNullOrEmpty(respUo))
				processConfigurationObject.setViewRole(respUo);

			if (runnable!=null){
				processConfigurationObject.setRunnable(runnable);
				processConfigurationObject.setPrefer(runnable);
			}

			if (!Strings.isNullOrEmpty(startRole)) {
				processConfigurationObject.setStartRole(startRole);
				processConfigurationObject.setCloneRole(startRole);
			}

			if (!Strings.isNullOrEmpty(category)){
				processConfigurationObject.setCategory(category);
			}
		}
	}

	public static Map<String,Object> jsonToHashMap(String strSettings) throws Exception{
		return Helper.jsonToHashMap(strSettings);
	}

	public static String hashMapToJson(Map<String,Object> mapSetting) throws Exception{
		return Helper.hashMapToJson(mapSetting);
	}

	public static URL getURL(HttpServletRequest request){
        URL hostUrl;
        try {
            hostUrl = new URL(request.getRequestURL().toString().toLowerCase());
            return hostUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getBaseURL(HttpServletRequest request){
		/*String baseUrl = request.getScheme() + "://" + request.getServerName();
		if (request.getServerPort() != 80)
			baseUrl += ":" + request.getServerPort();
		return baseUrl;*/
		return URLHelper.getHostURL(request);
	}

	public static String getRequestURLwithQs(HttpServletRequest request){
		String requestURLwithQs = request.getRequestURL().toString();

		if (!Strings.isNullOrEmpty(request.getQueryString()))
			requestURLwithQs += "?" + request.getQueryString();
		return requestURLwithQs;
	}

    public static String firstMatch(String str, String regex, String defaultValue){
        Matcher m = Pattern.compile(regex).matcher(str);

        if (m.find() && m.groupCount()>0)
            return m.group(1);
        else
            return defaultValue;
    }

    public static String getWarProperty(String key,String defaultValue){
		return ApplicationProperties.getInstance("META-INF/build-info.properties").getPropertyByKey(key,defaultValue);
	}

	public static String getWarVersion(){
    	return getWarProperty("build.version","x.x");
	}

	public static String getWarTimestamp(){
		return getWarProperty("build.time","01-01-1970 00:00");
	}

    public static String getResourceVer(){
    	return ToolkitConnector.getGlobalProperty("resources.version",getWarVersion());
	}

	public static File stream2file (InputStream in) throws IOException {
		return stream2file(in,null);
	}

	public static File stream2file (InputStream in, String name) throws IOException {
		final File tempFile;

		if (!Strings.isNullOrEmpty(name)){
			String tmpDirStr = System.getProperty("java.io.tmpdir");
			tempFile = new File(tmpDirStr,name.hashCode()+".tmp");
			if (tempFile.exists())
				return tempFile;
		} else {
			tempFile = File.createTempFile("stream2file", ".tmp");
		}

		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}

	public static String encode(String token, String chars){
		if (token==null)
			return null;
		StringBuilder s = new StringBuilder(token.length());
		CharacterIterator it = new StringCharacterIterator(token);
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			if (chars.indexOf(ch)!=-1)
				s.append("%").append(Integer.toHexString((int) ch));
			else
				s.append(ch);
		}

		token = s.toString();
		return token;
	}

	public static final String specialChars = "\\/:*?\"<>!.@%";

	public static String getSolrId(String targetId,String targetType){

		UserInfo ui = Session.getUserInfoNoExc();
		String loc = encode(ToolkitConnector.getSedeLocale(),specialChars);
		String codEnte = encode(ui.getCodEnte(),specialChars);
		String codAoo = encode(ui.getCodAoo(),specialChars);

		if ("fascicolo".equals(targetType) && !targetId.contains("|")){
			targetId = targetId.replaceFirst("/","|").replaceFirst("/","|");
		}
		targetId = encode(targetId,specialChars);

		switch(targetType){
			case "ente":
				targetId = String.format("%s.%s!@ente",loc,targetId);
				break;
			case "aoo":
				targetId = String.format("%s.%s!%s!@aoo",loc,codEnte,targetId);
				break;
			case "group":
			case "user":
				targetId = String.format("%s@%s",targetId,targetType);
				break;
			case "fascicolo":
				targetId = String.format("%s.%s!%s!%s@%s",loc,codEnte,codAoo,targetId,targetType);
				break;
			default:
				targetId = String.format("%s.%s!%s!%s@%s",loc,codEnte,codAoo,targetId,targetType);
				break;
		}
		return targetId;
	}



	/*public static String configuraMultitabGetHtml(String type, UserInfo user, String appName, HttpServletRequest request){
		String result = "";
		ConfigCustomApp configApp = CustomAppCache.getInstance(request, user, appName);
		if(configApp != null){
			if(type.equalsIgnoreCase("headerMenu"))
				result = configApp.getHeaderMenuString();
			if(type.equalsIgnoreCase("footer"))
				result = configApp.getFooterString();
			if(type.equalsIgnoreCase("sideMenu"))
				result = configApp.getSideMenuString();
			if(type.equalsIgnoreCase("styleApp"))
				result = configApp.getCustomStyle();
			if(type.equalsIgnoreCase("styleDefault"))
				result = configApp.getDefaultStyle();
		}
		return result;
	}*/
}
