package it.kdm.doctoolkit.utils;

import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.model.path.VirtualObject;
import it.kdm.doctoolkit.services.SOLRClient;
import it.kdm.doctoolkit.services.SolrPathInterface;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Lorenzo Lucherini on 10/22/14.
 */
public class SOLRResponse extends QueryResponse {

    public static final String SOLR_PATH_FIELD = "[FULLPATH]";
    private static final Logger logger = LoggerFactory.getLogger(SOLRResponse.class);

    public SOLRResponse(QueryResponse response) {
        setResponse(response.getResponse());
    }

    @Override
    public <T> List<T> getBeans(Class<T> type) {
        throw new IllegalStateException("Method getBeans() not supported");
    }


        public List<ICIFSObject> getFacetResults(String type, String format) {

        List<ICIFSObject> results = new ArrayList<>();

        List<FacetField> facetFields = getFacetFields();

        if ( facetFields!=null && facetFields.size() > 0 )
        {
            SimpleOrderedMap params = (SimpleOrderedMap)(getHeader().getAll("params").toArray())[0];
            String parentPath = ""+params.get("PATH");

            if( parentPath.endsWith("*") )
                parentPath = parentPath.substring(0, parentPath.length()-2);

            if( parentPath.endsWith("/") )
                parentPath = parentPath.substring(0, parentPath.length()-2);

            for (  FacetField ff : facetFields ) {

                List<FacetField.Count> counts = ff.getValues();

                for (FacetField.Count c : counts) {

//                    SolrDocument doc = createVirtualDocument(path, name, format);
                    SolrDocument doc = convertFacetCount(c, type, parentPath, format);

                    results.add(convertSolrDoc(doc));

                }
            }
        }

        return results;
    }
    public ICIFSObject getFirstResult() {
        List<ICIFSObject> res = getTypedResults();
        if (res.size()>0)
            return res.get(0);
        else
            return null;
    }

    public List<ICIFSObject> getTypedResults() {
        List<ICIFSObject> results = new ArrayList<>();
        for (SolrDocument doc : getResults()) {
            results.add(convertSolrDoc(doc));
        }

        return results;
    }


    public SolrDocument convertFacetCount( FacetField.Count fcount, String type, String parentPath , String format ){

        SolrDocument doc = new SolrDocument();

        long cnt = fcount.getCount();
        String value = fcount.getName();
        String name= fcount.toString();
//        String type = parent.getType();
        String facetFieldName = fcount.getFacetField().getName();
        String filterQuery=fcount.getAsFilterQuery();

        name = String.format(format, name);

        doc.setField("id", name +"@" + type);
        doc.setField("name",name);
        doc.setField("count",cnt);
        doc.setField("facet",true);
        doc.setField("facet.field", facetFieldName);
        doc.setField("facet.filter", filterQuery);

//        String parentPath = parent.getFEFullPath();

//        if( parentPath.endsWith("*") )
//            parentPath = parentPath.substring(0, parentPath.length()-2);
//
//        if( parentPath.endsWith("/") )
//            parentPath = parentPath.substring(0, parentPath.length()-2);

        doc.setField("VIRTUAL_PATH", parentPath + "/" + doc.getFieldValue("name") );

        return doc;
    }

    public <T extends ICIFSObject> List<T> getResults(Class<T> type) {
        List<T> results = new ArrayList<>();
        for (SolrDocument doc : getResults()) {
            results.add(convertSolrDoc(doc, type));
        }

        return results;
    }

    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
    private static String formateDate(Date date) {
        return formatter.withZoneUTC().print (date.getTime());
    }

    public static ICIFSObject convertSolrDoc(SolrDocument doc) {
        Class klass;
        String type = doc.getFirstValue("id").toString().split("@")[1];

        if(doc.containsKey("type")) {
            type = ""+doc.getFieldValue("type");
        }

        if (type.toUpperCase().equals("DOCUMENTO"))
            klass = Documento.class;
        else if (type.toUpperCase().equals("FASCICOLO"))
            klass = Fascicolo.class;
        else if (type.toUpperCase().equals("TITOLARIO"))
            klass = Titolario.class;
        else if (type.toUpperCase().equals("ENTE"))
            klass = Ente.class;
        else if (type.toUpperCase().equals("AOO"))
            klass = AOO.class;
        else if (type.toUpperCase().equals("FOLDER"))
            klass = Cartella.class;
        else if (type.toUpperCase().equals("USER"))
            klass = User.class;
        else if (type.toUpperCase().equals("GROUP"))
            klass = Group.class;
        else if (type.toUpperCase().equals("ANNO_FASCICOLO"))
            klass =  VirtualObject.class;
        else if (type.toUpperCase().equals("VIRTUAL"))
            klass =  VirtualObject.class;
        else
            klass = AnagraficaCustom.class;

        return convertSolrDoc(doc,klass);

    }

    public static <T extends ICIFSObject> T convertSolrDoc(SolrDocument doc, Class<T> klass) {
        if(!ICIFSObject.class.isAssignableFrom(klass)) {
            throw new IllegalArgumentException("Class must be a subclass of ICIFSObject");
        }
        T convertedObj;
        try {



            try {
                convertedObj = klass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }

            Map<String, Collection<Object>> fieldValues = doc.getFieldValuesMap();
            if(fieldValues==null){
                logger.error("fieldValues is null");
                logger.error("doc with fieldsvalue null is"+doc.toString());

            }
            for (String field : fieldValues.keySet()) {

                StringBuilder builder = new StringBuilder();

                if(fieldValues.get(field)!=null){
                    for (Object value : fieldValues.get(field)) {
                        String strVal;
                        if (value instanceof Date) {
                            strVal = formateDate((Date) value);
                        } else if (field.equals("CREATION_DATE") || field.equals("CREATED")) {
                            strVal = formateDate((Date) doc.getFieldValue("created_on"));
                        } else if (field.equals("MODIFIED")) {
                            strVal = formateDate((Date) doc.getFieldValue("modified_on"));
                        } else {
                            strVal = String.valueOf(value);
                        }
                        builder.append(strVal);
                        builder.append(',');
                    }
                    if (builder.length() > 0)
                        builder.deleteCharAt(builder.length() - 1);
                    convertedObj.setProperty(field, builder.toString());
                }else{
                    logger.trace("field name null is:{}",field);
                }
            }


//        Object pathObj = doc.getFieldValue(SOLR_PATH_FIELD);
//        if (pathObj != null) {
//            String path = String.valueOf(pathObj);
//            String convertedPath = Joiner.on('\\').join(Splitter.on('/').split(path));
//            //TODO: Rimuovere l'OLDPATH: è utilizzato solo per scopi di debug
//            convertedObj.setProperty("OLDPATH", convertedObj.getProperty("PATH"));
//            convertedObj.setProperty("PATH", convertedPath);
//        }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }

        return convertedObj;
    }
}
