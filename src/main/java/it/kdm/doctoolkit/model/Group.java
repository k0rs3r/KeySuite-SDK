package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;
import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 12/02/14
 * Time: 15.23
 * To change this template use File | Settings | File Templates.
 */
public class Group  extends ICIFSObject {

    public static final String TYPE = "group";

    public void setGroupId(String groupId) {
        this.setProperty("GROUP_ID", groupId);
    }

    public String getGroupId() {
        return this.getProperty("GROUP_ID");
    }

    public void setGroupName(String groupName) {
        this.setProperty("GROUP_NAME", groupName);
    }

    public String getGroupName() {
        return this.getProperty("GROUP_NAME");
    }


    public void setGruppoStruttura(boolean gruppoStruttura) {
        if (gruppoStruttura)
            this.setProperty("GRUPPO_STRUTTURA", "true");
        else
            this.setProperty("GRUPPO_STRUTTURA", "false");
    }

    public boolean getGruppoStruttura() {
        if (this.getProperty("GRUPPO_STRUTTURA").equals("true"))
            return true;
        else
            return false;
    }


    @Override
    protected void initProperties() {
        this.setProperty("GROUP_ID", "");
        this.setProperty("GROUP_NAME", "");
        this.setProperty("GRUPPO_STRUTTURA", "");
        this.setProperty("PARENT_GROUP_ID", "");
    }

    @Override
    protected String getComputedName() {
        return getGroupName();
    }

    @Override
    public boolean isDirectory() {
        return false;
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
        return getGroupId();
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
        return getGroupName();
    }
}
