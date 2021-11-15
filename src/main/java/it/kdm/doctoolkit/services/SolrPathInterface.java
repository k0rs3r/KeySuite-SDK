package it.kdm.doctoolkit.services;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.Acl;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.model.GenericCriteria;
import it.kdm.doctoolkit.model.Ordinamento;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.model.path.Root;
import it.kdm.doctoolkit.utils.SOLRResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by microchip on 17/02/16.
 */
public class SolrPathInterface {

    private SOLRClient client;

    public SolrPathInterface() {
        this.client = new SOLRClient();
    }


    public ICIFSObject reopenObject(String token, ICIFSObject obj) throws FileNotFoundException, SolrServerException {
        return client.openBySolrId(token, obj.getSolrId());
    }

    public <T extends ICIFSObject> List<T> search(String token, GenericCriteria criteria, HashMap<String,String> params, Class<T> klass) throws FileNotFoundException, SolrServerException {
        List<T> ret = client.search(token, criteria,params,klass);

        return ret;
    }

    public <T extends ICIFSObject> T searchObject(String token, GenericCriteria criteria, Class<T> klass) throws FileNotFoundException, SolrServerException {
        Optional<T> ret = client.openBySearch(token, criteria,klass,false);
        if (ret.isPresent())
            return ret.get();

        return null;
    }

    public List<HashMap<String,String>> getAclDataBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        SolrDocument doc = client.getAclBySolrId(token, solrId);

        Map<String,String> data = new HashMap<>();

        List<Acl> acl1 = getAclRightsBySolrId(doc);
        data.put("acl_rights", acl1.toString());

        List<Acl> acl2 = getAclInheritedBySolrId(doc);
        data.put("acl_inherited", acl2.toString());
        data.put("acl_inherits", acl2!=null? "true":"false");

        List lst = new ArrayList();
        lst.add(data);

        return lst;
    }

    public List<Acl> getAclRightsBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        SolrDocument doc = client.getAclBySolrId(token, solrId);
        return getAclRightsBySolrId(doc);
    }

    private List<Acl> getAclRightsBySolrId(SolrDocument doc) throws FileNotFoundException, SolrServerException {

        Map<String,Number> acl_rights = (Map<String,Number>)doc.getFieldValue(SOLRClient.ACL_RIGHTS);
        Map<String,List<String>> acl_profiles = (Map<String,List<String>>)doc.getFieldValue(SOLRClient.ACL_PROFILES);
        Map<String,String> acl_actors = (Map<String,String>)doc.getFieldValue(SOLRClient.ACL_ACTORS);

        List<Acl> acls = new ArrayList<>();

        for (String item : acl_rights.keySet()) {
            Acl acl = new Acl();

            if(item.equals("null")) {
                continue;
            }

            String[] pars = item.split("@");
            acl.setUtenteGruppo(pars[0]);
            acl.setActorType(pars[1]);
//            acl.setDiritti(acl_rights.get(item).toString());

            if(acl_actors==null || Strings.isNullOrEmpty(acl_actors.get(item)) ){
                acl.setDescription(item);
            }else{
                String descriptionAcl = acl_actors.get(item);
                acl.setDescription(descriptionAcl.split("~")[0]);
            }

            acl.setProfileList(acl_profiles.get(acl_rights.get(item)));

            acls.add(acl);
        }

        return acls;
    }

    public List<Acl> getAclExplicitBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        SolrDocument doc = client.getAclBySolrId(token,solrId);
        List<String> acl_exp = (List<String>)doc.getFieldValue(SOLRClient.ACL_EXPLICIT);
