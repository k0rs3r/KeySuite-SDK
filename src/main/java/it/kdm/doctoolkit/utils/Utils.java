package it.kdm.doctoolkit.utils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.LockStatus;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.Serializable;
import java.security.KeyException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static File getConfigHome() {
        return ApplicationProperties.CONFIG_HOME_FILE;
    }

    private static final Pattern SEDE_REGEX = Pattern.compile("\\s\\(([^()]*?)\\)(?:$|\\\\|\\/)");

    public static final char DOCNUM_SEP = '#';
    public static final Pattern DOCNUM_OPEN_REGEX = Pattern.compile(".+" + DOCNUM_SEP + "(?<docnum>\\d+)\\.\\w{2,4}$");

    private static MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();

    private Utils() {
    }

    public static String extractOptionalTokenKey(String token, String key, String def) {

        Preconditions.checkArgument(token != null,
                "Attempted to extract a key from a null token");

        Preconditions.checkArgument(!Strings.isNullOrEmpty(key),
                "Attempted to extract an empty or null key from a token");

        Pattern pattern = Pattern.compile(String.format("(?:\\||^)%s:([^|]*?)\\|.*", key));
        Matcher matcher = pattern.matcher(token);
        if (!matcher.find()) {
            return def;
        }

        return matcher.group(1);
    }

    public static String extractTokenKey(String token, String key) throws KeyException {

        String value = extractOptionalTokenKey(token, key, null);

        if (value==null) {
            throw new KeyException(String.format("Key %s not found in token: %s", key, token));
        }

        return value;
    }

    public static String addTokenKey(String token, String key, String value) {

        if (key == null || key.equals("")) {
            return token;
        }

        if (value == null || value.equals("")) {
            return token;
        }

        Pattern pattern = Pattern.compile(String.format("((?:\\||^)%s:)[^|]*?(\\|.*)", key));
        Matcher matcher = pattern.matcher(token);
        if (!matcher.find()) {
            token = token + String.format("%s:%s|", key, value);
        } else {
            token = matcher.replaceFirst(String.format("$1%s$2", value));
        }

        return token;
    }

    public static String removeTokenKey(String token, String key) {
        if (key == null || key.equals("")) {
            return token;
        }

        Pattern pattern = Pattern.compile(String.format("(\\||^)%s:[^|]*?\\|(.*)", key));
        Matcher matcher = pattern.matcher(token);
        if (matcher.find()) {
            token = matcher.replaceFirst("$1$2");
        }

        return token;
    }

    private static final Pattern tokenPattern = Pattern.compile("^(?:[^:|]+:[^:|]+\\|)+$");

    public static boolean isTokenWellFormed(String token) {
        Matcher matcher = tokenPattern.matcher(token);

        return matcher.matches();
    }

    public static boolean hasTokenKey(String token, String key) {
        if (key == null || key.equals("")) {
            return false;
        }

        Pattern pattern = Pattern.compile(String.format("(?:\\||^)%s:(?:[^|]*?)\\|.*", key));
        Matcher matcher = pattern.matcher(token);
        if (!matcher.find()) {
            return false;
        }

        return true;
    }

    public static String getPathParent(String path) {
        if (path.isEmpty() || path.equals("/")) {
            //TODO: Should i throw an exception?
            return "";
        }

        int idx = path.lastIndexOf('/');
        if (idx == -1) {
            return "";
        }

        return path.substring(0, path.lastIndexOf('/'));
    }

    public static String getPathName(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return "";
        }

        int idx = path.lastIndexOf('/');
        if (idx == -1) {
            return path;
        }

        return path.substring(idx + 1);
    }

    private static final long ms_day = 24*60*60*1000;
    public static DateTime parseDateTime(String dt){

        if ("EPOCH".equals(dt) || Strings.isNullOrEmpty(dt)) {
            dt = "1900-01-01";
        } else if (dt.startsWith("NOW")){
            long now_l = System.currentTimeMillis();
            Integer days = 0;

            if (dt.contains("-")){
                days = -Integer.parseInt(dt.split("-")[1].trim());
            } else if (dt.contains("+")) {
                days = Integer.parseInt(dt.split("\\+")[1].trim());
            }
            now_l = now_l + days*ms_day - now_l % (ms_day);
            return new DateTime(now_l);
        }

        if (!dt.contains("T"))
            dt += "T00:00:00.000Z";

        return Utils._parseDateTime(dt);
    }

    private static DateTime _parseDateTime(String dateTime) {
        try {
            if (!Strings.isNullOrEmpty(dateTime)) {
                return ISODateTimeFormat.dateTime().parseDateTime(dateTime);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return DateTime.now();
    }

    public static String formatDateTime(Date date){
        return formatDateTime(new DateTime(date),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String formatDateTime(DateTime date){
        return formatDateTime(date,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String formatDateTime(DateTime date, String formatter) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formatter);
        String str = fmt.print(date);
        return str;
    }

    public static Optional<String> extractSede(String name) {
        Matcher matcher = SEDE_REGEX.matcher(name);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.absent();
    }

    public static String getLocationFromPhysicalPath(String path) {
        //stefano
//        Joiner.on('/').join(
//                Splitter.on('\\').split(path));

        if (Strings.isNullOrEmpty(path)) {
            return "";
        }

        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }

        int index = path.indexOf('/');
        if (index == -1) {
            return path;
        }

        return path.substring(0, index);
    }
/*
    public static String getLocationFromAlfrescoPath(String path) {
        //stefano
//        Joiner.on('/').join(
//                Splitter.on('\\').split(path));

        if (Strings.isNullOrEmpty(path)) {
            return "";
        }

        if (path.charAt(0) == '\\') {
            path = path.substring(1);
        }

        int index = path.indexOf('\\');
        if (index == -1) {
            return path;
        }

        return path.substring(0, index);
    }
*/
    public static String parseProfileFromPath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }

        int idx = path.indexOf('/');
        if (idx == -1) {
            return path;
        } else {
            return path.substring(0, idx);
        }
    }

    public static String removeProfileFromPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return path;
        }

        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }

        int idx = path.indexOf('/');
        if (idx == -1) {
            return "";
        } else {
            return path.substring(idx + 1);
        }
    }

    private static Serializable scriptSede = null;


