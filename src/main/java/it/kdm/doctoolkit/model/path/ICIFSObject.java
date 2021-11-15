package it.kdm.doctoolkit.model.path;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lorenxs on 1/15/14.
 */
public abstract class ICIFSObject extends GenericObject implements Serializable{

    protected enum DescriptionType {ENTE, AOO, TITOLARIO, FASCICOLO, USER}
    private String server;
    private String share;
    protected static final String webURLS="/AppBPM/bridge?type=";

    private String parentPath;
    private String webURL;

    private String name;
    private boolean hidden;

    private String tag;

    private String sede;

    protected String getDescription(DescriptionType type) {

        String path = this.getProperty("PHYSICAL_PATH");

        if (path==null)
            return "";

        String[] parts = path.split("/");

        if (type.equals(DescriptionType.ENTE)) {
            return getProperty("DES_ENTE");
        } else if (type.equals(DescriptionType.AOO)) {
            return getProperty("DES_AOO");
        } else if (type.equals(DescriptionType.TITOLARIO)) {
            return parts[3];
        } else if (type.equals(DescriptionType.FASCICOLO)) {
            return parts[parts.length-1];
        }

        //default
        return parts[parts.length-1];
    }

    public String getDescriptionEnte() {
        return getDescription(DescriptionType.ENTE);
    }

    public String getDescriptionAOO() {
        return getDescription(DescriptionType.AOO);
    }

    public String getFullPath() {
        String path = getVirtualPath();
        if (Strings.isNullOrEmpty(path)) {
            StringBuilder builder = new StringBuilder();
            String parentPath = getParentPath();

            if (parentPath == null) {
                return "";
            }

            builder.append(parentPath);
            if (parentPath.length() == 0 || parentPath.charAt(parentPath.length()-1) != '/') {
                builder.append('/');
            }

            builder.append(getName());

            return builder.toString();
        } else {
            return path;
        }
    }

    public String getFEFullPathLinkName() {
        String path =getFEFullPath();
        return path; //Workaround encode # char (URL link)
    }
    
    
    public String getFEFullPathLinkNameForQS() {
        String path =getFEFullPath();
        path=path.replace("&", "%26");
        return path.replace("#","%23"); //Workaround encode # char (URL link)
    }

    public String getFEFullPath() {
        String path = unixPathConv(getFullPath());
        return path;
        //return path.replace("#","%23"); //Workaround encode # char (URL link)

    }

    @Override
    public String getFEName() {
        return getName();
    }

    public String getFENameLink() {
        return getName().replace("#","%23"); //Workaround encode # char (URL link)
    }

    public String getFullName() {
        return getName();
    }

    public String getReadableFullName() {
        String fullName = getFullName();
        try {
            fullName = URLDecoder.decode(fullName, "UTF-8");

            if(this instanceof Documento) {
                if (this.properties.containsKey("content_size")
                        && this.getProperty("content_size") != null) {
                    String content_size = this.getProperty("content_size");
                    Long size = Long.parseLong(content_size);
                    fullName += " (" + FileUtils.byteCountToDisplaySize(size) + ")";
                }
            }
        }
        catch(Exception exc){

        }
        return fullName;
    }
//
//    public String getProperty(String key, String format) throws Exception {
//
//        return getProperty(key, format, null, null);
//
//    }
//    public String getProperty(String key, String format, String language) throws Exception {
//
////        locale=locale!=null?locale: Locale.ITALY;
//
//
//        return getProperty(key, format, language, null);
//
//    }
//    public String getProperty(String key, String format, String language, String... args) throws Exception
//    {
//
//        String val = this.getProperty(key);
//        if(val==null)
//            return val;
//
//
//        try {
//
//
//            if (format != null && args != null) {
//
//                String[] toks = new String[args.length];
//                int i=0;
//                for (String exp : args) {
//
//                    Pattern pattern = Pattern.compile(exp);
//                    Matcher matcher = pattern.matcher(val);
//                    toks[i++] = matcher.find() && matcher.groupCount() > 0 ? matcher.group(1) : "";
//
//                }
//
//                val = String.format(format, toks);
//
//            } else if (format != null && language != null) {
//
//                if(!language.matches("[a-z]{2}-[A-Z]{2}")) {
//                    throw new Exception("Formato richiesto non valido");
//                }
//
//                Locale locale =null;
//                String[] opts = language.split("-");
//                locale = new Locale(opts[0], opts[1]);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'", locale);
//                Date date = sdf.parse(val);
//                SimpleDateFormat out = new SimpleDateFormat(format, locale);
//                val = out.format(date);
//
//            }
//        }catch(Exception exc){
//            throw new RuntimeException(exc);
//        }
//
//
//        return val;
//    }

    private static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public String getContentSummary() {

        /* TODO
        *  distinguere il tipo di oggetto e il campo con il contenuto summary
        * */
        String summary = this.getProperty("MAIL_BODY");

        if(summary!=null) {
            summary = summary.replaceAll("<[^>]+>", "");
        }

        return summary;
    }
    public String getMimeType() {
        return Utils.getContentType(getFullName().toLowerCase());
    }

