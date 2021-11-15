package it.kdm.orchestratore.beans.menu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SideNavSection implements Serializable {

    private String name;
    private String url;
    private String tooltip;
    private String[] roles;
    private List<SideNavMenuItem> menuItems;
    private HashMap<String, String> attributes;

    public SideNavSection(){
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public List<SideNavMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<SideNavMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
