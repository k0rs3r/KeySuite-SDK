package it.kdm.doctoolkit.model;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.path.ICIFSObject;

/**
 * Created by lorenxs on 2/20/14.
 */
public class Ente extends ICIFSObject {

    public static final String TYPE = "ente";
    private String FEName;

    public String getEnte() {
        return getProperty("COD_ENTE");
    }

    public void setEnte(String ente) {
        setProperty("COD_ENTE", ente);
    }

    @Override
    protected String getComputedName() {
        return "";
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

    @Override
    public String getID() {
        return null;
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
        return null;
    }

    @Override
    protected void initProperties() {

    }


    @Override
    public String getFullName(){return getProperty("DES_ENTE");}

    @Override
    public String getFEName() {
        return getProperty("DES_ENTE");
//
//        String name = getName();
//        if (Strings.isNullOrEmpty(name)) {
//        	return FEName;
//        }
//
//        return name;
    }

    public void setFEName(String FEName) {
        this.FEName = FEName;
    }
}
