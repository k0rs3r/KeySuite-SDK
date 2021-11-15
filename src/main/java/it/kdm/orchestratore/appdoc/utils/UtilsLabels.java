package it.kdm.orchestratore.appdoc.utils;

import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.services.ToolkitConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

/**
 * Created by danilo.russo on 15/11/2017.
 */
public class UtilsLabels {

    @Autowired
    private MessageSource messageSource;

    public static String getTypeDesc(String typeID, String filter) throws Exception
    {
        Map<String,String>  map = CallDocumentMgt.getDocumentsTypesMap(filter);

        if(map.containsKey(typeID))
            return map.get(typeID);
        else
            return String.format("[%s]", typeID);
    }

    public static String getHelpURL(String viewName) throws Exception
    {
        String helpUrl = ToolkitConnector.getHelpProperty("help.baseurl");
        if (helpUrl == null){
            return "#";
        }

        String helpPage = ToolkitConnector.getHelpProperty(String.format("bookmark.%s", viewName));
        if( helpPage != null) {
            helpUrl += "#"+helpPage;
        }

        return helpUrl;
    }

    public static String formatVirtualName(ICIFSObject obj, MessageSource messageSource, Locale locale){

        String virtualName = obj.getFEName();

        String prefixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.prefix");
        prefixVirtualId = prefixVirtualId != null ? prefixVirtualId:"#";

        if(virtualName.startsWith(prefixVirtualId)){
            virtualName = virtualName.substring(virtualName.indexOf(prefixVirtualId)+1);
        }

        String suffixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.suffix");
        suffixVirtualId = suffixVirtualId != null ? suffixVirtualId:"";

        if(!"".equals(suffixVirtualId) && virtualName.endsWith(suffixVirtualId) ){
            virtualName = virtualName.substring(0, virtualName.lastIndexOf(suffixVirtualId));
        }

        String labelKey = String.format("label.virtual.%s",  obj.getProperty("type"));
        String labelName = messageSource.getMessage(labelKey, null, labelKey, locale);

        virtualName = String.format(labelName +"%s", virtualName);

        return virtualName;
    }


public static String getFacetFomat(){

    String prefixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.prefix");
    prefixVirtualId = prefixVirtualId != null ? prefixVirtualId : "#";

    String suffixVirtualId = ToolkitConnector.getGlobalProperty("solrvirtualobjects.suffix");
    suffixVirtualId = suffixVirtualId != null ? suffixVirtualId : "";

    String format = prefixVirtualId + "%s" + suffixVirtualId;

    return format;
    }
//    public Map<String,String> setTypeLabelsMap(Map<String,String>  map){
//
//        for (String typeId : map.keySet()){
//            String defName = map.get(typeId);
//            String lblKey = String.format("label.tipologia.%s", typeId);
//            String typeName = messageSource.getMessage(lblKey, null, defName, null);
//
//            map.put(typeId, typeName);
//        }
//        return map;
//    }
}