//    public static Optional<String> extractSedeFromPathFE(String path) throws IOException {
//        return extractSedeFromPath(Joiner.on('\\').join(
//                Splitter.on('/').omitEmptyStrings().split(path)));
//    }

//    public static String removeSede(String name) {
//        Matcher matcher = SEDE_REGEX.matcher(name);
//        return matcher.replaceAll("");
//    }

    public static String getContentType(String filename) {
        return mimeMap.getContentType(filename.toLowerCase());
    }

    public static String addAffix(String name, String affix) {
        String newChildName;
        int idx = name.lastIndexOf('.');
        if (idx > -1) {
            newChildName = String.format("%s%s%s",
                    name.substring(0, idx), affix, name.substring(idx));
        } else {
            newChildName = String.format("%s%s", name, affix);
        }
        return newChildName;
    }



    public static File getSignCertVerify(String fileName) {
         File bpmconfig = new File(getConfigHome(), "verifysign");
        return new  File(bpmconfig, fileName);
    }



    private static final Pattern USER_REGEX = Pattern.compile("[^!]+!([^@]+)@user");

    public static String extractSolrUser(String userString) {

        if(userString == null)
            return "";

        Matcher matcher = USER_REGEX.matcher(userString);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return userString;
    }

    public static List<DocVersions> parseDocVersions(String xmlVersions) throws XMLStreamException {
        //estrae i dati relativi alle versioni nell'xml del metadato "VERSIONS" di docer
        List<DocVersions> docVersions = new ArrayList<>();
        OMElement xml = AXIOMUtil.stringToOM(xmlVersions);

        //ciclo tag version
        for (Iterator iter = xml.getChildren(); iter.hasNext(); ) {
            OMElement version = (OMElement) iter.next();

            //ciclo tag metadati versione
            HashMap<String, Object> values = new HashMap<>();
            for (Iterator iter2 = version.getChildren(); iter2.hasNext(); ) {
                OMElement child = (OMElement) iter2.next();
                String nodeName = child.getLocalName();
                String nodeValue = child.getText();
                values.put(nodeName, nodeValue);
            }

            DocVersions docVersion = new DocVersions();
            docVersion.importProperties(values);

            docVersions.add(docVersion);
        }

        return docVersions;
    }

    private static Integer[] convertToIntArray(String[] strings) {
        Integer[] intarray = new Integer[strings.length];
        int i = 0;
        for (String str : strings) {
            intarray[i] = Integer.parseInt(str);//Exception in this line
            i++;
        }
        return intarray;
    }

//    public static Integer[] reverseArrayOrder(String[] strings) {
//
//        Integer[] integers = convertToIntArray(strings);
//        Arrays.sort(integers, Collections.reverseOrder());
//        return integers;
//    }

    public static String[] reverseArrayOrder(String[] strings) {

        for( int i = 0; i<strings.length/2;i++)
        {
            String temp = strings[i];
            strings[i] = strings[strings.length - i - 1];
            strings[strings.length - i - 1] = temp;
        }

        return strings;
    }

    public static LockStatus buildLockStatus( String lock_to, String lock_by ){

        if (Strings.isNullOrEmpty(lock_to))
            return new LockStatus(false,null);
        else
            return new LockStatus(Utils.parseDateTime(lock_to).isAfterNow(), lock_by );
    }

    /*public static boolean noDocer(String token){
        return token.contains("noDocer:true");
    }*/
}
