package it.kdm.orchestratore.appdoc.utils;

import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.SOLRClient;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.utils.SOLRResponse;
import it.kdm.doctoolkit.utils.SolrUtils;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.orchestratore.appdoc.model.ResultList;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;

import java.security.KeyException;
import java.util.List;

/**
 * Created by enrico on 19/09/17.
 */
public class SolrUtilSearch {

    /*public String getEntityViewName(String entity_id) {

        try{
            return getGroupViewName(entity_id);
        } catch( Exception e) {
        }

        try{
            return getUserViewName(entity_id);
        } catch( Exception e) {
        }

        return entity_id;
    }

    //Dato un group_id ritorna il view_name del gruppo dato dalla seguente forma:  "GROUP_NAME (GROUP_ID)"
    private String getGroupViewName(String group_id) throws Exception {
        String groupViewName="";

        SOLRClient client = new SOLRClient();
        GenericCriteria criteria = new GenericCriteria();
        criteria.setRawProperty("GROUP_ID",group_id);
        criteria.setRawProperty("type","(group aoo ente)");
        SOLRResponse resp = client.rawSolrSearch(CallDocumentMgt.getToken(), criteria, null, true);
        SolrDocumentList result = resp.getResults();

        if(result.size()>0){
            String grp_id="";
            String grp_name="";
            if(result.get(0).containsKey("GROUP_ID")){
                grp_id = result.get(0).getFieldValue("GROUP_ID").toString();
            }else{
                throw new Exception("Errore nella generazione del groupViewName per il gruppo "+group_id+". Metadato GROUP_ID non presente nel risultato di ricerca!");
            }
            if(result.get(0).containsKey("GROUP_NAME")){
                grp_name = result.get(0).getFieldValue("GROUP_NAME").toString();
            }else{
                throw new Exception("Errore nella generazione del groupViewName per il gruppo "+group_id+". Metadato GROUP_NAME non presente nel risultato di ricerca!");
            }
            groupViewName = grp_name+" ("+grp_id+")";
        }else{
            throw new Exception("Errore nella generazione del groupViewName per il gruppo "+group_id+". Gruppo non esistente! La ricerca non ha prodotto risultati!");
        }

        return groupViewName;
    }


    //Dato un user_id ritorna il view_name dell'utente dato dalla seguente forma:  "FULL_NAME (USER_ID)"
    private String getUserViewName(String user_id) throws Exception {
        String userViewName="";

        SOLRClient client = new SOLRClient();
        GenericCriteria criteria = new GenericCriteria();
        criteria.setRawProperty("USER_ID",user_id);
        criteria.setRawProperty("type","user");
        SOLRResponse resp = client.rawSolrSearch(CallDocumentMgt.getToken(), criteria, null, true);
        SolrDocumentList result = resp.getResults();

        if(result.size()>0){
            String usr_id="";
            String usr_name="";
            if(result.get(0).containsKey("USER_ID")){
                usr_id = result.get(0).getFieldValue("USER_ID").toString();
            }else{
                throw new Exception("Errore nella generazione del userViewName per l'utente "+user_id+". Metadato USER_ID non presente nel risultato di ricerca!");
            }
            if(result.get(0).containsKey("FULL_NAME")){
                usr_name = result.get(0).getFieldValue("FULL_NAME").toString();
            }else{
                throw new Exception("Errore nella generazione del userViewName per l'utente "+user_id+". Metadato FULL_NAME non presente nel risultato di ricerca!");
            }
            userViewName = usr_name+" ("+usr_id+")";
        }else{
            throw new Exception("Errore nella generazione del userViewName per l'utente "+user_id+". Utente non esistente! La ricerca non ha prodotto risultati!");
        }

        return userViewName;
    }*/



    public static ResultList<ICIFSObject> solrQuery(String q, String type, String fq )
            throws Exception
    {
        String token = CallDocumentMgt.getToken();

        type = type!=null? String.format("type:%s",type) : "type:(titolario fascicolo folder documento)";

        SOLRClient client = new SOLRClient();
        SolrParams params = SolrUtils.parseQueryString(q);

        ModifiableSolrParams solrParams = new ModifiableSolrParams(params);


        ICIFSObject obj = null;


        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        String ente = CallDocumentMgt.getEnteCorrente();
        String aoo = CallDocumentMgt.getAOOCorrente();
        solrParams.add("fq", "COD_ENTE:" + ente);
        solrParams.add("fq", "COD_AOO:" + aoo);

        //TODO: filtri impliciti da mettere in configurazione
        solrParams.add("fq", type);
        solrParams.add("fq", fq);
        String location = ToolkitConnector.extractSedeFromToken(token);
        QueryResponse qr = client.getServer(location).query(solrParams, SolrRequest.METHOD.POST);
        SOLRResponse rsp = new SOLRResponse(qr);
        List<ICIFSObject> documenti = rsp.getTypedResults();
        long totResults = qr.getResults().getNumFound();
        int qtime = rsp.getQTime();

        ResultList<ICIFSObject> results = new ResultList<>(documenti);
        results.setNumFound(totResults);
        results.setQtime(qtime);
        results.setParentObject(obj);

        return results;
    }
}
