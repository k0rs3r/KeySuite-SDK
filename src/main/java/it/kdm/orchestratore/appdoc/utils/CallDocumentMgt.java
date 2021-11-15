package it.kdm.orchestratore.appdoc.utils;

import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.services.DocerService;
import it.kdm.doctoolkit.services.SolrPathInterface;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.orchestratore.session.Session;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.util.*;

public class CallDocumentMgt {

    @Autowired
    private MessageSource messageSource;

//     private final static String appName = "AppDoc";
	private static final Logger logger = LoggerFactory.getLogger(CallDocumentMgt.class);


    public static Documento refreshDocumentObject(String token, String docNum) throws DocerApiException, IOException {
        SolrPathInterface solrInterface = new SolrPathInterface();
//        Documento doc = DocerService.recuperaProfiloDocumento(CallDocumentMgt.getToken(), docNum);
        Documento doc = null;
        try {
            doc = solrInterface.openByDocnum(CallDocumentMgt.getToken(), docNum);
        } catch (SolrServerException e) {
            throw new DocerApiException(e);
        }
//        PathInterface pathInterface = new PathInterface(CallDocumentMgt.getToken(), CallDocumentMgt.getEnteCorrente());
//        pathInterface.buildPath(doc);

        return doc;
    }

    public static String getToken() throws DocerApiException {
        return Session.getUserInfo().getJwtToken();
    }
    
    
    public static String getToken(String sede) throws DocerApiException {
    	return getToken();
	}

    public static String getAooCode(String aooDesc) throws DocerApiException {
        return Session.getUserInfoNoExc().getCodAoo();
    }

	public static String getEnteCorrente()throws DocerApiException {
		String result = null;
		try {
			 result = Session.getUserInfo().getEnte().getCod();
		 }
		 catch(Exception e){
			 throw new DocerApiException(e.getMessage(),500);
		 }
		
		return result;
	}
	
	public static String getAOOCorrente() throws DocerApiException{
		String result = null;
		try {
			 result = Session.getUserInfo().getCurrentAoo().getCod();
		 }
		 catch(Exception e){
			 throw new DocerApiException(e.getMessage(),500);
		 }
		return result;
	}
    public static String getAooCorrente() throws DocerApiException{
        String result = null;
        try {
            result = Session.getUserInfo().getCurrentAoo().getCod();
        }
        catch(Exception e){
            throw new DocerApiException(e.getMessage(),500);
        }
        return result;
    }
	public static String getUtenteCorrente()throws DocerApiException {
		String result = null;
		try {
			 result = Session.getUserInfo().getUsername();
		 }
		 catch(Exception e){
			 throw new DocerApiException(e.getMessage(),500);
		 }
		return result;
	}

	public static String getEmailUtenteCorrente()throws DocerApiException {
		String result = null;
		try {
			 result = Session.getUserInfo().getEmail();
		 }
		 catch(Exception e){
			 throw new DocerApiException(e.getMessage(),500);
		 }
		return result;
	}

	/*public static HashMap<String,HashMap<String,String>> getUO()throws DocerApiException {
		HashMap<String,HashMap<String,String>> result = null;
		try {
			 result = Session.getUserInfo().getStructureGroup();
		 }
		 catch(Exception e){
			 throw new DocerApiException(e.getMessage(),500);
		 }
		return result;
	}*/

    public static List<HashMap<String,String>> parseExtendedACL(String aclExt) throws  Exception{
        String[] aclParts = aclExt.split(";");

        List<HashMap<String,String>> aclList = new ArrayList<HashMap<String,String>>();
//        PathInterface pi = new PathInterface(CallDocumentMgt.getToken(), CallDocumentMgt.getEnteCorrente());

        if (aclParts.length==0)
            return aclList;

        for (String part : aclParts) {
            if (part.equals(""))
                continue;

            String[] keyvalue = part.split(":");
            String rights = keyvalue[1];

            String userGroup = keyvalue[0].substring(1);

            HashMap<String,String> objAcl = new HashMap<String,String>();
            objAcl.put("userGroup", userGroup);
            objAcl.put("diritti",rights);

            aclList.add(objAcl);

        }

        return aclList;

    }

    public final static String opt_document_types = "optDocTypes";
    public final static String opt_attachment_types = "optAttTypes";

