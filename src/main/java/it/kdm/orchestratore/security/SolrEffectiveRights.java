package it.kdm.orchestratore.security;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.SOLRClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by microchip on 04/11/14.
 */
public class SolrEffectiveRights extends BaseEffectiveRights {

    @Override
    public long getEffectiveRights(Object target, String token, String user) {
        String effectiveRights="";

        String solrId = ((ICIFSObject)target).getSolrId();
        try{
            Strings.isNullOrEmpty(((ICIFSObject)target).getProperty("user_rights"));
        }catch(Exception e){
            e.printStackTrace();
        }

        if (Strings.isNullOrEmpty(((ICIFSObject)target).getProperty("user_rights"))) {
            SOLRClient client = new SOLRClient();
            try {
                target = client.openBySolrId(token,solrId,"/getacl");
            } catch (SolrServerException e) {
                throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, e);
            } catch (FileNotFoundException e) {
                throw new SolrException(SolrException.ErrorCode.NOT_FOUND, e);
            }
        }

        effectiveRights = ((ICIFSObject) target).getProperty("user_rights");
        if(effectiveRights==null)
            return Long.parseLong("0");
        else
            return Long.parseLong(effectiveRights);

    }


    @Override
    public Map<String,String> getRules() {
        //definizione dei diritti base gestiti mappati con il documentale
        Map<String,String> rules = new HashMap<>();
        rules.put("read","1");
        rules.put("download","2");
        rules.put("cronologia","1");
        rules.put("retrieveACL","4");
        rules.put("readVersion","8");
        rules.put("lock","128");
        rules.put("unlock","4096");
        rules.put("rename","1024");
        rules.put("move","2048");
        rules.put("listaVersioni","16");
        rules.put("openInEdit","256");
        rules.put("edit","64");
        rules.put("creaDocumento","32");
        rules.put("creaFascicolo","16384|32768");
        rules.put("creaTitolario","16384|32768");
        rules.put("creaFolder","16384|32768");
        rules.put("creaVersioni","512");
        rules.put("elimina","8192");
        rules.put("sicurezza","16384");

        //alias per appDoc
        rules.put("VAI_A_PROFILO","#read");
        rules.put("VERSIONI","#listaVersioni");
        rules.put("SCARICA","#download");
        rules.put("OPEN_EDIT","#openInEdit");
        rules.put("CREA_TITOLARIO","#creaTitolario");
        rules.put("CREA_FASCICOLO","#creaFascicolo");
        rules.put("APRI","#openInEdit");
        rules.put("DELETE","#elimina");
        rules.put("CREA_DOCUMENTO","#creaDocumento");
        rules.put("CREA_CARTELLA","#creaFolder");


        return rules;
    }

    public long remapValue(List<String> value){

        long result = 0;

        for (String prof : value) {
            if(prof.equalsIgnoreCase("FullAccess"))
                result |=  this.getRoleMask("full");
            else if(prof.equalsIgnoreCase("NormalAccess"))
                result |=  this.getRoleMask("edit");
            else if(prof.equalsIgnoreCase("ReadOnlyAccess"))
                result |=  this.getRoleMask("read");
            else if(prof.equalsIgnoreCase("ViewProfileAccess"))
                result |=  this.getRoleMask("view");
            else if(prof.equalsIgnoreCase("Contributor"))
                result |=  this.getRoleMask("create");
            else if(prof.equalsIgnoreCase("CreateReadAccess"))
                result |=  this.getRoleMask("collaborator");
        }

        return result;
    }
}
