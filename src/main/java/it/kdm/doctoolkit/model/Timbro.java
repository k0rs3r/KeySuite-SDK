package it.kdm.doctoolkit.model;

import javax.activation.DataHandler;

/**
 * Created by antsic on 14/01/15.
 */
public class Timbro {

    private int dpi;
    private int maxw;
    private int maxh;
    private String format;
    private DataHandler timbroImg;

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getMaxw() {
        return maxw;
    }

    public void setMaxw(int maxw) {
        this.maxw = maxw;
    }

    public int getMaxh() {
        return maxh;
    }

    public void setMaxh(int maxh) {
        this.maxh = maxh;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public DataHandler getTimbroImg() {
        return timbroImg;
    }

    public void setTimbroImg(DataHandler timbroImg) {
        this.timbroImg = timbroImg;
    }
}
