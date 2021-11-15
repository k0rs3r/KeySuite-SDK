package it.kdm.orchestratore.beans.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Header implements Serializable {

    private String logo;
    private String altTitle;
    List<MainMenuItem> menuItems;

    public Header(){
        super();
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<MainMenuItem> getMenuItems() {
        return menuItems;
    }

    /*public MainMenuItem getMenuItem(String appName){
        if (menuItems==null || appName==null)
            return null;
        for( MainMenuItem m : menuItems)
            if (appName.equalsIgnoreCase(m.getAppName()))
                return m;
        return null;
    }*/

    public void setMenuItems(List<MainMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }
}
