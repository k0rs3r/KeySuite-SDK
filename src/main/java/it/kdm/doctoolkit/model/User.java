package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;
import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 12/02/14
 * Time: 12.28
 * To change this template use File | Settings | File Templates.
 */
public class User extends ICIFSObject {

    public static final String TYPE = "user";

    public void setUserId(String userId) {
        this.setProperty("USER_ID", userId);
    }

    public String getUserId() {
        return this.getProperty("USER_ID");
    }

    public void setNome(String nome) {
        this.setProperty("FIRST_NAME", nome);
    }

    public String getNome() {
        return this.getProperty("FIRST_NAME");
    }

    public void setCognome(String cognome) {
        this.setProperty("LAST_NAME", cognome);
    }

    public String getCognome() {
        return this.getProperty("LAST_NAME");
    }

    public void setEmail(String email) {
        this.setProperty("EMAIL_ADDRESS", email);
    }

    public String getEmail() {
        return this.getProperty("EMAIL_ADDRESS");
    }

    public void setNomeCompleto(String nome) {
        this.setProperty("FULL_NAME", nome);
    }

    public String getNomeCompleto() {
        return this.getProperty("FULL_NAME");
    }

    @Override
    protected void initProperties() {
        this.setProperty("USER_ID", "");
        this.setProperty("FIRST_NAME", "");
        this.setProperty("LAST_NAME", "");
        this.setProperty("EMAIL_ADDRESS", "");
        this.setProperty("FULL_NAME", "");
    }

    @Override
    protected String getComputedName() {
        return getNomeCompleto();
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
        return getUserId();
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
        return getNomeCompleto();
    }
}
