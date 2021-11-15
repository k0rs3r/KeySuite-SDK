package it.kdm.doctoolkit.services;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.path.*;
import it.kdm.doctoolkit.utils.SOLRResponse;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.utils.CacheManager;
import it.kdm.doctoolkit.utils.TreeViewProfile;

import it.kdm.orchestratore.session.ActorsCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mvel2.MVEL;
import org.mvel2.optimizers.OptimizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.security.KeyException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lorenxs on 1/16/14.
 */
public class PathInterface {

    private static final String CLASSIFICA = "CLASSIFICA";
    //public static final String PARENT_CLASSIFICA = "parentClassifica";
    private static final String PROGRESSIVO = "PROGR_FASCICOLO";
    //public static final String PROGRESSIVO_PADRE = "progressivoPadre";
    private static final String ANNO = "ANNO_FASCICOLO";

    private static final String USER_ID = "USER_ID";
    private static final String GROUP_ID = "GROUP_ID";
    public static final String NOT_FOUND = "$NO~VALUE$";
    public static final String PATH = "PATH";
    public static final String VIRTUAL_PATH = "virtual_path";
    public static final String DOCNUM = "DOCNUM";
    public static final String HAS_TILDE = "HAS_TILDE";
    public static final String INHERITS_ACL = "INHERITS_ACL";
    public static final String AOO = "COD_AOO";
    public static final String DEFAULT_PROFILE = "default";

    private static final Logger log = LoggerFactory.getLogger(PathInterface.class);
    public static final String LOCATION = "location";
    public static final String FACET_LIMIT = "10000";
    public static final String ADMIN_USER = "admin";

    private final String ente;

    private final PathMatchingResourcePatternResolver resolver;

    private static ConcurrentMap<String, TreeViewProfile> conf = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, HashMap> profileCache = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Serializable> scriptCache = new ConcurrentHashMap<>();
    private final TokenGeneratorCallback tokenGenerator;

    private class CifsComparator implements Comparator<ICIFSObject> {

        private final Ordinamento.orderByEnum ordType;

        public CifsComparator(Ordinamento.orderByEnum orderType) {
            ordType = orderType;
        }

        @Override
        public int compare(ICIFSObject c1, ICIFSObject c2) {
            int i = c1.getName().compareTo(c2.getName());
            if (ordType == Ordinamento.orderByEnum.ASC) {
                return i;
            } else {
                return -i;
            }
        }
    }

    private class GenericComparator implements Comparator<ICIFSObject> {

        private final String ordName;
        private final Ordinamento.orderByEnum ordType;

        public GenericComparator(Ordinamento orderType) {
            ordName = orderType.getNomeCampo();
            ordType = orderType.getTipo();
        }

        @Override
        public int compare(ICIFSObject c1, ICIFSObject c2) {
            String name1 = c1.getProperty(ordName);
            String name2 = c2.getProperty(ordName);

            int i;
            if (name1 == null && name2 == null) {
                i=0;
            } else if (name1 == null) {
                i=1;
            } else if (name2 == null) {
                i=-1;
            } else {
                i = name1.compareTo(name2);
            }

            if (ordType == Ordinamento.orderByEnum.ASC) {
                return i;
            } else {
                return -i;
            }
        }
    }

    public interface TokenGeneratorCallback {
        public class TokenGenerationException extends Exception {
            public TokenGenerationException(String msg) {
                super(msg);
            }
            public TokenGenerationException(Throwable t) {
                super(t);
            }
        }

        public String getToken(String realPath, String virtualPath) throws TokenGenerationException;
        public String getToken(String sede) throws TokenGenerationException;
    }

    private class DefaultTokenGeneratorCallback implements TokenGeneratorCallback {

        private final String token;

        public DefaultTokenGeneratorCallback(String token) {
            this.token = token;
        }

        @Override
        public String getToken(String realPath, String virtualPath) {
            return token;
        }

        @Override
        public String getToken(String sede) {
            return token;
        }
    }

    public PathInterface(TokenGeneratorCallback callback, String ente) throws DocerApiException, IOException {
        this.tokenGenerator = callback;
        this.ente = ente;

        resolver = new PathMatchingResourcePatternResolver();

        OptimizerFactory.setDefaultOptimizer("reflective");
    }

    public PathInterface(String token, String ente) throws DocerApiException, IOException {
        TokenGeneratorCallback callback = new DefaultTokenGeneratorCallback(token);
        this.tokenGenerator = callback;
        this.ente = ente;

        resolver = new PathMatchingResourcePatternResolver();

        OptimizerFactory.setDefaultOptimizer("reflective");
    }

    public String getToken(String realPath, String virtualPath) throws TokenGeneratorCallback.TokenGenerationException {
        return tokenGenerator.getToken(realPath, virtualPath);
    }

    public String getToken(String sede) throws TokenGeneratorCallback.TokenGenerationException {
        return tokenGenerator.getToken(sede);
    }

    public String getToken() throws TokenGeneratorCallback.TokenGenerationException {
        return getToken(null, "");
    }

    public String getEnte() {
        return ente;
    }

    private String unixPathConv(String unixPath) {
        return Joiner.on('\\').join(
                Splitter.on('/').split(unixPath));
    }

    public <T extends ICIFSObject> T openByPath(String path, Class<T> klass) throws IOException, DocerApiException {
        ICIFSObject obj = openByPath(path);
        if (!klass.isInstance(obj)) {
            throw new DocerApiException(String.format("%s is not of type %s", path, klass.getName()), 500);
        }

        return klass.cast(obj);
    }

    public <T extends ICIFSObject> T openByUncachedPath(String path, Class<T> klass) throws IOException, DocerApiException {
        ICIFSObject obj = openByUncachedPath(path);
        if (!klass.isInstance(obj)) {
            throw new DocerApiException(String.format("%s is not of type %s", path, klass.getName()), 500);
        }

        return klass.cast(obj);
    }

    public <T extends ICIFSObject> T openByPathFE(String path, Class<T> klass) throws IOException, DocerApiException {
        return openByPath(unixPathConv(path), klass);
    }

    public ICIFSObject openByPath(String path) throws IOException, DocerApiException {
        return openByPath(path, true);
    }

    private ICIFSObject openByPath(String path, boolean applyShift) throws IOException, DocerApiException {
        path = normalizePath(path);

        try {
            if (isFakeDelete(path) || isDeleted(path)) {
                throw new FileNotFoundException(path);
            } else {
                Optional cachedObj = CacheManager.cifsCache().get(path);
                if (cachedObj.isPresent() && cachedObj.get() instanceof ICIFSObject) {
                    ICIFSObject obj = (ICIFSObject) cachedObj.get();
                    if (obj.getLocation().equalsIgnoreCase(extractSede(getToken()))) {
                        return obj;
                    }
                }
                return openByUncachedPath(path, applyShift);
            }
        } catch (TokenGeneratorCallback.TokenGenerationException | CacheException e) {
            throw new IOException(e);
        }
    }

    private boolean isRoot(String path) {
        return path.isEmpty() || path.equals("\\");
    }

    public ICIFSObject openByUncachedPath(String path) throws IOException, DocerApiException {
        return openByUncachedPath(path, true);
    }

