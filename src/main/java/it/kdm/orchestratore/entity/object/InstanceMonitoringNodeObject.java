package it.kdm.orchestratore.entity.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by maupet on 20/04/15.
 */
public class InstanceMonitoringNodeObject {

    private List<MonitoringNodeObject> humanTaskNode = new ArrayList<MonitoringNodeObject>();
    private List<MonitoringNodeObject> eventNode = new ArrayList<MonitoringNodeObject>();
    private List<MonitoringNodeObject> otherNode = new ArrayList<MonitoringNodeObject>();

    private String parentInstanceState;

    public String getParentInstanceState() {
        return parentInstanceState;
    }

    public void setParentInstanceState(String parentInstanceState) {
        this.parentInstanceState = parentInstanceState;
    }

    public List<MonitoringNodeObject> getHumanTaskNode() {
        return humanTaskNode;
    }

    public void setHumanTaskNode(List<MonitoringNodeObject> humanTaskNode) {
        this.humanTaskNode = humanTaskNode;
    }

    public List<MonitoringNodeObject> getEventNode() {
        return eventNode;
    }

    public void setEventNode(List<MonitoringNodeObject> eventNode) {
        this.eventNode = eventNode;
    }

    public List<MonitoringNodeObject> getOtherNode() {
        return otherNode;
    }

    public void setOtherNode(List<MonitoringNodeObject> otherNode) {
        this.otherNode = otherNode;
    }
}
