package it.kdm.orchestratore.entity.object;

import java.util.List;

/**
 * Created by antsic on 12/09/16.
 */
public class GroupByPotOwnersWrap{
    private List<GroupByPotOwners> groupByPotOwnersList;

    public GroupByPotOwnersWrap() {
    }

    public GroupByPotOwnersWrap(List<GroupByPotOwners> groupByPotOwnersList) {
        this.groupByPotOwnersList = groupByPotOwnersList;
    }

    public List<GroupByPotOwners> getGroupByPotOwnersList() {
        return groupByPotOwnersList;
    }

    public void setGroupByPotOwnersList(List<GroupByPotOwners> groupByPotOwnersList) {
        this.groupByPotOwnersList = groupByPotOwnersList;
    }
}
