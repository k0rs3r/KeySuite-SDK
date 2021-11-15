package it.kdm.orchestratore.entity;

import java.util.List;

public class CacheUser extends CacheActor {

    private String groupsString;
    private List<String> groups;

    public CacheUser(){
        super();
        setType("user");
    }

    public String getUserId() {
        return getId();
    }

    public void setUserId(String userId) {
        setId(userId);
    }

    public String getGroupsString() {
        return groupsString;
    }

    public void setGroupsString(String groupsString) {
        this.groupsString = groupsString;
    }

    public String getFullName() {
        return getName();
    }

    public void setFullName(String fullName) {
        setName(fullName);
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

}