//        Map<String,List<String>> acl_profiles = (Map<String,List<String>>)doc.getFieldValue(SOLRClient.ACL_PROFILES);
        Map<String,String> acl_actors = (Map<String,String>)doc.getFieldValue(SOLRClient.ACL_ACTORS);

        List<Acl> acls = new ArrayList<>();
        //for (String item : acl_exp.keySet()) {
    if(acl_exp !=null)
        for (String a : acl_exp)
        {
            String actor = a.split("\\:")[0];

            if(actor.equals("null"))
                continue;

            String profile = a.split("\\:")[1]; //fullAccess
            Acl acl = new Acl();

            String[] pars = actor.split("@");
            acl.setUtenteGruppo(pars[0]);
            acl.setActorType(pars[1]);
//            acl.setDiritti(profile);

//            acl.setDescription(acl_actors.get(actor.toLowerCase()).split("~")[0]);

            String acl_actor = actor.toLowerCase();
            String desc_actor = acl_actor.split("@")[0];

            if (acl_actors.containsKey(acl_actor) &&
                    acl_actors.get(acl_actor).contains("~")) {

                desc_actor = acl_actors.get(acl_actor).split("~")[0];

            }

            acl.setDescription(desc_actor);

            acl.setProfileList(Collections.singletonList(profile));

            acls.add(acl);
        }

        return acls;
    }

    public List<Acl> getAclInheritedBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        SolrDocument doc = client.getAclBySolrId(token, solrId);
        return getAclInheritedBySolrId(doc);
    }
    private List<Acl> getAclInheritedBySolrId(SolrDocument doc) throws FileNotFoundException, SolrServerException {
//        SolrDocument doc = client.getAclBySolrId(token,solrId);
        String solrId = (String) doc.getFieldValue("id");

        Map<String,List<String>> acl_inherited = (Map<String,List<String>>)doc.getFieldValue(SOLRClient.ACL_INHERITED);
        Map<String,String> acl_actors = (Map<String,String>)doc.getFieldValue(SOLRClient.ACL_ACTORS);

        Boolean acl_inherits = (Boolean)doc.getFieldValue(SOLRClient.ACL_INHERITS);

        if(!acl_inherits)
            return null;

        List<Acl> acls = new ArrayList<>();

        if(acl_inherited!=null) {
            acl_inherited.remove(solrId);
            for (String key : acl_inherited.keySet()) {

                List<String> list = acl_inherited.get(key);

                //dati parent
                String id_parent = key;
                String desc_parent = id_parent;

                if (acl_actors.containsKey(id_parent) )
                    desc_parent = acl_actors.get(id_parent);


                for (String a : list) {

                    String actor = a.split("\\:")[0];

                    if (actor.equals("null"))
                        continue;

                    String profile = a.split("\\:")[1]; //fullAccess
                    Acl acl = new Acl();

                    String[] pars = actor.split("@");
                    acl.setUtenteGruppo(pars[0]);
                    acl.setActorType(pars[1]);
        //            acl.setDiritti(profile);


                    String acl_actor = actor.toLowerCase();
                    String desc_actor = acl_actor.split("@")[0];

                    if (acl_actors.containsKey(acl_actor) &&
                            acl_actors.get(acl_actor).contains("~")) {

                        desc_actor = acl_actors.get(acl_actor).split("~")[0];

                    }

                    acl.setDescription(desc_actor);
                    acl.setProfileList(Collections.singletonList(profile));

                    acl.setParentNodeId(id_parent);
                    acl.setParentDescription(desc_parent);

                    acls.add(acl);
                }






            }
        }

        return acls;
    }
    public ICIFSObject openBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        return client.openBySolrId(token, solrId);
    }

    public ICIFSObject openTrashedByDocnum(String token, String solrId) throws FileNotFoundException, SolrServerException {
        return client.openByDocnum(token, solrId, "/trashcan");
    }
    public ICIFSObject openTrashedByPath(String token, String path) throws FileNotFoundException, SolrServerException {
        return client.openByPath(token, path, "/trashcan");
    }
    public Documento openByDocnum(String token, String docnum) throws SolrServerException, FileNotFoundException {
        return client.openByDocnum(token,docnum);
    }

    public Documento openByDocnum(String token, String docnum, Collection<String> fl) throws SolrServerException, FileNotFoundException {
        return client.openByDocnum(token,docnum,fl);
    }

    public ICIFSObject openByPath(String token, String path) throws IOException, DocerApiException {
        try {

            if(path.equals("/"))
                return new Root();

            return client.openByPath(token, path);
        } catch (SolrServerException e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }

    public List<ICIFSObject> contentByPath(String token, String path, int limit, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        //includeDocs = true;
        return childrenByPath(token, path, true, limit, ordinamenti);
    }

    public SOLRResponse contentByPath(String token, String queryString) throws IOException, DocerApiException {
        try {
            return client.contentByPath(token, queryString);

        } catch (SolrServerException e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }


    public List<ICIFSObject> navByPath(String token, String path, int limit, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        //includeDocs = false;
        return childrenByPath(token, path,false,limit,ordinamenti);
    }

    public List<ICIFSObject> navByPath(String token, String path, int limit, SolrParams params, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        //includeDocs = false;
        try {

            SOLRResponse rsp = client.searchSolrByPath(token, path, params, limit, false, ordinamenti);
            List<ICIFSObject> resultsList  = rsp.getTypedResults();

            List<ICIFSObject> facetResults  = new ArrayList<>();
            if(rsp.getFacetFields()!=null && rsp.getFacetFields().size()>0) {


                if( path.endsWith("*") )
                    path = path.substring(0, path.length()-2);

                if( path.endsWith("/") )
                    path = path.substring(0, path.length()-2);

                SolrPathInterface solrInterface = new SolrPathInterface();
                ICIFSObject parent = solrInterface.openByPath(token, path);

                String prefixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.prefix");
                prefixVirtualId = prefixVirtualId != null ? prefixVirtualId : "#";

                String suffixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.suffix");
                suffixVirtualId = suffixVirtualId != null ? suffixVirtualId : "";

                String format = prefixVirtualId + "%s" + suffixVirtualId;
                facetResults  = rsp.getFacetResults(parent.getType(), format);

//                for(ICIFSObject result : resultsList) {
//                    if ("true".equals(result.getProperty("facet"))) {
//                        String objPath = result.getFEFullPath();
//                        ICIFSObject facetObj = solrInterface.openByPath(token, objPath);
//
//                        for (String key : facetObj.properties.keySet())
//                            if (!result.properties.containsKey(key))
//                                result.setProperty(key, facetObj.properties.get(key));
//
//                    }
//                }

                resultsList.addAll(facetResults);

            }


            return resultsList;

        } catch (SolrServerException e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }

    private List<ICIFSObject> childrenByPath(String token, String path, boolean includeDocs, int limit, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        try {
            return client.searchByPath(token,path, limit, includeDocs, ordinamenti);
        } catch (SolrServerException e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }

}