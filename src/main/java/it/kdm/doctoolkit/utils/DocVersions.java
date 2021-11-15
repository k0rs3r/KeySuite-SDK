package it.kdm.doctoolkit.utils;

import it.kdm.doctoolkit.model.GenericObject;
import org.joda.time.DateTime;


import java.util.HashMap;

/**
 * Created by microchip on 17/03/16.
 */
public class DocVersions extends GenericObject{


    public int getNumber() {
        return Integer.parseInt(this.getProperty("number"));
    }

    public void setNumber(int number) {
        this.setProperty("number",String.valueOf(number));
    }

    public DateTime getDate() {
        return new DateTime(this.getProperty("date"));
    }

    public void setDate(DateTime date) {
        this.setProperty("date", String.valueOf(date));
    }

    public String getUserId() {
        return this.getProperty("userId");
    }

    public void setUserId(String userId) {
        this.setProperty("userId",userId);
    }

    public String getFileName() {
        return this.getProperty("filename");
    }

    public void setFileName(String fileName) {
        this.setProperty("filename",fileName);
    }

    public String getComment() {
        return this.getProperty("comment");
    }

    public void setComment(String comment) {
        this.setProperty("comment",comment);
    }

    @Override
    protected void initProperties() {

    }
}
