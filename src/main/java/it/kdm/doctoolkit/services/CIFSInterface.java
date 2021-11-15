package it.kdm.doctoolkit.services;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyException;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.Cartella;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.model.Fascicolo;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.utils.CacheManager;
import it.kdm.doctoolkit.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.jcs.access.exception.CacheException;
import org.apache.solr.client.solrj.SolrServerException;

/**
 * Created by danilo.russo on 31/03/2016.
 */
public class CIFSInterface extends AppInterface{

    public CIFSInterface()   {

        super();

    }

//    public CIFSInterface(TokenGeneratorCallback callback, String ente) throws DocerApiException, IOException {
//        super( callback, ente);
//    }

    public <T extends ICIFSObject> T openByUncachedPath(String token, String path, Class<T> klass) throws IOException, DocerApiException {
        ICIFSObject obj = super.openByPath(token, path);
        if (!klass.isInstance(obj)) {
            throw new DocerApiException(String.format("%s is not of type %s", path, klass.getName()), 500);
        }

        return klass.cast(obj);
    }

    public ICIFSObject openByUncachedPath(String token, String path) throws IOException, DocerApiException {
        ICIFSObject obj = super.openByPath(token, path);

        return obj;
    }
    public void deleteByPath(String token, String path) throws DocerApiException, IOException {
        try {

            super.deleteByPath(token, path);
            markDeleted(path);

        } catch ( Exception e) {
            throw new IOException(e);
        }
    }

