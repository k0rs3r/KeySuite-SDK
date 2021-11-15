package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: lorenzo
 * Date: 10/9/13
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Cartella extends ICIFSObject implements Serializable {

    public static final String TYPE = "folder";

    public String getEnte() {
        return getProperty("COD_ENTE");
    }

    public Cartella setEnte(String ente) {
        setProperty("COD_ENTE", ente);
        return this;
    }

    public String getAoo() {
        return getProperty("COD_AOO");
    }

    public Cartella setAoo(String aoo) {
        setProperty("COD_AOO", aoo);
        return this;
    }

    public String getCartellaSuperiore() {
        return getProperty("PARENT_FOLDER_ID");
    }

    public Cartella setCartellaSuperiore(String id) {
        setProperty("PARENT_FOLDER_ID", id);
        return this;
    }

    public String getProprietario() {
        return getProperty("FOLDER_OWNER");
    }

    public Cartella setProprietario(String proprietario) {
        setProperty("FOLDER_OWNER", proprietario);
        return this;
    }

    public String getNome() {
        return getProperty("FOLDER_NAME");
    }

    public Cartella setNome(String nome) {
        setProperty("FOLDER_NAME", nome);
        return this;
    }

    public String getDescrizione() {
        return getProperty("DES_FOLDER");
    }

    public Cartella setDescrizione(String descrizione) {
        setProperty("DES_FOLDER", descrizione);
        return this;
    }

    @Override
    protected void initProperties() {
        setProperty("COD_ENTE", "");
        setProperty("COD_AOO", "");
    }

    @Override
	public String getFEName() {
		return this.getNome();
	}
	
	@Override
	public String getFEId() {
		return getID();
	}
	
	@Override
	public String getFEAuthor() {
		String author = this.getProprietario();
        if ("".equals(author))
            return getProperty("CREATOR");
        else
            return author;
	}
	
	@Override
	public String getFEDate() {
		return this.getProperty("MODIFIED");
	}
	
    @Override
    protected String getComputedName() {
        return getNome();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    /*
    @Deprecated
    public String getId() {
        return getID();
    }


    @Deprecated
    public Cartella setId(String id) {
        return setID(id);
    }
*/
    @Override
    public String getID() {
        return getProperty("FOLDER_ID");
    }

    public Cartella setID(String id) {
        setProperty("FOLDER_ID", id);
        return this;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getCreator() {
        return null;
    }

    @Override
    public String getLastModifier() {
        return null;
    }

    @Override
    public String getVersionID() {
        return null;
    }

    @Override
    public String getAbstract() {
        return getNome();
    }
    @Override
    public String getMimeTypeCSS(){
    	return "glyphicon glyphicon-folder-open";
    }
    
    @Override
    public String getWebURL() {
        return webURLS+
        		TYPE+
        		"&COD_ENTE="+getEnte()+
        		"&COD_AOO="+getAoo()+
        		"&FOLDER_ID="+getID();
    }
}