    private String unixPathConv(String windowsPath) {
        if(windowsPath == null) {
            return "";
        }

        return Joiner.on('/').join(
                Splitter.on('\\').split(windowsPath));
    }

    /**
     * L'ultima parte del path dell'oggetto. Richiede che il path sia valorizzato.
     * @return l'ultima parte del path
     */
    public String getName() {
        String vPath = this.getProperty("VIRTUAL_PATH");
        if (Strings.isNullOrEmpty(vPath)) {
            return getComputedName();
        } else {
            return vPath.substring(vPath.lastIndexOf('/')+1);
        }

    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
//        this.name = name;
    }

    protected abstract String getComputedName();

    /**
     * L'estensione del file dell'oggetto. Richiede che il path sia valorizzato
     * @return l'estensione
     */
    public String getExtension() {
        return "";
    }

    public abstract boolean isDirectory();
    public abstract boolean isFile();
   
    /**
     * La dimensione dell'oggetto in byte.
     * Se il path rappresenta una cartella la dimensione ritornera' 0
     * @return
     */
    public abstract long getSize();

    /**
     * Un identificatore univoco del'oggetto
     * @return
     */
    public abstract String getID();

    public String getSolrId() {

        String solrId = getProperty("id");
        //in caso di retrieve dell'object da docer tramite ws (non viene tornato il metdato id di solr)
        if (Strings.isNullOrEmpty(solrId)) {
            return buildSolrId(this);
        }

        return solrId;
    }
    /**
     * La stringa che descrive il tipo dell'oggetto
     * @return
     */
    public abstract String getType();

    @Override
    public String getBusinessType() {

        if(this.properties.containsKey("count") ){
            return "facet";
        }

        if (Strings.isNullOrEmpty(businessType)) {
            return getType();
        }

        return businessType;
    }

    /**
     * L'autore dell'oggetto
     * @return
     */
    @Override
    public String getFEAuthor(){
    	return this.getProperty("AUTHOR_ID");
    }



    /**
     * L'utente che ha creato l'oggetto
     * @return
     */
    public String getCreator(){
        return Utils.extractSolrUser(this.getProperty("CREATOR"));
    }

    /**
     * L'utente che ha modificato per ultimo l'oggetto
     * @return
     */
    public String getLastModifier() {
    	return Utils.extractSolrUser(this.getProperty("MODIFIER"));
    }

    /**
     * La data di ultima modifica dell'oggetto
     * @return
     */
    public DateTime getModifiedDate() {
        return Utils.parseDateTime(this.getProperty("modified_on"));
    }

    public String getFEShortDate(String fieldName) {
        DateTime dt = Utils.parseDateTime(this.getProperty(fieldName));
        return Utils.formatDateTime(dt,"dd-MM-yyyy");
    }

    public String getFELongDate(String fieldName) {
        DateTime dt = Utils.parseDateTime(this.getProperty(fieldName));
        return Utils.formatDateTime(dt,"dd-MM-yyyy HH:mm");
    }

    public Date getPropertyDate( String PropertyName, Locale locale) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale);
        String strout = properties.get(PropertyName);

        if(strout==null)
            return null;

        Date date = new Date();

        try {

            if ( strout!=null )
                date = sdf.parse(strout);
            else
                date = sdf.parse("1970-01-01");

        }
        catch(Exception exc){
            return null;
        }

        return date;
    }

    /**
     * La data di creazione dell'oggetto
     * @return
     */
    public DateTime getCreationDate() {
        return Utils.parseDateTime(this.getProperty("created_on"));
    }

    /**
     * Una stringa che identifica la versione dell'oggetto: un timestamp, un id
     * o un checksum.
     * @return
     */
    public abstract String getVersionID();

    /**
     * Lo snippet di testo associato all'oggetto
     * @return
     */
    public abstract String getAbstract();

