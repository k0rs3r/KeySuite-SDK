package it.kdm.orchestratore.appdoc.utils;

import it.kdm.doctoolkit.model.Acl;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.model.GenericCriteria;
import it.kdm.doctoolkit.services.SOLRClient;
import it.kdm.doctoolkit.services.SolrPathInterface;
import it.kdm.doctoolkit.utils.SOLRResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsACL
{
    public static final String SolrIdPattern = "([^\\!]+)\\.([^\\!]+)\\!([^\\!]+)\\!([^\\!]+)\\@(.+)";
	public static final String ObjKeyPattern = "([^\\|\\$]+)\\$?([^\\|]+)?\\|?([^\\|]+)?\\|?(.+)?";
    private static final String MSG_NOT_VALID_ID = "Oggetto con id non valido, impossibile recuperare la ACL";

	private static final Logger logger = LoggerFactory.getLogger(UtilsACL.class);

//	//contains the servletcontext path
//	private String ftlPath;
//
//	//contains all files in servlet context
//	private Set<String> ftlFiles;
//	private ServletContext sc;



	@SuppressWarnings("unchecked")
	public UtilsACL(/*String ftlPath, ServletContext sc*/) {
//		this.ftlFiles = sc.getResourcePaths(ftlPath);
//		this.ftlPath =ftlPath;
//		this.sc=sc;
	}

	public String getAOO(String nodeId) throws Exception{


		Pattern p0 = Pattern.compile(this.SolrIdPattern);
		Matcher m0 = p0.matcher(nodeId);

		if (!m0.matches())
			throw new Exception("Oggetto con id non valido, impossibile recuperare la ACL");

		return  String.valueOf(m0.group(3));
	}

	public String getSede(String nodeId) throws Exception{


		Pattern p0 = Pattern.compile(this.SolrIdPattern);
		Matcher m0 = p0.matcher(nodeId);

		if (!m0.matches())
			throw new Exception("Oggetto con id non valido, impossibile recuperare la ACL");

		return  String.valueOf(m0.group(1));

	}

	public String getObjKey(String nodeId) throws Exception{

		Pattern p0 = Pattern.compile(this.SolrIdPattern);
		Matcher m0 = p0.matcher(nodeId);

		if (!m0.matches())
			throw new Exception("Oggetto con id non valido, impossibile recuperare la ACL");

		return   String.valueOf(m0.group(4));

	}
    public boolean isValidNode(String nodeId){

        Pattern p0 = Pattern.compile(SolrIdPattern);
        Matcher m0 = p0.matcher(nodeId);

        return m0.matches();
    }

    public List<Acl> getACLNode( String nodeId) throws Exception {

        if (!isValidNode(nodeId))
            throw new Exception(MSG_NOT_VALID_ID);

        SolrPathInterface SPI = new SolrPathInterface();
        List<Acl> aclList = SPI.getAclExplicitBySolrId(CallDocumentMgt.getToken(), nodeId);

        return aclList;
    }

	public String getSolrAttr( String nodeId, String field) throws Exception {

		SOLRClient client = new SOLRClient();

		HashMap<String, String> params = new HashMap<>();
		params.put("fl", field);
		params.put("id", nodeId);

		GenericCriteria gc = new GenericCriteria();
		if (gc.properties.containsKey("ENABLED"))
			gc.properties.remove("ENABLED");

		gc.setRawProperty("id", nodeId);

		SOLRResponse rsp = client.rawSolrSearch(CallDocumentMgt.getToken(), gc, params, true);

		List<Documento> docList = rsp.getResults(Documento.class);

		for (int i = 0; i < docList.size(); i++) {

			Documento doc = docList.get(i);

			return doc.getProperty(field);
		}

		return null;
	}
//	public List<Acl> getACLNode( String nodeId, String fl) throws Exception
//	{
//        if (!isValidNode(nodeId))
//            throw new Exception(MSG_NOT_VALID_ID);
//
//		SOLRClient client = new SOLRClient();
//
//		HashMap<String, String> params = new HashMap<>();
//		params.put("fl",fl);
//		params.put("id", nodeId);
//
//		GenericCriteria gc = new GenericCriteria();
//		if(gc.properties.containsKey("ENABLED"))
//			gc.properties.remove("ENABLED");
//
//		gc.setRawProperty("id", nodeId);
//
//		SOLRResponse rsp = client.rawSolrSearch(CallDocumentMgt.getToken(), gc, params, true);
//
//		List<Documento> docList = rsp.getResults(Documento.class);
//
//		List<Acl> aclList = new ArrayList<Acl>();
//
//		//costruisco la list di acl
//		for (int i = 0; i < docList.size(); i++) {
//
//			Documento doc = docList.get(i);
//
//			Object acl_rights = doc.getProperty("[acl_rights]");
//			String strLst = acl_rights!=null? acl_rights.toString() : "";
//
//
//			for ( String tokn : strLst.split(",")){
//
//				Pattern p = Pattern.compile("\\{([^\\@]+)\\@user\\=([^\\}]+)\\}"); //es. "admin@user:12345" "STUDIO_PIROLA!gdeseta@user:FullAccess"
//				Matcher m = p.matcher(tokn);
//
//				if(! m.find())
//					continue;
//
//				String user = m.group(1)!=null? m.group(1): tokn;
//
//
//				if(user.length()==36){
//
//					Pattern p2 = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"); //es. "4411077c-92cc-4400-8372-482c2f10246e"
//					Matcher m2 = p.matcher(tokn);
//
//					if(m2.matches())
//						continue;
//				}
//
//
//				String rights = m.group(2)!=null? m.group(2): "";
//
//				rights = rights.equals("32767")? "0": //FullAccess
//						(rights.equals("NormalAccess")? "1":
//								(rights.equals("ReadOnlyAccess")? "2":
//										(rights.equals("ViewProfileAccess")? "3":
//												(rights.equals("Contributor")? "5":  "2"))));
//
//				Acl acl = new Acl();
//				acl.setUtenteGruppo(user);
//				acl.setDiritti(rights);
//				aclList.add(acl);
//			}
//
////			if (doc.properties.containsKey("[acl_inherited]")) {
////				Object acl_inherited = doc.getProperty("[acl_inherited]");
////				String strLst2 = acl_inherited != null ? acl_inherited.toString() : "";
////				strLst2 = "";
////			}
//		}
//
//		return aclList;
//	}
}
