package it.kdm.orchestratore.beans.menu;

import java.io.Serializable;
import java.util.HashMap;

public class SideNavSubButton implements Serializable {

    private String icon;
    private String tooltip;
    private String url;
    private HashMap<String, String> attributes;
    private String[] roles;

    public SideNavSubButton(){
        super();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
