package it.kdm.orchestratore.appdoc.utils.controller;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.SolrPathInterface;
import it.kdm.orchestratore.appdoc.model.DocActionItem;
import it.kdm.orchestratore.appdoc.utils.CallDocumentMgt;
import it.kdm.orchestratore.appdoc.utils.KDMDateUtil;
import it.kdm.orchestratore.security.Security;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

public class ControllerDocOperationsUtil
{


	//private static final String ENTE_PLACE_HOLDER = "%ENTE%";
	
	//private static final String AOO_PLACE_HOLDER = "%AOO%";
	
	//private static final String ALFRESCO_FOLDER_PREFIX = "/app:company_home/cm:DOCAREA/cm:" + ENTE_PLACE_HOLDER + "/cm:" + AOO_PLACE_HOLDER + "/cm:DOCUMENTI";
	
	/*public static Folder assembleFromCartellaToFolder(Cartella cartella, String parentId)
	{
		String folderName = formatFolderName(cartella.getNome());
		
		Folder folder = new Folder();
		
		folder.setLabel(folderName);
		
		folder.setId(cartella.getID());
		
		folder.setLoad_on_demand(true);
		
		folder.setParentId(parentId);
		
		return folder;
	}*/
	
	/*public static String formatFolderName(String folderName)
	{
		return folderName;
	}*/
	
	/*public static List<Cartella> searchFoldersByParentId(String accessType, String nodeId) throws Exception
	{
		Cartella parametri_ricerca = new Cartella();
		
		parametri_ricerca.setEnte(CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setAoo(CallDocumentMgt.getAOOCorrente());
		
		if (accessType.equals("owner")) {
			parametri_ricerca.setProprietario(CallDocumentMgt.getUtenteCorrente());
		}
		else {
			parametri_ricerca.setProprietario("");
		}
		
		if (nodeId != null && !nodeId.equals("<node-id>")) {
			parametri_ricerca.setCartellaSuperiore(nodeId);
		}
		else {
			parametri_ricerca.setCartellaSuperiore("");
		}
		
		List<Cartella> results = DocerService.ricercaCartella(CallDocumentMgt.getToken(), parametri_ricerca);
		
		return results;
	}*/

    /*public static Documento creaUnitàDocumentaria(Documento documentoPrincipale , List<Documento> allegati , String folderId,String token,Fascicolo fascicolo) throws DocerApiException{
        UnitaDocumentaria unitDoc = DocerService.creaUnitaDocumentaria(token, documentoPrincipale, allegati, null, null, null);
        if(folderId != null)
            DocerService.aggiungiDocumentiACartella(token, folderId, unitDoc.DocumentoPrincipale.getDocNum());
        else if(fascicolo != null)
            ServizioFascicolazione.fascicolaUnitaDocumentaria(token, unitDoc, fascicolo);



        return unitDoc.DocumentoPrincipale;
    }*/

