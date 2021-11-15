package it.kdm.orchestratore.appdoc.utils;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.Corrispondente;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.model.PersonaFisica;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.DocerService;
import it.kdm.doctoolkit.services.SOLRClient;
import it.kdm.doctoolkit.services.SolrPathInterface;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.utils.CacheManager;
import it.kdm.doctoolkit.utils.SOLRResponse;
import it.kdm.orchestratore.appdoc.model.DocumentoFirmatario;
import it.kdm.orchestratore.appdoc.model.FirmatarioPojo;
import it.kdm.orchestratore.appdoc.properties.PropertiesReader;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import it.kdm.orchestratore.utils.KDMUtils;
import it.kdm.orchestratore.utils.ResourceUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jcs.access.exception.CacheException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsFTL
{
	private static final Logger logger = LoggerFactory.getLogger(UtilsFTL.class);
	
	//contains the servletcontext path
	private String ftlPath;
	private String ftlSystemPath;
	private Set ftlSystemFiles;

	//contains all files in servlet context
	private Set<String> ftlFiles;
	private ServletContext sc;

	private static final String pathFltCartelle = "/templates/cartelle/";


	@SuppressWarnings("unchecked")
	public UtilsFTL(String ftlPath, ServletContext sc) {
		String resFileSystemPath = PropertiesReader.getInstance().getGlobalProperty("resources.folder");
		this.ftlSystemPath = resFileSystemPath + ((ftlPath != null && !ftlPath.equalsIgnoreCase("")) ? (ftlPath.startsWith("/") ? ftlPath.substring(1) : ftlPath ): "");
		this.ftlSystemFiles = getResourcesFiles(ftlSystemPath);

		if (sc==null)
		    sc = Session.getRequest().getSession().getServletContext();

		this.ftlFiles = sc.getResourcePaths(ftlPath);
		this.ftlPath =ftlPath;
		this.sc=sc;

		if (!this.ftlPath.endsWith("/"))
		    this.ftlPath += "/";
	}

    @SuppressWarnings("unchecked")
    public UtilsFTL(String ftlPath) {
        this(ftlPath,null);
    }

    @SuppressWarnings("unchecked")
    public UtilsFTL() {
        this("/templates/",null);
    }


	@SuppressWarnings("unchecked")
	public UtilsFTL(String ftlPath,String ftlSystemPath, ServletContext sc) {

		this.ftlFiles = sc.getResourcePaths(ftlPath);
		this.ftlPath = ftlPath;
		this.ftlSystemPath = ftlSystemPath==null ? "/": ftlSystemPath;
		this.sc=sc;
	}
	private Set getResourcesFiles(String path){
		Set<String> resourcesFiles = new HashSet<String>();
		try {
			File folder = new File(path);
			if(folder.exists() && folder.isDirectory()) {
				for (final File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory()) {
						resourcesFiles.add(fileEntry.getAbsolutePath());
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		if(resourcesFiles.isEmpty()){
			resourcesFiles = null;
		}

		return resourcesFiles;
	}

	public String getDynamicFTLOLD(String tipology, String ente, String codAoo, String codUo, String mode) throws IOException
	{
		String path = "";
		String systemPath = "";
		List<String> pathSearch = new ArrayList<String>();
		List<String> pathFilesSearch = new ArrayList<String>();

		path =  ftlPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + codUo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath =  ftlSystemPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + codUo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path =  ftlPath + ente.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath =  ftlSystemPath + ente.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + tipology.toLowerCase() + "-else.ftl";
		systemPath = ftlSystemPath + tipology.toLowerCase() + "-else.ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		for (String p : pathFilesSearch) {
			if(ftlSystemFiles!=null && ftlSystemFiles.contains(p)){
				InputStream is = new FileInputStream(new File(p));
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				return writer.toString();
			}
			//se esiste ritorna e break
		}


		for (String p : pathSearch) {
			if(ftlFiles.contains(p)){
				InputStream is = sc.getResourceAsStream(p);
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				return writer.toString();
			}
			//se esiste ritorna e break
		}


		logger.info("file ftl not found tipology:{} ente:{} codAoo:{} codUo:{} mode:{}",tipology,ente,codAoo,codUo,mode);

		StringWriter writer = new StringWriter();
		return writer.toString();
	}

    public static String getDynamicFTL(String type) throws IOException
    {
        return getDynamicFTL(type,null);
    }

    public static String getDynamicFTL(String type, String mode) throws IOException
    {
        UserInfo ui = Session.getUserInfoNoExc();
        return new UtilsFTL().getDynamicFTL(type,ui.getCodEnte(),ui.getCodAoo(),null,mode);
    }

	public String getDynamicFTL(String tipology, String ente, String aoo, String codUo, String mode) throws IOException
	{
	    tipology = StringUtils.isEmpty(tipology) ? "" : tipology.toLowerCase();
		mode = StringUtils.isEmpty(mode) ? "" : mode.toLowerCase();

		if (tipology.endsWith(".ftl"))
		    tipology = tipology.substring(0,tipology.length()-4);

        if (mode.endsWith(".ftl"))
            mode = mode.substring(0,mode.length()-4);

        String path;

        if (!Strings.isNullOrEmpty(tipology) && !Strings.isNullOrEmpty(mode))
		    path = ftlPath + tipology + "-" + mode + ".ftl";
		else
		    path = ftlPath + tipology + mode + ".ftl";

		//path = ftlPath + tipology != null ? tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl" : mode.toLowerCase() + ".ftl" ;
		InputStream file = ResourceUtils.getResourceAsStream(null,ente, aoo,path);
		StringWriter writer = new StringWriter();
		if(file != null) {
			IOUtils.copy(file, writer);
			return writer.toString();
		}

		//Primo bk
        if (!Strings.isNullOrEmpty(tipology))
            path = ftlPath + tipology + "-else.ftl";
        else
            path = ftlPath + "else.ftl";

		//path  = ftlPath + ( tipology != null ? tipology.toLowerCase() + "-else.ftl" : "else.ftl") ;
		file = ResourceUtils.getResourceAsStream(null, ente, aoo,path);
		if(file != null) {
			IOUtils.copy(file, writer);
			return writer.toString();
		}

		//Secondo bk
		path  = ftlPath + mode.toLowerCase() + ".ftl";
		file = ResourceUtils.getResourceAsStream(null, ente, aoo,path);
		if(file != null) {
			IOUtils.copy(file, writer);
			return writer.toString();
		}

		throw new FileNotFoundException();
	}

	public String getDynamicJsp(String tipology, String ente, String codAoo, String codUo, String mode) throws IOException
	{
		String path = "";
		String systemPath = "";
		List<String> pathSearch = new ArrayList<String>();
		List<String> pathFilesSearch = new ArrayList<String>();

		path =  ftlPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + codUo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath =  ftlSystemPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + codUo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + ente.toLowerCase() + "-" + codAoo.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path =  ftlPath + ente.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath =  ftlSystemPath + ente.toLowerCase() + "-" + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + tipology.toLowerCase() + "-" + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + tipology.toLowerCase() + "-else.ftl";
		systemPath = ftlSystemPath + tipology.toLowerCase() + "-else.ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		path = ftlPath + mode.toLowerCase() + ".ftl";
		systemPath = ftlSystemPath + mode.toLowerCase() + ".ftl";
		pathSearch.add(path);
		pathFilesSearch.add(systemPath);

		for (String p : pathFilesSearch) {
			if(ftlSystemFiles!=null && ftlSystemFiles.contains(p)){
				InputStream is = new FileInputStream(new File(p));
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				return writer.toString();
			}
			//se esiste ritorna e break
		}


		for (String p : pathSearch) {
			if(ftlFiles.contains(p)){
				InputStream is = sc.getResourceAsStream(p);
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				return writer.toString();
			}
			//se esiste ritorna e break
		}


		logger.info("file ftl not found tipology:{} ente:{} codAoo:{} codUo:{} mode:{}",tipology,ente,codAoo,codUo,mode);

		StringWriter writer = new StringWriter();
		return writer.toString();
	}

	public static String getProperty(HttpServletRequest request, String fieldName, String defaultValue) {
		logger.info("getProperty" );

		fieldName = fieldName.toLowerCase();

		return request.getParameterMap().containsKey( fieldName ) ? request.getParameter(fieldName) : defaultValue;
	}


	public static HashMap<String, String> updateCustomProperties(HttpServletRequest request,String token,String ente,String aoo, HashMap<String, String> properties) {
		logger.info("updateProperties");


		List<String> baseprofile = new ArrayList<String>();
		Map<String, Boolean> customFields = new HashMap<String, Boolean>();

		//Carico il base profile
//		String[] metadata = "COD_ENTE,COD_AOO,TIPO_COMPONENTE, DOCNAME,TYPE_ID,STATO_ARCHIVISTICO,ARCHIVE_TYPE,COD_UO,TIPO_PROTOCOLLAZIONE".split(",");
//		List<String> baseprofile = Arrays.asList(metadata);

		try {
			Optional obj = CacheManager.confCache().get("docer_baseprofile");

			if(obj.isPresent() && obj.get() instanceof List) {
				baseprofile = (List<String>) obj.get();
			}
			else {
				baseprofile = DocerService.recuperaParametriDiConfigurazione(token, "baseprofile", "*");
				CacheManager.confCache().put("docer_baseprofile", baseprofile);
			}

		} catch (CacheException e) {
			throw new RuntimeException(e);
		}


	//Carico i custom fields
		try {
			Optional obj = CacheManager.confCache().get("docer_customfields");

			if(obj.isPresent() && obj.get() instanceof Map) {
				customFields = (Map<String, Boolean>) obj.get();
			}

		} catch (CacheException e) {
			throw new RuntimeException(e);
		}

	//Check dei parametri nella richiesta con baseprofile e custom fields
		Enumeration<String> parmameters = request.getParameterNames();
		while (parmameters.hasMoreElements()) {
			String attName = parmameters.nextElement();
			String custom = null;
			String attKey = attName.toLowerCase();
			Boolean hasCustomTrue = false;

			if( baseprofile.contains(attKey) )
				continue;

			if (!customFields.containsKey(attKey)) {
				custom = DocerService.recuperaParametroDiConfigurazione(token, "fields", attName.toLowerCase(), "custom");
				hasCustomTrue = (custom != null && custom.equals("true"));
				customFields.put(attKey, hasCustomTrue);
			}

			if(!customFields.get(attKey) )
				continue;

			if (attName.equals("PAROLE_CHIAVE")) {
				String value = StringUtils.replace(request.getParameter(attName), "+", "");

				properties.put(attName, value);
			} else {
				properties.put(attName, request.getParameter(attName));
			}
		}


		try {
			CacheManager.confCache().put("docer_customfields", customFields);
		} catch (CacheException e) {
			throw new RuntimeException(e);
		}

		return properties;
	}

	public static HashMap<String, String> resetCustomProperties(HttpServletRequest request,String token,String ente,String aoo, HashMap<String, String> properties) {
		logger.info("resetCustomProperties");

		List<String> baseprofile =DocerService.recuperaParametriDiConfigurazione(token, "baseprofile", "*");
//		String[] metadata = "COD_ENTE,COD_AOO,TIPO_COMPONENTE, DOCNAME,TYPE_ID,STATO_ARCHIVISTICO,ARCHIVE_TYPE,COD_UO,TIPO_PROTOCOLLAZIONE".split(",");
//		List<String> baseprofile = Arrays.asList(metadata);

		Enumeration<String> parmameters = request.getParameterNames();
		while (parmameters.hasMoreElements()) {
			String attName = parmameters.nextElement();

			attName = attName.toLowerCase();

			if( baseprofile.contains(attName.toLowerCase()) )
				continue;

			String type = DocerService.recuperaParametroDiConfigurazione(token, "fields", attName.toLowerCase(), "type");

			if(type==null) {
				properties.remove(attName);
			}
		}

		return properties;
	}

	public static String getSystemProperty(String fieldName, String defaultValue) {

		logger.info("getSystemProperty" );

		String val = ToolkitConnector.getGlobalProperty(fieldName);
		val = val!=null? val : defaultValue;

		return val;
	}
	public static String syncLocalData(HttpServletRequest request, String remoteLocation){

		logger.info("syncLocalData... reimport data from " + remoteLocation);

		UtilsFTL utils = new UtilsFTL(pathFltCartelle, request.getSession().getServletContext());

		URI uri;
		String url = ToolkitConnector.getGlobalProperty("solr-synclocaldata-url");

		if(url==null)
			return null;

		if(remoteLocation!=null) {
			try {
				uri = appendUri(url, "entity=" + remoteLocation);
				url = uri.toString();
			} catch (Exception e) {
				throw new RuntimeException("Parametro solr-synclocaldata-url non correttamente configurato.");
			}
		}
		try {
			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString();
			uri = appendUri(url, "ts=" + randomUUIDString);
			url = uri.toString();
		}
		catch(Exception e){
			throw new RuntimeException("Errore generazione timestamp url sincronizzazione.");
		}


		int responseCode = -1;
		String responseText = "";
		try {
			responseText = utils.sendGet(url);
		}catch(Exception exc){
			throw new RuntimeException(exc);
		}

		return responseText;
	}

	public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
		URI oldUri = new URI(uri);

		String newQuery = oldUri.getQuery();
		if (newQuery == null) {
			newQuery = appendQuery;
		} else {
			newQuery += "&" + appendQuery;
		}

		URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
				oldUri.getPath(), newQuery, oldUri.getFragment());

		return newUri;
	}

	public static Map<String, String> getSedi() throws DocerApiException {

		//String sedePrefix = String.format("%s.", prefix);

		Map<String, String> sedi = new HashMap<String, String>();

		//Enumeration e = properties.propertyNames();
		//while (e.hasMoreElements()) {
		//	String key = (String) e.nextElement();

		//	if( key.startsWith(sedePrefix) ) {
				String sede = ToolkitConnector.getSedeLocale();  //key.replace(sedePrefix, "");
				sedi.put(sede.toLowerCase(),sede.toUpperCase() );
		//	}
		//}

		return sedi;

	}

	public static ModelAndView reloadFolderModelAndView(ICIFSObject folder, ModelAndView model, HttpServletRequest request) throws DocerApiException, FileNotFoundException, SolrServerException {

		String token = CallDocumentMgt.getToken();
		SolrPathInterface solrInterface = new SolrPathInterface();

		folder.setProperty("location", ToolkitConnector.extractSedeFromToken(token));
		folder = solrInterface.reopenObject(token, folder);

		String remoteloc = folder.getProperty("location");
		//UtilsFTL.syncLocalData(request, remoteloc);

		model.setViewName("redirect:loadContentTreeView?p=" + URLHelper.encodeCIFSPath(folder.getVirtualPath()));

		return model;

	}
	// HTTP GET request
	public String sendGet(String url) throws Exception {

		logger.info("Sending 'GET' request to URL : " + url);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "KDM AppDoc");

		int responseCode = con.getResponseCode();
		logger.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String responseText = response.toString();

		//print result
		logger.info(responseText);


		return responseText;

	}


	public static Documento creaDocumentoSenzaFile(HttpServletRequest request,
												   String token,
												   ICIFSObject parent,
												   String tipo_componente,
												   String docname) throws Exception {

		// metadati di archiviazione
		String defType = ToolkitConnector.getGlobalProperty("defaultDocType");
		String docType = UtilsFTL.getProperty(request, "TYPE_ID", defType );
		String tipo_richiesta = request.getParameter("TIPO_RICHIESTA");
		String ente = parent.getProperty("COD_ENTE");
		String aoo = parent.getProperty("COD_AOO");
		String cod_uo = request.getParameter("UO");
		String stato_archivistico = UtilsFTL.getProperty(request, "STATO_ARCHIVISTICO", "0");

//		String descrizione = request.getParameter("descrizione");

		String pathCartella = parent.getFullPath();

		Documento doc = new Documento();
		doc.setEnte(ente);
		doc.setAoo(aoo);
		doc.setProperty("COD_UO", cod_uo);
		doc.setTipoComponente(tipo_componente);
		doc.setDocName(docname);
		doc.setDocType(docType);
		doc.setStatoArchivistico(stato_archivistico);
//		doc.setDescrizione(descrizione);

		doc.setProperty("ARCHIVE_TYPE", "PAPER");
		doc.setProperty("SENZA_FILE", "true");
		doc.setProperty("NO_FILE", "true");
		Documento documento = DocerService.creaDocumento(token, doc, null);

		return documento;
	}

	public static List<String> parseSolrIdsToDocnums(String solrIds){

		List<String> list = new ArrayList<String>();
		String regex ="!([0-9]+)\\@documento";


		if(solrIds!=null) {
			Matcher m2 = Pattern.compile(regex)
					.matcher(solrIds);
			while (m2.find()) {
				list.add(m2.group(1));
			}
		}
		return list;
	}

	public static ArrayList<DocumentoFirmatario> getDocumentoFirmatari(String docNum, boolean verificaCertificati, String dataVerifica) throws Exception {
		SOLRClient client = new SOLRClient();
		String token = Session.getUserInfo().getJwtToken();
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("q","+type:related +related:*!"+docNum+"@documento");
		solrParams.set("fl","related");

		Collection<Object> related = new ArrayList<>();
		SOLRResponse rsp = client.select(token, solrParams);
		if (rsp.getResults().getNumFound()>0) {
			related = rsp.getResults().get(0).getFieldValues("related");
			solrParams.clear();
			solrParams.set("q","+id:("+ StringUtils.join(related, " ")+") ");
			solrParams.set("fl","id,DOCNAME,FIRMATARIO,DOCNUM,created_on");
		} else {
			solrParams.clear();
			solrParams.set("q", "+DOCNUM:" + docNum);
			solrParams.set("fl","id,DOCNAME,FIRMATARIO,DOCNUM,created_on");
		}

		//DOCNUM:(886879 886880) and FIRMATARIO:*Firmatario*
		ArrayList<DocumentoFirmatario> list = new ArrayList<>();
		rsp = client.select(token,solrParams);
		for(SolrDocument tmp:rsp.getResults()){

			String docName = (String)tmp.getFieldValue("DOCNAME");
			DocumentoFirmatario documentoFirmatario = new DocumentoFirmatario();
			documentoFirmatario.setDocname(docName);
			String firmatario = (String)tmp.getFieldValue("FIRMATARIO");
			if(!Strings.isNullOrEmpty(firmatario) && firmatario.contains("Firmatario")){
				Documento documento = new Documento();
				documento.setProperty("FIRMATARIO", firmatario);
				List<Corrispondente> corrispondenteList;
				String docNumItem = (String)tmp.getFieldValue("DOCNUM");
				if (verificaCertificati==true) {
					Date date = null;
					if (!Strings.isNullOrEmpty(dataVerifica))
						date = new SimpleDateFormat("yyyy-MM-dd").parse(dataVerifica);

					corrispondenteList = new ArrayList<>();
					List<PersonaFisica> personeFisiche = DocerService.getFirmatari(token, docNumItem, date);
					for (PersonaFisica p : personeFisiche)
						corrispondenteList.add(p);
				} else {
					corrispondenteList = documento.getFirmatari();
				}


				for(Corrispondente tmp1 : corrispondenteList){
					FirmatarioPojo firmatarioPojo = new FirmatarioPojo();
					firmatarioPojo.setCodiceFiscale(tmp1.properties.get("codicefiscale"));
					firmatarioPojo.setCodiceEsito(tmp1.getCodiceEsito());
					firmatarioPojo.setConformita(tmp1.getConformita());
					firmatarioPojo.setControlloCatenaTrusted(tmp1.getControlloCatenaTrusted());
					firmatarioPojo.setControlloCertificato(tmp1.getControlloCertificato());
					firmatarioPojo.setControlloCrittografico(tmp1.getControlloCrittografico());
					firmatarioPojo.setControlloCRL(tmp1.getControlloCRL());
					firmatarioPojo.setDataFirma(tmp1.getDataFirma());
					firmatarioPojo.setDenominazione(tmp1.getDenominazione());
					documentoFirmatario.getFirmatarioPojoList().add(firmatarioPojo);
				}

			}
			list.add(documentoFirmatario);
		}
		return list;
	}

	public String processImports(String template, String tempRoot)  throws IOException {

		String processed=template;
		Matcher matcher = getMatcher(processed,"<#import \"([^\"]+)\" +as +([a-z0-9]+)[^>]*>");
		while (matcher.find())
		{
			String directive = matcher.group(1);
			String alias = matcher.group(2);

			String macroftl = "";
			String p = tempRoot + directive;
			if(this.ftlFiles.contains(p)){
				InputStream is = sc.getResourceAsStream(p);
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				macroftl = writer.toString();

				if(macroftl!="") {

					Matcher matcher2 = getMatcher(processed,"<@ "+alias+"\\.([a-z]+)[^>]*>");
					while (matcher2.find()) {

						String fragmentPos = matcher.group(0);
						String fragmentId = matcher.group(1);

						Matcher matcher3 = getMatcher(macroftl,"<#macro "+ fragmentId +">(\\\\w+)<\\/#macro>");
						if(matcher3.find()) {
							String fragment = matcher.group(0);
							processed.replaceAll(fragmentPos, fragment);
						}
					}

					processed = processed.replaceAll(directive, macroftl);
					processed.replaceAll("<@" + alias + ".", "<@");
				}

			}

		}
		return processed;
	}

	private Matcher getMatcher(String text, String regex){

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		return pattern.matcher(text);
	}

	public static ModelAndView addWebDavObjects(ModelAndView model){

		String webdavurl = ToolkitConnector.getGlobalProperty("webdav.client.share");
		if (webdavurl != null) {
			model.addObject("webdavurl", webdavurl);

			String webdavroot = ToolkitConnector.getGlobalProperty("webdav.server.root");
			webdavroot= webdavroot!=null ? webdavroot : "";
			model.addObject("webdavroot", webdavroot);

			String webdavtypes = ToolkitConnector.getGlobalProperty("win-activex-file-types");
			webdavtypes= webdavtypes!=null ? webdavtypes : "";
			model.addObject("webdavtypes", webdavtypes);
		}

		String texttypes = ToolkitConnector.getGlobalProperty("txt-browser-file-types");
		texttypes= texttypes!=null ? texttypes : "";
		model.addObject("texttypes", texttypes);

		return model;
	}

    public static String processTemplate(String template, Map buffer, MessageSource messageSource) throws IOException, TemplateException {
        UserInfo ui = Session.getUserInfoNoExc();
        return processTemplate(ui.getCodEnte(),ui.getCodAoo(),template,buffer,null,messageSource);
    }

    public static String processTemplate(String ente, String aoo, String template, Map buffer, HttpServletRequest request, MessageSource messageSource) throws IOException, TemplateException {

	    if (template.length()>3 && template.substring(template.length()-4).equalsIgnoreCase(".ftl"))
	        template = new UtilsFTL().getDynamicFTL(template);

	    if (request==null)
            request = Session.getRequest();

        Configuration cfg = new Configuration();

        MacroTemplatesLoader mtl = new MacroTemplatesLoader(request.getSession().getServletContext(), ente, aoo);
        cfg.setTemplateLoader(mtl);

        String ftl = render(template, buffer, cfg);

        if (messageSource!=null) {
            ftl = KDMUtils.translateMessages(ftl, null, messageSource, request.getLocale());
        }

        return ftl;
    }

    public static String render( String template, Map<String, ?> buffer, Configuration cfg ) throws TemplateException, IOException {
        Writer out = new StringWriter();
        Template t = new Template("temp", new StringReader(template), cfg);
        t.process(buffer, out);
        return out.toString();
    }

}
