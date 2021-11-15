package it.kdm.doctoolkit.model.path;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by lorenxs on 2/20/14.
 */
public class VirtualObject extends ICIFSObject {

    private final String type;

    public VirtualObject(String type) {
        this.type = type;
    }

    public String getEnte() {
        return getProperty("COD_ENTE");
    }

    public void setEnte(String ente) {
        setProperty("COD_ENTE", ente);
    }

    public String getAoo() {
        return getProperty("COD_AOO");
    }

    public void setAoo(String aoo) {
        setProperty("COD_AOO", aoo);
    }

    @Override
    protected String getComputedName() {
        return type;
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
        return getName();
    }

    @Override
    public String getType() {
        return type;
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
    
}