    /*public static String creaUnitàDocumentaria_OLD(Documento documentoPrincipale , List<Documento> allegati , String folderId,String token,Fascicolo fascicolo) throws DocerApiException{
		
		HashMap<String, String> renameHash = new HashMap<String, String>();
		UnitaDocumentaria unitDoc = null;
		//CREA CORRELATI
		if(documentoPrincipale != null){
			String guid = UUID.randomUUID().toString() + "." + documentoPrincipale.getExtension();
			String docName = documentoPrincipale.getDocName();
			renameHash.put(guid, docName);
			documentoPrincipale.setDocName(guid);
			List<Documento> allegatiNew = new ArrayList<Documento>();
			for (Documento documento : allegati) {
				String guidAllegato = UUID.randomUUID().toString() + "." + documento.getExtension();
				String docNameAllegato = documento.getDocName();
				renameHash.put(guidAllegato, docNameAllegato);
				documento.setDocName(guidAllegato);
				documento.setHidden(true);
				allegatiNew.add(documento);
			}
			if (allegatiNew.size() == 0){
				//PRINCIPALE senza allegati
				documentoPrincipale.setHidden(true);
				unitDoc = DocerService.creaUnitaDocumentaria(token, documentoPrincipale, null, null, null, null);
				if(folderId != null)
					DocerService.aggiungiDocumentiACartella(token, folderId, unitDoc.DocumentoPrincipale.getDocNum());
				else if(fascicolo != null)
					ServizioFascicolazione.fascicolaUnitaDocumentaria(token, unitDoc, fascicolo);
				Documento docToUpdate = new Documento();
				docToUpdate.setDocNum(unitDoc.DocumentoPrincipale.getDocNum());
				docToUpdate.setDocName(docName);
				docToUpdate.setHidden(false);
				documentoPrincipale = DocerService.aggiornaDocumento(token, docToUpdate);
				unitDoc = DocerService.recuperaUnitaDocumentaria(token, documentoPrincipale.getDocNum());
			}
			else{
				//PRINCIPALE
				documentoPrincipale.setHidden(true);
				unitDoc = DocerService.creaUnitaDocumentaria(token, documentoPrincipale, allegatiNew, null, null, null);
				if(folderId != null)
					DocerService.aggiungiDocumentiACartella(token, folderId, unitDoc.DocumentoPrincipale.getDocNum());
				else if(fascicolo != null)
					ServizioFascicolazione.fascicolaUnitaDocumentaria(token, unitDoc, fascicolo);
				Documento docToUpdate = new Documento();
				docToUpdate.properties.clear();
				docToUpdate.setDocNum(unitDoc.DocumentoPrincipale.getDocNum());
				docToUpdate.setDocName(docName);
				docToUpdate.setHidden(false);
				documentoPrincipale = DocerService.aggiornaDocumento(token, docToUpdate);
				for (Documento documento : unitDoc.getAllegati()) {
				//ALLEGATI 
					if(folderId != null)
						DocerService.aggiungiDocumentiACartella(token, folderId, documento.getDocNum());
					docToUpdate = new Documento();
					docToUpdate.properties.clear();
					docToUpdate.setDocNum(documento.getDocNum());
					docToUpdate.setDocName(renameHash.get(documento.getDocName()));
					docToUpdate.setHidden(false);
					DocerService.aggiornaDocumento(token, docToUpdate);
				}
				unitDoc = DocerService.recuperaUnitaDocumentaria(token, documentoPrincipale.getDocNum());
			}
			
		}
		//CREA SCORRELATI
		else{
			for (Documento documento : allegati) {
				documento.setHidden(true);
				String guidAllegato = UUID.randomUUID().toString() + "." + documento.getExtension();
				String docNameAllegato = documento.getDocName();
				renameHash.put(guidAllegato, docNameAllegato);
				documento.setDocName(guidAllegato);
				unitDoc = DocerService.creaUnitaDocumentaria(token, documento, null, null, null, null);
				if(folderId != null)
					DocerService.aggiungiDocumentiACartella(token, folderId, unitDoc.getDocumentoPrincipale().getDocNum());
				else if(fascicolo != null)
					ServizioFascicolazione.fascicolaUnitaDocumentaria(token, unitDoc, fascicolo);
				Documento docToUpdate = new Documento();
				docToUpdate.properties.clear();
				docToUpdate.setDocNum(unitDoc.getDocumentoPrincipale().getDocNum());
				docToUpdate.setDocName(renameHash.get(guidAllegato));
				docToUpdate.setHidden(false);
				documento = DocerService.aggiornaDocumento(token, docToUpdate);
				unitDoc = DocerService.recuperaUnitaDocumentaria(token, documento.getDocNum());
			}
			
		}
		return unitDoc.DocumentoPrincipale.getDocNum();
	}*/
	/*public static Cartella searchFolderById(String folderId) throws DocerApiException
	{
		Cartella parametri_ricerca = new Cartella();
		
		parametri_ricerca.setEnte(CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setAoo(CallDocumentMgt.getAOOCorrente());
		parametri_ricerca.setID(folderId);
		
		List<Cartella> results = DocerService.ricercaCartella(CallDocumentMgt.getToken(), parametri_ricerca);
		
		Cartella folder = results.iterator().next();
		
		return folder;
	}*/
	
	/*public static List<Cartella> searchDemoClienti(String name) throws Exception
	{
		Cartella parametri_ricerca = new Cartella();
		
		parametri_ricerca.setEnte(CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setAoo(CallDocumentMgt.getAOOCorrente());
		
		parametri_ricerca.setProprietario("");
		
		parametri_ricerca.setCartellaSuperiore("1765");
		
		parametri_ricerca.setNome(name);
		
		List<Cartella> results = DocerService.ricercaCartella(CallDocumentMgt.getToken(), parametri_ricerca);
		
		return results;
	}*/
	
	/*public static String[] tokenizeFolderPathName(String folderId) throws DocerApiException, UnsupportedEncodingException
	{
		Cartella folder = searchFolderById(folderId);
		
		String folderPathName = folder.getProperty("FOLDER_PATH");
		
		String[] tokenizedPathName = transformPathName(folderPathName);
		
		return tokenizedPathName;
	}*/
	
	/*private static String[] transformPathName(String folderPathName) throws UnsupportedEncodingException
	{
		String[] splittedPathName = folderPathName.split("/");
		
		String[] tokenizedPathName = Arrays.copyOfRange(splittedPathName, 6, splittedPathName.length);
		
		for (int i = 0; i < tokenizedPathName.length; i++) {
			tokenizedPathName[i] = tokenizedPathName[i].trim().replace("cm:", "");
		}
		
		return tokenizedPathName;
	}*/
	
	/*public static ContextElement[] tokenizeFolderPathNameToContextItems(String folderId) throws DocerApiException, UnsupportedEncodingException
	{
		Cartella folder = searchFolderById(folderId);
		
		String folderPathName = folder.getProperty("FOLDER_PATH");
		
		ContextElement[] tokenizedPathName = transformPathNameToContextItems(folderPathName);
		
		return tokenizedPathName;
	}*/
	
	/*private static ContextElement[] transformPathNameToContextItems(String folderPathName) throws UnsupportedEncodingException
	{
		String[] splittedPathName = folderPathName.split("/");
		
		String[] tokenizedPathName = Arrays.copyOfRange(splittedPathName, 6, splittedPathName.length);
		
		ContextElement[] results = new ContextElement[tokenizedPathName.length];
		
		for (int i = 0; i < tokenizedPathName.length; i++) {
			results[i] = new ContextElement(null, null, tokenizedPathName[i].trim().replace("cm:", ""), null);
		}
		
		return results;
	}*/
	
