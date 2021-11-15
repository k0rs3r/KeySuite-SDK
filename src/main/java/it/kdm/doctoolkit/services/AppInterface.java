package it.kdm.doctoolkit.services;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.core.env.PropertyResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.security.KeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by danilo.russo on 31/03/2016.
 */
public class AppInterface extends SolrPathInterface {

    private static PropertyResolver properties;
    public static final String PATH = "PATH";
//    public static final String HAS_TILDE = "HAS_TILDE";
    public static final String INHERITS_ACL = "INHERITS_ACL";
    public static final String AOO = "COD_AOO";
    public static final String DEF_ROOT = "sede";
    public static final String DEF_DOCTYPE = "defaultDocType";
    public static final String DEF_ANNO = "defaultAnnoFascicolo";
//    public static final String DEF_ANNO = "1900";


//    private final String ente;


    public AppInterface( )   {

        //load from config
        //properties = new Properties();
//        File myloc = new File(Utils.getConfigHome(), "system.properties");
//        try(InputStream inputStream = new FileInputStream(myloc)) {
//            properties.load(inputStream);
//        }
//        catch (FileNotFoundException e){
//            throw new RuntimeException(e);
//        }
//        catch (IOException e){
//            throw new RuntimeException(e);
//        }
        try{
            properties = ApplicationProperties.getInstance("system.properties").getProp();
        }catch (Exception w){
            throw new RuntimeException(w);
        }
    }

    public static String getDefaultDocType(){
        return properties.getProperty(DEF_DOCTYPE).toString();
    }
    public static String getDefaultRoot(){
        return properties.getProperty(DEF_ROOT).toString();
    }
    public static String getDefaultAnnoFascicolo(){
//        return properties.get(DEF_ANNO).toString();
        Calendar now = Calendar.getInstance();
        DateFormat date = new SimpleDateFormat("yyyy");
        String year = getDatePart("yyyy");

        return year;
    }

    private static String getDatePart(String format){

        String dp = (new SimpleDateFormat(format)).format(new Date());
        return dp;
    }

