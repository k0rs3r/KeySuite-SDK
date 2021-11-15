package it.kdm.orchestratore.beans;

public class NodeInstanceLogSummary {
    private String processId;
    private String nodeName;
    private String nodeType;
    private String startDate;
    private String endDate;
    private String nodeId;
    private String connection;
    private Integer nodeRawId;
    private Integer workItemId;
    private Integer nodeInstanceId;
    private Integer processInstanceId;
    private Integer workItemId2;

    public NodeInstanceLogSummary() {
    }

    public Integer getNodeRawId() {
        return this.nodeRawId;
    }

    public void setNodeRawId(Integer nodeRawId) {
        this.nodeRawId = nodeRawId;
    }

    public Integer getWorkItemId() {
        return this.workItemId;
    }

    public void setWorkItemId(Integer workItemId) {
        this.workItemId = workItemId;
    }

    public Integer getNodeInstanceId() {
        return this.nodeInstanceId;
    }

    public void setNodeInstanceId(Integer nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public Integer getProcessInstanceId() {
        return this.processInstanceId;
    }

    public void setProcessInstanceId(Integer processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getConnection() {
        return this.connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getProcessId() {
        return this.processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getWorkItemId2() {
        return workItemId2;
    }

    public void setWorkItemId2(Integer workItemId2) {
        this.workItemId2 = workItemId2;
    }
}
