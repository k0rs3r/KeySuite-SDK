package it.kdm.orchestratore.beans.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainMenuItem implements Serializable {

    private String icon;
    private String link;
    private String title;
    private String backgroundColor;
    private String textColor;
    private String[] roles;
    private String appName;
    private String targetUri;
    private HashMap<String, String> revriteHeader;
    private Boolean enabled = true;
    private String altTitle;
    private String regex;
    //private Boolean defaultAppForContext;

    public MainMenuItem(){
        super();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public HashMap<String, String> getRevriteHeader() {
        return revriteHeader;
    }

    public void setRevriteHeader(HashMap<String, String> revriteHeader) {
        this.revriteHeader = revriteHeader;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
/*
    public Boolean getDefaultAppForContext() {
        return defaultAppForContext;
    }

    public void setDefaultAppForContext(Boolean defaultAppForContext) {
        this.defaultAppForContext = defaultAppForContext;
    }
    */
}
