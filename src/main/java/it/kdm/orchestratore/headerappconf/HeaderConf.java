package it.kdm.orchestratore.headerappconf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antsic on 12/06/17.
 */
public class HeaderConf {

    private List<String> appEnabled = new ArrayList<>();

    public List<String> getAppEnabled() {
        return appEnabled;
    }

    public void setAppEnabled(List<String> appEnabled) {
        this.appEnabled = appEnabled;
    }
}
