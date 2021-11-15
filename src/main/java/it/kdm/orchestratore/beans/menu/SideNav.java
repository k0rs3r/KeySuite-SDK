package it.kdm.orchestratore.beans.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SideNav implements Serializable {

    private String appName;
    private String title;
    private String tooltip;
    private String url;
    private HashMap<String, String> attributes;
    private String useJtree;

    private List<SideNavSection> sections;

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

    public List<SideNavSection> getSections() {
        return sections;
    }

    public void setSections(List<SideNavSection> sections) {
        this.sections = sections;
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

    public String getUseJtree() {
        return useJtree;
    }

    public void setUseJtree(String useJtree) {
        this.useJtree = useJtree;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}
