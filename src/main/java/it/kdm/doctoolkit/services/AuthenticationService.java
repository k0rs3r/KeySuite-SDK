package it.kdm.doctoolkit.services;

import com.google.common.base.Strings;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import it.kdm.doctoolkit.clients.AuthenticationServiceStub;
import it.kdm.doctoolkit.clients.AuthenticationServiceStub.Login;
import it.kdm.doctoolkit.clients.AuthenticationServiceStub.LoginResponse;
import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.orchestratore.session.ActorsCache;
import it.kdm.orchestratore.session.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setAuthenticationEpr(url);
//    }

    public static String login(String username, String codiceEnte, String applicazione) throws DocerApiException {
        return login(username, ToolkitConnector.makeSecurePassword(username), codiceEnte, applicazione);
    }

    public static String login(String username, String password, String codiceEnte, String applicazione) throws DocerApiException
     {
         log.debug("Login attempt with the following credentials: {}:{}", username, "******");

         if ("true".equals(System.getProperty("noDocer","false"))){
             String token = Session.getUserInfo().getDocerToken();
             if (Strings.isNullOrEmpty(token))
                 throw new AccessDeniedException("not authenticated");
             return token;
         }

         //un po meno di un giorno di Docer
         //Integer expiration = Integer.parseInt(System.getProperty("tokenExpiration","80000"));
         //expiration =  (int) (System.currentTimeMillis()/1000) + expiration;

         Login login = new Login();
		 login.setUsername(username);
		 login.setPassword(password);
		 login.setCodiceEnte(codiceEnte);
		 login.setApplication(applicazione);
		
		 LoginResponse resp = null;
		 
		 
         try
         {
             //inizializza il toolkit in locale
             AuthenticationServiceStub auth = ClientManager.INSTANCE.getAuthenticationClient(applicazione);
             resp = auth.login(login);

             String token = resp.get_return();
             /*DocerServicesStub.GetUserResponse userResponse = DocerService.getUserInfo(token, username);
             DocerServicesStub.KeyValuePair[] kvps = userResponse.get_return();
             String fullNameUsername = "";
             for (DocerServicesStub.KeyValuePair kvp : kvps) {
                 if (kvp.getKey().equals("FULL_NAME")) {
                     fullNameUsername = kvp.getValue();
                     break;
                 }
             }*/


             String fullNameUsername = Utils.extractOptionalTokenKey(token,"fullName",null);

             if (Strings.isNullOrEmpty(fullNameUsername)){
                 fullNameUsername = ActorsCache.getDisplayName(username);

                 if (Strings.isNullOrEmpty(fullNameUsername)) {
                     fullNameUsername = username;
                 }

                 token = Utils.addTokenKey(token, "fullName", fullNameUsername);
             }

             //token = Utils.addTokenKey(token, "expiration", expiration.toString());

             return token;
             
         }
         catch (Exception e)
         {
             log.error("login error toolkit username:"+username+" password:"+"******"+" codiceEnte:"+codiceEnte+" applicazione:"+applicazione);
             throw new DocerApiException(e);
         }
     }

     public static void logout(String token) throws DocerApiException
     {
         /*if (no Docer(token))
             return;
        
         Logout logout = new Logout();
         logout.setToken(token);
         
         try
         {
             String sede = ToolkitConnector.extractSedeFromToken(token);
        	 AuthenticationServiceStub auth = ClientManager.INSTANCE.getAuthenticationClient(sede);
        	 auth.logout(logout);

         }
         catch (Exception e)
         {

             throw new DocerApiException(e);
         }*/
         return;
     }
     
 	public static String getToken(String User, String Pass, String Ente, String App) throws DocerApiException {
 		return AuthenticationService.login(User, Pass, Ente, App);
 	}
 	
 	 public static boolean verificaToken(String token) throws DocerApiException {

         String md5 = Utils.extractOptionalTokenKey(token,"md5",null);
         String md5check = DigestUtils.md5Hex(Utils.removeTokenKey(token,"md5"));

         if (!md5check.equals(md5)){
             throw new MalformedJwtException("invalid token");
         }

         String exp = Utils.extractOptionalTokenKey(token,"expiration","0");

         DateTime date = new DateTime(Long.parseLong(exp));

         if (date.isBeforeNow())
             throw new ExpiredJwtException(null,null,"expired token");

         return true;
     }
}
