package it.kdm.orchestratore.appBpm.utils;

import com.google.common.base.Strings;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.MalformedURLException;

//utility to check if resource exist
public class ExistResource {
	private static final Logger logger = LoggerFactory.getLogger(ExistResource.class);
	// Check if resource exist in classpath
    public static String existResource(HttpServletRequest request,String resourceString, String type) {

		ServletContext context = request.getSession().getServletContext();

		String fileSep = "/"; //File.separator;
		String pathSearch = fileSep + "snippet" + fileSep;
		String pathFallback = fileSep + "WEB-INF" + fileSep + "commons" + fileSep + "snippet" + fileSep;

		String resourceComplete = resourceString + type;
		boolean exist= false;
		try {
			UserInfo userInfo = (UserInfo) Session.getUserInfo();
			if (userInfo.getEnte() != null && !Strings.isNullOrEmpty(userInfo.getEnte().getCod())) {

				String resourceCompleteEnte = userInfo.getEnte().getCod() + "-" + resourceString + type;
				InputStream is = context.getResourceAsStream(pathSearch + resourceCompleteEnte);

				if (is != null) {
					return pathSearch + resourceCompleteEnte;
				}
			}
		} catch (Exception e) {
			logger.error("ERROR: Session.getUserInfo failed ");
			logger.error("ERROR: Session.getUserInfo failed {}:", e);
			e.printStackTrace();
		}
//		InputStream is = context.getResourceAsStream(pathSearch + resourceComplete);
		exist = existRes(context, pathSearch + resourceComplete);

//		if (is != null) {
		if(exist){
//			logger.info("resource loaded:" + resourceComplete);
			return pathSearch + resourceComplete;
		} else {
//			logger.info("resource does not exist:" + resourceComplete);

			resourceComplete = "anagrafica" + type;
//			is = context.getResourceAsStream(pathSearch + resourceComplete);
			exist = existRes(context, pathSearch + resourceComplete);
//			if (is != null) {
			if(exist){

				logger.info("resource loaded:" + resourceComplete);
				return pathSearch + resourceComplete;

			} else {

				logger.info("resource not exist:" + resourceComplete);
				return pathFallback + "generic" + type;

			}
		}
	}

	private static boolean existRes(ServletContext context, String resURl){
		boolean exist = false;

		try {
			exist = context.getResource(resURl) != null;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			//logger.error("resources");
		}

		return exist;
	}
}
