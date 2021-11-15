package it.kdm.orchestratore.beans.menu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SideNavMenuItem implements Serializable {

    private String icon;
    private String label;
    private String tooltip;
    private String url;
    private String[] roles;
    private String otherHtml;
    private List<SideNavSubButton> buttons;
    private HashMap<String, String> attributes;

    public SideNavMenuItem(){
        super();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public List<SideNavSubButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<SideNavSubButton> buttons) {
        this.buttons = buttons;
    }

    public String getOtherHtml() {
        return otherHtml;
    }

    public void setOtherHtml(String otherHtml) {
        this.otherHtml = otherHtml;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
