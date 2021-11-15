package it.kdm.doctoolkit.model.path;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

/**
 * Created by lorenxs on 2/20/14.
 */
public class Root extends ICIFSObject {

    public static final String TYPE = "root";
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
    public String getFullPath() {
        return "/";
    }
    public String getVirtualPath() {
        return "/";
    }

    public String getPhysicalPath() {
        return "/";
    }


    @Override
    public String getFEName() {
        String name = getName();
        if (Strings.isNullOrEmpty(name)) {
        	return FEName;
        }
        
        return name;
    }

    public void setFEName(String FEName) {
        this.FEName = FEName;
    }
}