	/*public static <T> Object convertFromJsonStringToGeneric(String jsonString, Class<T> valueType) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		Object jsonObject = null;
		
		if (mapper.canSerialize(valueType)) {
			jsonObject = mapper.readValue(jsonString, valueType);
		}
		
		return jsonObject;
	}*/
	
	/*public static List<AnagraficaCustom> searchCustomRegistry(String field, String anagrafica, String prefix, String query) throws Exception
	{
		AnagraficaCustom parametri_ricerca = new AnagraficaCustom();
		
		parametri_ricerca.setEnte(CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setAoo(CallDocumentMgt.getAOOCorrente());
		parametri_ricerca.setProperty(prefix + field, query);
		
		List<AnagraficaCustom> anagraficaCustomList = DocerService.ricercaAnagraficaCustom(CallDocumentMgt.getToken(), anagrafica, parametri_ricerca);
		
		return anagraficaCustomList;
	}*/
	
	/*public static Feed assembleFromAnagraficaCustomToFeed(AnagraficaCustom anagraficaCustom, String fieldParam, String nomeAnagrafica)
	{
		Feed feed = new Feed();
		
		feed.setType(nomeAnagrafica);
        feed.setCssClass("anagraficaType-"+feed.getType());
		feed.setId(anagraficaCustom.getProperty("COD_" + fieldParam));
		
		feed.setName(anagraficaCustom.getProperty("DES_" + fieldParam));
		
		// feed.setSuggest(anagraficaCustom.getProperty("DES_" + fieldParam));
		
		return feed;
	}*/


	/*public static SearchUsersResponse searchUsers(String query) throws DocerApiException
	{
		return searchUsers(query,"FULL_NAME");
	}*/
	/*public static SearchUsersResponse searchUsers(String query, String searchInField) throws DocerApiException
	{
		KeyValuePair[] criteria = buildCriteria(searchInField, query);

				SearchUsersResponse searchUsersResponse = DocerService.searchUsers(CallDocumentMgt.getToken(), criteria);
		
		return searchUsersResponse;
	}*/

	/*public static GetGroupsOfUserResponse searchRoles(String userId) throws DocerApiException
	{
		GetGroupsOfUserResponse getGroupsOfUserResponse = DocerService.getGroupsOfUser(CallDocumentMgt.getToken(), userId);

		return getGroupsOfUserResponse;
	}*/

	/*public static SearchGroupsResponse searchGroups(String query) throws DocerApiException
	{
		return searchGroups(query, "GROUP_NAME");
	}*/


	/*public static SearchGroupsResponse searchGroups(String query, String searchInField) throws DocerApiException
	{
		KeyValuePair[] criteria = buildCriteria(searchInField, query);
		
		SearchGroupsResponse searchGroupsResponse = DocerService.searchGroups(CallDocumentMgt.getToken(), criteria);

		return searchGroupsResponse;
	}*/

	/*public static SOLRResponse searchGroups(String query, int limit) throws Exception
	{
		return searchGroups(query, "GROUP_NAME", limit, null);
	}*/

	/*public static SOLRResponse searchGroups(String query, String searchInField, int limit) throws Exception
	{
		return searchGroups(query, searchInField, limit, null);
	}*/

	/*public static SOLRResponse searchGroups(String query, String searchInField, int limit, GenericCriteria criteria)  throws Exception
	{
		SOLRClient client = new SOLRClient();

		HashMap<String, String> params = new HashMap<>();
		params.put("fl", "GROUP_ID,GROUP_NAME");

		GenericCriteria gc = criteria!=null? criteria : new GenericCriteria();
		gc.setRawProperty("type","group");
		gc.setRawProperty(searchInField, query);
		gc.setMaxElementi(limit);
		SOLRResponse solrResponse = client.rawSolrSearch(CallDocumentMgt.getToken(), gc, params, true);

		return solrResponse;
	}*/

	/*public static SOLRResponse searchWorkGroups(String query, boolean gruppo_struttura, int limit) throws Exception {
		return searchTypedGroups(query,"GROUP_NAME", gruppo_struttura, limit);
	}*/

	/*public static SOLRResponse searchTypedGroups(String query, String searchInField, boolean gruppo_struttura, int limit) throws Exception
	{
		GenericCriteria gc = new GenericCriteria();
		gc.setRawProperty("GRUPPO_STRUTTURA", gruppo_struttura? "true":"false");

		SOLRResponse solrResponse = searchGroups(query, searchInField, limit, gc);

		return solrResponse;
	}*/

	
	/*public static KeyValuePair[] buildCriteria(String key, String value)
	{
		KeyValuePair[] criteria = new KeyValuePair[1];
		
		KeyValuePair criterion = new KeyValuePair();
		
		criterion.setKey(key);
		
		criterion.setValue(value);
		
		criteria[0] = criterion;
		
		return criteria;
	}*/

	/*public static KeyValuePair buildCriterion(String key, String value)
	{
		KeyValuePair criterion = new KeyValuePair();

		criterion.setKey(key);
		criterion.setValue(value);

		return criterion;
	}*/

