package it.kdm.doctoolkit.model.path;

/**
 * Created by lorenxs on 2/20/14.
 */
public class Profile extends ICIFSObject {

    public static final String TYPE = "profile";

    public String getEnte() {
        return getProperty("COD_ENTE");
    }

    public void setEnte(String ente) {
        setProperty("COD_ENTE", ente);
    }

    @Override
    public String getFEParentPathLinkName() {
        return getFEParentPath();
    }
    @Override
    public String getFEParentPath() {
        return "/";
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
}
