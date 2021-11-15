package it.kdm.orchestratore.appBpm.utils;

import it.kdm.doctoolkit.utils.SolrUtils;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;

/**
 * Created by microchip on 04/05/17.
 */
public class QueryStringUtils {
//    public static String setQsParam(String name, String value) {
//        String queryString = getCurrentHttpRequest().getQueryString();
//        SolrParams qsParams = SolrUtils.parseQueryString(queryString);
//        ModifiableSolrParams params = new ModifiableSolrParams(qsParams);
//
//        params.set(name, value);
//
//        return params.toString();
//    }

    public static String encodeURIComponent(String decoded) {
        try {
            return URLEncoder.encode(decoded, "UTF-8").replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decodeURIComponent(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModifiableSolrParams getParams() {
        String queryString = getCurrentHttpRequest().getQueryString();
        return getParams(queryString);
    }
    public static ModifiableSolrParams getParams(String queryString) {
        SolrParams qsParams = SolrUtils.parseQueryString(queryString);
        ModifiableSolrParams params = new ModifiableSolrParams(qsParams);

        return params;
    }

    private static HttpServletRequest getCurrentHttpRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }

        throw new IllegalAccessError("Not called in the context of an HTTP request");
    }


    public static String[] getValues( String name ) {
        return getParams().getParams(name);
    }

    public static String get( String name, String def ) {
      return getParams().get(name,def);
    }
    public static ModifiableSolrParams set( String name, String val ) {
      return getParams().set(name,val);

    }
    public static ModifiableSolrParams add( String name, String val ) {
        return getParams().add(name, val);
    }
    public static ModifiableSolrParams remove( String name, String val ) {
        ModifiableSolrParams mp = getParams();
        mp.remove(name, val);
        return mp;

    }
    public static ModifiableSolrParams remove( String name ) {
        ModifiableSolrParams mp = getParams();
        mp.remove(name);
        return mp;

    }

    public static String solrEncode(String text) {
        HashSet<Character> charsToEncode = new HashSet<Character>();
        //"\\/:*?\"<>!.@"
        charsToEncode.add('\\');
        charsToEncode.add('/');
        charsToEncode.add(':');
        charsToEncode.add('*');
        charsToEncode.add('?');
        charsToEncode.add('"');
        charsToEncode.add('<');
        charsToEncode.add('>');
        charsToEncode.add('|');
        charsToEncode.add('.');
        charsToEncode.add('@');

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<text.length(); i++) {
            char ch = text.charAt(i);
            if (charsToEncode.contains(ch)) {
                builder.append('%');
                if (ch < 0x10) {
                    builder.append('0');
                }
                builder.append(Integer.toHexString(ch));
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }
}
