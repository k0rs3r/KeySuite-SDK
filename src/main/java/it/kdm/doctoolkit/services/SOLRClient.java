package it.kdm.doctoolkit.services;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.utils.SOLRResponse;
import it.kdm.doctoolkit.utils.SolrUtils;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.orchestratore.utils.RestTemplateFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Lorenzo Lucherini on 9/1/14.
 */
public class SOLRClient {

    public static final String ZK_HOST = "zkHost";

    private static final Logger logger = LoggerFactory.getLogger(SOLRClient.class);

    public static final String MULTIVALUE_SEP = "||";

    //TODO: Configurable
    private static final long MAX_TIME = 1500;

    //private final String zkPort;
    private final String collection;
    //private final String collectionFormat;

    private final String shards_tolerant = "true";

    // Nuovo controllo delle sedi non disponibili
    private final String shards_info = "true";
    private final Integer max_shard_timeouts = 5;

    private HashMap<Class<? extends ICIFSObject>, String> typeMap = new HashMap<>();

    private static HashMap<String, SolrClient> coreClients = new HashMap<>();
    //private static HashMap<String, String> locations = new HashMap<>();

    public static final String ACL_INHERITED = "acl_inherited";
    public static final String ACL_EXPLICIT = "acl_explicit";
    public static final String ACL_RIGHTS = "acl_rights";
    public static final String ACL_ACTORS = "acl_actors";
    public static final String ACL_PROFILES = "acl_profiles";
    public static final String USER_RIGTHS = "user_rights";
    public static final String ACL_INHERITS = "acl_inherits";

    private String getHost(String sede) {
        return ToolkitConnector.getGlobalProperty("zookeeper." + sede, System.getProperty( ZK_HOST , "127.0.0.1:9983" ) );
    }