    public LockStatus getLockStatus(String token, Documento doc) throws IOException, DocerApiException {
        try {
        //TODO: SOSTITUIRE CON INFO DENTRO doc (lock_To, modified_by)
//            doc.getProperty("lock_To");
//            doc.getProperty("modified_by");
            return DocerService.recuperaLock(token, doc);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void lock(String token, Documento doc) throws DocerApiException, IOException {
        try {
            DocerService.lock(token, doc);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void unlock(String token, Documento doc) throws DocerApiException, IOException {
        try {
            DocerService.unlock(token, doc);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    public InputStream readByPath(String token, String path) throws IOException, DocerApiException {
        try {
            ICIFSObject object = openByPath(token, path);

            if (object instanceof Documento) {

                SOLRClient client = new SOLRClient();

                InputStream in = client.downloadByPath(token, (Documento) object);
                return in;

            }
            else {
                throw new IOException("Only documents can be read");
            }
        } catch (SolrServerException  | KeyException e) {
            throw new IOException(e);
        }
    }


    public ICIFSObject renameByPath(String token, String source, String destination) throws IOException, DocerApiException {

//        source = normalizePath(source);
//        destination = normalizePath(destination);

        try {
            super.openByPath(token, destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException ex) {

            ICIFSObject object = super.openByPath(token, source);

            if (object instanceof Cartella) {
                return renameFolder(token, (Cartella) object, destination);
            } else if (object instanceof Documento) {
                return renameFile(token, (Documento) object, destination);
            } else if (object instanceof Fascicolo) {
                return renameSottoFascicolo(token, (Fascicolo) object, destination);
            } else {
                throw new AccessDeniedException("Only Documents and Folders can be renamed");
            }
        }
    }

    public Cartella renameFolder(String token, Cartella folder, String destination) throws DocerApiException, IOException {

        try {
            // getFolder must throw a FileNotFoundException
            // if the folder does not exists
            super.openByPath(token, destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException e) {
            try {
                Cartella newFolder = new Cartella();
                newFolder.properties.clear();
                newFolder.setID(folder.getID());

                String oldParent = folder.getParentPath();
                String newParent = Utils.getPathParent(destination);

                ICIFSObject object = super.openByPath(token, newParent);

                // Parent mismatch, moving the folder somewhere else
                if (!oldParent.equals(newParent)) {

//                    if (!getCurrentUser(token).equalsIgnoreCase(ADMIN_USER)) {
//                        throw new AccessDeniedException("Operazioni di move ammesse solo da utenze di amministrazione");
//                    }

                    if (destination.contains(folder.getFullPath())) {
                        throw new AccessDeniedException("Cannot move a folder into itself or any of its children");
                    }

                    Cartella destFolder;
                    if (object instanceof Cartella) {
                        destFolder = (Cartella) object;
                    } else {
//                        destFolder = forceLoadFolder(token, oldParent, object.getFullPath());
                        throw new RuntimeException( "Object isnt instanceof Cartella...");
                    }

                    newFolder.setCartellaSuperiore(destFolder.getID());
                    newFolder.setParentPath(destFolder.getFullPath());
                }

                String oldBaseName = folder.getName();
                String newBaseName = Utils.getPathName(destination);

                // Rename
                if (!oldBaseName.equals(newBaseName)) {
                    newFolder.setNome(newBaseName);
//                    newFolder.setName(newBaseName);
                }


                DocerService.aggiornaCartella(token, newFolder);

//                folder.setName(newBaseName);
                folder.setNome(newBaseName);
                folder.setParentPath(newParent);

                return folder;
            } catch (Exception e1) {
//                log.error(e1.getMessage(), e1);
                throw new AccessDeniedException(e1.getMessage());
            }
        }
    }

    public Documento renameFile(String token, Documento file, String destination) throws IOException, DocerApiException{
        // Destination must not exist

        try {

            try {
                super.openByPath(token, destination);
                throw new FileAlreadyExistsException(destination);
            } catch (FileNotFoundException e) {

                String oldPath = file.getFullPath();
                String oldParent = file.getParentPath();
                String newParent = Utils.getPathParent(destination);

                ICIFSObject oldFolder = super.openByPath(token, oldParent);
                ICIFSObject newFolder = super.openByPath(token, newParent);

                // Move
                if (!oldParent.equals(newParent)) {
                    if (!oldFolder.getLocation().equals(newFolder.getLocation())) {
                        throw new AccessDeniedException("Operazioni di move ammesse solo entro la stessa sede");
                    }

                    if (oldFolder instanceof Cartella &&
                            newFolder instanceof Cartella) {
                        DocerService.aggiungiDocumentiACartella(token,
                                (Cartella) newFolder, file);
                    } else if (oldFolder instanceof Fascicolo &&
                            newFolder instanceof Fascicolo) {
                        Fascicolo oldFascicolo = (Fascicolo) oldFolder;
                        Fascicolo newFascicolo = (Fascicolo) newFolder;

                        if (!oldFascicolo.getClassifica().equals(newFascicolo.getClassifica())) {
                            throw new AccessDeniedException("Operazione di move ammessa solo entro la stessa classifica");
                        }

                        UnitaDocumentaria ud = new UnitaDocumentaria();
                        ud.setDocumentoPrincipale(file);

                        ServizioFascicolazione.fascicolaUnitaDocumentaria(token, ud, newFascicolo);
                    } else {
                        throw new AccessDeniedException("Operazioni di move ammesse solo entro folders");
                    }

                    file.setParentPath(newParent);
                }

                String oldBaseName = file.getName();
                String newBaseName = Utils.getPathName(destination);

                // Rename
                if (!oldBaseName.equals(newBaseName)) {
                    Documento doc = new Documento();
                    doc.properties.clear();
                    doc.setDocNum(file.getDocNum());
                    doc.setDocName(newBaseName);

//                    doc.properties.put(HAS_TILDE, Boolean.toString(false));
                    DocerService.aggiornaDocumento(token, doc);

//                    file.setName(newBaseName);
                    file.setDocName(newBaseName);
                }

            }
            return  file;

        } catch (Exception e) {
//            log.error(e.getMessage(), e);
            throw new AccessDeniedException(e.getMessage());
        }
    }

    public List<ICIFSObject> contentByPath(String token, String path, int limit, Ordinamento... ordinamenti) throws IOException, DocerApiException {
        //includeDocs = true;
        List<ICIFSObject> contents = super.contentByPath(token, path, limit, ordinamenti);
        List<ICIFSObject> out = new ArrayList<ICIFSObject>();

        HashSet<String> _map = new HashSet<String>();

        for (ICIFSObject obj : contents) {
            if(!_map.contains(obj.getName())) {
                _map.add(obj.getName());
                out.add(obj);
            }
        }
        return out;
    }
    public static String convertPath(String path){

//        if (Strings.isNullOrEmpty(path) || path.length() <= 1) {
//            return path;
//        }

//            path = "\\" + getDefaultRoot() + path;

//        if(path.equals("\\") || path.equals("\\*")) {
//            path = "\\" + getDefaultRoot() + "\\*";
//        }

//        if(path.equals("\\") )
//            path = "\\*";

        String appo = path;
        if(appo.contains("\\"))
            path = appo.replace("\\", "/");

        //TODO: Optimize
        String normalPath = '/' + Joiner.on('/').skipNulls().join(Splitter.on('/').omitEmptyStrings().split(path));


        return normalPath;

    }
    public Fascicolo renameSottoFascicolo(String token, Fascicolo fascicolo, String destination) throws DocerApiException, IOException {

        try {
            super.openByPath(token, destination);
            throw new FileAlreadyExistsException(destination);
        } catch (FileNotFoundException e) {

            ICIFSObject parent = super.openByPath(token, fascicolo.getParentPath());
            if (!(parent instanceof Fascicolo)) {
                throw new AccessDeniedException("Solo i SottoFascicoli possono essere rinominati");
            }

            Fascicolo newFascicolo = new Fascicolo();
            newFascicolo.properties.clear();
            newFascicolo.setEnte(fascicolo.getEnte());
            newFascicolo.setAoo(fascicolo.getAoo());
            newFascicolo.setAnno(fascicolo.getAnno());
            newFascicolo.setClassifica(fascicolo.getClassifica());
            newFascicolo.setProgressivo(fascicolo.getProgressivo());
            newFascicolo.setProgressivoPadre(fascicolo.getProgressivoPadre());

            String newParent = Utils.getPathParent(destination);

            if (!parent.getFullPath().equals(newParent)) {
                throw new AccessDeniedException("I SottoFascicoli possono solo essere rinominati");
            }

            String oldBaseName = fascicolo.getName();
            String newBaseName = Utils.getPathName(destination);

            // Rename
            if (!oldBaseName.equals(newBaseName)) {
                newFascicolo.setDescrizione(newBaseName);
//                newFascicolo.setName(newBaseName);
            }

            try {
//                newFascicolo.properties.put(HAS_TILDE, Boolean.toString(false));
                newFascicolo = ServizioFascicolazione.aggiornaFascicolo(token, newFascicolo, null);

                newFascicolo.setParentPath(newParent);

                return newFascicolo;

            } catch (Exception e1) {
                throw new IOException(e1);
            }
        }
    }

//    private Cartella forceLoadFolder(String token,String realPath, String virtualPath) throws FileNotFoundException, DocerApiException  {
//        CartellaCriteria c = new CartellaCriteria();
//        c.setMaxElementi(1);
//        c.properties.put(PATH, realPath);
//        List<Cartella> cartelle = DocerService.ricercaCartella(token, c);
//        if (cartelle.isEmpty()) {
//            throw new FileNotFoundException(realPath);
//        }
//        return cartelle.get(0);
//    }

    public void deleteByPath(String token, String path) throws DocerApiException, IOException {
        try {
//            path = normalizePath(path);

            ICIFSObject object = openByPath(token, path);

            if (object instanceof Cartella) {
                DocerService.rimuoviCartella(token, (Cartella) object);
            } else if (object instanceof Documento) {
                DocerService.rimuoviDocumento(token, (Documento) object);
            } else if (object instanceof Fascicolo) {
                DocerService.rimuoviFascicolo(token, (Fascicolo) object);
            } else {
                throw new AccessDeniedException(path);
            }
        } catch ( Exception e) {
            throw new IOException(e);
        }
    }


    public ICIFSObject createFolder(String token, String path, String owner) throws IOException {
        try {
            String name = Utils.getPathName(path);
            String parentPath = Utils.getPathParent(path);
            ICIFSObject parent = super.openByPath(token, parentPath);

//            it.kdm.doctoolkit.security.Security security = new it.kdm.doctoolkit.security.Security();
//            try {
//                checkAcl(token, parent, security.creaFolder);
//            }
//            catch (Exception e){
//                throw new AccessDeniedException(parent.getFullPath());
//            }

            if (parent instanceof Cartella) {
                Cartella parentFolder = (Cartella) parent;
                Cartella cartella = new Cartella()
                        .setEnte(parentFolder.getEnte())
                        .setAoo(parentFolder.getAoo())
                        .setCartellaSuperiore(parentFolder.getID())
                        .setNome(name);
                boolean inherits;
                if (Strings.isNullOrEmpty(parentFolder.getCartellaSuperiore())) {
                    inherits = false;
                } else {
                    inherits = true;
                }
//                cartella.properties.put(INHERITS_ACL, Boolean.toString(inherits));
                Cartella ret = DocerService.creaCartellaCifs(token,
                        cartella);
                ret.setParentPath(parent.getFullPath());

                return ret;
            } else if (parent instanceof Fascicolo) {
                Fascicolo fascicolo = (Fascicolo) parent;
                Fascicolo sottoFascicolo = new Fascicolo();
                sottoFascicolo.setEnte(fascicolo.getEnte());
                sottoFascicolo.setAoo(fascicolo.getAoo());
                sottoFascicolo.setClassifica(fascicolo.getClassifica());
                sottoFascicolo.setAnno(fascicolo.getAnno());
                sottoFascicolo.setProgressivoPadre(fascicolo.getProgressivo());
                sottoFascicolo.setDescrizione(name);

//                sottoFascicolo.properties.put(HAS_TILDE, Boolean.toString(false));
//                sottoFascicolo.properties.put(INHERITS_ACL, Boolean.toString(true));

                fascicolo = ServizioFascicolazione.creaFascicoloCifs(token, sottoFascicolo, null);
                fascicolo.setParentPath(parent.getFullPath());

                return fascicolo;

            }else if (parent instanceof Titolario) {

                Titolario titolario = (Titolario) parent;
                Fascicolo fascicolo = new Fascicolo();
                fascicolo.setEnte(titolario.getEnte());
                fascicolo.setAoo(titolario.getAoo());
                fascicolo.setClassifica(titolario.getClassifica());
                fascicolo.setAnno(this.getDefaultAnnoFascicolo());
                fascicolo.setProgressivoPadre("");
                fascicolo.setDescrizione(name);


                fascicolo = ServizioFascicolazione.creaFascicoloCifs(token, fascicolo, null);
                fascicolo.setParentPath(parent.getFullPath());

                return fascicolo;
            }

        } catch (DocerApiException e) {
//            log.error(e.getMessage(), e);
            throw new AccessDeniedException(e.getMessage());
        } catch (Exception e) {
            throw new IOException(e);
        }

        throw new AccessDeniedException(path);
    }
    public Documento createFile(String token, Documento file, String parentPath) throws DocerApiException, IOException {


            ICIFSObject object = super.openByPath(token, parentPath);
//            if (!(object instanceof Cartella) && !(object instanceof Fascicolo)) {
//                throw new AccessDeniedException(parentPath);
//            }

//            try {
//                file.setParentPath(parentPath);
//                super.openByPath(token, file.getFullPath());
//                throw new FileAlreadyExistsException("FileAlreadyExists: " + file.getFullPath());
//            } catch (FileNotFoundException e) {}

            file.setEnte(getEnte(token));
            file.setAoo(object.properties.get(AOO));

//            file.properties.put(HAS_TILDE, Boolean.toString(false));
//            file.properties.put(INHERITS_ACL, Boolean.toString(true));

            if (object instanceof Cartella) {
                Cartella parent = (Cartella) object;

                file.setProperty("PARENT_FOLDER_ID", parent.getID());
            } else if (object instanceof Fascicolo) {
                Fascicolo parent = (Fascicolo) object;

                file.setProperty("PARENT_FASCICOLO_ID", String.format("%s|%s|%s",
                        parent.getClassifica(), parent.getAnno(), parent.getProgressivo()));
            }

//            String node_uuid = object.getSolrId();
//            if(Strings.isNullOrEmpty(node_uuid)) {
//                node_uuid = object.properties.get("node_id");
//            }
//
//            if (Strings.isNullOrEmpty(node_uuid)) {
//                throw new AccessDeniedException("Node id not found in parent object: " + object.getFullPath());
//            }
//
//            file.setProperty("PARENT_NODE_ID", node_uuid);

            file = DocerService.creaDocumentoCIFS(token, file);

            return file;

    }

    public String getEnte(String token) {

        try{
            return Utils.extractTokenKey(token,"ente");
        }catch(Exception e ){
                throw new RuntimeException(e);
        }
    }


    public <T extends ICIFSObject> T openByPath(String token, String path, Class<T> klass) throws IOException, DocerApiException {
        ICIFSObject obj = super.openByPath(token, path);
        if (!klass.isInstance(obj)) {
            throw new DocerApiException(String.format("%s is not of type %s", path, klass.getName()), 500);
        }

        return klass.cast(obj);
    }
}
