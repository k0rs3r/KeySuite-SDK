package it.kdm.orchestratore.appdoc.model.fascicolo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.kdm.orchestratore.appdoc.model.AutoCompletionItem;

/**
 * Created by microchip on 15/06/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemFascicolo extends AutoCompletionItem {
    private boolean visible = true;
    private boolean primario;
    private String path;

    public boolean isPrimario() {
        return primario;
    }

    public void setPrimario(boolean primario) {
        this.primario = primario;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