	/*public static Feed assembleFromgRolesSearchItemToFeed(Feed feed, SearchItem item) {

		String userId = feed.getId();

		//feed.setRoles("{\"id\":\"ADMINS_GDL_gruppo3\",\"name\":\"gestione del gruppo di lavoro gruppo3 (ADMINS_GDL_gruppo3)\",\"html\":\"\",\"num\":-1,\"url\":\"\",\"title\":\"\",\"type\":\"GROUP\",\"cssClass\":\"userGroup-GROUP\",\"annoFascicolo\":null,\"progressivoFascicolo\":null,\"classifica\":null,\"descClassifica\":null},{\"id\":\"ADMINS_GDL_gruppodilavoro1\",\"name\":\"Gruppo di amministrazione 1 (ADMINS_GDL_gruppodilavoro1)\",\"html\":\"\",\"num\":-1,\"url\":\"\",\"title\":\"\",\"type\":\"GROUP\",\"cssClass\":\"userGroup-GROUP\",\"annoFascicolo\":null,\"progressivoFascicolo\":null,\"classifica\":null,\"descClassifica\":null}");

 		return feed;

	}
	public static Feed assembleFromUsersSearchItemToFeed(SearchItem item, boolean isUsersGroupsSearch)
	{
		Feed feed = new Feed();

		KeyValuePair[] metadatas = item.getMetadata();

        feed.setType("USER");
        feed.setCssClass("userGroup-"+feed.getType());
        String id = "";

		if (metadatas != null) {
			for (KeyValuePair keyValuePair : metadatas) {
				switch (keyValuePair.getKey())
				{
					case "USER_ID":
						//String text = isUsersGroupsSearch ? keyValuePair.getValue() + "/User" : keyValuePair.getValue();
                        id = keyValuePair.getValue();
						feed.setId(keyValuePair.getValue());
						//feed.setLabel(text);
						// feed.setSuggest(text);
						break;

                    case "FULL_NAME":
                        feed.setName(keyValuePair.getValue());
                        break;


				}
			}

            feed.setName(feed.getName() + " (" + id + ")");
		}

        if ("".equals(feed.getName()))
            feed.setName(feed.getId());

		return feed;
	}*/

	/*public static Feed assembleFromAuthorsSearchItemToFeed(SearchItem item, boolean isUsersGroupsSearch)
	{
		return assembleFromAuthorsSearchItemToFeed(item, isUsersGroupsSearch, "[FULL_NAME] ([USER_ID])");
	}
	public static Feed assembleFromAuthorsSearchItemToFeed(SearchItem item, boolean isUsersGroupsSearch, String format)
	{
		Feed feed = new Feed();
		
		KeyValuePair[] metadatas = item.getMetadata();

        feed.setType("USER");
        feed.setCssClass("userGroup-"+feed.getType());
        String fullName="";
        String userID="";
		if (metadatas != null) {
			for (KeyValuePair keyValuePair : metadatas) {
				switch (keyValuePair.getKey())
				{
					case "FULL_NAME":
						
						fullName=keyValuePair.getValue();
						format= format.replace("[FULL_NAME]", fullName);
						break;

                    case "USER_ID":
                        userID = keyValuePair.getValue();
                        format= format.replace("[USER_ID]", userID);
                        break;

				
				}
			}
		}
		String name=fullName+" ("+userID+")";

		//feed.setId(name);
		feed.setId(format);
		feed.setName(name);
        if ("".equals(feed.getName()))
            feed.setName(feed.getId());

		return feed;
	}
	
	
	public static Feed assembleFromGroupsSearchItemToFeed(SearchItem item, boolean isUsersGroupsSearch)
	{
		Feed feed = new Feed();
		
		KeyValuePair[] metadatas = item.getMetadata();

        feed.setType("GROUP");
        feed.setCssClass("userGroup-"+feed.getType());

        String id = "";
        String name = "";

		if (metadatas != null) {
			for (KeyValuePair keyValuePair : metadatas) {
				switch (keyValuePair.getKey())
				{
					case "GROUP_ID":
						//String text = isUsersGroupsSearch ? keyValuePair.getValue() + "/Group" : keyValuePair.getValue();
						id = keyValuePair.getValue();
						feed.setId(keyValuePair.getValue());
						//feed.setLabel(text);
						// feed.setSuggest(text);
						break;

                    case "GROUP_NAME":
                        name = keyValuePair.getValue();
                        feed.setName(keyValuePair.getValue());
                        break;

				}
			}

            feed.setName(feed.getName() + " (" + id + ")");
		}

        if ("".equals(feed.getName()))
            feed.setName(feed.getId());

        return feed;
	}

	public static Feed assembleFromGroupsSolrItemToFeed(Group group, boolean isUsersGroupsSearch)
	{
		Feed feed = new Feed();

		feed.setType("GROUP");
		feed.setCssClass("userGroup-"+feed.getType());

		String id = "";
		String name = "";

		if (group != null) {
			id=group.getProperty("GROUP_ID");
			name=group.getProperty("GROUP_NAME");

			feed.setId(id);
			feed.setName(name);
			feed.setName(name + " (" +  id + ")");
		}

		if ("".equals(feed.getName()))
			feed.setName(feed.getId());

		return feed;
	}
    public static Feed assembleFromUserRole(Map.Entry roleUser)
    {
        Feed feed = new Feed();
        feed.setType("GROUP");
        feed.setCssClass("userGroup-"+feed.getType());
        String id = "";
        String name = "";
        if(roleUser!=null) {
                feed.setId((String) roleUser.getKey());
                feed.setName((String) roleUser.getValue());

            feed.setName(feed.getName() + " (" + feed.getId() + ")");
        }
        if ("".equals(feed.getName()))
            feed.setName(feed.getId());
        return feed;
    }

	public static Map<String, String> retrieveDocumentTypes(String enteCod, String aooCod) throws DocerApiException
	{
		Map<String, String> documentTypes = DocerService.recuperaTipiDocumento(CallDocumentMgt.getToken(), enteCod, aooCod);
		
		return documentTypes;
	}
	
	public static Feed assembleFromDocTypeNameToFeed(String code, String documentType)
	{
		Feed feed = new Feed();

        feed.setType("DOCTYPE");
        feed.setCssClass("documentType-"+feed.getType());
        feed.setId(code);
		
		feed.setName(documentType);
		
		// feed.setSuggest(documentType);
		
		return feed;
	}

    public static List<Documento> searchDocumentsByParams(DocumentoCriteria searchParameters, List<Ordinamento> orderBy) throws Exception
    {
        List<Documento> searchDocumentList = DocerService.ricercaUnitaDocumentarie(CallDocumentMgt.getToken(), searchParameters, orderBy);

        return searchDocumentList;
    }*/
/*
    public static List<Documento> searchDocumentsByParams(Map<String, String> searchParameters, String query, int limit, List<Ordinamento> orderBy) throws Exception
	{
		
		DocumentoCriteria parametri_ricerca = new DocumentoCriteria();
		if (searchParameters != null) parametri_ricerca.properties.putAll(searchParameters);
		parametri_ricerca.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setProperty("COD_AOO", CallDocumentMgt.getAOOCorrente());

		// parametri_ricerca = setOptionalParameters(parametri_ricerca,
		// searchParameters);
		if(query != null)parametri_ricerca.setKeywords(query);
		
		List<Documento> searchDocumentList = DocerService.ricercaUnitaDocumentarie(CallDocumentMgt.getToken(), parametri_ricerca, orderBy);
			
		return searchDocumentList;
	}
*/
	/*public static Documento setOptionalParameters(Documento parametri_ricerca, Map<String, String> searchParameters)
	{
		if (searchParameters != null) {
			for (String key : searchParameters.keySet()) {
				
				parametri_ricerca.setProperty(key, searchParameters.get(key));
			}
		}
		return parametri_ricerca;
	}
	
	public static List<Documento> searchDocumentsByFolderId(String folderId) throws Exception
	{
		List<Documento> filesList = DocerService.recuperaDocumentiCartella(CallDocumentMgt.getToken(), folderId);
		
		
		return filesList;
	}*/
	
