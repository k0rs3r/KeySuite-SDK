package it.kdm.orchestratore.appBpm.model;

import java.util.Set;

/**
 * Created by Łukasz Kwasek on 06/05/14.
 */
public class CallActivityMap {

    private Long parentProcessInstanceId;
    private Long processInstanceId;
    private String nodeName;
    private String name;
    private String contextId;
    private String version;

    private String processId;
    private String description;
    private String state;
    private String outcome;
    private String startDate;
    private String endDate;
    private String lastModificationDate;
    private Long parentProcessInstanceid;
    private String parentProcessName;
    private String parentProcessVersion;
    private String parentProcessStatus;
    private Set eventTypes;


    public Long getParentProcessInstanceid() {
        return parentProcessInstanceid;
    }

    public void setParentProcessInstanceid(Long parentProcessInstanceid) {
        this.parentProcessInstanceid = parentProcessInstanceid;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getParentProcessName() {
        return parentProcessName;
    }

    public void setParentProcessName(String parentProcessName) {
        this.parentProcessName = parentProcessName;
    }

    public String getParentProcessVersion() {
        return parentProcessVersion;
    }

    public void setParentProcessVersion(String parentProcessVersion) {
        this.parentProcessVersion = parentProcessVersion;
    }

    public String getParentProcessStatus() {
        return parentProcessStatus;
    }

    public void setParentProcessStatus(String parentProcessStatus) {
        this.parentProcessStatus = parentProcessStatus;
    }

    public Set getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Set eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
}
