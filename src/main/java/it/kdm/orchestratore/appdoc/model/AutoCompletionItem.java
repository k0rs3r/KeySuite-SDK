package it.kdm.orchestratore.appdoc.model;

/**
 * Created by microchip on 23/03/17.
 */
public class AutoCompletionItem {
    private String guid;
    private String id;
    private String name;
    private boolean isDefault;

    public AutoCompletionItem() {
        this.guid = java.util.UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