	public static List<Ordinamento> buildOrderByList(String orderBy)
	{
	    if (Strings.isNullOrEmpty(orderBy)){
            return Collections.singletonList(new Ordinamento("DOCNUM", Ordinamento.orderByEnum.ASC ) );
        }else{
            List<Ordinamento> orderByList = new ArrayList<>();

            if (orderBy != null && !"".equals(orderBy)) {
                String[] orderByElems = orderBy.split(",");

                if (orderByElems != null) {
                    for (int i = 0; i < orderByElems.length; i++) {
                        String[] orderByFields = orderByElems[i].split(":");

                        if (orderByFields != null && orderByFields.length == 2) {

                            Ordinamento orderByItem = new Ordinamento();

                            orderByItem.setNomeCampo(orderByFields[0]);

                            orderByItem.setTipo(orderByFields[1].equals("ASC") ? Ordinamento.orderByEnum.ASC : Ordinamento.orderByEnum.DESC);

                            orderByList.add(orderByItem);
                        }
                    }
                }
            }
            return orderByList;
        }
	}
	
	/*public static Folder buildRootFolder(String folderId, String folderLabel)
	{
		Folder folder = new Folder();
		
		folder.setId(folderId);
		
		folder.setLabel(folderLabel);
		
		folder.setParentId("");
		
		folder.setType("R");
		
		folder.setLoad_on_demand(true);
		
		return folder;
	}*/
	
	/*public static Folder assembleFromTitolarioToFolder(Titolario titolario, String nodeId, String parentId)
	{
		Folder folder = new Folder();
		
		folder.setId(nodeId);
		
		folder.setLabel(titolario.getDescrizione());
		
		folder.setParentId(parentId);
		
		folder.setType("T");
		
		folder.setLoad_on_demand(true);
		
		return folder;
		
	}*/
	
	/*public static Folder assembleFromFascicoloToFolder(Fascicolo fascicolo, String nodeId, String parentId)
	{
		Folder folder = new Folder();
		
		folder.setId(nodeId + "_" + fascicolo.getAnno());
		
		folder.setLabel(fascicolo.getDescrizione());
		
		folder.setParentId(parentId);
		
		folder.setType("F");
		
		folder.setAnno(fascicolo.getAnno());
		
		folder.setLoad_on_demand(true);
		
		return folder;
	}*/
	
	/* INIZIO: modifiche aggiunte al 7/01/2014 */
	public static Titolario getFilingPlanByClassifica(String classifica,String piano, String aoo) throws Exception
	{
		TitolarioCriteria parametri_ricerca = new TitolarioCriteria();
		parametri_ricerca.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setProperty("COD_AOO",aoo);
		parametri_ricerca.setProperty("CLASSIFICA" , classifica);

		if (parametri_ricerca.properties.containsKey("ENABLED")) parametri_ricerca.properties.remove("ENABLED");

		if (!Strings.isNullOrEmpty(piano)) {
			parametri_ricerca.setProperty("PIANO_CLASS", piano);
		}
		else {
			parametri_ricerca.setProperty("ENABLED", "true");
		}

		SolrPathInterface solrInterface = new SolrPathInterface();
//		List<Titolario> filingPlanList = DocerService.ricercaTitolari(CallDocumentMgt.getToken(), parametri_ricerca);
		
		Titolario filingPlan = solrInterface.searchObject(CallDocumentMgt.getToken(),parametri_ricerca,Titolario.class);


//		if (!filingPlanList.isEmpty()) {
//			filingPlan = filingPlanList.iterator().next();
//
			String modified = filingPlan.getProperty("MODIFIED");
			
			if (modified != null) {
				filingPlan.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
			}
//		}
		
		return filingPlan;
	}
	