    public void updateModifierByPath(String token, String path, String modifier) throws IOException, DocerApiException {
        try {
            Documento doc = openByPath(token, path, Documento.class);
            doc.setProperty("MODIFIER", modifier);
            doc.setProperty("content_modified_by", modifier);
            CacheManager.cifsCache().put(doc.getFullPath(), doc);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public void markDeleted(String path) throws IOException {

        try {
            CacheManager cache = CacheManager.cifsCache();
            cache.put(path, "DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

//    public Cartella renameFolder(String token, Cartella folder, String destination) throws DocerApiException, IOException  {
//
//            Cartella cartella = super.renameFolder(token, folder, destination);
//
//            markDeleted(cartella.getFullPath());
//            try {
//            CacheManager.cifsCache().put(cartella.getFullPath(), cartella);
//            } catch (CacheException e){
//                throw new IOException(e);
//            }
//
//            return cartella;
//    }
//
//    public Fascicolo renameSottoFascicolo(String token, Fascicolo fascicolo, String destination) throws DocerApiException, IOException  {
//
//        Fascicolo newFascicolo = renameSottoFascicolo(token, fascicolo, destination) ;
//        markDeleted(fascicolo.getFullPath());
//        try{
//            CacheManager.cifsCache().put(newFascicolo.getFullPath(), newFascicolo);
//        }catch (Exception e){
//            throw new IOException(e);
//        }
//        return newFascicolo;
//    }
//
//    public Documento renameFile(String token, Documento file, String destination) throws IOException, DocerApiException{
//
//        String oldPath = file.getFullPath();
//        Documento documento = super.renameFile( token, file, destination);
//
//        markDeleted(oldPath);
//        try{
//
//            CacheManager.cifsCache().put(documento.getFullPath(), documento);
//        }catch (Exception e){
//            throw new IOException(e);
//        }
//        return documento;
//    }

//    public ICIFSObject renameByPath(String token, String source, String destination) throws IOException, DocerApiException{
//
//        ICIFSObject obj = super.renameByPath(token, source, destination);
//
////        markDeleted(source);
//        try{
//            CacheManager.cifsCache().remove(source);
//        }catch (Exception e){
//            throw new IOException(e);
//        }
//
//        return obj;
//    }

    public void fakeDelete(String token,String path) throws IOException {

        try {

            CacheManager cache = CacheManager.cifsCache();
            cache.put(path, "FAKE-DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    public boolean isFakeDelete(String path) throws IOException {
        try {

            Optional obj = CacheManager.cifsCache().get(path);

            return obj.isPresent() && obj.get().toString().equals("FAKE-DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }
   public boolean isDeleted(String path) throws IOException {
        path = convertPath(path);

        try {

            Optional obj = CacheManager.cifsCache().get(path);
            return obj.isPresent() && obj.get().toString().equals("DELETE");
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }
    public Documento createFile(String token, Documento file, String parentPath) throws DocerApiException, IOException {
        try {

            file = super.createFile(token,   file,   parentPath);

            CacheManager.cifsCache().put(file.getFullPath(), file);
            return file;

        } catch (CacheException e) {
            throw new IOException(e);
        }
    }
    public ICIFSObject createFolder(String token, String path, String owner) throws IOException {

        ICIFSObject obj = super.createFolder(token, path, owner);
        try{
            CacheManager.cifsCache().put(obj.getFullPath(), obj);
        }catch (Exception e){
            throw new IOException(e);
        }
        return obj;
    }

    public void updateFileSizeByPath(String token, String path, long size) throws IOException, DocerApiException {

        try {
            Documento doc = openByPath(token, path, Documento.class);
             updateSize(doc, size);
        } catch (/*CacheException */ Exception e) {
            throw new IOException(e);
        }
    }

    public void updateFileVersionByPath(String token, String path, String version) throws IOException, DocerApiException {

        try {
            Documento doc = openByPath(token, path, Documento.class);
            updateVersion(doc, version);
        } catch (/*CacheException */ Exception e) {
            throw new IOException(e);
        }
    }
    public InputStream readByPath(String token, String path) throws IOException, DocerApiException {

        if(path.endsWith("desktop.ini")){

            try {
                String icon = "4";
                String desc = "ICIFSObject";

                if (path.matches(".+/\\$Autori/desktop.ini")) {
                    icon = "7";
                    desc = "Cartella virtuale";
                }

                String str = "[.ShellClassInfo]\r\n";
                str += "InfoTip=Cartella virtuale.\r\n";
                str += "IconFile=C:\\Windows\\System32\\imageres.dll,-186\r\n";
                str += String.format("IconIndex=%s", icon);

                StringReader reader = new StringReader(str);
                InputStream desktop_ini = new ReaderInputStream(reader);

                return desktop_ini;
            }catch(Exception e){
                //do nothing

            }
        }


        try {
            ICIFSObject object = openByPath(token, path);

            if (object instanceof Documento) {

                Documento doc = (Documento) object;

                SOLRClient client = new SOLRClient();

//                Security security = new Security();
//                try {
//                    checkAcl(token, object, security.read);
//                }
//                catch (Exception e){
//                    throw new AccessDeniedException(doc.getFullPath());
//                }

                Optional<String> filePath = CacheManager.fileCache().get(doc.getVersionID());
                if (filePath.isPresent()) {
                    File tmpFile = new File(filePath.get());
                    if (tmpFile.exists()) {
                        //updateSize(doc, tmpFile.length());
                        return new FileInputStream(filePath.get());
                    }
                }
                File tmpFile = File.createTempFile(doc.getName(), "bin");

                it.kdm.doctoolkit.model.DocerFile dfile = DocerService.downloadDocument(token, ((Documento) object).getDocNum());
                InputStream in = dfile.getContent().getInputStream();

                FileOutputStream out = new FileOutputStream(tmpFile);

                IOUtils.copy(in, out);
                updateSize(doc, tmpFile.length());
                CacheManager.fileCache().put(dfile.toString(), tmpFile.getAbsolutePath());


                return new FileInputStream(tmpFile);

            } else {
                throw new IOException("Only documents can be read");
            }
        } catch ( Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void updateSize(Documento doc, long size) throws IOException {
        try {
            doc.properties.put("content_size", Long.toString(size));
            doc.properties.put("CONTENT_SIZE", Long.toString(size));
            CacheManager.cifsCache().put(doc.getFullPath(), doc);
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    private void updateVersion(Documento doc, String version) throws IOException {
        try {
            doc.properties.put("VERSION_ID", version);
            CacheManager.cifsCache().put(doc.getFullPath(), doc);
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }
    public ICIFSObject openByPath(String token, String path) throws IOException, DocerApiException {
        path = convertPath(path);

        try {
            if (isFakeDelete(path) || isDeleted(path)) {
                throw new FileNotFoundException(path);
            } else {
                Optional cachedObj = CacheManager.cifsCache().get(path);
                if (cachedObj.isPresent() && cachedObj.get() instanceof ICIFSObject) {
                    ICIFSObject obj = (ICIFSObject) cachedObj.get();
                    if (obj.getLocation().equalsIgnoreCase(getDefaultRoot())) {
                        return obj;
                    }
                }
                ICIFSObject obj = openByUncachedPath(token, path);

                return obj;
            }
        } catch ( CacheException e) {
            throw new IOException(e);
        }
    }

    public Boolean cacheFile(String path, File file) {

        try {
            CacheManager.cifsCache().put(path, file);
        }catch(Exception e){
            //TODO
        }

        return false;
    }

    public Object getCachedFile(String path )  throws IOException{

            try {

                Optional obj = CacheManager.cifsCache().get(path);

                if(obj.isPresent() )
                    return obj.get();

            } catch (CacheException e) {
                throw new IOException(e);
            }

        return null;

    }
    public static Boolean isDesktopIni(String path) {

        if (path.endsWith("desktop.ini"))
            return true;

        return false;
    }

    public static void addTip(String path, String tip) throws IOException {

        try {

            CacheManager cache = CacheManager.tipsCache();
            cache.put(path, tip);
        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    public static boolean hasTip(String path) throws IOException {
        try {

            Optional obj = CacheManager.tipsCache().get(path);

            return obj.isPresent() && !"".equals(obj.get().toString());

        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

    public static String getTip(String path) throws IOException {

        try {

            Optional obj = CacheManager.tipsCache().get(path);
            return  obj.get().toString() ;

        } catch (CacheException e) {
            throw new IOException(e);
        }
    }

}
