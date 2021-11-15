package it.kdm.orchestratore.appdoc.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;

/**
 * Created by Lorenzo Lucherini on 9/26/14.
 */
public class NavObject {

    private String feName;
    private String feFullPath;
    private String feFullPathLinkName;
    private String location;
    private String feParentPath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    private String type;
    private String businessType;

    public NavObject(ICIFSObject obj) {
        this.feName = obj.getFEName();
        this.feFullPath = obj.getFEFullPath();
        this.feFullPathLinkName = obj.getFEFullPathLinkName();
        this.feParentPath = obj.getFEParentPath();
        this.location = obj.getLocation();
        this.type = obj.getType();
        this.businessType = obj.getBusinessType();
    }

    public String getFEName() {
        return feName;
    }

    public String getFEFullPath() {
        return feFullPath;
    }

    public String getFEFullPathLinkName() {
        return feFullPathLinkName;
    }

    public String getLocation() {
        return location;
    }

    public String getFEParentPath() {
        return feParentPath;
    }
}
