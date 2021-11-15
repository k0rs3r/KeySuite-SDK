package it.kdm.orchestratore.utils;

import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.services.ToolkitConnector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by antsic on 06/04/16.
 */
public class GeneratePathModifyDoc {



    public static String getFileUrlToModifyDocument(Documento doc, String sede, String clientip, String serverip, String sessionId, String currentUser, String token) throws Exception{
        if(ToolkitConnector.getGlobalProperty("modify.online.document.isWebDav")!=null && ToolkitConnector.getGlobalProperty("modify.online.document.isWebDav").equals("true")){
            return getWebDavPath(doc, sede,clientip,serverip,sessionId,currentUser,token);
        }else{
            return getCifsPath(doc,sede);
        }
    }



    private static String getCifsPath(Documento doc, String sede) throws Exception{
        String sedeDocUrl = ToolkitConnector.getGlobalProperty("host."+sede.toUpperCase());
        sedeDocUrl = sedeDocUrl.toUpperCase().replace("HTTP://","");
        String sedeName = sedeDocUrl.toUpperCase().replace(":8080/","");

        //http://192.168.0.107:8080/
        String cifsRootPath = ToolkitConnector.getGlobalProperty("cifs.rootPath");
        String host = String.format(cifsRootPath,sedeName);

        //String host = ToolkitConnector.getGlobalProperty("cifs.rootPath");
        //String share = doc.getShare();
//        PathInterface pathInterface = new PathInterface(token, doc.getEnte());
//        pathInterface.buildPath(doc);
        //String path = URLHelper.encodeCIFSPath(doc.getFEFullPath());
        String path = doc.getFEFullPath();



        String content = "[InternetShortcut]\r\nURL=file://" + host + path;
        return content;
    }


    private static String getWebDavPath(Documento doc, String sede, String clientip, String serverip, String sessionId, String currentUser, String token) throws Exception{


        String extension = FilenameUtils.getExtension(doc.getDocName());
        String docnameWithoutExt = FilenameUtils.removeExtension(doc.getDocName());
        //String clientip = Utils.extractTokenKey(token,"ipaddr");


        InetAddress inetAddress = InetAddress.getByName(clientip);
        boolean isIntranet = inetAddress.isSiteLocalAddress();

        if ("0:0:0:0:0:0:0:1".equals(clientip)) {
            clientip = "127.0.0.1";
        }

        String guid = UUID.randomUUID().toString();
        sessionId = StringUtils.replace(sessionId, "+", "");

        Map map = new HashMap<String, String>();
        map.put("userid", currentUser);
        map.put("filename", docnameWithoutExt);
        map.put("extension", extension);
        map.put("location", sede.toLowerCase());
        clientip = clientip.replaceAll("\\.","_");
        map.put("clientip", clientip);

        map.put("serverip", serverip);
        map.put("guid", guid);
        map.put("sessionid", sessionId);

        //carica nella mappa tutti i metadati del documento per eventualmente poterli utilizzare in webdavlink-format
        for (String meta : doc.properties.keySet()) {
            map.put(meta, doc.properties.get(meta));
        }

        String webdavlinkFormat = null;

        if (isIntranet) {
            webdavlinkFormat = ToolkitConnector.getGlobalProperty("webdavlink-format-intranet");
        } else {
            webdavlinkFormat = ToolkitConnector.getGlobalProperty("webdavlink-format-internet");
        }
        StrSubstitutor sub = new StrSubstitutor(map);
        String webdavlink = sub.replace(webdavlinkFormat);

        String webdavstorepathFormat = ToolkitConnector.getGlobalProperty("webdavstorepath-format");
        String storepath = sub.replace(webdavstorepathFormat);

        String fullPath = FilenameUtils.getFullPath(storepath);
        fullPath = fullPath + "token.txt";


        //scrittura del file nello store
        File file = new File(storepath);
        FileOutputStream os = FileUtils.openOutputStream(file);
        os.close();
        writeTokenInFile(token, fullPath);
        String content = "[InternetShortcut]\r\nURL=" + webdavlink;
        return content;
    }

    private static void writeTokenInFile(String token, String file) throws  Exception{
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print(token);
        writer.close();
    }


}
