package it.kdm.orchestratore.security;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import it.kdm.orchestratore.session.Session;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;

import java.security.KeyException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class Security {

    private PropertyResolver properties;
    public static String EXTENDED_ACL_META_NAME = "ACL_EXT";


    private static final Logger logger = LoggerFactory.getLogger(Security.class);
	
	private IEffectiveRights efManager;
	private long effectiveRights;
	private ICIFSObject target;
	private String token;
	private Map<String,String> rules = null;
	private boolean userIsAdmin = false;


	public static final String read = "read";
	public static final String edit = "edit";
	public static final String sicurezza = "sicurezza";
	public static final String cronologia = "cronologia";
	public static final String listaVersioni = "listaVersioni";
	public static final String download = "download";
	public static final String elimina = "elimina";
	public static final String creaDocumento = "creaDocumento";
	public static final String openInEdit = "openInEdit";
    public static final String creaFascicolo = "creaFascicolo";
    public static final String creaTitolario = "creaTitolario";
    public static final String creaFolder = "creaFolder";
    public static final String creaVersioni = "creaVersioni";


	public Security(){
		
	}

	public static Map<String,Boolean> getAllowedUserOptions(String token, ICIFSObject target) throws Exception {
		Security sec = new Security();
		String user = Utils.extractTokenKey(token, "uid");
		sec.init(target,token,user);
		return sec.getAllowedUserOptions();
	}

	public Map<String,Boolean> getAllowedUserOptions() throws KeyException {
		Map<String,Boolean> userOptions = new HashMap<>();

		for (String rightsKey:this.rules.keySet()) {
			userOptions.put(rightsKey, applyFilterRules(rightsKey));
		}

		return userOptions;
	}

    public long getEffectiveRights() {
        return effectiveRights;
    }

	public void init(ICIFSObject target,String token,String user) throws Exception {
		this.target = target;
		this.token = token;

		//importConfig from config
        //properties = new Properties();
//		File myloc = new File(Utils.getConfigHome(), "system.properties");
//		try(InputStream inputStream = new FileInputStream(myloc)) {
//            properties.importConfig(inputStream);
//        }
		properties = ApplicationProperties.getInstance("system.properties").getProp();

        String efManager = ToolkitConnector.getGlobalProperty("effectiveRightsManager");
		this.efManager = connectManager(efManager);
		((BaseEffectiveRights)this.efManager).setProperties(properties);

		//per la gestione delle global rules (senza target)
		if (this.target==null)
			this.effectiveRights = 1;
		else
			this.effectiveRights = this.efManager.getEffectiveRights(target,token,user);

		this.rules = this.efManager.getRules();

		//carica anche tutte le regole custom nella mappa
		String prefix = this.efManager.getClass().getSimpleName();
		String ruleKey = prefix + ".rule.";

		MutablePropertySources propSrcs = ((ConfigurableEnvironment) properties).getPropertySources();
		StreamSupport.stream(propSrcs.spliterator(), false)
				.filter(ps -> ps instanceof EnumerablePropertySource)
				.map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
				.flatMap(Arrays::<String>stream)
				.forEach(key -> {
					if (key.contains(ruleKey))
						this.rules.put(key.replace(ruleKey,""),properties.getProperty(key));
				} );

		/*for (Object k:properties.keySet()) {
			String key = (String)k;
			if (key.contains(ruleKey))
				this.rules.put(key.replace(ruleKey,""),properties.getProperty(key));
		}*/



		userIsAdmin = Session.getUserInfo().isAdmin();
    }

	public static boolean checkRights(ICIFSObject target, String rightsKey) throws Exception {
		String token = Session.getUserInfo().getJwtToken();
		return Security.checkRights(token,target,rightsKey);
	}

	public static boolean checkRights(String token, ICIFSObject target, String rightKey) throws Exception {
		Security sec = new Security();
		String user = Utils.extractTokenKey(token, "uid");
		sec.init(target,token,user);
		return sec.checkRights(rightKey);
	}

    public boolean checkRights(String rightsKey){
		/* Func. #178: sposto il test in applyFilterRules dove admin è sempre abilitato solo nel caso di filtro su gruppo */
//		if (this.userIsAdmin==true)
//			return true;

		if (!this.rules.containsKey(rightsKey))
			return false;

		//apply security filter rule
		try {
			return applyFilterRules(rightsKey);
		} catch (KeyException e) {
			e.printStackTrace();
			logger.error("Apply filterRule failed. "+e.getMessage());
		}

		return false;
    }

	private boolean checkRight(long rights){
		long result = (getEffectiveRights() & rights);
		return (result == rights);
		
	}

	private boolean checkAllRights(String rights) {
		String[] bits = rights.split("\\|");
		for (int i=0;i<bits.length;i++)
			if(!Strings.isNullOrEmpty(bits[i]) && checkRight(Long.parseLong(bits[i])))
				return true;

		return false;
	}

	private boolean applyFilterRules(String rightsKey) throws KeyException {
		String filterRule = this.rules.get(rightsKey);
		if (Strings.isNullOrEmpty(filterRule)) {
			logger.warn("Parametro SolrEffectiveRights.rule." + filterRule + " non configurato, l'applicazione potrebbe non mostrare alcune funzioni collegate a questo tipo di autorizzazione.");
			return false;
		} else  {

			//applico la substitution delle chiavi in configurazione
			if (this.target!=null && filterRule.contains("${")) {
				StrSubstitutor sub = new StrSubstitutor(target.properties);
				filterRule = sub.replace(filterRule);
			}

			//parse rule
			String[] pars = filterRule.split("\\s+");
			for (String param : pars) {
				if ('#'==param.charAt(0) || isNumber(param)) {
					//gestione del parametro # o un numero
					if (isNumber(param)) {
						if (!checkAllRights(param))
							return false;
					} else {
						if (this.rules.containsKey(param.substring(1))) {
							if (!checkAllRights(this.rules.get(param.substring(1))))
								return false;
						} else{
							if (!checkRight(1)) //default a 1
								return false;
						}
					}
				} else if ('@'==param.charAt(0)) {

					/* Func. #178: admin è sempre abilitato nel caso di filtro su gruppo */
					if (this.userIsAdmin==true)
						continue;

					//process group
					//extract user groups from token
					String regExp = param.substring(1) + "(;|$)";
					String groups;
					try {
						groups = Utils.extractTokenKey(token, "userGroup");
					} catch (KeyException e) {
						e.printStackTrace();
						throw e;
					}
					Pattern pattern = Pattern.compile(regExp);
					Matcher matcher = pattern.matcher(groups);
					if (!matcher.find()){
						return false;
					}
				} else {
					//process metadata
					if (this.target==null)
						return false;

					String regexp = ".*"; //default se non presente
					String[] params = param.split("=", 2);
					if (params.length>1) {
						regexp = params[1];
					}

					String meta = this.target.getProperty(params[0]);

					if (meta==null)
						meta="";

					Pattern pattern = Pattern.compile(regexp);
					Matcher matcher = pattern.matcher(meta);
					if (!matcher.find()){
						return false;
					}
				}
			}
		}

		return true;
	}

	private boolean isNumber(String str) {
		return NumberUtils.isNumber(str);
	}

	private IEffectiveRights connectManager(String effectiveRightsManager) throws Exception {
		IEffectiveRights efManager;
		try {
            @SuppressWarnings("rawtypes")
			Class rClass = Class.forName(effectiveRightsManager);
            efManager = (IEffectiveRights)rClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            logger.error("Initializing effectiveRightsManager error.", e);
            throw e;
        }

       return efManager;
    }



}
