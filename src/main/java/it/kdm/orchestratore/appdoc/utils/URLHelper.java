package it.kdm.orchestratore.appdoc.utils;

import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.utils.EncodeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 25/03/14
 * Time: 16.55
 * To change this template use File | Settings | File Templates.
 */
public class URLHelper {

    private static final String REGEX_SERVICE_PORT = "^http[s]?://[^\\/]+\\:[0-9]+\\/.+$";

    public static String decodeRequestUrl(HttpServletRequest request){
    	
        String encoded = request.getRequestURI();
        encoded =  encoded.replace("+","%2B");
        String decoded = URLHelper.decode(encoded);
        decoded = decoded.replace(request.getContextPath(),"");
        return decoded;
    }

    public static String encodeCIFSPath(String cifsPath) {
        String path = cifsPath;

        //inverte eventualmente gli slash per funzionare indipendentemente con i path
        //genereti da getFullPath o getFEFullpath
        cifsPath = cifsPath.replace("\\","/");

        if (cifsPath.startsWith("/"))
            path = cifsPath.substring(1); //skip char '/'

        String encodedPath = URLHelper.encode(path);


        encodedPath = "/"+encodedPath;

        return encodedPath;
    }

    public static String encode(String decoded) {
        String uri_encoded=decoded;

        try {
            uri_encoded = EncodeUtils.encodeURIComponent(decoded);
//        	uri_encoded = uri_encoded.replace("%23","#");
//            URI uri = new URI(null,null,uri_encoded,null);
//            uri_encoded = uri.toASCIIString(); //SKIP PER TEST
//            uri_encoded = uri_encoded.replace("#","%23"); //workaround percarattere # nei link
        }
        catch(Exception e) {}
        return uri_encoded;
    }

    public static String decode(String encoded) {
        String uri_decoded=encoded;
        try {
            uri_decoded = URLDecoder.decode(encoded, "UTF-8");

        }
        catch(Exception e) {}
        return uri_decoded;
    }

    public static String encodeParameters(HttpServletRequest  request, String skipString) {
        List<NameValuePair> nameValuePairs = null;
        if (request.getParameterMap().size() > 0) {
            nameValuePairs = new ArrayList<NameValuePair>(request.getParameterMap().size());
            Enumeration en = request.getParameterNames();
            while(en.hasMoreElements()) {
            	String name = en.nextElement().toString();
            	if(!name.equalsIgnoreCase(skipString)){
            		nameValuePairs.add(new BasicNameValuePair(name, request.getParameter(name)));
            	}
            }
        }

        String encodedParams = null;
        if (nameValuePairs != null) {
            encodedParams = URLEncodedUtils.format(nameValuePairs, "UTF-8");
        }

        return encodedParams;
    }



    public static String encodeParameters(HttpServletRequest  request, List<String> skipString) {
        List<NameValuePair> nameValuePairs = null;
        if (request.getParameterMap().size() > 0) {
            nameValuePairs = new ArrayList<NameValuePair>(request.getParameterMap().size());
            Enumeration en = request.getParameterNames();
            while(en.hasMoreElements()) {
                String name = en.nextElement().toString();
                if(!skipString.contains(name)){
                    nameValuePairs.add(new BasicNameValuePair(name, request.getParameter(name)));
                }
            }
        }

        String encodedParams = null;
        if (nameValuePairs != null) {
            encodedParams = URLEncodedUtils.format(nameValuePairs, "UTF-8");
        }

        return encodedParams;
    }

    public static String encodeSolrId(String solrId) {
        if(solrId.contains("%2f")){
            solrId = solrId.replace("%2f","%252f");
        }
        return solrId;
    }

    public static String getHostURL(HttpServletRequest request) {
        //String currurl = request.getRequestURL().toString();
        String url = Session.getServerScheme(request)+"://"+Session.getServerName(request);
        int port = Session.getServerPort(request);
        boolean isSSL = Session.isSSL(request);

        if (isSSL && port != 443 || !isSSL && port != 80)
            url += ":" + port;

        //String url = request.getScheme() + "://" + Session.getServerName(request);
        //url += currurl.matches(REGEX_SERVICE_PORT) ?":" + request.getServerPort() :"";
        return url;
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new HashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
