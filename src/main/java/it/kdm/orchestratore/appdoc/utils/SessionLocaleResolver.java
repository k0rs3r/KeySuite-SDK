package it.kdm.orchestratore.appdoc.utils;

import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

// The Spring SessionLocaleResolver loads the default locale prior
// to the requests locale, we want the reverse.
@Component("localeResolverAppDoc")
public class SessionLocaleResolver extends org.springframework.web.servlet.i18n.SessionLocaleResolver{

    public SessionLocaleResolver(){
        //TODO: make this configurable
        this.setDefaultLocale(new Locale("it", "IT"));
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
        UserInfo userSessionInfo = null;
        try{
            //userSessionInfo = Session.getUserInfo((UserRoleRequestWrapper)request, "AppDoc");
            userSessionInfo = Session.getUserInfo();
            return new Locale(userSessionInfo.getLanguage(),userSessionInfo.getCountry());
        }catch(Exception e){
        	
        }     
        locale = determineDefaultLocale(request);
        return locale;
    }

    @Override
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = request.getLocale();
        if (defaultLocale == null) {
            defaultLocale = getDefaultLocale();
        }
        return defaultLocale;
    }

}