	public static Fascicolo getDossierByIds(String classifica, String progressivo, String anno, String aoo) throws Exception
	{
		
		FascicoloCriteria parametri_ricerca = new FascicoloCriteria();
		
		parametri_ricerca.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setProperty("COD_AOO",aoo);
		parametri_ricerca.setProperty("CLASSIFICA" , classifica);
		parametri_ricerca.setProperty("PROGR_FASCICOLO" , progressivo);
		parametri_ricerca.setProperty("ANNO_FASCICOLO",anno);

		if (parametri_ricerca.properties.containsKey("ENABLED")) parametri_ricerca.properties.remove("ENABLED");

		SolrPathInterface solrInterface = new SolrPathInterface();
		Fascicolo dossier = solrInterface.searchObject(CallDocumentMgt.getToken(), parametri_ricerca, Fascicolo.class);

		if (dossier!=null) {
			String modified = dossier.getProperty("MODIFIED");
			if (modified != null) {
				dossier.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
			}
		}

//		List<Fascicolo> dossiersList = DocerService.ricercaFascicoli(CallDocumentMgt.getToken(), parametri_ricerca);
		
//		Fascicolo dossier = null;
//
//		if (!dossiersList.isEmpty()) {
//			dossier = dossiersList.iterator().next();
//			String modified = dossier.getProperty("MODIFIED");
//			if (modified != null) {
//				dossier.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
//			}
//		}
		
		return dossier;
	}
	
	/* FINE: modifiche aggiunte al 7/01/2014 */
	
	/*public static List<Titolario> searchFilingPlan(String parentClassifica) throws Exception
	{
		
		List<Titolario> filingPlanListToRet = new ArrayList<Titolario>();
		
		Titolario parametri_ricerca = new Titolario();
		
		parametri_ricerca.setEnte(CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setAoo(CallDocumentMgt.getAOOCorrente());
		parametri_ricerca.setParentClassifica(parentClassifica);
		
		if (parametri_ricerca.properties.containsKey("ENABLED")) parametri_ricerca.properties.remove("ENABLED");
		
		List<Titolario> filingPlanList = DocerService.ricercaTitolari(CallDocumentMgt.getToken(), parametri_ricerca);
		
		for (Titolario titolario : filingPlanList) {
			if (titolario.getEnabled()) {
				filingPlanListToRet.add(titolario);
			}
			String modified = titolario.getProperty("MODIFIED");
			
			if (modified != null) {
				titolario.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
			}
		}
		
		return filingPlanListToRet;
	}*/
	
	/*public static List<Fascicolo> searchDossiers(String parentClassifica, String parentProgressive, String anno) throws Exception
	{
		List<Fascicolo> dossiersListToret = new ArrayList<Fascicolo>();
		
		FascicoloCriteria parametri_ricerca = new FascicoloCriteria();
		
		parametri_ricerca.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setProperty("COD_AOO",CallDocumentMgt.getAOOCorrente());
		parametri_ricerca.setProperty("CLASSIFICA" , parentClassifica);
		parametri_ricerca.setProperty("PARENT_PROGR_FASCICOLO" , parentProgressive);
		parametri_ricerca.setProperty("ANNO_FASCICOLO",anno);
		
		if (parametri_ricerca.properties.containsKey("ENABLED")) parametri_ricerca.properties.remove("ENABLED");
		
		List<Fascicolo> dossiersList = DocerService.ricercaFascicoli(CallDocumentMgt.getToken(), parametri_ricerca);
		
		for (Fascicolo fascicolo : dossiersList) {
			if (fascicolo.getEnabled()) {
				dossiersListToret.add(fascicolo);
			}
			String modified = fascicolo.getProperty("MODIFIED");
			if (modified != null) {
				fascicolo.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
			}
		}
		
		return dossiersListToret;
	}*/
	
	/*public static List<Documento> searchDossierDocuments(String parentClassifica, String progressive, String anno) throws Exception
	{
		FascicoloCriteria parametri_ricerca = new FascicoloCriteria();
		
		parametri_ricerca.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		parametri_ricerca.setProperty("COD_AOO",CallDocumentMgt.getAOOCorrente());
		parametri_ricerca.setProperty("CLASSIFICA" , parentClassifica);
		parametri_ricerca.setProperty("PROGR_FASCICOLO" , progressive);
		parametri_ricerca.setProperty("ANNO_FASCICOLO",anno);	
		
		if (parametri_ricerca.properties.containsKey("ENABLED")) parametri_ricerca.properties.remove("ENABLED");
		
		List<Documento> documentsList = DocerService.recuperaDocumentiFascicolo(CallDocumentMgt.getToken(), parametri_ricerca);
		
		for (Documento documento : documentsList) {
			
			String modified = documento.getProperty("MODIFIED");
			if (modified != null) {
				documento.setProperty("MODIFIED", KDMDateUtil.formatDate(modified));
			}
		}
		
		return documentsList;
	}*/
	