    public static Map<String, String> getOptionalTypes(String token, String ente, String aoo) throws Exception {

        Map<String, String> tipi = new HashMap<String, String>();
        tipi = getOptionalDocumentTypes(CallDocumentMgt.getToken(), ente, aoo);

        Map<String, String> tipi_allegato = getOptionalAttachmentTypes("NON_DICHIARATO", ente, aoo);
        for (String typeId : tipi_allegato.keySet())
            tipi.put(typeId, tipi_allegato.get(typeId));

        return tipi;
    }

    public static Map<String, String> getOptionalDocumentTypes(String token, String ente, String aoo) throws Exception {

        Map<String, String> tipi_principale = new HashMap<String, String>();

        Map<String, String> tipi_documento = DocerService.recuperaTipiDocumentoPerComponente(CallDocumentMgt.getToken(), ente, aoo, "PRINCIPALE");

        String typesList = ToolkitConnector.getGlobalProperty("optionalDocTypes");

//			if (typesList == null) {
//				String errmsg = "Configurazione dei tipi principale disponibili non trovata";
//				logger.error("editUD error:" + errmsg);
//				throw new RuntimeException(errmsg);
//			}

        if (typesList != null) {
            for (String typeId : typesList.split(",")) {
                if (tipi_documento.containsKey(typeId)) {
                    tipi_principale.put(typeId, tipi_documento.get(typeId));
                }
            }
        }
        return tipi_principale;
    }

    public static Map<String, String> getOptionalAttachmentTypes(Documento currDoc, Locale locale) throws Exception {
        String codEnte = currDoc.getEnte();
        String codAoo = currDoc.getAoo();
        String tipo_principale = currDoc.getType();

        return getOptionalAttachmentTypes(tipo_principale, codEnte, codAoo);
    }

    public static Map<String, String> getOptionalAttachmentTypes(String tipo_principale, String codEnte, String codAoo) throws Exception {

        Map<String, String> tipi_documento = new HashMap<String, String>();

        try {
            tipi_documento = DocerService.recuperaTipiDocumentoPerComponente(CallDocumentMgt.getToken(), codEnte, codAoo, "ALLEGATO");

        } catch (DocerApiException e) {
            throw e;
        }

        Map<String, String> tipi_allegato = new HashMap<String, String>();
        String typesList = ToolkitConnector.getGlobalProperty(String.format("optionalAttTypes.%s", tipo_principale));
        typesList = (typesList != null ? typesList : ToolkitConnector.getGlobalProperty("optionalAttTypes"));

        if (typesList != null) {

            for (String typeId : typesList.split(",")) {
                if (tipi_documento.containsKey(typeId)) {
                    tipi_allegato.put(typeId, tipi_documento.get(typeId));
                }
            }
        } else {
            tipi_allegato = tipi_documento;
        }

        return tipi_allegato;
    }

    public static Map<String, String> getDocumentsTypesMap(String filter) throws Exception {

        Map<String, String> map = new HashMap<String, String>();

        String ente = Session.getUserInfo().getEnte().getCod();
        String aoo = Session.getUserInfo().getCurrentAoo().getCod();

        String token = CallDocumentMgt.getToken();

        Map<String, String> cachedMap = CacheTypes.get(ente, aoo, filter);

        if (cachedMap != null)
            return cachedMap;

        if (filter != null && !"".equals(filter)) {

            if (opt_document_types.equals(filter)) {
                map = getOptionalDocumentTypes(token, ente, aoo);
            } else if (filter.startsWith(opt_attachment_types + ".")) {

                String tipo_principale = filter.split("\\.")[1];

                map = getOptionalAttachmentTypes(tipo_principale, ente, aoo);
            }

        } else {
            map = getOptionalTypes(CallDocumentMgt.getToken(), ente, aoo);

        }

        CacheTypes.put(map, ente, aoo, filter);

        return map;

    }
/*
    public static Documento setExtendedACL(Documento doc, List<Acl> list) throws Exception {
        String aclExt = buildExtendedACL(list);
        doc.setProperty(Security.EXTENDED_ACL_DOCUMENTO_META_NAME,aclExt);

        return doc;
    }
    public static String buildExtendedACL(List<Acl> list) throws Exception{
        String aclExt = "";
        //finalizza le acl per recuperare il type (user o group)
        List<HashMap<String,String>> aclFinalized = CallDocumentMgt.finalizeAcl(list);

        for (HashMap<String,String> a : aclFinalized) {
            String type = String.valueOf(a.get("type").charAt(0));
            String id = a.get("userGroup");
            String rights = a.get("diritti");
            aclExt+=type+id+":"+rights+";";
        }

        return aclExt;
    }
    */

}
