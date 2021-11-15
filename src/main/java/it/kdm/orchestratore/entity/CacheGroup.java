package it.kdm.orchestratore.entity;

public class CacheGroup extends CacheActor {

    private boolean isGruppoStruttura;

    public CacheGroup(){
        super();
    }

    public String getGroupId() {
        return getId();
    }

    public void setGroupId(String groupId) {
        setId(groupId);
    }

    public String getGroupName() {
        return getName();
    }

    public void setGroupName(String groupName) {
        setName(groupName);
    }

    public boolean isGruppoStruttura() {
        return isGruppoStruttura;
    }

    public void setGruppoStruttura(boolean gruppoStruttura) {
        isGruppoStruttura = gruppoStruttura;
    }
}