	/*public static String buildAlfrescoFolderPath(String folderId) throws DocerApiException, UnsupportedEncodingException
	{
		String alfrescoFolderPath = new String();
		
		String[] splittedFolderPath = folderId.split("/");
		
		for (String name : splittedFolderPath) {
			if (!"".equals(name)) {
				String encodedName = new String(name.trim().getBytes(), "UTF-8");
				
				alfrescoFolderPath = alfrescoFolderPath + "/" + "cm" + ":" + encodedName;
			}
		}
		
		String alfrescoFolderPrefix = ALFRESCO_FOLDER_PREFIX;
		
		alfrescoFolderPrefix = alfrescoFolderPrefix.replace(ENTE_PLACE_HOLDER, CallDocumentMgt.getEnteCorrente());
		alfrescoFolderPrefix = alfrescoFolderPrefix.replace(AOO_PLACE_HOLDER, CallDocumentMgt.getAOOCorrente());
		
		return alfrescoFolderPrefix + alfrescoFolderPath;
	}*/

	public static Map<String, DocActionItem> retrieveUserAllowedOptions(ICIFSObject target) throws Exception {
		Map<String,Boolean> userOptions = Security.getAllowedUserOptions(CallDocumentMgt.getToken(),target);
		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();

		for (String rightsKey: userOptions.keySet()) {
			String messageKey = "label.security." + rightsKey;
			StaticMessageSource sms = new StaticMessageSource();
			Locale loc = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getLocale();
			String label = sms.getMessage(messageKey, null, messageKey, loc);
			userAllowedOptions.put(rightsKey.toUpperCase(), new DocActionItem(rightsKey.toUpperCase(), label, userOptions.get(rightsKey)));
		}

		return userAllowedOptions;
	}

//	public static Map<String, DocActionItem> retrieveUserAllowedOptions(AOO aoo) throws Exception
//	{
//		// 0 FULL
//		// 1 NORMAL (EDIT)
//		// 2 READONLY
//		Security security = new Security();
//		security.init(aoo, CallDocumentMgt.getToken(), CallDocumentMgt.getUtenteCorrente());
//		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();
//		userAllowedOptions.put("CREA_CARTELLA", new DocActionItem("CREA_CARTELLA", "Crea Cartella", security.checkRights(security.creaFolder)));
//		userAllowedOptions.put("CREA_TITOLARIO", new DocActionItem("CREA_TITOLARIO", "Crea Titolario", security.checkRights(security.creaTitolario)));
//
////		userAllowedOptions.put("EDIT", new DocActionItem("EDIT", "Modifica profilo", security.checkRights(security.edit)));// security.checkRights(security.edit)));
////		userAllowedOptions.put("CREA_DOCUMENTO", new DocActionItem("CREA_DOCUMENTO", "Importa documenti", security.checkRights(security.creaDocumento)));
////		userAllowedOptions.put("CREA_FASCICOLO", new DocActionItem("CREA_FASCICOLO", "Crea Fascicolo", security.checkRights(security.creaFascicolo)));
////		userAllowedOptions.put("APRI", new DocActionItem("APRI", "Apri", security.checkRights(security.openInEdit)));
////		userAllowedOptions.put("SICUREZZA", new DocActionItem("SICUREZZA", "SICUREZZA", security.checkRights(security.sicurezza)));
//		return userAllowedOptions;
//	}
//
//	public static Map<String, DocActionItem> retrieveUserAllowedOptions(Fascicolo fascicolo) throws Exception
//	{
//		// 0 FULL
//		// 1 NORMAL (EDIT)
//		// 2 READONLY
//		Security security = new Security();
//		security.init(fascicolo, CallDocumentMgt.getToken(), CallDocumentMgt.getUtenteCorrente());
//		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();
//		userAllowedOptions.put("EDIT", new DocActionItem("EDIT", "Modifica profilo", security.checkRights(security.edit)));// security.checkRights(security.edit)));
//		userAllowedOptions.put("CREA_DOCUMENTO", new DocActionItem("CREA_DOCUMENTO", "Importa documenti", security.checkRights(security.creaDocumento)));
//		userAllowedOptions.put("CREA_FASCICOLO", new DocActionItem("CREA_FASCICOLO", "Crea Fascicolo", security.checkRights(security.creaFascicolo)));
//		userAllowedOptions.put("APRI", new DocActionItem("APRI", "Apri", security.checkRights(security.openInEdit)));
//		userAllowedOptions.put("SICUREZZA", new DocActionItem("SICUREZZA", "SICUREZZA", security.checkRights(security.sicurezza)));
//		return userAllowedOptions;
//	}
//	public static Map<String, DocActionItem> retrieveUserAllowedOptions(Cartella cartella) throws Exception
//	{
//		// 0 FULL
//		// 1 NORMAL (EDIT)
//		// 2 READONLY
//		Security security = new Security();
//		security.init(cartella, CallDocumentMgt.getToken(), CallDocumentMgt.getUtenteCorrente());
//		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();
//		userAllowedOptions.put("EDIT", new DocActionItem("EDIT", "Modifica cartella", security.checkRights(security.edit)));// security.checkRights(security.edit)));
//		userAllowedOptions.put("DELETE", new DocActionItem("DELETE", "Elimina cartella", security.checkRights(security.elimina)));
//		userAllowedOptions.put("CREA_DOCUMENTO", new DocActionItem("CREA_DOCUMENTO", "Importa documenti", security.checkRights(security.creaDocumento)));
//		userAllowedOptions.put("CREA_CARTELLA", new DocActionItem("CREA_CARTELLA", "Crea Cartella", security.checkRights(security.creaFolder)));
//		userAllowedOptions.put("APRI", new DocActionItem("APRI", "Apri", security.checkRights(security.openInEdit)));
//		userAllowedOptions.put("SICUREZZA", new DocActionItem("SICUREZZA", "Sicurezza", security.checkRights(security.sicurezza)));
//		return userAllowedOptions;
//	}
//
//	public static Map<String, DocActionItem> retrieveUserAllowedOptions(Titolario titolario) throws Exception
//	{
//		// 0 FULL
//		// 1 NORMAL (EDIT)
//		// 2 READONLY
//		Security security = new Security();
//		security.init(titolario, CallDocumentMgt.getToken(), CallDocumentMgt.getUtenteCorrente());
//		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();
//		userAllowedOptions.put("EDIT", new DocActionItem("EDIT", "Modifica profilo", security.checkRights(security.edit)));
//		userAllowedOptions.put("CREA_TITOLARIO", new DocActionItem("CREA_TITOLARIO", "Crea Titolario", security.checkRights(security.creaTitolario)));
//		userAllowedOptions.put("CREA_FASCICOLO", new DocActionItem("CREA_FASCICOLO", "Crea Fascicolo", security.checkRights(security.creaFascicolo)));
//		userAllowedOptions.put("APRI", new DocActionItem("APRI", "Apri", security.checkRights(security.openInEdit)));
//		userAllowedOptions.put("SICUREZZA", new DocActionItem("SICUREZZA", "Sicurezza", security.checkRights(security.sicurezza)));
//		return userAllowedOptions;
//	}
//	public static Map<String, DocActionItem> retrieveUserAllowedOptions(Documento currDoc) throws Exception
//	{
//		// 0 FULL
//		// 1 NORMAL (EDIT)
//		// 2 READONLY
//		Security security = new Security();
//		security.init(currDoc, CallDocumentMgt.getToken(), CallDocumentMgt.getUtenteCorrente());
//		// int rights =
//		// DocerService.recuperaDirittiEffettivi(CallDocumentMgt.getToken(),
//		// currDoc.getDocNum(), CallDocumentMgt.getUtenteCorrente());
//		// String lockedUser =
//		// DocerService.recuperaUserLock(CallDocumentMgt.getToken(),
//		// currDoc.getDocNum());
//		Map<String, DocActionItem> userAllowedOptions = new HashMap<String, DocActionItem>();
//
//		userAllowedOptions.put("VAI_A_PROFILO", new DocActionItem("VAI_A_PROFILO", "Apri", security.checkRights(security.read)));
//		userAllowedOptions.put("EDIT", new DocActionItem("EDIT", "Modifica profilo", security.checkRights(security.edit)));
//		userAllowedOptions.put("SICUREZZA", new DocActionItem("SICUREZZA", "Sicurezza", security.checkRights(security.sicurezza)));
//		userAllowedOptions.put("CRONOLOGIA", new DocActionItem("CRONOLOGIA", "Cronologia", security.checkRights(security.cronologia)));
//		userAllowedOptions.put("VERSIONI", new DocActionItem("VERSIONI", "Versioni", security.checkRights(security.listaVersioni)));
//		userAllowedOptions.put("SCARICA", new DocActionItem("SCARICA", "Scarica", security.checkRights(security.download)));
//		userAllowedOptions.put("ELIMINA", new DocActionItem("ELIMINA", "Elimina", security.checkRights(security.elimina)));
//		userAllowedOptions.put("OPEN_EDIT", new DocActionItem("OPEN_EDIT", "Modifica file", security.checkRights(security.openInEdit)));
//
//		return userAllowedOptions;
//	}
//
	