    private ICIFSObject openByUncachedPath(String path, boolean applyShift) throws IOException, DocerApiException {
        try {
            HashMap<String, String> context;
            String profile = null;

            path = normalizePath(path);

            if (isRoot(path)) {
                context = new HashMap<>();
                context.put("type", Root.TYPE);
            } else {
                profile = Utils.parseProfileFromPath(path);

                if (applyShift) {
                    path = restoreShift(path, profile);
                }

                HashMap<String, Object> env = new HashMap<>();
                env.put("ente", getDescriptionEnte(getEnte()));
                env.put("path", path);
                env.put("sede", extractSede(getToken()));

                Serializable contextBuilder = getContextBuilder(profile);
                context = (HashMap<String, String>) MVEL.executeExpression(contextBuilder, env);
            }

            String type = context.get("type");
            context.put(VIRTUAL_PATH, path);

            ICIFSObject obj = null;
            try {
                obj = openByPath(context, type);
            } catch (FileNotFoundException e) {
                if (applyShift) {
                    path = applyShift(path, profile);
                }
                FileNotFoundException ex = new FileNotFoundException(path);
                ex.setStackTrace(e.getStackTrace());
                throw ex;
            }

            obj.setShare(profile);

            if (context.containsKey("BusinessType"))
                obj.setBusinessType(context.get("BusinessType"));

            if (obj instanceof Root) {
                // Path puo' essere la sola root
                obj.setParentPath("");
                obj.setName("");
            } else {

                if (applyShift) {
                    path = applyShift(path, profile);
                }
                obj.setParentPath(Utils.getPathParent(path));
                String name = Utils.getPathName(path);

                obj.setName(name);
            }


            CacheManager.cifsCache().put(obj.getFullPath(), obj, getCustomCacheAttributes(profile, obj));
            return obj;
        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    private IElementAttributes getCustomCacheAttributes(String profile, ICIFSObject obj) throws IOException, DocerApiException, CacheException {
        IElementAttributes defaultElementAttributes = CacheManager.cifsCache().getDefaultElementAttributes();

        if (!Strings.isNullOrEmpty(profile)) {
            Optional<Serializable> script = getCacheScript(profile);
            if (script.isPresent()) {
                HashMap<String, Object> env = new HashMap<>();
                env.put("obj", obj);
                env.put("cacheAttributes", defaultElementAttributes);

                return (IElementAttributes) MVEL.executeExpression(script.get(), env);
            }
        }

        return defaultElementAttributes;
    }

    private boolean isShiftApplicable(String path, String profile) throws IOException, DocerApiException {
        String shift = getShift(profile);
        if (!Strings.isNullOrEmpty(shift)) {
            return Utils.removeProfileFromPath(path).startsWith(shift);
        }

        return true;
    }

    private String applyShift(String path, String profile) throws IOException, DocerApiException {
        String shift = getShift(profile);
        if (!Strings.isNullOrEmpty(shift)) {
            //path = Utils.removeProfileFromPath(path).replace(shift, "");
        	path = Utils.removeProfileFromPath(path);

            while( StringUtils.indexOf(path, shift)>-1 &&
                    StringUtils.indexOf(path, shift)<2 )
        	    path = StringUtils.replace(path, shift, "", 1);

            StringBuilder sb = new StringBuilder();
            sb.append('\\');
            sb.append(profile);
            if (!path.isEmpty() && path.charAt(0) != '\\') {
                sb.append('\\');
            }

            sb.append(path);
            path = sb.toString();
        }

        return path;
    }

    private String getShift(String profile) throws IOException, DocerApiException {
        return (String)getProfile(profile).get("shift");
    }

    private String restoreShift(String path, String profile) throws IOException, DocerApiException {
        String shift = getShift(profile);
        if (!Strings.isNullOrEmpty(shift)) {
            StringBuilder sb = new StringBuilder();
            sb.append('\\');
            sb.append(profile);

            if (shift.charAt(0) != '\\') {
                sb.append('\\');
            }
            sb.append(shift);

            path = Utils.removeProfileFromPath(path);
            if (!path.isEmpty() && path.charAt(0) != '\\') {
                sb.append('\\');
            }
            sb.append(path);

            path = sb.toString();
        }

        return path;
    }

    public ICIFSObject openByPathFE(String path) throws IOException, DocerApiException {
        return openByPath(unixPathConv(path));
    }

    private Serializable getContextBuilder(String profile) throws IOException, DocerApiException {
        String ctxBuilder = (String) getProfile(profile).get("buildContext");
        return getScript(ctxBuilder);
    }

    private Serializable getPathBuilder(String profile) throws IOException, DocerApiException {
        String ctxBuilder = (String) getProfile(profile).get("buildPath");
        return getScript(ctxBuilder);
    }

    private Optional<Serializable> getCacheScript(String profile) throws IOException, DocerApiException {
        String scriptPath = (String) getProfile(profile).get("cacheCustomization");
        if (scriptPath == null) {
            return Optional.absent();
        }
        return Optional.of(getScript(scriptPath));
    }

    public ArrayList getNavigationConfig(String profileName, String tag) throws IOException, DocerApiException {
        HashMap tagConf = getTagConfig(profileName, tag);
        return (ArrayList)tagConf.get("NAV");
    }

    private HashMap getTagConfig(String profileName, String tag) throws IOException, DocerApiException {
        HashMap profileConfig = getProfile(profileName);
        String searchName = (String) profileConfig.get("search");
        if (Strings.isNullOrEmpty(searchName)) {
            searchName = "default";
        }

        HashMap searches = (HashMap) getConfiguration().getSearches();
        HashMap search = (HashMap) searches.get(searchName);

        return (HashMap) search.get(tag);
    }

    public Optional<ArrayList> getContentConfig(String profileName, String tag) throws IOException, DocerApiException {
        HashMap tagConf = getTagConfig(profileName, tag);
        if (tagConf != null) {
            return Optional.of((ArrayList) tagConf.get("CONTENT"));
        } else {
            return Optional.absent();
        }
    }

    private int getMaxResults(String profile) throws IOException, DocerApiException {
        return (Integer) getProfile(profile).get("maxResults");
    }

    private HashMap getProfile(String profile) throws IOException, DocerApiException {
        boolean getDefault = profile.equals(getDescriptionEnte(ente));

        String key = Joiner.on('|').join(ente, profile);
        if (!profileCache.containsKey(key)) {
            TreeViewProfile configuration = getConfiguration();
            HashMap profileConf;
            if (getDefault) {
                profileConf = configuration.getProfileConfig(configuration.getDefaultProfile());
            } else {
                profileConf = configuration.getProfileConfig(profile);
            }
            if (profileConf == null) {
                throw new FileNotFoundException("Invalid profile specified: " + profile);
            }

            profileCache.putIfAbsent(key, profileConf);
        }

        return profileCache.get(key);
    }

    private Serializable getScript(String location) throws IOException {
        location = resolveVariables(location);
        if (!scriptCache.containsKey(location)) {
            Resource res = resolver.getResource(location);
            String script = IOUtils.toString(res.getInputStream());
            Serializable compiled = MVEL.compileExpression(script);
            scriptCache.putIfAbsent(location, compiled);
        }

        return scriptCache.get(location);
    }

    private Pattern resourceVariablesPattern = Pattern.compile("\\$\\{(.+?)\\}");

    private String resolveVariables(String location) {
        Matcher matcher = resourceVariablesPattern.matcher(location);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            String variable = matcher.group(1);
            matcher.appendReplacement(sb, System.getProperty(variable));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
/*
    private ICIFSObject openByCriteria(HashMap<String, String> args, String type) throws DocerApiException, FileNotFoundException {
        if (Strings.isNullOrEmpty(type)) {
            throw new FileNotFoundException();
        }

        String virtualPath = args.remove(VIRTUAL_PATH);

        try {
            switch (type) {
                case Root.TYPE:
                    Root root = new Root();
                    root.setEnte(ente);
                    Optional<String> rootName = ToolkitConnector.getRootName();
                    if (rootName.isPresent()) {
                        root.setFEName(rootName.get());
                    } else {
                        root.setFEName(getDescriptionEnte(ente));
                    }
                    return root;
                case Profile.TYPE:
                    Profile profile = new Profile();
                    profile.setEnte(getEnte());
                    return profile;
                case Titolario.TYPE:
                    TitolarioCriteria tc = new TitolarioCriteria();
                    tc.properties.putAll(args);
                    List<Titolario> titolari = DocerService.ricercaTitolari(getToken(args.get(PATH), virtualPath), tc);
                    return returnObject(titolari);
                case Fascicolo.TYPE:
                    FascicoloCriteria fc = new FascicoloCriteria();
                    fc.properties.putAll(args);
                    List<Fascicolo> ret = DocerService.ricercaFascicoli(getToken(args.get(PATH), virtualPath), fc);
                    return returnObject(ret);
                case Documento.TYPE:
                    return DocerService.recuperaProfiloDocumento(getToken(args.get(PATH), virtualPath), args.get(PATH));
                case User.TYPE:
                    return DocerService.recuperaUtente(getToken(args.get(PATH), virtualPath), args.get(USER_ID));
                case Group.TYPE:
                    return DocerService.recuperaGruppo(getToken(args.get(PATH), virtualPath), args.get(GROUP_ID));
                case Cartella.TYPE:
                    CartellaCriteria cc = new CartellaCriteria();
                    cc.properties.putAll(args);
                    List<Cartella> cartelle = DocerService.ricercaCartella(getToken(args.get(PATH), virtualPath), cc);
                    if (cartelle.isEmpty()) {
                        throw new FileNotFoundException();
                    }
                    return cartelle.get(0);
                default:

                    ICIFSObject parent;
                    try {
                        parent = openByPath(Utils.getPathParent(virtualPath), false);
                    } catch (IOException e) {
                        throw makeFileNotFoundException(e);
                    }

                    VirtualObject virtualObject = new VirtualObject(type);
                    virtualObject.properties.putAll(parent.properties);
                    virtualObject.properties.putAll(args);

                    virtualObject.setEnte(getEnte());
                    virtualObject.setAoo(parent.properties.get("COD_AOO"));

                    return virtualObject;
            }
        } catch (DocerApiException e) {
            if (e.errorCode == 404) {
                throw makeFileNotFoundException(e);
            } else {
                throw e;
            }
        } catch (IOException | TokenGeneratorCallback.TokenGenerationException e) {
            throw makeFileNotFoundException(e);
        }
    }
*/
    private FileNotFoundException makeFileNotFoundException(Exception e) {
        FileNotFoundException ex = new FileNotFoundException(e.getMessage());
        ex.setStackTrace(e.getStackTrace());
        return ex;
    }

    private ICIFSObject openByPath(HashMap<String, String> args, String type) throws DocerApiException, FileNotFoundException {
        if (Strings.isNullOrEmpty(type)) {
            throw new FileNotFoundException();
        }

        String virtualPath = args.remove(VIRTUAL_PATH);

        SOLRClient client = new SOLRClient();

        try {
            String token = getToken(args.get(PATH), virtualPath);
            switch (type) {
                case Root.TYPE:
                    Root root = new Root();
                    root.setEnte(ente);
                    Optional<String> rootName = ToolkitConnector.getRootName();
                    if (rootName.isPresent()) {
                        root.setFEName(rootName.get());
                    } else {
                        root.setFEName(getDescriptionEnte(ente));
                    }
                    return root;
                case Profile.TYPE:
                    Profile profile = new Profile();
                    profile.setEnte(getEnte());
                    profile.setProperty(PATH, args.get(PATH));
                    return profile;
                case it.kdm.doctoolkit.model.AOO.TYPE:
                    return client.openByPath(token, args.get(PATH), it.kdm.doctoolkit.model.AOO.class);
                case Titolario.TYPE:
                    List<String> path = Splitter.on('\\').splitToList(args.get(PATH));
                    StringBuilder sb = new StringBuilder();
                    sb.append("\\.\\");
                    Joiner.on('\\').appendTo(sb, path.subList(2, path.size()));
                    return client.openByPath(token, sb.toString(), Titolario.class, false);
                case Fascicolo.TYPE:
                case Cartella.TYPE:
                case Documento.TYPE:
                    if (args.containsKey(DOCNUM)) {
                        token = getToken(args.get("sedeStr"));
                        return client.openByDocnum(token, args.get(DOCNUM));
                    } else {
                        SolrDocument doc = client.openSolrByPath(token, args.get(PATH));
                        Object solrType = doc.get("type");
                        if(solrType == null) {
                            throw new FileNotFoundException(virtualPath);
                        }
                        Class<? extends ICIFSObject> klass;
                        if (solrType.equals("documento")) {
                            klass = Documento.class;
                        } else if (solrType.equals("fascicolo")) {
                            klass = Fascicolo.class;
                        } else if (solrType.equals("folder")) {
                            klass = Cartella.class;
                        } else {
                            throw new FileNotFoundException(virtualPath);
                        }

                        return SOLRResponse.convertSolrDoc(doc, klass);
                    }
                case User.TYPE:
                    return DocerService.recuperaUtente(token, args.get(USER_ID));
                case Group.TYPE:
                    return DocerService.recuperaGruppo(token, args.get(GROUP_ID));
                default:

                    ICIFSObject parent;
                    try {
                        parent = openByPath(Utils.getPathParent(virtualPath), false);
                    } catch (IOException e) {
                        throw makeFileNotFoundException(e);
                    }

                    VirtualObject virtualObject = new VirtualObject(type);
                    virtualObject.setBusinessType(type);
                    virtualObject.properties.putAll(parent.properties);
                    virtualObject.properties.putAll(args);

                    virtualObject.setEnte(getEnte());
                    virtualObject.setAoo(parent.properties.get("COD_AOO"));

                    return virtualObject;
            }
        } catch (DocerApiException e) {
            if (e.errorCode == 404) {
                throw makeFileNotFoundException(e);
            } else {
                throw e;
            }
        } catch (IOException | TokenGeneratorCallback.TokenGenerationException | SolrServerException e) {
            throw makeFileNotFoundException(e);
        }
    }

    private <T> T returnObject(List<T> ret) throws FileNotFoundException {
        if (ret.isEmpty()) {
            throw new FileNotFoundException();
        } else {
            return ret.get(0);
        }
    }

    private ICIFSObject returnObject(ICIFSObject ret) throws FileNotFoundException {
        if (ret==null) {
            throw new FileNotFoundException();
        } else {
            return ret;
        }
    }

    private <T extends ICIFSObject> Optional<T> initializeObject(T object, String parentPath) throws IOException, DocerApiException, CacheException {
        String profile = Utils.parseProfileFromPath(parentPath);
        if (object.properties.containsKey(PATH)) {
            buildPath(object, profile);
        } else {
            object.setParentPath(applyShift(parentPath, profile));
            object.setShare(profile);
        }

        String path = object.getFullPath();
        if (isFakeDelete(path) || isDeleted(path)) {
            return Optional.absent();
        }

        // This is needed to maintain the correct file size
        if (object instanceof Documento) {
            Optional obj = CacheManager.cifsCache().get(path);
            if (obj.isPresent() && obj.get() instanceof ICIFSObject) {
                ICIFSObject cached = (ICIFSObject) obj.get();
                if (cached.properties.containsKey("content_size")) {
                    object.properties.put("content_size",
                            cached.properties.get("content_size"));
                } else if (cached.properties.containsKey("CONTENT_SIZE")) {
                    object.properties.put("CONTENT_SIZE",
                            cached.properties.get("CONTENT_SIZE"));
                }
            }
        }

        CacheManager.cifsCache().put(path, object, getCustomCacheAttributes(profile, object));
        return Optional.of(object);
    }

    private <T extends Collection<ICIFSObject>> T initializeObject(ICIFSObject object, String parentPath,
                                                                   T results, boolean testOpen)
            throws IOException, CacheException, DocerApiException {

        Optional<ICIFSObject> objectOptional = initializeObject(object, parentPath);
        if (objectOptional.isPresent()) {
            try {
                if (testOpen) {
                    openByPath(parentPath + '\\' + objectOptional.get().getName(), false);
                }
                results.add(object);

            } catch (FileNotFoundException ex) {
                // Do nothing
            }
        }

        return results;
    }

    private <T extends Collection<ICIFSObject>> T initializeObject(ICIFSObject object, String parentPath, T results) throws IOException, CacheException, DocerApiException {
        return initializeObject(object, parentPath, results, false);
    }

    private List<ICIFSObject> searchByTag(HashMap<String, String> args, String tag, List config,
                                          String path, String shiftedPath,
                                          int maxResults, Ordinamento... ordinamenti)
            throws IOException, DocerApiException {
        try {
            List<ICIFSObject> results = new ArrayList<>();

            // Check if parent exists
            String type = args.get("type");
            ICIFSObject obj = openByPath(shiftedPath);

            Ordinamento ord = new Ordinamento();
            ord.setNomeCampo("name");
            ord.setTipo(Ordinamento.orderByEnum.ASC);

            if (ordinamenti != null && ordinamenti.length > 0) {
                ord = ordinamenti[0];
            }

            SOLRClient client = new SOLRClient();

            String virtualPath = args.get(VIRTUAL_PATH);

            for (Object item : config) {
                String searchName = item.toString();

                String token = getToken(obj.getProperty(PATH), virtualPath);
                if (searchName.equals("nav-titolario-search") ||
                        searchName.equals("nav-sub-titolario-search")) {
					
				log.info("nav-sub-titolario-search path:{} obj:{}" , path, obj.getName() );
				
                GenericCriteria criteria = new GenericCriteria();
                criteria.properties.clear();

                //criteria.setProperty("COD_AOO", obj.properties.get(AOO));
                //criteria.setProperty("type", "fascicolo");

				criteria.setMaxElementi(maxResults);
                //criteria.setMaxElementi(0);

                HashMap<String, String> params = new HashMap<>();
                params.put("qt", "/select");
				//params.put("qt", "/navigation");

                // letterFilter contiene una o più lettere maiuscole
                String letterFilter = obj.getName();

                if (letterFilter.matches("[a-zA-Z].*")) {
                	criteria.setRawProperty("name" , letterFilter + "*" );
                    //params.put("f.parentIds.regex", String.format("(?i:[%s].*)", letterFilter));
                } else {
					criteria.setRawProperty("name" , "/[^a-zA-Z].*/" );
                    //params.put("f.parentIds.regex", "[^a-zA-Z].*");
                }
				
				params.put("max-age", "300" );
				
				//params.put("fq", String.format("{!join from=parent to=id}+type:fascicolo +depth:3 +COD_AOO:%s", obj.properties.get(AOO) )  );
				params.put("cljoin", obj.properties.get(AOO) );

                criteria.setOrderBy(ord.getNomeCampo(), ord.getTipo());
				
				log.info("params:\n{}",params  );
				log.info("criteria:\n{}",criteria  );
				

                List<Titolario> titolari = client.search(token, criteria, params, Titolario.class);

             /* distinct dei titolari duplicati restituiti dalla nuova ricerca */ 
                HashMap<String, Titolario> appo = new HashMap<String, Titolario>();
				for (Titolario t : titolari) {
					appo.put(t.getName(), t);
                }
				
				titolari = new ArrayList<Titolario>(appo.values());
			 /* fine distinct */
				
				for (Titolario t : titolari) {
                    results = initializeObject(t, path, results);
                }
                
				/*
				
				SOLRResponse resp = client.rawSolrSearch(token, criteria, params, true);
				
				FacetField field = resp.getFacetField("parentIds");

                if (field.getValues() != null) {
                    List<String> ids = new ArrayList<>();
                    Pattern facetFormat = Pattern.compile("<([^<>]+)>");
                    for (FacetField.Count c : field.getValues()) {
                        String name = c.getName();
                        Matcher matcher = facetFormat.matcher(name);
                        if (matcher.find()) {
                            ids.add(matcher.group(1));
                        }
                    }

                    List<Titolario> titolari = client.searchByIds(token, ids, Titolario.class, true);

                    Set<String> classifiche = new HashSet<>();

                    Collections.sort(titolari, new GenericComparator(ord));

                    if (maxResults < titolari.size()) {
                        titolari = titolari.subList(0, maxResults);
                    }

                    for (Titolario t : titolari) {
                        if (!classifiche.contains(t.getName())) {
                            results = initializeObject(t, path, results);
                            classifiche.add(t.getName());
                        }
                    }
                }*/

                } else if (searchName.equals("nav-titolario-search-default") ||
                        searchName.equals("nav-sub-titolario-search-default")) {
                    TitolarioCriteria titolario = new TitolarioCriteria();
                    titolario.properties.clear();

                    titolario.setProperty("COD_ENTE", getEnte());
                    titolario.setProperty("COD_AOO", obj.properties.get(AOO));
                    if (searchName.equals("nav-titolario-search-default")) {
                        titolario.setProperty("PARENT_CLASSIFICA", "");
                    } else {
                        titolario.setProperty("PARENT_CLASSIFICA", obj.getProperty(CLASSIFICA));
                    }

                    //titolario.setRawProperty("DES_TITOLARIO", obj.getProperty("DES_TITOLARIO"));

                    //titolario.setProperty(LOCATION, obj.getLocation());

                    titolario.setMaxElementi(maxResults);
                    titolario.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    List<Titolario> titolari = client.search(token, titolario, Titolario.class);


                    for (Titolario t : titolari) {
//                        if (!Strings.isNullOrEmpty(letterFilter)) {
//                            String desc = t.getDescrizione();
//                            if (!Strings.isNullOrEmpty(desc)
//                                    && !letterFilter.equalsIgnoreCase(desc.substring(0,1))) {
//                                continue;
//                            }
//                        }
                        results = initializeObject(t, path, results);
                    }

                } else if (searchName.equals("nav-fascicolo-search")) {
                    FascicoloCriteria fascicolo = new FascicoloCriteria();

                    fascicolo.properties.clear();

                    fascicolo.setProperty("COD_ENTE", getEnte());
                    fascicolo.setProperty("COD_AOO", obj.properties.get(AOO));
                    fascicolo.setProperty(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    //fascicolo.setProperty(ANNO, obj.properties.get(ANNO));
                    fascicolo.setProperty("PARENT_PROGR_FASCICOLO", "");

                    fascicolo.setMaxElementi(maxResults);
                    fascicolo.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    List<Fascicolo> fascicoli = client.search(token, fascicolo, Fascicolo.class);

                    for (Fascicolo f : fascicoli) {
                        results = initializeObject(f, path, results, true);
                    }
                } else if (searchName.equals("nav-aoo-search")) {
                    AOOCriteria root = new AOOCriteria();
                    root.setProperty("COD_ENTE", getEnte());
                    root.properties.remove("ENABLED");
                    root.setProperty("enabled", "true");

                    root.setMaxElementi(maxResults);
                    root.setOrderBy(ord.getNomeCampo(), ord.getTipo());


                    List<AOO> children = client.search(token, root, AOO.class);
                    //List<AOO> children = DocerService.ricercaAOO(token, root);
                    for (AOO child : children) {
                        results = initializeObject(child, path, results);
                    }
                } else if (searchName.equals("nav-letter-search")) {

                    VirtualObject a = new VirtualObject("LETTERA_TITOLARIO");
                    a.properties.put("COD_ENTE", getEnte());
                    a.properties.put(AOO, obj.getProperty(AOO));
                    a.setName("@0-9");
                    a.properties.put("DES_TITOLARIO", "@1-9*");
                    results = initializeObject(a, path, results);
                    a.setProperty(PATH, obj.getProperty(PATH) + '\\' + "@1-9");

                    for (char c='A'; c<='Z'; c++) {
                        a = new VirtualObject("LETTERA_TITOLARIO");
                        a.properties.put("COD_ENTE", getEnte());
                        a.properties.put(AOO, obj.getProperty(AOO));
                        a.setName(Character.toString(c));
                        a.properties.put("DES_TITOLARIO", c + "*");
                        results = initializeObject(a, path, results);
                        a.setProperty(PATH, obj.getProperty(PATH) + '\\' + c);
                    }

                } else if (searchName.equals("nav-anno-fascicolo-search")) {
                    //TODO: Usare search, serve gestione degli orderby
                    FascicoloCriteria fascicolo = new FascicoloCriteria();
                    fascicolo.setProperty("COD_ENTE", getEnte());
                    fascicolo.setProperty("COD_AOO", obj.properties.get(AOO));
                    fascicolo.setProperty(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    fascicolo.setProperty("PARENT_PROGR_FASCICOLO","");

                    fascicolo.setOrderBy("ANNO_FASCICOLO", Ordinamento.orderByEnum.ASC);
                    fascicolo.setMaxElementi(1);

                    Set<String> anni = new TreeSet<>();

                    List<Fascicolo> fascicoli = DocerService.ricercaFascicoli(token, fascicolo);
                    if (!fascicoli.isEmpty()) {
                        int min = Integer.parseInt(fascicoli.get(0).getAnno());
                        fascicolo.setOrderBy("ANNO_FASCICOLO", Ordinamento.orderByEnum.DESC);
                        fascicoli = DocerService.ricercaFascicoli(token, fascicolo);
                        int max = Integer.parseInt(fascicoli.get(0).getAnno());

                        assert min <= max : "L'anno piú recente non puó essere minore di quello piú vecchio";

                        if (ord.getTipo() == Ordinamento.orderByEnum.ASC) {
                            for (int i=min; i<=max; i++) {
                                anni.add(Integer.toString(i));
                            }
                        } else {
                            for (int i=max; i>=min; i--) {
                                anni.add(Integer.toString(i));
                            }
                        }
                    }

                    for (String anno : anni) {
                        VirtualObject a = new VirtualObject("ANNO_FASCICOLO");
                        a.properties.put("ente", getEnte());
                        a.properties.put("COD_ENTE", getEnte());
                        a.properties.put("aoo", fascicolo.getProperty(AOO));
                        a.properties.put("COD_AOO", fascicolo.getProperty(AOO));
                        a.properties.put("classifica", fascicolo.getProperty(CLASSIFICA));
                        a.properties.put("CLASSIFICA", fascicolo.getProperty(CLASSIFICA));
                        a.properties.put("ANNO_FASCICOLO", anno);
                        a.setName(anno);
                        results = initializeObject(a, path, results);
                    }
                } else if (searchName.equals("nav-sub-fascicolo-search")) {
                    FascicoloCriteria fascicolo = new FascicoloCriteria();
                    fascicolo.properties.clear();

                    fascicolo.setProperty("COD_ENTE", getEnte());
                    fascicolo.setProperty("COD_AOO", obj.properties.get(AOO));
                    fascicolo.setProperty(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    fascicolo.setProperty("PARENT_PROGR_FASCICOLO", obj.properties.get(PROGRESSIVO));

                    fascicolo.setProperty(LOCATION, obj.getLocation());

                    fascicolo.setMaxElementi(maxResults);
                    fascicolo.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    List<Fascicolo> fascicoli = client.search(token, fascicolo, Fascicolo.class);
                    for (Fascicolo f : fascicoli) {
                        results = initializeObject(f, path, results);
                    }
                } else if (searchName.equals("nav-autori-mandato-search")) {
                    /*
                    List<Acl> acls = DocerService.recuperaACLFascicolo(getToken(), getEnte(), getAoo(),
                            obj.properties.get(PROGRESSIVO),
                            obj.properties.get(ANNO), obj.properties.get(CLASSIFICA));

                    for (Acl acl : acls) {
                        VirtualObject a = new VirtualObject("AUTORE_MANDATO");
                        a.properties.put("ente", getEnte());
                        a.properties.put("aoo", getAoo());
                        a.properties.put("AUTORE_MANDATO", acl.getUtenteGruppo());
                        a.properties.put(PROGRESSIVO, obj.properties.get(PROGRESSIVO));
                        a.properties.put(ANNO, obj.properties.get(ANNO));
                        a.setName("Documenti di " + acl.getUtenteGruppo());

                        results = initializeObject(a, path, results);
                    }
                    */

                    TreeSet<ICIFSObject> orderedObjects = new TreeSet<>(new CifsComparator(ord.getTipo()));

                    //genera il nodo per DOCUMENTI_MANDATO
                    VirtualObject vo = new VirtualObject("DOCUMENTI_MANDATO");
                    vo.properties.put("COD_ENTE", obj.properties.get("COD_ENTE"));
                    vo.properties.put("COD_AOO", obj.properties.get("COD_AOO"));
                    vo.properties.put(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    vo.properties.put(PROGRESSIVO, obj.properties.get(PROGRESSIVO));
                    vo.properties.put(ANNO, obj.properties.get(ANNO));
                    String name = "$Tutti i documenti";
                    vo.setName(name);
                    vo.setBusinessType("DOCUMENTI_MANDATO");

                    orderedObjects = initializeObject(vo, path, orderedObjects);
                    vo.setProperty(PATH, obj.getProperty(PATH) + '\\' + name);

                    vo = new VirtualObject("DOCUMENTI_PER_AUTORE_MANDATO");
                    vo.properties.put("COD_ENTE", obj.properties.get("COD_ENTE"));
                    vo.properties.put("COD_AOO", obj.properties.get("COD_AOO"));
                    vo.properties.put(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    vo.properties.put(PROGRESSIVO, obj.properties.get(PROGRESSIVO));
                    vo.properties.put(ANNO, obj.properties.get(ANNO));
                    name = "$Documenti per autore";
                    vo.setName(name);
                    vo.setBusinessType("DOCUMENTI_PER_AUTORE_MANDATO");

                    orderedObjects = initializeObject(vo, path, orderedObjects);
                    vo.setProperty(PATH, obj.getProperty(PATH) + '\\' + name);

                    results.addAll(orderedObjects);
                } else if (searchName.equals("nav-cartelle-autori-mandato-search")) {
                    DocumentoCriteria doc = new DocumentoCriteria();
                    doc.properties.clear();

                    doc.setProperty("COD_ENTE", getEnte());
                    doc.setProperty("COD_AOO", obj.properties.get(AOO));
                    doc.setProperty("CLASSIFICA", obj.properties.get(CLASSIFICA));
                    if (obj.properties.containsKey(ANNO)) {
                        doc.setProperty("ANNO_FASCICOLO", obj.properties.get(ANNO));
                    }
                    if (obj.properties.containsKey(PROGRESSIVO)) {
                        String searchPath = obj.properties.get(PROGRESSIVO) + "*";
                        doc.setRawProperty("PROGR_FASCICOLO", searchPath);
                        //doc.setProperty("PROGR_FASCICOLO", obj.properties.get(PROGRESSIVO));
                    }

                    doc.setProperty(LOCATION, obj.getLocation());

                    doc.setMaxElementi(maxResults);
                    doc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    HashMap<String, String> params = new HashMap<>();
//                    params.put("allow_field", "allow_content");

                    List<Documento> docs = client.search(token, doc, params, Documento.class);

                    Set<String> authors = new HashSet<>();
                    Pattern authorPattern = Pattern.compile("([^(]+)\\s\\([^)]+\\)");
                    for (Documento d : docs) {
                        String author_id = d.getProperty("AUTHOR_ID");
                        if (author_id.equals("")) {
                            continue;
                        }

                        Matcher matcher = authorPattern.matcher(author_id);
                        if (matcher.matches()) {
                            author_id = matcher.group(1);
                        }

                        authors.add(author_id);
                    }

                    TreeSet<ICIFSObject> orderedObjects = new TreeSet<>(new CifsComparator(ord.getTipo()));

                    for (String author : authors) {
                        VirtualObject a = new VirtualObject("AUTORE_MANDATO");
                        a.properties.put("COD_ENTE", getEnte());
                        a.properties.put("COD_AOO", obj.properties.get(AOO));
                        a.properties.put("AUTORE_MANDATO", author);
                        a.properties.put(CLASSIFICA, obj.properties.get(CLASSIFICA));
                        a.properties.put(PROGRESSIVO, obj.properties.get(PROGRESSIVO));
                        a.properties.put(ANNO, obj.properties.get(ANNO));
                        String name = String.format("$Documenti di %s", author);
                        a.setName(name);
                        a.setBusinessType("AUTORE_MANDATO");

                        orderedObjects = initializeObject(a, path, orderedObjects);

                        a.properties.put(PATH, obj.getProperty(PATH) + '\\' + name);
                    }

                    results.addAll(orderedObjects);

                } else if (searchName.equals("content-autore-mandato-search")) {
                    DocumentoCriteria doc = new DocumentoCriteria();
                    doc.properties.clear();

                doc.setRawProperty("AUTHOR_ID", GenericCriteria.solrEscape(args.get("AUTORE_MANDATO")) + "*");
                    doc.setRawProperty("PROGR_FASCICOLO", obj.properties.get(PROGRESSIVO) + "*");
                    doc.setProperty(ANNO, obj.properties.get(ANNO));
                    doc.setProperty(CLASSIFICA, obj.properties.get(CLASSIFICA));
                    doc.setProperty("COD_ENTE", getEnte());
                    doc.setProperty("COD_AOO", obj.properties.get(AOO));

                    doc.setProperty(LOCATION, obj.getLocation());

                    doc.setOrderBy(ord.getNomeCampo(), ord.getTipo());
                    doc.setMaxElementi(maxResults);

                    HashMap<String, String> params = new HashMap<>();
//                    params.put("allow_field", "allow_content");

                    List<Documento> docs = client.search(token, doc, params, Documento.class);

                    for (Documento d : docs) {
                        d.setName(Utils.addAffix(d.getName(), Utils.DOCNUM_SEP + d.getDocNum()));
                        String oldPath = d.properties.remove(PATH);
                        results = initializeObject(d, path, results);
                        d.setProperty(PATH, oldPath);
                    }
                } else if (searchName.equals("documenti-mandato-search")) {
                    DocumentoCriteria doc = new DocumentoCriteria();
                    doc.properties.clear();

                    doc.setProperty("COD_ENTE", getEnte());
                    doc.setProperty("COD_AOO", obj.properties.get(AOO));
                    doc.setProperty("CLASSIFICA", obj.properties.get(CLASSIFICA));
                    doc.setProperty("ANNO_FASCICOLO", obj.properties.get(ANNO));

                    String searchPath = obj.properties.get(PROGRESSIVO) + "*";
                    doc.setRawProperty("PROGR_FASCICOLO", searchPath);

                    doc.setProperty(LOCATION, obj.getLocation());

                    doc.setMaxElementi(maxResults);
                    doc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    HashMap<String, String> params = new HashMap<>();
//                    params.put("allow_field", "allow_content");

                    List<Documento> docs = client.search(token, doc, params, Documento.class);

                    for (Documento d : docs) {
                        d.setName(Utils.addAffix(d.getName(), Utils.DOCNUM_SEP + d.getDocNum()));
                        String oldPath = d.properties.remove(PATH);
                        results = initializeObject(d, path, results);
                        d.setProperty(PATH, oldPath);
                    }
                } else if (searchName.equals("content-document-search")) {
                    DocumentoCriteria doc = new DocumentoCriteria();
                    doc.properties.clear();

                    doc.setProperty("COD_ENTE", getEnte());
                    doc.setProperty("COD_AOO", obj.properties.get(AOO));
                    doc.setProperty("CLASSIFICA", obj.properties.get(CLASSIFICA));
                    if (obj.properties.containsKey(ANNO)) {
                        doc.setProperty("ANNO_FASCICOLO", obj.properties.get(ANNO));
                    }
                    if (obj.properties.containsKey(PROGRESSIVO)) {
                        doc.setProperty("PROGR_FASCICOLO", obj.properties.get(PROGRESSIVO));
                    }

                    doc.setProperty(LOCATION, obj.getLocation());

                    doc.setMaxElementi(maxResults);
                    doc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    HashMap<String, String> params = new HashMap<>();
//                    params.put("allow_field", "allow_content");

                    List<Documento> docs = client.search(token, doc, params, Documento.class);
                    for (Documento d : docs) {
                        results = initializeObject(d, path, results);
                    }
                } else if (searchName.equals("nav-folder-search")
                        || searchName.equals("nav-cross-folder-search")) {
                    ICIFSObject parent;
                    if (obj instanceof Cartella) {
                        parent =  obj;
                    } else {
                        parent = forceLoadFolder(args.get(PATH), virtualPath);
                    }

                    HashMap<String, String> params = new HashMap<>();

                    CartellaCriteria cc = new CartellaCriteria();

                    List<String> comps = Splitter.on('\\').splitToList(parent.getProperty(PATH));

                    StringBuilder sb = new StringBuilder();
                    if (searchName.equals("nav-folder-search")) {
                        cc.setProperty(LOCATION, parent.getLocation());
                        Joiner.on('/').appendTo(sb, comps);
                    } else {
                        sb.append("/./");
                        Joiner.on('/').appendTo(sb, comps.subList(2, comps.size()));
                    }

                    sb.append("/*");

                    params.put("PATH", sb.toString());

                    cc.setMaxElementi(maxResults);
                    cc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    List<Cartella> cartelle = client.search(token, cc, params, Cartella.class);

                    for (Cartella c : cartelle) {
                        results = initializeObject(c, path, results);
                    }
                } else if (searchName.equals("nav-folders-container")) {
                    CartellaCriteria cartella = new CartellaCriteria();
                    cartella.properties.clear();

                    cartella.setEnte(ente);
                    cartella.setAoo(obj.properties.get(AOO));
                    cartella.setProperty("PARENT_FOLDER_ID", "");

                    cartella.setProperty(LOCATION, obj.getLocation());

                    List<Cartella> folders = client.search(token, cartella, Cartella.class);
                    if (folders.size() > 0) {
                        TreeSet<Cartella> ts = new TreeSet<>(new CifsComparator(ord.getTipo()));

                        for (Cartella folder : folders) {
                            ts.add(folder);
                        }

                        for (Cartella folder : ts) {
                            results = initializeObject(folder, path, results);
                        }
                    }
                } else if (searchName.equals("nav-folders-personal")) {
                    ICIFSObject parent;
                    if (obj instanceof Cartella) {
                        parent =  obj;
                    } else {
                        parent = forceLoadFolder(args.get(PATH), virtualPath);
                    }

                    CartellaCriteria cc = new CartellaCriteria();
                    cc.properties.clear();

                    cc.setProperty("COD_ENTE", getEnte());
                    cc.setProperty("COD_AOO", obj.properties.get(AOO));
                    cc.setProperty("PARENT_FOLDER_ID", parent.getID());

                    String userName = getCurrentUser(getToken(args.get(PATH), virtualPath));
                    if (!userName.equals(ADMIN_USER)) {
                        cc.setProperty("DES_FOLDER", userName);
                    }

                    cc.setProperty(LOCATION, obj.getLocation());

                    cc.setMaxElementi(maxResults);
                    cc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                    List<Cartella> cartelle = client.search(token, cc, Cartella.class);

                    for (Cartella c : cartelle) {
                        results = initializeObject(c, path, results);
                    }
                } else if (searchName.equals("content-folder-search")
                        || searchName.equals("content-cross-folder-search")) {
                    Cartella cartella;
                    if (obj instanceof  Cartella) {
                        cartella = (Cartella) obj;
                    } else {
                        cartella = forceLoadFolder(args.get(PATH), virtualPath);
                    }

                    List<Documento> docs;

                    DocumentoCriteria doc = new DocumentoCriteria();
                    doc.properties.clear();

                    if (cartella.getFullPath().contains("Temporanei")) {
                        docs = client.searchByPath(token, cartella.getProperty("PATH"), maxResults, Documento.class);
                    } else {

                        //List<Documento> docs = DocerService.recuperaDocumentiCartella(getToken(), cartella, ordinamenti);

                        doc.setProperty("COD_ENTE", ente);
                        doc.setProperty("COD_AOO", cartella.getAoo());
                        doc.setProperty("PARENT_FOLDER_ID", cartella.getID());

                        if (searchName.equals("content-folder-search")) {
                            doc.setProperty(LOCATION, obj.getLocation());
                        }

                        doc.setMaxElementi(maxResults);
                        doc.setOrderBy(ord.getNomeCampo(), ord.getTipo());

                        HashMap<String, String> params = new HashMap<>();
//                    params.put("allow_field", "allow_content");

                        docs = client.search(token, doc, params, Documento.class);
                    }
                    for (Documento d : docs) {
                        results = initializeObject(d, path, results);
                    }

                } else {
                    throw new IllegalArgumentException("Searchname not recognised: " + searchName);
                }
            }

            return results;
        } catch (CacheException | KeyException | SolrServerException | TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }
  
    public List<ICIFSObject> navByPath(String path, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        try {
            path = normalizePath(path);

            if (isRoot(path)) {
                return searchProfiles(path);
            }

            String profile = Utils.parseProfileFromPath(path);

            String shiftedPath = path;

            path = restoreShift(path, profile);

            HashMap<String, Object> env = new HashMap<>();
            env.put("ente", getDescriptionEnte(getEnte()));
            env.put("path", path);
            env.put("sede", extractSede(getToken()));

            Serializable contextBuilder = getContextBuilder(profile);
            HashMap<String, String> context = (HashMap<String, String>) MVEL.executeExpression(contextBuilder, env);
            context.put(VIRTUAL_PATH, path);
            String tag = context.get("tag");

            List config = getNavigationConfig(profile, tag);
            int maxResults = getMaxResults(profile);
            return searchByTag(context, tag, config, path, shiftedPath, maxResults, ordinamenti);
        } catch (TokenGeneratorCallback.TokenGenerationException | SolrServerException e) {
            throw new IOException(e);
        }
    }

    private List<ICIFSObject> searchProfiles(String path) throws IOException, DocerApiException, TokenGeneratorCallback.TokenGenerationException, SolrServerException {

        AOOCriteria root = new AOOCriteria();
        root.setProperty("COD_ENTE", getEnte());
        root.setProperty("type","aoo");

        root.properties.remove("ENABLED");
        root.setProperty("enabled", "true");

        Set<String> validNames = new HashSet<>();

        SOLRClient client = new SOLRClient();

        List<AOO> children = client.search(getToken(path, ""), root, AOO.class);

        //List<AOO> children = DocerService.ricercaAOO(getToken(path), root);
        for (AOO child : children) {
            validNames.add(child.getName());
        }

        List<ICIFSObject> profiles = new ArrayList<>();
        for (String profile : getConfiguration().listProfiles()) {
            Profile p = new Profile();
            p.setEnte(ente);
            p.setParentPath("");
            if (profile.equals(DEFAULT_PROFILE)) {
                p.setName(getDescriptionEnte(ente));
                profiles.add(p);
            } else if (validNames.contains(profile)) {
                p.setName(profile);
                profiles.add(p);
            }
        }

        return profiles;
    }

    public List<ICIFSObject> navByPathFe(String path, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        return navByPath(unixPathConv(path), ordinamenti);
    }

    public List<ICIFSObject> contentByPath(String path, Ordinamento... ordinamenti) throws IOException, DocerApiException {

        try {
            path = normalizePath(path);

            if (isRoot(path)) {
                return searchProfiles(path);
            }
            String profile = Utils.parseProfileFromPath(path);

            String shiftedPath = path;
            path = restoreShift(path, profile);

            HashMap<String, Object> env = new HashMap<>();
            env.put("ente", getDescriptionEnte(getEnte()));
            env.put("path", path); //parsedPath.getPath());
            env.put("sede", extractSede(getToken()));

            Serializable contextBuilder = getContextBuilder(profile);
            HashMap<String, String> context = (HashMap<String, String>) MVEL.executeExpression(contextBuilder, env);
            context.put(VIRTUAL_PATH, path);
            String tag = context.get("tag");

            Optional<ArrayList> config = getContentConfig(profile, tag);
            if (config.isPresent()) {
                int maxResults = getMaxResults(profile);
                return searchByTag(context, tag, config.get(), path, shiftedPath, maxResults, ordinamenti); //parsedPath.getPath());
            } else {
                return new ArrayList<>();
            }
        } catch (TokenGeneratorCallback.TokenGenerationException | SolrServerException e) {
            throw new IOException(e);
        }
    }

    private String extractSede(String token) {
        String sede = Utils.extractOptionalTokenKey(token, "app",ToolkitConnector.getSedeLocale());
        return sede;
    }

    private String normalizePath(String path) {
        if (Strings.isNullOrEmpty(path) || path.length() <= 1) {
            return path;
        }

        //TODO: Optimize
        return '\\' + Joiner.on('\\').skipNulls().join(Splitter.on('\\').omitEmptyStrings().split(path));
    }

    public List<ICIFSObject> contentByPathFE(String path, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        return contentByPath(unixPathConv(path), ordinamenti);
    }

    public String buildPath(ICIFSObject object) throws IOException, DocerApiException {
        return buildPath(object, getDefaultProfile());
    }

    public String buildPath(ICIFSObject object, String profile) throws IOException, DocerApiException {
        return buildPath(object, profile, false);
    }

    public String buildPath(ICIFSObject object, String profile, boolean creation) throws IOException, DocerApiException {
        if (Strings.isNullOrEmpty(profile)) {
            profile = getDefaultProfile();
        }

        if (!(object instanceof Root)) {
            Serializable pathMaker = getPathBuilder(profile);

            HashMap<String, Object> env = new HashMap<>();
            env.put("ente", getDescriptionEnte(getEnte()));
            env.put("obj", object);
            env.put("profile", profile);

            object = (ICIFSObject) MVEL.executeExpression(pathMaker, env);

            profile = object.getShare();
            try {
                getProfile(profile);
            } catch (IOException e) {
                log.warn("Invalid profile specified: " + profile);
                profile = getDefaultProfile();
            }

            String path = object.getFullPath();
            if (isShiftApplicable(path, profile)) {
                path = applyShift(path, profile);
                object.setParentPath(Utils.getPathParent(path));
                object.setName(Utils.getPathName(path));
            } else {
                object.setParentPath(profile);
                if (!creation && object instanceof Documento) {
                    object.setName(Utils.addAffix(object.getName(),
                            Utils.DOCNUM_SEP + ((Documento) object).getDocNum()));
                }
            }

        }

        return object.getFullPath();
    }

    private String getDefaultProfile() throws IOException, DocerApiException {
        String profile = getConfiguration().getDefaultProfile();
        if (profile.equals(DEFAULT_PROFILE)) {
            profile = getDescriptionEnte(getEnte());
        }

        return profile;
    }

//    public String getDescriptionTitolario(String classifica) throws IOException, DocerApiException {
//        HashMap<String, String> context = new HashMap<>();
//        context.put(CLASSIFICA, classifica);
//        context.put("type", Titolario.TYPE);
//        return getDescription(context);
//    }

//    public String getDescriptionFascicolo(String classifica, String progressivo, String anno) throws IOException, DocerApiException {
//        HashMap<String, String> context = new HashMap<>();
//        context.put(CLASSIFICA, classifica);
//        context.put(PROGRESSIVO, progressivo);
//        context.put(ANNO, anno);
//        context.put("type", Fascicolo.TYPE);
//        return getDescription(context);
//    }

    public String getDescriptionUser(String userId) throws IOException, DocerApiException {
        /*HashMap<String, String> context = new HashMap<>();
        context.put(USER_ID, userId);
        context.put("type", User.TYPE);
        String ret = getDescription(context);
        if(ret != null && ret.isEmpty()) {
            return userId;
        } else {
            return ret;
        }*/
        return ActorsCache.getDisplayName(userId);
    }

//    public String getDescriptionAOO(String codiceAoo) throws IOException, DocerApiException {
//        String cacheKey = Integer.toString((ente + codiceAoo).hashCode());
//        try {
//            Optional cached = CacheManager.descCache().get(cacheKey);
//            if (cached.isPresent()) {
//                return cached.get().toString();
//            } else {
//                AOO aoo = DocerService.getAOO(getToken(), ente, codiceAoo);
//                String desc = aoo.properties.get("DES_AOO");
//                CacheManager.descCache().put(cacheKey, desc);
//                return desc;
//            }
//        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
//            throw new IOException(e);
//        }
//    }

    public String getDescriptionEnte(String codiceEnte) throws DocerApiException, IOException {
        /*String cacheKey = Integer.toString(codiceEnte.hashCode());
        try {
            Optional cached = CacheManager.descCache().get(cacheKey);
            if (cached.isPresent()) {
                return cached.get().toString();
            } else {
                Amministrazione enteItem = DocerService.getEnte(getToken(), codiceEnte);
                String desc = enteItem.getDenominazione();
                CacheManager.descCache().put(cacheKey, desc);
                return desc;
            }
        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }*/
        return ActorsCache.getDisplayName(codiceEnte);
    }

    public String getDescriptionGroup(String groupId) throws IOException, DocerApiException {
        /*HashMap<String, String> context = new HashMap<>();
        context.put(GROUP_ID, groupId);
        context.put("type", Group.TYPE);
        String ret = getDescription(context);
        if(ret != null && ret.isEmpty()) {
            return groupId;
        } else {
            return ret;
        }*/
        return ActorsCache.getDisplayName(groupId);
    }

    /*private String getDescription(HashMap<String, String> context) throws IOException, DocerApiException {

        try {
            String cacheKey = Integer.toString(context.hashCode());
            Optional cached = CacheManager.descCache().get(cacheKey);
            //System.out.println("controllo cache:"+new Timestamp(new Date().getTime()).toString());
            if (cached.isPresent()) {
                if (cached.get().toString().equals(NOT_FOUND)) {
                    return null;
                } else {
                    return cached.get().toString();
                }
            } else {

                String type = context.remove("type");
                try {
                    ICIFSObject obj = openByCriteria(context, type);
                    String ret = obj.getName();
                    CacheManager.descCache().put(cacheKey, ret);
                    return ret;
                } catch (FileNotFoundException e) {
                    CacheManager.descCache().put(cacheKey, NOT_FOUND);
                    return null;
                }
            }
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }*/

    public void deleteByPath(String path) throws DocerApiException, IOException {
        try {
            path = normalizePath(path);

            ICIFSObject object = openByPath(path);

            if (object instanceof Cartella) {
                DocerService.rimuoviCartella(getToken(object.getProperty(PATH), path), (Cartella) object);
                markDeleted(path);
            } else if (object instanceof Documento) {
                DocerService.rimuoviDocumento(getToken(object.getProperty(PATH), path), (Documento) object);
                markDeleted(path);
            } else if (object instanceof Fascicolo) {
                DocerService.rimuoviFascicolo(getToken(object.getProperty(PATH), path), (Fascicolo) object);
                markDeleted(path);
            } else {
                throw new AccessDeniedException(path);
            }
        } catch (TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    public void markDeleted(String path) throws IOException {
        path = normalizePath(path);

        try {
            if (!isRoot(path)) {
                path = applyShift(path, Utils.parseProfileFromPath(path));
            }
            //cache della pathInterface
            CacheManager cache = CacheManager.cifsCache();
            cache.put(path, "DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        } catch (DocerApiException e) {
            throw new IOException(e);
        }
    }

    public boolean isDeleted(String path) throws IOException {
        path = normalizePath(path);

        try {
            if (!isRoot(path)) {
                path = applyShift(path, Utils.parseProfileFromPath(path));
            }

            Optional obj = CacheManager.cifsCache().get(path);
            return obj.isPresent() && obj.get().toString().equals("DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        } catch (DocerApiException e) {
            throw new IOException(e);
        }
    }

    public void fakeDelete(String path) throws IOException {
        path = normalizePath(path);

        try {
            if (!isRoot(path)) {
                path = applyShift(path, Utils.parseProfileFromPath(path));
            }

            CacheManager cache = CacheManager.cifsCache();
            cache.put(path, "FAKE-DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        } catch (DocerApiException e) {
            throw new IOException(e);
        }
    }

    public void undelete(String path) throws IOException {
        path = normalizePath(path);

        try {
            if (!isRoot(path)) {
                path = applyShift(path, Utils.parseProfileFromPath(path));
            }

            CacheManager cache = CacheManager.cifsCache();
            cache.remove(path);
        } catch (CacheException e) {
            throw new IOException(e);
        } catch (DocerApiException e) {
            throw new IOException(e);
        }
    }

    public boolean isFakeDelete(String path) throws IOException {
        try {
            if (!isRoot(path)) {
                path = applyShift(path, Utils.parseProfileFromPath(path));
            }
            Optional obj = CacheManager.cifsCache().get(path);

            return obj.isPresent() && obj.get().toString().equals("FAKE-DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        } catch (DocerApiException e) {
            throw new IOException(e);
        }
    }

    private static Cache<String, Integer> rightsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private int getAcl(String token, String id) throws IOException {
        try {
            String username = Utils.extractTokenKey(token, "uid");

            String key = id+username;

            Integer right = rightsCache.getIfPresent(key);
            if (right == null) {
                right = new SOLRClient().getAcl(token, id, username);
                rightsCache.put(key, right);
            }

            return right;
        } catch (KeyException | SolrServerException | FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public InputStream readByPath(String path) throws IOException, DocerApiException {
        try {
            ICIFSObject object = openByUncachedPath(path);

            if (object instanceof Documento) {

                Documento doc = (Documento) object;

                SOLRClient client = new SOLRClient();

                String token = getToken(object.getProperty(PATH), object.getFullPath());
                int right = getAcl(token, doc.getProperty("id"));

                String username = Utils.extractTokenKey(token, "uid");

                if ((right & 0b10) == 0x0) {
                    throw new AccessDeniedException(doc.getFullPath());
                }

                Optional<String> filePath = CacheManager.fileCache().get(doc.getVersionID());
                if (filePath.isPresent()) {
                    File tmpFile = new File(filePath.get());
                    if (tmpFile.exists()) {
                        updateSize(doc, tmpFile.length());
                        return new FileInputStream(filePath.get());
                    }
                }
                File tmpFile = File.createTempFile(doc.getName(), "bin");
                try (InputStream in = client.downloadByPath(token,
                        (Documento) object);
                     FileOutputStream out = new FileOutputStream(tmpFile)) {

                    IOUtils.copy(in, out);
                    updateSize(doc, tmpFile.length());
                    CacheManager.fileCache().put(doc.getVersionID(), tmpFile.getAbsolutePath());
                }

                return new FileInputStream(tmpFile);

            } else {
                throw new IOException("Only documents can be read");
            }
        } catch (TokenGeneratorCallback.TokenGenerationException | SolrServerException
                | CacheException | KeyException e) {
            throw new IOException(e);
        }
    }

    public Documento prepareFile(String filename) throws IOException, DocerApiException {
        Documento doc = new Documento();
        doc.setEnte(getEnte());
        doc.setDocName(filename);
        doc.setDocType("DOCUMENTO");

        return doc;
    }

    public ICIFSObject createFolder(String path, String owner) throws IOException {
        try {
            String name = Utils.getPathName(path);
            String parentPath = Utils.getPathParent(path);
            ICIFSObject parent = openByPath(parentPath);

            String token = getToken(parent.getProperty(PATH), parent.getFullPath());
            int right = getAcl(token, parent.getProperty("id"));

            if ((right & 0x2000) == 0x0) {
                throw new AccessDeniedException(path);
            }

            if (parent instanceof Cartella) {
                Cartella parentFolder = (Cartella) parent;
                Cartella cartella = new Cartella()
                        .setEnte(parentFolder.getEnte())
                        .setAoo(parentFolder.getAoo())
                        .setCartellaSuperiore(parentFolder.getID())
                        .setNome(name);
                boolean inherits;
                if (Strings.isNullOrEmpty(parentFolder.getCartellaSuperiore())) {
                    inherits = false;
                } else {
                    inherits = true;
                }
//                cartella.properties.put(INHERITS_ACL, Boolean.toString(inherits));
                Cartella ret = DocerService.creaCartellaCifs(token,
                        cartella);
                ret.setParentPath(parent.getFullPath());
                CacheManager.cifsCache().put(ret.getFullPath(), ret);
                return ret;
            } else if (parent instanceof Fascicolo) {
                Fascicolo fascicolo = (Fascicolo) parent;
                Fascicolo sottoFascicolo = new Fascicolo();
                sottoFascicolo.setEnte(fascicolo.getEnte());
                sottoFascicolo.setAoo(fascicolo.getAoo());
                sottoFascicolo.setClassifica(fascicolo.getClassifica());
                sottoFascicolo.setAnno(fascicolo.getAnno());
                sottoFascicolo.setProgressivoPadre(fascicolo.getProgressivo());
                sottoFascicolo.setDescrizione(name);

//                sottoFascicolo.properties.put(HAS_TILDE, Boolean.toString(false));
//                sottoFascicolo.properties.put(INHERITS_ACL, Boolean.toString(true));

                fascicolo = ServizioFascicolazione.creaFascicoloCifs(token, sottoFascicolo, null);
                fascicolo.setParentPath(parent.getFullPath());
                CacheManager.cifsCache().put(fascicolo.getFullPath(), fascicolo);

                return fascicolo;
            }
        } catch (DocerApiException e) {
            log.error(e.getMessage(), e);
            throw new AccessDeniedException(e.getMessage());
        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }

        throw new AccessDeniedException(path);
    }

    public Documento createFile(Documento file, String parentPath) throws DocerApiException, IOException {
        try {

            ICIFSObject object = openByPath(parentPath);
            if (!(object instanceof Cartella) && !(object instanceof Fascicolo)) {
                throw new AccessDeniedException(parentPath);
            }

            try {
                file.setParentPath(parentPath);
                openByPath(file.getFullPath());
                throw new FileAlreadyExistsException("FileAlreadyExists: " + file.getFullPath());
            } catch (FileNotFoundException e) {}

            file.setEnte(getEnte());
            file.setAoo(object.properties.get(AOO));

            file.properties.put(HAS_TILDE, Boolean.toString(false));
            file.properties.put(INHERITS_ACL, Boolean.toString(true));

            if (object instanceof Cartella) {
                Cartella parent = (Cartella) object;

                file.setProperty("PARENT_FOLDER_ID", parent.getID());
            } else if (object instanceof Fascicolo) {
                Fascicolo parent = (Fascicolo) object;

                file.setProperty("PARENT_FASCICOLO_ID", String.format("%s|%s|%s",
                        parent.getClassifica(), parent.getAnno(), parent.getProgressivo()));
            }

            String node_uuid = object.properties.get("NODE_UUID");
            if(Strings.isNullOrEmpty(node_uuid)) {
                node_uuid = object.properties.get("node_id");
            }

            if (Strings.isNullOrEmpty(node_uuid)) {
                throw new AccessDeniedException("Node id not found in parent object: " + object.getFullPath());
            }

            file.setProperty("PARENT_NODE_ID", node_uuid);

            file = DocerService.creaDocumentoCIFS(getToken(object.getProperty(PATH), object.getFullPath()), file);

            String profile = Utils.parseProfileFromPath(parentPath);
            buildPath(file, profile, true);

            file.setParentPath(object.getFullPath());

            CacheManager.cifsCache().put(file.getFullPath(), file);
            return file;

        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    public void updateFileSizeByPath(String path, long size) throws IOException, DocerApiException {
        Documento doc = openByPath(path, Documento.class);
        updateSize(doc, size);
    }

    public void updateModifierByPath(String path, String modifier) throws IOException, DocerApiException {
        try {
            Documento doc = openByPath(path, Documento.class);
            doc.setProperty("MODIFIER", modifier);
            doc.setProperty("content_modified_by", modifier);
            CacheManager.cifsCache().put(doc.getFullPath(), doc);
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    private void updateSize(Documento doc, long size) throws IOException {
        try {
            doc.properties.put("content_size", Long.toString(size));
            doc.properties.put("CONTENT_SIZE", Long.toString(size));
            CacheManager.cifsCache().put(doc.getFullPath(), doc);
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    public void writeByUncachedPath(String path, InputStream data, long size, boolean replace) throws IOException, DocerApiException {
        Documento doc = openByUncachedPath(path, Documento.class);
        updateDoc(doc, data, size, replace);
    }

    public void writeByPath(String path, InputStream data, long size, boolean replace) throws IOException, DocerApiException {
        Documento doc = openByPath(path, Documento.class);
        updateDoc(doc, data, size, replace);
    }

    private void updateDoc(Documento doc, InputStream data, long size, boolean replace) throws DocerApiException, IOException {
        try {
            if (replace) {
                DocerService.sovrascriviUltimaVersione(getToken(doc.getProperty(PATH), doc.getFullPath()), doc, data);
            } else {
                DocerService.aggiungiNuovaVersione(getToken(doc.getProperty(PATH), doc.getFullPath()), doc, data);
            }
            updateSize(doc, size);
        } catch (TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    public void renameByPath(String source, String destination) throws IOException, DocerApiException {

        source = normalizePath(source);
        destination = normalizePath(destination);

        try {
            openByPath(destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException ex) {

            ICIFSObject object = openByPath(source);
            if (object instanceof Cartella) {
                renameFolder((Cartella)object, destination);
            } else if (object instanceof Documento) {
                renameFile((Documento) object, destination);
            } else if (object instanceof Fascicolo) {
                renameSottoFascicolo((Fascicolo) object, destination);
            } else {
                throw new AccessDeniedException("Only Documents and Folders can be renamed");
            }
        }
    }

    private void renameFile(Documento file, String destination) throws IOException, DocerApiException {
        // Destination must not exist

        try {
            try {
                openByPath(destination);
                throw new FileAlreadyExistsException(destination);
            } catch (FileNotFoundException e) {

                String oldPath = file.getFullPath();
                String oldParent = file.getParentPath();
                String newParent = Utils.getPathParent(destination);

                ICIFSObject oldFolder = openByPath(oldParent);
                ICIFSObject newFolder = openByPath(newParent);

                // Move
                String token = getToken(oldFolder.getProperty(PATH), oldFolder.getFullPath());
                if (!oldParent.equals(newParent)) {
                    if (!oldFolder.getLocation().equals(newFolder.getLocation())) {
                        throw new AccessDeniedException("Operazioni di move ammesse solo entro la stessa sede");
                    }

                    if (oldFolder instanceof Cartella &&
                            newFolder instanceof Cartella) {
                        DocerService.aggiungiDocumentiACartella(token,
                                (Cartella) newFolder, file);
                    } else if(oldFolder instanceof Fascicolo &&
                            newFolder instanceof Fascicolo) {
                        Fascicolo oldFascicolo = (Fascicolo) oldFolder;
                        Fascicolo newFascicolo = (Fascicolo) newFolder;

                        if (!oldFascicolo.getClassifica().equals(newFascicolo.getClassifica())) {
                            throw new AccessDeniedException("Operazione di move ammessa solo entro la stessa classifica");
                        }

                        UnitaDocumentaria ud = new UnitaDocumentaria();
                        ud.setDocumentoPrincipale(file);

                        ServizioFascicolazione.fascicolaUnitaDocumentaria(token, ud, newFascicolo);
                    } else {
                        throw new AccessDeniedException("Operazioni di move ammesse solo entro folders");
                    }

                    file.setParentPath(newParent);
                }

                String oldBaseName = file.getName();
                String newBaseName = Utils.getPathName(destination);

                // Rename
                if (!oldBaseName.equals(newBaseName)) {
                    Documento doc = new Documento();
                    doc.properties.clear();
                    doc.setDocNum(file.getDocNum());
                    doc.setDocName(newBaseName);

                    doc.properties.put(HAS_TILDE, Boolean.toString(false));
                    DocerService.aggiornaDocumento(token, doc);

                    file.setName(newBaseName);
                    file.setDocName(newBaseName);
                }

                markDeleted(oldPath);
                CacheManager.cifsCache().put(file.getFullPath(), file);
            }
        } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e) {
            log.error(e.getMessage(), e);
            throw new AccessDeniedException(e.getMessage());
        }
    }

    private String getCurrentUser(String token) throws KeyException {
        return Utils.extractTokenKey(token, "uid");
    }

    public void renameFolder(Cartella folder, String destination) throws DocerApiException, IOException {

        try {
            // getFolder must throw a FileNotFoundException
            // if the folder does not exists
            openByPath(destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException e) {
            try {
                Cartella newFolder = new Cartella();
                newFolder.properties.clear();
                newFolder.setID(folder.getID());

                String oldParent = folder.getParentPath();
                String newParent = Utils.getPathParent(destination);

                ICIFSObject object = openByPath(newParent);

                // Parent mismatch, moving the folder somewhere else
                String token = getToken(folder.getProperty(PATH), folder.getFullPath());
                if (!oldParent.equals(newParent)) {

                    if (!getCurrentUser(token).equalsIgnoreCase(ADMIN_USER)) {
                        throw new AccessDeniedException("Operazioni di move ammesse solo da utenze di amministrazione");
                    }

                    if (destination.contains(folder.getFullPath())) {
                        throw new AccessDeniedException("Cannot move a folder into itself or any of its children");
                    }

                    Cartella destFolder;
                    if (object instanceof Cartella) {
                        destFolder = (Cartella) object;
                    } else {
                        destFolder = forceLoadFolder(oldParent, object.getFullPath());
                    }
                    newFolder.setCartellaSuperiore(destFolder.getID());
                    newFolder.setParentPath(destFolder.getFullPath());
                }

                String oldBaseName = folder.getName();
                String newBaseName = Utils.getPathName(destination);

                // Rename
                if (!oldBaseName.equals(newBaseName)) {
                    newFolder.setNome(newBaseName);
                    newFolder.setName(newBaseName);
                }


                DocerService.aggiornaCartella(token, newFolder);
                markDeleted(folder.getFullPath());

                folder.setName(newBaseName);
                folder.setNome(newBaseName);
                folder.setParentPath(newParent);

                CacheManager.cifsCache().put(folder.getFullPath(), folder);
            } catch (CacheException | TokenGeneratorCallback.TokenGenerationException | KeyException e1) {
                log.error(e1.getMessage(), e1);
                throw new AccessDeniedException(e1.getMessage());
            }
        }
    }

    private Cartella forceLoadFolder(String realPath, String virtualPath) throws FileNotFoundException, DocerApiException, TokenGeneratorCallback.TokenGenerationException {
        CartellaCriteria c = new CartellaCriteria();
        c.setMaxElementi(1);
        c.properties.put(PATH, realPath);
        List<Cartella> cartelle = DocerService.ricercaCartella(getToken(realPath, virtualPath), c);
        if (cartelle.isEmpty()) {
            throw new FileNotFoundException(realPath);
        }
        return cartelle.get(0);
    }

    public void renameSottoFascicolo(Fascicolo fascicolo, String destination) throws DocerApiException, IOException {

        try {
            openByPath(destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException e) {

            ICIFSObject parent = openByPath(fascicolo.getParentPath());
            if (!(parent instanceof Fascicolo)) {
                throw new AccessDeniedException("Solo i SottoFascicoli possono essere rinominati");
            }

            Fascicolo newFascicolo = new Fascicolo();
            newFascicolo.properties.clear();
            newFascicolo.setEnte(fascicolo.getEnte());
            newFascicolo.setAoo(fascicolo.getAoo());
            newFascicolo.setAnno(fascicolo.getAnno());
            newFascicolo.setClassifica(fascicolo.getClassifica());
            newFascicolo.setProgressivo(fascicolo.getProgressivo());
            newFascicolo.setProgressivoPadre(fascicolo.getProgressivoPadre());

            String newParent = Utils.getPathParent(destination);

            if (!parent.getFullPath().equals(newParent)) {
                throw new AccessDeniedException("I SottoFascicoli possono solo essere rinominati");
            }

            String oldBaseName = fascicolo.getName();
            String newBaseName = Utils.getPathName(destination);

            // Rename
            if (!oldBaseName.equals(newBaseName)) {
                newFascicolo.setDescrizione(newBaseName);
                newFascicolo.setName(newBaseName);
            }

            try {
                newFascicolo.properties.put(HAS_TILDE, Boolean.toString(false));
                newFascicolo = ServizioFascicolazione.aggiornaFascicolo(
                        getToken(parent.getProperty(PATH), parent.getFullPath()), newFascicolo, null);

                newFascicolo.setParentPath(newParent);

                markDeleted(fascicolo.getFullPath());
                CacheManager.cifsCache().put(newFascicolo.getFullPath(), newFascicolo);
            } catch (CacheException | TokenGeneratorCallback.TokenGenerationException e1) {
                throw new IOException(e1);
            }
        }
    }

    private synchronized TreeViewProfile getConfiguration() throws IOException {
        if (!conf.containsKey(ente)) {
            conf.putIfAbsent(ente, new TreeViewProfile(ente));
        }

        return conf.get(ente);
    }

    public LockStatus getLockStatus(Documento doc) throws IOException, DocerApiException {
        try {
            return DocerService.recuperaLock(getToken(doc.getProperty(PATH), doc.getFullPath()), doc);
        } catch (TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    public void lock(Documento doc) throws DocerApiException, IOException {
        try {
            DocerService.lock(getToken(doc.getProperty(PATH), doc.getFullPath()), doc);
        } catch (TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }

    public void unlock(Documento doc) throws DocerApiException, IOException {
        try {
            DocerService.unlock(getToken(doc.getProperty(PATH), doc.getFullPath()), doc);
        } catch (TokenGeneratorCallback.TokenGenerationException e) {
            throw new IOException(e);
        }
    }
}