    public SolrClient getLocalCore(String sede) {

        //String key = "core_" + sede;

        CloudSolrClient server = getServer();

        String collection = server.getDefaultCollection();

        String url = server.getZkStateReader().getClusterState().getCollection(collection).getActiveSlices().iterator().next().getLeader().getBaseUrl();

        SolrClient coreClient = coreClients.get(url);

        if (coreClient==null){
            synchronized (coreClients){
                coreClient = coreClients.get(url);
                if (coreClient==null){
                    coreClient = new HttpSolrClient.Builder(url).build();
                    coreClients.put(url,coreClient);
                }
            }
        }

        return coreClient;


        /*try {

            synchronized (coreClients) {
                if (!coreClients.containsKey(key)) {

                    CloudSolrClient server = getServer();

                    String collection = server.getDefaultCollection();

                    String shard = server.getZkStateReader().getClusterState().getActiveSlices(collection).iterator().next().getName();

                    String url = server.getZkStateReader().getLeaderUrl(collection,shard,3000);

                    servers.put(key, new HttpSolrClient(url));
                }

                return coreClients.get(key);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

    }

    public CloudSolrClient getServer(){
        return getServer(ToolkitConnector.getSedeLocale());
    }

    private static CloudSolrClient solrClient = null;

    public CloudSolrClient getServer(String sede) {
        try {

            if (solrClient==null){
                synchronized (coreClients) {
                    if (solrClient==null) {
                        String zkHost = getHost(sede);
                        CloudSolrClient.Builder builder = new CloudSolrClient.Builder((List) null).withZkHost(Arrays.asList(zkHost.split(",")));
                        solrClient = builder.build();
                        String collection = solrClient.getZkStateReader().getClusterState().getCollectionsMap().keySet().iterator().next();
                        solrClient.setDefaultCollection(collection);
                    }
                }
            }

            return solrClient;

            /*if (!sede.equals(ToolkitConnector.getSedeLocale())) {
                logger.info("Sede cambiata a: {}", sede);
            }

            synchronized (servers) {
                if (!servers.containsKey(sede)) {
                    CloudSolrClient server = new CloudSolrClient(getHost(sede));
                    server.setDefaultCollection(collection);

                    servers.put(sede, server);
                }

                return (CloudSolrClient) servers.get(sede);
            }*/

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /*LoadingCache<String, Optional<String>> shards = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build(
                    new CacheLoader<String, Optional<String>>() {
                        @Override
                        public Optional<String> load(String sede) throws Exception {
                            CloudSolrClient server = getServer(sede);
                            try {
                                server.connect();

                                Pattern nodeFormat = Pattern.compile("(.+)_.+");

                                Set<String> liveNodes = server.getZkStateReader().getClusterState().getLiveNodes();
                                int i=1;
                                StringBuilder shard = new StringBuilder();
                                for(String liveNode : liveNodes) {
                                    Matcher matcher = nodeFormat.matcher(liveNode);
                                    if (matcher.matches()) {
                                        String host = matcher.group(1);
                                        //shard.append(String.format("%s/solr/%s_%s_replica%d|",
                                        shard.append(String.format(collectionFormat,
                                                host, collection, sede, i++));
                                    }
                                }

//                                shard.delete(shard.length()-1, shard.length());
                                return Optional.of(shard.toString());
                            } catch (Exception e) {
                                return Optional.absent();
                            }
                        }
                    }
            );*/

    public SOLRClient() {

        collection = ToolkitConnector.getGlobalProperty("solr.collection","DOCER");

        typeMap.put(AOO.class, "aoo");
        typeMap.put(Titolario.class, "titolario");
        typeMap.put(Fascicolo.class, "fascicolo");
        typeMap.put(Documento.class, "documento");
        typeMap.put(Cartella.class,  "folder"   );
        typeMap.put(ICIFSObject.class, "*");
    }

    public Map<String,String> getServerInfo(){


        Map<String,Object> result = this.genericRequest("/admin/info/properties",null);
        result = (Map) result.get("system.properties");

        String versionInfo = (String) result.get("version.info");

        List<String> infos = new ArrayList<>();

        if (!Strings.isNullOrEmpty(versionInfo)){
            infos = Arrays.asList(versionInfo.split(","));
        } else {
            String path = (String) result.get("jetty.base");

            File dir = new File(path+"/solr-webapp/webapp/WEB-INF/lib");

            if (dir.exists()) {
                File[] files = dir.listFiles((dir1, name) -> name.startsWith("solr-"));

                for (int i = 0; i < files.length; i++) {
                    infos.add(files[i].getName());
                }
            }
        }

        Map<String,String> ret = new LinkedHashMap<>();

        for(String jar : infos){
            jar = jar.substring(0,jar.length()-4);
            int idx = jar.indexOf("-",6);
            ret.put(jar.substring(0,idx),jar.substring(idx+1));
        }

        return ret;
    }

    private final Pattern ticketPattern = Pattern.compile("dmsticket:(.+)$");

    /*private String extractAlfTicket(String token) throws KeyException, DocerApiException {
        String docerTicket = Utils.extractTokenKey(token, "ticketDocerServices");
        TicketCipher tc = new TicketCipher();
        String ticket = tc.decryptTicket(tc.decryptTicket(docerTicket));

        Matcher matcher = ticketPattern.matcher(ticket);
        if(!matcher.find()) {
            throw new IllegalStateException("Not alfresco ticket found");
        }

        return matcher.group(1);
    }*/

    public SOLRResponse select(String token, SolrParams params) throws SolrServerException {
        String location = ToolkitConnector.extractSedeFromToken(token);
        ModifiableSolrParams solrParams = new ModifiableSolrParams(params);
        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        solrParams.add("fq",fqSession(token));

        QueryResponse qr = null;
        try {
            qr = this.getServer(location).query(solrParams, SolrRequest.METHOD.POST);
        } catch (IOException e) {
            throw new SolrServerException(e);
        }
        SOLRResponse rsp = new SOLRResponse(qr);

        return rsp;
    }
    public SOLRResponse select(SolrParams params) throws SolrServerException {
        //QueryResponse qr = this.getServer(ToolkitConnector.getSedeLocale()).query(params, SolrRequest.METHOD.POST);
        SOLRResponse rsp= null;
        try {
            QueryResponse  qr = this.getServer(ToolkitConnector.getSedeLocale()).query(params, SolrRequest.METHOD.POST);
            rsp = new SOLRResponse(qr);
        } catch (IOException e) {
            throw new SolrServerException(e);
        }



        return rsp;
    }

    public InputStream downloadByPath(String token, Documento doc) throws IOException, SolrServerException, KeyException, DocerApiException {

        throw new UnsupportedOperationException();

        /*String sede = ToolkitConnector.extractSedeFromToken(token);
        String urlSede = ToolkitConnector.getGlobalProperty("sede." + sede);

        URL baseUrl = new URL(urlSede);
        URL alfrescoUrl = new URL(baseUrl.getProtocol(), baseUrl.getHost(), 8081, "");

        String downloaUrl = doc.properties.get("download_url");

        URL url = new URL(alfrescoUrl, "alfresco" + downloaUrl + "?ticket=" + extractAlfTicket(token));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        if (connection.getResponseCode() != 200) {
            throw new AccessDeniedException("Ticket non valido");
        }

        return connection.getInputStream();*/
    }

    public int getAcl(String token, String solrId, String user) throws KeyException, SolrServerException, FileNotFoundException {
        HashMap<String, String> params = new HashMap<>();
        params.put("ticket", user);
        params.put("id", solrId);
        params.put("qt", "/getacl");
        SOLRResponse response = rawSolrSearch(token, null, params, true);

        NamedList<Object> rsp = response.getResponse();
        if (rsp == null) {
            throw new FileNotFoundException(solrId);
        }

        Map acl =null;

        ArrayList responseArray = (ArrayList) rsp.get("response");

        if (responseArray == null) {
            acl= (Map) rsp.get("doc");

        } else if (responseArray.size() >= 1) {
            acl = (Map) responseArray.get(0);

        }

        //Map acl = (Map) responseArray.get(0);
        if (acl == null) {
            throw new FileNotFoundException(solrId);
        }

        Number number = (Number) acl.get("effectiveRights");
        if (number == null) {
            number = 0;
        }

        return number.intValue();
    }

    public SolrDocument getAclBySolrId(String token, String solrId) throws SolrServerException, FileNotFoundException {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        solrParams.set("qt", "/getacl");
        solrParams.set("id", solrId);

        String sede;
        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
            sede = ToolkitConnector.extractSedeFromToken(token);
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

        SolrDocument doc = (SolrDocument)rsp.getResponse().get("doc");

        if (doc == null) {
            throw new FileNotFoundException(solrId);
        }

        return doc;
    }


    public ICIFSObject openBySolrId(String token, String solrId) throws FileNotFoundException, SolrServerException {
        return openBySolrId(token,solrId, "/getfs");
    }

    public ICIFSObject openBySolrId(String token, String solrId, String handler) throws SolrServerException, FileNotFoundException {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        solrParams.set("qt", handler);
        solrParams.set("id", solrId);

        String sede;
        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
            sede = ToolkitConnector.extractSedeFromToken(token);
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

        SolrDocument doc = (SolrDocument)rsp.getResponse().get("doc");

        if (doc == null) {
            throw new FileNotFoundException(solrId);
        }

        return rsp.convertSolrDoc(doc);
    }
    public Documento openByDocnum(String token, String docnum) throws SolrServerException, FileNotFoundException {
        List<String> fl = new ArrayList<>();
        fl.add("*");
        fl.add(SOLRResponse.SOLR_PATH_FIELD);
        return openByDocnum(token, docnum, fl);
    }

    public Documento openByDocnum(String token, String docnum, Collection<String> fl) throws SolrServerException, FileNotFoundException {

        return openByDocnum(token, docnum, fl, "/selectall");
    }

    public Documento openByDocnum(String token, String docnum, String handler) throws SolrServerException, FileNotFoundException {
        List<String> fl = new ArrayList<>();
        fl.add("*");
        fl.add(SOLRResponse.SOLR_PATH_FIELD);
        return openByDocnum(token, docnum, fl, handler);
    }
    public Documento openByDocnum(String token, String docnum, Collection<String> fl, String handler) throws SolrServerException, FileNotFoundException {

        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        solrParams.set("qt", handler);

        String sede;
        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
            sede = ToolkitConnector.extractSedeFromToken(token);
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        solrParams.set("rows", 1);
        String flString = org.apache.commons.lang3.StringUtils.join(fl,',');

//        solrParams.set("fl", String.format("*,%s", SOLRResponse.SOLR_PATH_FIELD));
        solrParams.set("fl", flString);


        solrParams.set("q", String.format("location:%s AND DOCNUM:%s", sede, docnum));

//        solrParams.set("shards.tolerant", shards_tolerant);

//        solrParams.set("_route_", sede);

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

        SolrDocumentList results = rsp.getResults();

        if (results.getNumFound() == 0) {
            throw new FileNotFoundException(docnum);
        }

        if (results.getNumFound() > 1) {
            throw new IllegalStateException("More than one item found for docnum: " + docnum);
        }

        return rsp.getResults(Documento.class).get(0);
    }

    public ICIFSObject openByPath(String token, String path, String handler) throws SolrServerException, FileNotFoundException {

        List<String> fl = new ArrayList<>();
        fl.add("*");
        fl.add(SOLRResponse.SOLR_PATH_FIELD);

        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        solrParams.set("qt", handler);

        String sede;
        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
            sede = ToolkitConnector.extractSedeFromToken(token);
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        solrParams.set("rows", 1);
        String flString = org.apache.commons.lang3.StringUtils.join(fl,',');

        solrParams.set("fl", flString);


        solrParams.set("q", String.format("location:%s AND FULLPATH:%s", sede, path));


        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

        SolrDocumentList results = rsp.getResults();

        if (results.getNumFound() == 0) {
            throw new FileNotFoundException(path);
        }

        if (results.getNumFound() > 1) {
            throw new IllegalStateException("More than one item found for path: " + path);
        }

        return rsp.getResults(ICIFSObject.class).get(0);
    }
    public ICIFSObject openByPath(String token, String path) throws SolrServerException, FileNotFoundException {
        return SOLRResponse.convertSolrDoc(openSolrByPath(token, path));
    }

    public <T extends ICIFSObject> T openByPath(String token, String path, Class<T> klass) throws SolrServerException, FileNotFoundException {
        return openByPath(token, path, klass, true);
    }


    public <T extends ICIFSObject> T openByPath(String token, String path, Class<T> klass, boolean unique) throws SolrServerException, FileNotFoundException {
        return SOLRResponse.convertSolrDoc(openSolrByPath(token, path, unique), klass);
    }

    public SolrDocument openSolrByPath(String token, String path) throws SolrServerException, FileNotFoundException {
        return openSolrByPath(token, path, true);
    }

    public SolrDocument openSolrByPath(String token, String path, boolean unique) throws SolrServerException, FileNotFoundException {

        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

//        solrParams.set("rows", 1);
        solrParams.set("fl", String.format("*,%s", SOLRResponse.SOLR_PATH_FIELD));

        solrParams.set("PATH", path);

        String sede = ToolkitConnector.extractSedeFromToken(token);

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

        SolrDocumentList results = rsp.getResults();

        if (results.getNumFound() == 0) {
            throw new FileNotFoundException(path);
        }

        if (unique && results.getNumFound() > 1 ) {

            for(int i=1 ; i<results.size(); i++) {

                SolrDocument d1 = results.get(i-1);
                SolrDocument d2 = results.get(i);
                if( !d1.getFieldValue("name").equals(d2.getFieldValue("name"))) {
                    throw new IllegalStateException("More than one item found for path: " + path);
                }
            }
        }

        return results.get(0);
    }

    public List<ICIFSObject> searchByPath(String token, String path, int maxResults, boolean includeDocs, Ordinamento... ordinamenti) throws SolrServerException, FileNotFoundException {
        return searchSolrByPath(token, path, null, maxResults, includeDocs, ordinamenti).getTypedResults();
    }

    public <T extends ICIFSObject> List<T> searchByPath(String token, String path, int maxResults, Class<T> type) throws SolrServerException, FileNotFoundException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.add("q", String.format("type:%s", typeMap.get(type)));

        return searchSolrByPath(token, path, params, maxResults, true).getResults(type);
    }