//    protected static String addComponent(String basePath, String component) {
//
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(basePath), "basePath was null or empty");
//
//        if (basePath.charAt(basePath.length()-1) == '\\') {
//            basePath = basePath.substring(0, basePath.length()-1);
//        }
//
//        return String.format("%s\\%s", basePath, component);
//    }

    public String getEnte() {
        return this.getProperty("COD_ENTE");
    }
    public String getAOO() {
        return this.getProperty("COD_AOO");
    }

    public String getVirtualPath() {
        return this.getProperty("VIRTUAL_PATH");
    }

    public String getPhysicalPath() {
        return this.getProperty("PHYSICAL_PATH");
    }

    @Override
    public String toString() {
        return getVirtualPath();
    }

    /**
     * L'indirizzo del server CIFS
     * @return
     */
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    /**
     * L'indirizzo del documentale da puntare con il toolkit
     * sintassi: http://localhost:8080/
     * @return
     */
    public String getLocation() {
        String location = this.getProperty("location");

        if(location != null && location != "")
            return location;

        String path = this.getProperty("PHYSICAL_PATH");
        return Utils.getLocationFromPhysicalPath(path);
    }

    /**
     * Il root CIFS dell'oggetto, corrispondente con il profilo della treeview
     * @return
     */
    public String getShare() {

        return share;

    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getParentPath() {
        if (!Strings.isNullOrEmpty(this.parentPath))
            return this.parentPath;

        if (getVirtualPath()==null)
            return null;

        String[] pars = getVirtualPath().split("/");
        if (pars.length==0)
            return null;

        pars = Arrays.copyOfRange(pars,0,pars.length-1);

        return Joiner.on("/").join(pars);
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getFEParentPathLinkNameForQS() {
        return getFEParentPathLinkName().replace("&","%26"); //Workaround encode # char (URL link)
    }

    public String getFEParentPathLinkName() {
        String path = getFEParentPath();
        return path.replace("#","%23"); //Workaround encode # char (URL link)
    }
    public String getFEParentPath() {
        String path = unixPathConv(getParentPath());
        //return path.replace("#","%23"); //Workaround encode # char (URL link)
        return path;
    }

    public String getFEPartialPath(int index) {
        String path = getFEFullPath();
        List<String> splitPath = Splitter.on('/').splitToList(path);
        return Joiner.on('/').join(splitPath.subList(0, index));
    }

    public String getFEPartialPathForQS(int index) {
        String path = getFEFullPathLinkNameForQS();
        List<String> splitPath = Splitter.on('/').splitToList(path);
        return Joiner.on('/').join(splitPath.subList(0, index));
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }
    public String getMimeTypeCSS(){
    	String mimeType = getMimeType();
		String mimeTypeCSS = "fticon-file";
		
		switch (mimeType) {
			case "application/msword":
				mimeTypeCSS = "fticon-file-word";
				break;
			case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
				mimeTypeCSS = "fticon-file-word";
				break;
			case "text/plain":
				mimeTypeCSS = "fticon-file";
				break;
			case "application/vnd.ms-excel":
				mimeTypeCSS = "fticon-file-excel";
				break;
			case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
				mimeTypeCSS = "fticon-file-excel";
				break;
			case "application/zip":
				mimeTypeCSS = "fticon-file-zip";
				break;
			case "application/x-rar-compressed":
				mimeTypeCSS = "fticon-file-zip";
				break;
			case "application/pdf":
				mimeTypeCSS = "fticon-file-pdf";
				break;
			case "application/xml":
				mimeTypeCSS = "fticon-file-xml";
				break;
			case "application/vnd.ms-powerpoint":
				mimeTypeCSS = "fticon-file-powerpoint";
				break;
			case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
				mimeTypeCSS = "fticon-file-powerpoint";
				break;
			case "message/rfc822":
				mimeTypeCSS = "fticon-file-eml";
				break;
			case "application/vnd.ms-outlook":
				mimeTypeCSS = "fticon-file-msg";
				break;
		}
		return mimeTypeCSS;
	}

    public boolean isHidden() {
        try {
            String enabled = properties.get("ENABLED");
            if (Strings.isNullOrEmpty(enabled)) {
                return false;
            } else {
                return !Boolean.parseBoolean(enabled);
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDeleted() {
        return isHidden();

    }

    public void setHidden(boolean hidden) {
        properties.put("ENABLED", Boolean.toString(!hidden));
    }

    protected String buildSolrId( ICIFSObject target ) {
        String idFormat = "%s.%s!%s%s@%s";
        String location = target.getLocation();
        String codAOO = target.getProperty("COD_AOO");
        String codEnte = target.getProperty("COD_ENTE");
        String type = null;
        String sid=null;

        if (target instanceof Documento) {
            sid = "!" + target.getID();
            type = "documento";
        } else if (target instanceof Fascicolo) {
            String pianoClass = encode(target.getProperty("PIANO_CLASS"));

            sid = "!" + encode(target.getProperty("CLASSIFICA"));
            if (!Strings.isNullOrEmpty(pianoClass))
                sid += "$"+pianoClass;

            sid += "|" + target.getProperty("ANNO_FASCICOLO");
            sid += "|" + encode(target.getProperty("PROGR_FASCICOLO"));
            sid = sid.replaceAll("/","%2f");
            type = "fascicolo";
        } else if (target instanceof AOO) {
            sid = "!";
            type = "aoo";
        } else if (target instanceof Titolario) {
            String pianoClass = encode(target.getProperty("PIANO_CLASS"));

            sid = "!" + encode(target.getID());
            if (!Strings.isNullOrEmpty(pianoClass))
                sid += "$"+pianoClass;

            type = "titolario";
        } else if (target instanceof Cartella) {
            sid = "!" + target.getID();
            type = "folder";
        } else {
            sid = "";
            type = "";
        }

        String finalId = String.format(idFormat,location,codEnte,codAOO,sid,type);

        return finalId;
    }

    private String encode(String text) {
        if(Strings.isNullOrEmpty(text)){
            return text;
        }

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
