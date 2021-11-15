package it.kdm.orchestratore.session;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.utils.TicketCipher;
import it.kdm.doctoolkit.utils.Utils;
import keysuite.cache.ClientCache;
import keysuite.cache.ClientCacheAuthUtils;
import keysuite.docer.client.Group;
import keysuite.docer.client.User;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {

	private String jwtToken;
	private String docerToken;
	private String docTicket;
	private Claims claims;
    private User user;
    private List<String> allAoos = new ArrayList<>();

    public UserInfo(String username, String aoo){
        String token = ClientCacheAuthUtils.getInstance().simpleJWTToken(aoo, username);
        setJwtToken(token);
        setDocerToken(ClientCacheAuthUtils.getInstance().convertToDocerToken(token));
    }

    public Map<String,Object> getOptions(){
        return new LinkedHashMap<>(this.user.getOptions());
    }

    private String getFirstAoo(String codEnte){
        Collection<Group> aoos = ClientCache.getInstance().getAllAOOs();
        for ( Group aoo : aoos ){
            if (aoo.getCodEnte().equals(codEnte))
                return aoo.getDocerId();
        }
        return null;
    }

    public UserInfo(String token){
        if (token.contains("uid:")) {
            String username = Utils.extractOptionalTokenKey(token,"username",null);
            String aoo = Utils.extractOptionalTokenKey(token,"aoo",null);
            String ente = Utils.extractOptionalTokenKey(token,"ente",null);
            if (Strings.isNullOrEmpty(ente) || Strings.isNullOrEmpty(username))
                throw new RuntimeException("invalid token");
            if (Strings.isNullOrEmpty(aoo))
                aoo = getFirstAoo(ente);

            setJwtToken(ClientCacheAuthUtils.getInstance().simpleJWTToken(aoo, username));
            setDocerToken(token);
        } else {
            setJwtToken(token);
            setDocerToken(ClientCacheAuthUtils.getInstance().convertToDocerToken(token));
        }
    }

    public UserInfo(){
        this.user = new User();
    }

    public Claims getClaims(){
        return claims;
    }

    public void setJwtToken(String jwtToken){

        this.jwtToken = jwtToken;
        this.claims = ClientCacheAuthUtils.getInstance().parseClaims(jwtToken);
        this.user = null;
        allAoos.clear();

        if (claims==null){
            throw new JwtException("invalid jwt token:"+jwtToken);
        }

        String codAoo = this.claims.getAudience();

        Group aoo = ClientCache.getInstance().getAOO(codAoo);

        if (!"system".equals(codAoo) && aoo==null)
            throw new JwtException("aoo "+codAoo+ "not exists");

        String username = this.claims.getSubject();

        this.user = ClientCacheAuthUtils.getInstance().getUserByClaims(claims);

        if (this.user==null){
            throw new JwtException("user "+username+ "not exists in aoo:"+codAoo);
        }

        assert username.equals(this.user.getUserName());
        assert codAoo.equals(this.user.getCodAoo());

        allAoos.add(codAoo);

        if ("system".equals(codAoo)){
            return;
        }

        User user0 = ClientCache.getInstance().getUser(aoo.getCodAoo(), this.user.getUserName());
        List<String> groups = Arrays.asList(user0.getGroups());

        Collection<Group> aoos = ClientCache.getInstance().getAllAOOs();

        for( Group a : aoos ){
            if (!a.getCodEnte().equals(user.getCodEnte()))
                continue;
            String cod = a.getCodAoo();
            if ( (groups.contains(cod+"@group")||user.isAdmin()) && !allAoos.contains(cod))
                allAoos.add(cod);
        }

    }

    public void setDocerToken(String docerToken){
        String aoo = Utils.extractOptionalTokenKey(docerToken,"aoo",null);
        String prefix = Utils.extractOptionalTokenKey(docerToken,"prefix",null);
        if (Strings.isNullOrEmpty(aoo)) {
            String ente = Utils.extractOptionalTokenKey(docerToken,"ente",null);
            aoo = getFirstAoo(ente);
            docerToken += "aoo:" + aoo + "|";
        }

        if (!"system".equals(aoo)){
            Group cacheAoo = ClientCache.getInstance().getAOO(aoo);

            if (cacheAoo==null)
                throw new RuntimeException("aoo "+aoo+ " non esiste");

            String aooPrefix = cacheAoo.getPrefix();

            if (prefix!=null){
                if (!prefix.equals(aooPrefix))
                    throw new RuntimeException("invalid token prefix");
            } else {
                if (!Strings.isNullOrEmpty(aooPrefix))
                    docerToken += "prefix:" + aooPrefix + "|";
            }
        }

        this.docerToken = docerToken;

        String ticket = Utils.extractOptionalTokenKey(docerToken, "ticketDocerServices",null);
        if (ticket!=null){
            docTicket = new TicketCipher().decryptTicket(ticket);
        } else {
            docTicket = null;
        }
    }

    public String getCurrentTreeviewProfile() {
        if (isAuthenticated()){
            Group ente = ClientCache.getInstance().getEnte(this.getCodEnte());
            Group aoo = ClientCache.getInstance().getAOO(this.getCodAoo());
            return "/"+ ToolkitConnector.getSedeLocale() + "/" + ente.getName() + "/" + aoo.getName() + "/" ;
        } else {
            return null;
        }
        //return currentTreeviewProfile;
    }

    public List<String> getActors() {
        List<String> groups = getGroups();
        if (isAuthenticated()){
            groups.add(0,this.user.getUserName());
        }
        return groups;
    }

    public List<String> getUserTokens() {
        if (isAuthenticated()){
            List<String> tokens = getGroups().stream().map( group -> "G"+group ).collect(Collectors.toList());
            tokens.add(0,"U"+this.user.getUserName());
            return tokens;
        }else{
            return new ArrayList<>();
        }
    }

    public List<String> getStructures() {
        if (isAuthenticated()){
            return ClientCache.getInstance().getUserGroups(getCodAoo(),getUsername()).stream().filter( g -> {
                return !g.isAOO() && !g.isEnte() && g.isStruttura();
            }).map( g -> g.getDocerId() ).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
        //return structures; //structureGroup == null ? new ArrayList<>() : new ArrayList(structureGroup.keySet());
    }

    public List<String> getRoles() {
        if (isAuthenticated()){
            return ClientCache.getInstance().getUserGroups(getCodAoo(),getUsername()).stream().filter( g -> {
                return !g.isAOO() && !g.isEnte() && !g.isStruttura();
            }).map( g -> g.getDocerId() ).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
        //return roles;
    }

    public List<String> getAllAoos(){
        if (isAuthenticated()) {
            return allAoos;
        } else
            return new ArrayList<>();
    }

	public String getCountry() {
        return LocaleContextHolder.getLocale().getCountry();
	}

	public String getLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
	}

	public Locale getLocale() {
        return new Locale(getLanguage(),getCountry(),this.getCodAoo());
    }

	public String getUsername() {
        return this.user.getUserName();
	}

    public String getPrefixedUsername() {
        if (this.user.getPrefix()!=null)
            return this.user.getPrefix()+this.user.getUserName();
        else
            return this.user.getUserName();
    }

	public String getFullname() {
        return this.user.getFullName();
	}

	private CodDesc getCodDesc(String codEnte, String groupId){
	    if (groupId==null)
	        return null;
	    Group cGroup = ActorsCache.getInstance().getGroup(codEnte,groupId);
	    if (cGroup!=null)
	        return new CodDesc(groupId,cGroup.getGroupName());
	    else
	        return new CodDesc(groupId,groupId);
    }

    public String getCodEnte(){
	    return this.user.getCodEnte();
    }

	public CodDesc getEnte() {
        return getCodDesc(null,getCodEnte());
	}

	public String getCodAoo() {
	    return this.user.getCodAoo();
    }

	public CodDesc getAoo() {
        return getCodDesc(null,getCodAoo());
	}

    public CodDesc getCurrentAoo() {
        return getCodDesc(null,getCodAoo());
    }

	public String getEmail() {
        return this.user.getEmail();
	}

	public String getJwtToken() {
		return this.jwtToken;
	}

	public String getToken(){
	    return this.docerToken;
    }

    public String getDocerToken(){
        return this.docerToken;
    }

    public String getDocTicket(){
        return this.docTicket;
    }

	public void setToken(String docerToken){
        setDocerToken(docerToken);
    }

	public boolean isAuthenticated() {
        return user.getUserName()!=null && user.getCodAoo()!=null ; // !Strings.isNullOrEmpty(username) && claims!=null;
    }

	public boolean isAdmin() {
        return isAuthenticated() && user.isAdmin();
	}

    public boolean isGuest() {
        return isAuthenticated() && "guest".equals(user.getUserName());
    }

	public boolean hasRole(String... groups){
        return hasGroup(groups);
    }

    public boolean hasGroup(String... groups) {
	    if (isAdmin() || groups==null || groups.length==0)
	        return true;

	    if (!isAuthenticated())
	        return false;

	    List<String> groupsLst = this.getGroups();

	    for( String group : groups){
	        if (Strings.isNullOrEmpty(group))
	            continue;
	        String[] _groups = group.split(",");
	        for(String g : _groups) {

	            if (g.contains("*")){
                    g = g.replaceAll("\\.?\\*",".*");
                    for( String grp : groupsLst ){
                        if (grp.matches(g))
                            return true;
                    }
                } else {
                    if (groupsLst.contains(g) || "guest".equals(g) && isGuest())
                        return true;
                }
            }
        }

        return false;
    }

    public List<String> getGroups() {
		if (isAuthenticated())
            return new ArrayList(Arrays.asList(this.user.getGroups()));
        else
            return new ArrayList<>();
	}
}