	/*public static List<Fascicolo> searchFascicolo(String fascicoloDescrizione) throws DocerApiException
	{
		FascicoloCriteria fascicoloCriteria = new FascicoloCriteria();
		fascicoloCriteria.setProperty("DES_FASCICOLO", fascicoloDescrizione);
		fascicoloCriteria.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		fascicoloCriteria.setProperty("COD_AOO", CallDocumentMgt.getAOOCorrente());
		List<Fascicolo> listFascicolo = DocerService.ricercaFascicoli(CallDocumentMgt.getToken(), fascicoloCriteria);
		
		return listFascicolo;
	}*/
	
	/*public static List<Titolario> searchTitolario(String titolarioDescrizione) throws DocerApiException
	{
		TitolarioCriteria titolarioCriteria = new TitolarioCriteria();
		titolarioCriteria.setProperty("DES_TITOLARIO", titolarioDescrizione);
		titolarioCriteria.setProperty("COD_ENTE", CallDocumentMgt.getEnteCorrente());
		titolarioCriteria.setProperty("COD_AOO", CallDocumentMgt.getAOOCorrente());
		List<Titolario> listTitolario = DocerService.ricercaTitolari(CallDocumentMgt.getToken(), titolarioCriteria);
		
		return listTitolario;
	}*/
}