    public SOLRResponse contentByPath(String token, String queryString) throws SolrServerException {
        SolrParams params = SolrUtils.parseQueryString(queryString);

        ModifiableSolrParams solrParams = new ModifiableSolrParams(params);
        String path = solrParams.get("p");
        solrParams.remove("p");

        return searchSolrByPath(token, path, solrParams,-1,true,null);
    }

    public SOLRResponse searchSolrByPath(String token, String path, SolrParams params, int maxResults, boolean includeDocs, Ordinamento... ordinamenti) throws SolrServerException {

        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        try {

            if (params != null) {
                solrParams.add(params);
//            for (Map.Entry<String, String> param : params.entrySet()) {
//                solrParams.set(param.getKey(), param.getValue());
//            }
            }

            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        if (maxResults>=0)
            solrParams.set("rows", maxResults);

        if (ordinamenti!=null && ordinamenti.length>0){
            Collection<String> ords = new ArrayList<>();
            for (int i=0;i<ordinamenti.length;i++) {
                ords.add(ordinamenti[i].getNomeCampo() + " " + ordinamenti[i].getTipo().toString().toLowerCase());
            }

            String sort = org.apache.commons.lang3.StringUtils.join(ords,",");

            solrParams.set("sort", sort);

        }
        String navPath = path.endsWith("/*")? path : ( path.endsWith("/") ? path + "*" : path + "/*");
        solrParams.set("PATH", navPath);
        if (!includeDocs)
            solrParams.add("fq", "-type:documento");

        String sede = ToolkitConnector.extractSedeFromToken(token);

        solrParams.add("fq",fqSession(token));

        try {
            return new SOLRResponse(getServer(sede).query(solrParams));
        } catch (IOException e) {
            throw new SolrServerException(e);
        }
    }

    public static String fqSession(String token){
        String ente = Utils.extractOptionalTokenKey(token, "ente","*");
        String aoo = Utils.extractOptionalTokenKey(token, "aoo","*");
        String q = String.format( "(+COD_ENTE:%s +COD_AOO:%s) type:(aoo ente user group)",ente,aoo);
        return q;
    }

    public <T extends ICIFSObject> Optional<T> openBySearch(String token, GenericCriteria criteria, Class<T> klass, boolean distributed) throws SolrServerException {
        return openBySearch(token, criteria, null, klass, distributed);
    }

    public <T extends ICIFSObject> Optional<T> openBySearch(String token, GenericCriteria criteria, HashMap<String, String> params, Class<T> klass, boolean distributed) throws SolrServerException {
        List<T> results = search(token, criteria, params, klass);

        if (results.size() > 1) {
            throw new IllegalArgumentException("Found more than one result");
        }

        if (results.isEmpty()) {
            return Optional.absent();
        }

        return Optional.of(results.get(0));
    }

    public <T extends ICIFSObject> List<T> search(String token, GenericCriteria criteria, Class<T> klass) throws SolrServerException {
        return search(token, criteria, null, klass);
    }

    public <T extends ICIFSObject> List<T> search(String token, GenericCriteria criteria,
                                                  HashMap<String, String> params,
                                                  Class<T> klass) throws SolrServerException {
        return genericSearch(token, criteria, params, klass, false);
    }

    public <T extends ICIFSObject> List<T> enterpriseSearch(String token, GenericCriteria criteria, Class<T> klass) throws SolrServerException {
        return enterpriseSearch(token, criteria, null, klass);
    }

    public <T extends ICIFSObject> List<T> enterpriseSearch(String token, GenericCriteria criteria,
                                                       HashMap<String, String> params,
                                                       Class<T> klass) throws SolrServerException {

        if (params == null) {
            params = new HashMap<>();
        }

//        if (criteria.getProperty("location") == null) {
//            try {
//                Map<String, String> sedi = ToolkitConnector.getListaSedi();
//                HashSet<String> shardValues = new HashSet<>();
//                for (String key : sedi.keySet()) {
//                    Optional<String> sede = shards.get(key);
//                    if (sede.isPresent()) {
//                        shardValues.add(sede.get());
//                    }
//                }
//                params.put("shards", Joiner.on(',').join(shardValues));
//            } catch (Exception e) {
//                throw new IllegalStateException(e);
//            }
//        }

        return genericSearch(token, criteria, params, klass, true);
    }

    private <T extends ICIFSObject> List<T> genericSearch(String token, GenericCriteria criteria, Class<T> klass,
                                                          boolean distributed) throws SolrServerException {
        return genericSearch(token, criteria, null, klass, distributed);
    }

    private <T extends ICIFSObject> List<T> genericSearch(String token, GenericCriteria criteria,
                                                          HashMap<String, String> params, Class<T> klass,
                                                          boolean distributed) throws SolrServerException {
        if (criteria == null) {
            throw new IllegalArgumentException("criteria cannot be null");
        }

        String type = typeMap.get(klass);
        if(!"*".equals(type)) {
            criteria.setProperty("type", type);
        }
        SOLRResponse rsp = rawSolrSearch(token, criteria, params, distributed);
        return rsp.getResults(klass);
    }

    public Map<String,Object> genericRequest(String path, SolrParams params){

        String url = ((HttpSolrClient) getLocalCore(ToolkitConnector.getSedeLocale())).getBaseURL();
        url = url.substring(0,url.lastIndexOf("/")) + path;

        ModifiableSolrParams mparams;
        if (params==null)
            mparams = new ModifiableSolrParams();
        else
            mparams = new ModifiableSolrParams(params);

        mparams.add("wt","json");

        url += mparams.toQueryString();

        Map<String,Object> res = RestTemplateFactory.getInstance().getRestTemplate().getForObject(url, Map.class);
        return res;
    }

    public NamedList adminRequest(String token,Map<String, String> params){
        String location = ToolkitConnector.getSedeLocale();

        ModifiableSolrParams solrParams = new ModifiableSolrParams();


        String ente = Utils.extractOptionalTokenKey(token, "ente",null);

        String aoo = Utils.extractOptionalTokenKey(token, "aoo",null);

        solrParams.set("ticket","admin");

        if (params != null) {

            for ( String key : params.keySet()) {
                Object val = params.get(key);
                if (val==null)
                    continue;
                String value = null;
                if (val instanceof String[])
                    value = ((String[])val)[0];
                else
                    value = val.toString();

                if ("${COD_ENTE}".equals(value))
                    solrParams.set(key, ente);
                else if ("${COD_AOO}".equals(value))
                    solrParams.set(key, aoo);
                else
                    solrParams.set(key, value);
            }
        }

        QueryResponse qr = null;
        try {
            qr = getLocalCore(location).query(solrParams, SolrRequest.METHOD.POST);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return qr.getResponse();
    }

    public SOLRResponse openByQuery(String token,String q) throws SolrServerException {
        return rawSolrSearch(token,null,Collections.singletonMap("q",q),false);
    }

    public SOLRResponse rawSolrSearch(String token, Map<String, String> params) throws SolrServerException {
        return rawSolrSearch(token,null,params,false);
    }

    public SOLRResponse rawSolrSearch(String token, GenericCriteria criteria, Map<String, String> params,
                                      boolean multiSede) throws SolrServerException {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        if(multiSede) {
            solrParams.set("shards", "ALL");
            solrParams.set("shards.tolerant", shards_tolerant);
        }

        try {
            solrParams.set("ticket", Utils.extractTokenKey(token, "uid"));
        } catch (KeyException e) {
            throw new IllegalStateException(e);
        }

        if (criteria != null) {
            int maxRows = criteria.removeMaxElementi();
            solrParams.set("rows", maxRows);

            String orderBy = criteria.removeOrderBy();
            if (!Strings.isNullOrEmpty(orderBy)) {
                int idx = orderBy.indexOf('=');
                assert idx > 0;
                String field = orderBy.substring(0, idx);
                String direction = orderBy.substring(idx + 1);

                Ordinamento.orderByEnum ord = Ordinamento.orderByEnum.valueOf(direction);
                if (ord == Ordinamento.orderByEnum.ASC) {
                    solrParams.set("sort", field + " asc");
                } else {
                    solrParams.set("sort", field + " desc");
                }
            }

            StringBuilder builder = new StringBuilder();

            String fullTextQuery = criteria.getFullTextQuery();
            if (!Strings.isNullOrEmpty(fullTextQuery)) {
                builder.append(fullTextQuery);
            }

            for (Map.Entry<String, String> entry : criteria.properties.entrySet()) {
                if (builder.length() > 0) {
                    builder.append(" AND ");
                }

                String value = entry.getValue();

                if (value == null) {
                    builder.append("NOT ");
                }

                builder.append(entry.getKey());
                builder.append(':');

                if (value == null) {
                    builder.append('*');
                } else if (value.contains(MULTIVALUE_SEP)) {
                    builder.append('(');
                    for (String val : Splitter.on(MULTIVALUE_SEP).omitEmptyStrings().split(value)) {
                        builder.append('\"');
                        builder.append(val);
                        builder.append('\"');
                        builder.append(' ');
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    builder.append(')');
                } else {
                    builder.append('\"');
                    builder.append(value);
                    builder.append('\"');
                }
            }

            for (Map.Entry<String, String> entry : criteria.getRawProperties().entrySet()) {
                if (builder.length() > 0) {
                    builder.append(" AND ");
                }

                builder.append(entry.getKey());
                builder.append(':');
                builder.append(entry.getValue());
            }

            if (builder.length() > 0) {
                solrParams.set("q", builder.toString());
            }
        }

        solrParams.set("fl", String.format("*,%s", SOLRResponse.SOLR_PATH_FIELD));

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                solrParams.set(param.getKey(), param.getValue());
            }
        }

        String location = null;
        if (params != null && params.containsKey("id")) {
            String id = params.get("id");
            if (id != null) {
                int idx = id.indexOf('.');
                if (idx > 0) {
                    location = id.substring(0, idx);
                }
            }
        }

        if (criteria != null) location = criteria.getProperty("location");

        if (location == null) {
            location = ToolkitConnector.extractSedeFromToken(token);
            /* FIX NON VALIDA
            try {
                location = ToolkitConnector.getSedeLocale();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            */
        }

//        if (!multiSede) {
//            solrParams.add("location", location);
//        }

//        solrParams.set("shards.tolerant", shards_tolerant);
//        solrParams.set("shards.info", shards_info);

        solrParams.add("fq",fqSession(token));

        QueryResponse qr = null;
        try {
            qr = getServer(location).query(solrParams, SolrRequest.METHOD.POST);
        } catch (IOException e) {
            throw new SolrServerException(e);
        }

//        List<Object> queredShards = qr.getHeader().getAll("shards.info");
//
//        if( location!=null && queredShards!=null ) {
//
//            for(Object singleShard : queredShards) {
//
//                if (singleShard!=null) {
//                    //TODO : cercare e cachare la sede mancante
//                }
//            }
//        }

        return new SOLRResponse(qr);
    }

    public <T extends ICIFSObject> List<T> searchByIds(String token, Class<T> type, boolean distributed, String... ids)
            throws SolrServerException {

        return searchByIds(token, Arrays.asList(ids), type, distributed);
    }

    public <T extends ICIFSObject> List<T> searchByIds(String token, Iterable<String> ids,
                                                       Class<T> type, boolean distributed)
            throws SolrServerException {

        HashMap<String, String> params = new HashMap<>();
        params.put("qt", "/get");

        List<T> ret = new ArrayList<>();

        int chunkSize = 100;

        StringBuilder builder = new StringBuilder();
        int i=0;
        for(String id : ids) {
            // Ad ogni ciclo viene concatenato l'id
            builder.append(id);

            if (++i % chunkSize == 0) {
                // Ogni 100 elementi viene eseguita la richiesta di ids e viene resettato lo StringBuilder
                params.put("ids", builder.toString());
                SOLRResponse resp = rawSolrSearch(token, null, params, distributed);
                ret.addAll(resp.getResults(type));
                builder = new StringBuilder();
            } else {
                // Altrimenti viene aggiunto il separatore
                builder.append(',');
            }
        }

        if (i % chunkSize != 0) {
            // L'ultima chiamata non è stata ancora eseguita e c'è un separatore da rimuovere alla fine
            builder.deleteCharAt(builder.length() - 1);
            params.put("ids", builder.toString());
            SOLRResponse resp = rawSolrSearch(token, null, params, distributed);
            ret.addAll(resp.getResults(type));
        }

        return ret;
    }

    private Map<String,Object> solrUpdateRequest(String userId, String handler, String id, Map<String,Object> doc, Map<String,Object> params, Map<String,Object> ... otherDocs ) {

        String location = ToolkitConnector.getSedeLocale();

        if (userId==null) {
            userId = ToolkitConnector.getSysUser();
        }

        if (id!=null && doc!=null)
            doc.put("id",id);

        UpdateRequest req;
        if (handler.equals("/dummy") || handler.equals("/delete") || handler.equals("/deleteByQuery"))
            req = new UpdateRequest("/update");
        else
            req = new UpdateRequest(handler);

        if (params!=null){
            ModifiableSolrParams mparams = new ModifiableSolrParams();
            for( String key : params.keySet()){
                Object v = params.get(key);

                if (v instanceof String[])
                    mparams.add(key, (String[]) v);
                else
                    mparams.add(key, v.toString());
            }
            req.setParams(mparams);
        }

        req.setParam("ticket",userId);

        if (handler.equals("/dummy")) {

        } else if (handler.equals("/deleteByQuery")) {
            req.deleteByQuery(id);
        } else if (handler.equals("/delete")){
            req.deleteById(id);
        } else {

            if (doc!=null){
            SolrInputDocument idoc = new SolrInputDocument();

            for ( String key : doc.keySet() ){
                idoc.addField(key,doc.get(key));
            }

            req.add(idoc);
        }

            if (otherDocs != null && otherDocs.length>0){
                for( int i=0; i<otherDocs.length; i++){
                    SolrInputDocument idoc2 = new SolrInputDocument();
                    for ( String key : otherDocs[i].keySet() ){
                        idoc2.addField(key,otherDocs[i].get(key));
                    }
                    req.add(idoc2);
                }
            }
        }

        NamedList<?> result = null;
        try {
            result = getServer(location).request(req);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NamedList<?> header =(NamedList<?>) result.get("responseHeader");

        if (header==null || header.get("status")==null)
            throw new RuntimeException("invalid solr response");

        int status = ((Number) header.get("status")).intValue();

        if (status == 0 ){
            Map res = (Map<String,Object>) result.get("processAdd");
            if(res==null){
                return new HashMap<>();
            }
            return res;
        } else {
            NamedList<?> error =(NamedList<?>) result.get("error");

            String msg = (String) error.get("msg");

            if (msg==null)
                msg = "errore sconosciuto";

            throw new SolrException(SolrException.ErrorCode.getErrorCode(status),msg);
        }
    }

    public void delete(String userId, String id){
        solrUpdateRequest(userId,"/delete",id,null,null);
    }

    public void delete(String id){
        solrUpdateRequest(null,"/delete",id,null,null);
    }

    public void deleteByQuery(String query){
        solrUpdateRequest(null,"/deleteByQuery",query,null,null);
    }

    public void commit(){
        Map params = new HashMap();
        params.put("commit","true");
        params.put("waitSearcher","true");

        //params.put("softCommit","true");
        solrUpdateRequest(null,"/dummy",null,null,params);
    }

    public void update(Map<String,Object> doc){
        solrUpdateRequest(null,"/update",null,doc,null);
    }

    public void multiUpdate(String userId, Map<String,Object> params, Map<String,Object> ... docs){
        solrUpdateRequest(userId,"/update",null,null,params, docs);
    }

    public void multiUpdate(Map<String,Object> params, Map<String,Object> ... docs){
        solrUpdateRequest(null,"/update",null,null,params, docs);
    }

    public void update(String id, Map<String,Object> doc){
        solrUpdateRequest(null,"/update",id,doc,null);
    }

    public void update(String userId, String id, Map<String,Object> doc){
        solrUpdateRequest(userId,"/update",id,doc,null);
    }

    public void update(String userId, String id, Map<String,Object> doc, Map<String,Object> params){
        solrUpdateRequest(userId,"/update",id,doc,params);
    }

    public String insert(Map<String,Object> doc){
        return (String) solrUpdateRequest(null,"/create",null,doc,null).get("id");
    }

    public String insert(String id, Map<String,Object> doc){
        return (String) solrUpdateRequest(null,"/create",id,doc,null).get("id");
    }

    public String insert(String userId, String id, Map<String,Object> doc){
        return (String) solrUpdateRequest(userId,"/create",id,doc,null).get("id");
    }

    public String insert(String userId, String id, Map<String,Object> doc, Map<String,Object> params){
        return (String) solrUpdateRequest(userId,"/create",id,doc,params).get("id");
    }

    public String insert_or_update(Map<String,Object> doc) {
        return insert_or_update(null,null,doc,null);
    }

    public String insert_or_update(String id, Map<String,Object> doc) {
        return insert_or_update(null,id,doc,null);
    }

    public String insert_or_update(String userId, String id, Map<String,Object> doc) {
        return insert_or_update(userId,id,doc,null);
    }

    public String insert_or_update(String userId, String id, Map<String,Object> doc, Map<String,Object> params){

        if (id==null)
            id = (String) doc.get("id");

        if (id==null)
            throw new RuntimeException("id must be specified");

        try{
            SolrDocument d = get(userId,id,"id");

            update(userId,id,doc,params);
            return id;
        } catch(SolrException se){
            if (se.code() == SolrException.ErrorCode.NOT_FOUND.code)
                return insert(userId,id,doc,params);
            else
                throw se;
        }
    }

    public SolrDocument get(String id){
        return get(null,id,null);
    }

    public SolrDocument get(String userId,String id, String... fl){
        String location = ToolkitConnector.getSedeLocale();

        if (userId==null) {
            userId = ToolkitConnector.getSysUser();
        }

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("id",id);
        solrParams.set("ticket", userId);
        solrParams.set( CommonParams.QT, "/get");
        solrParams.set("fl", fl);

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(location).query(solrParams));
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SolrDocument doc = (SolrDocument) rsp.getResponse().get("doc");

        if (doc==null)
            throw new SolrException(SolrException.ErrorCode.NOT_FOUND,id +" not found");

        return doc;
    }

    public SolrDocument getByHandler(String handler,String userId,String id, String... fl){
        String location = ToolkitConnector.getSedeLocale();

        if (userId==null) {
            userId = ToolkitConnector.getSysUser();
        }

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.set("id",id);
        solrParams.set("ticket", userId);
        solrParams.set( CommonParams.QT, handler);
        solrParams.set("fl", fl);

        SOLRResponse rsp = null;
        try {
            rsp = new SOLRResponse(getServer(location).query(solrParams));
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SolrDocument doc = (SolrDocument) rsp.getResponse().get("doc");

        if (doc==null)
            throw new SolrException(SolrException.ErrorCode.NOT_FOUND,id +" not found");

        return doc;
    }

    public SolrDocumentList buildResultsList(SOLRResponse rsp){

        SolrDocumentList results = rsp.getResults();

        String prefixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.prefix");
        prefixVirtualId = prefixVirtualId != null ? prefixVirtualId:"#";

        String suffixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.suffix");
        suffixVirtualId = suffixVirtualId != null ? suffixVirtualId:"";

        String format = prefixVirtualId + "%s" + suffixVirtualId;

        List<FacetField> facetFields = rsp.getFacetFields();
        if ( facetFields!=null && facetFields.size() > 0 )
        {
            SimpleOrderedMap params = (SimpleOrderedMap)(rsp.getHeader().getAll("params").toArray())[0];
            String parentPath = ""+params.get("PATH");

            for (  FacetField ff : facetFields ) {

                String field = ff.getName();

                List<FacetField.Count> counts = ff.getValues();

                for (FacetField.Count c : counts) {

                    long cnt = c.getCount();
                    String value = c.getName();

                    String name= c.toString();

                    SolrDocument doc = createVirtualDocument(parentPath, name, format);

                    results.add(doc);

                }
            }
        }

        return results;
    }

    public SolrDocument createVirtualDocument( String parentPath, String docName, String format ){

        SolrDocument doc = new SolrDocument();

        String name = String.format(format, docName);

        doc.setField("id", name +"@virtual");
        doc.setField("name",name);

        if( parentPath.endsWith("*") )
            parentPath = parentPath.substring(0, parentPath.length()-2);

        if( parentPath.endsWith("/") )
            parentPath = parentPath.substring(0, parentPath.length()-2);

        doc.setField("VIRTUAL_PATH", parentPath + "/" + doc.getFieldValue("name") );

        return doc;
    }

    public static String encodeId(String token)
    {
        final String idChars = "\\/:*?\"<>!.@";
        final char encPerc = '%';

        String specialChars = idChars + encPerc;

        StringBuilder s = new StringBuilder(token.length());

        CharacterIterator it = new StringCharacterIterator(token);
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            if (specialChars.indexOf(ch)!=-1)
                s.append("" + encPerc).append(Integer.toHexString((int) ch));
            else
                s.append(ch);
        }

        token = s.toString();

        return token;
    }
}
