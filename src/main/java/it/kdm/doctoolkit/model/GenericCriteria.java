package it.kdm.doctoolkit.model;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 13/02/14
 * Time: 11.31
 * To change this template use File | Settings | File Templates.
 */
public class GenericCriteria extends GenericObject {

    private final static char[] chars2Escape = new char[] {
            ' ', '+', '-' , '&', '|', '!', '(', ')', '{', '}',
            '[', ']', '^', '"', '~', '?', ':', '\\', '/'
    };

    public static final String ORDER_BY = "$ORDER_BY";
    public static final String MAX_RESULTS = "$MAX_RESULTS";
    public static final int MAX_RESULTS_DEFAULT = 100;

    private HashMap<String, String> unescapedProperties;
    private String query;

    public GenericCriteria() {
        unescapedProperties = new HashMap<>();
    }

    public void setOrderBy(String field) {
        this.setOrderBy(field,Ordinamento.orderByEnum.ASC);
    }

    public void setOrderBy(String field, Ordinamento.orderByEnum direction) {
        this.setProperty(ORDER_BY,field+"="+direction.toString());
    }

    public String getOrderBy() {
        return this.getProperty(ORDER_BY);
    }

    public String removeOrderBy() {
        return properties.remove(ORDER_BY);
    }

    public int getMaxElementi() {
        String val = this.getProperty(MAX_RESULTS);
        if (Strings.isNullOrEmpty(val)) {
            return MAX_RESULTS_DEFAULT;
        }
        return Integer.parseInt(val);
    }

    public int removeMaxElementi() {
        int ret = getMaxElementi();
        this.properties.remove(MAX_RESULTS);
        return ret;
    }

    public void setMaxElementi(int maxElementi) {
        this.setProperty(MAX_RESULTS,String.valueOf(maxElementi));
    }

    public void setRawProperty(String key, String value) {
        unescapedProperties.put(key, value);
    }

    public Map<String, String> getRawProperties() {
        return unescapedProperties;
    }

    public static String solrEscape(String text) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (Arrays.binarySearch(chars2Escape, c) >= 0) {
                builder.append('\\');
            }
            builder.append(c);
        }

        return builder.toString();
    }

    public void setFullTextQuery(String query) {
        this.query = query;
    }

    public String getFullTextQuery() {
        return this.query;
    }

    @Override
    protected void initProperties() {
        properties.put("enabled", Boolean.toString(true));
    }

    public String getEnte() {
        return properties.get("COD_ENTE");
    }

    public void setEnte(String ente) {
        properties.put("COD_ENTE",ente);
    }

    public String getAoo() {
        return properties.get("COD_AOO");
    }

    public void setAoo(String aoo) {
        properties.put("COD_AOO",aoo);
    }
}
