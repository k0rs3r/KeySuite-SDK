package it.kdm.orchestratore.entity.object;

import java.util.Date;

/**
 * Created by maupet on 20/04/15.
 */
public class MonitoringNodeObject {

    private String nodeName;
    private long state;
    private Date dateIn;
    private String input;
    private String output;
    private String nodeType;
    private String processId;
    private long processInstanceId;
    private String contextId;
    private String taskStatus;
    private String descriptionProcess;
    /*

     */
    private String taskId;
    private  long workItemId;
    private long requestInfoId;
    private String requestInfoStatus;

    public MonitoringNodeObject() {
    }

    public MonitoringNodeObject(long workItemId, String nodeName, long state, Date dateIn,
                                String input, String output,
                                String nodeType, String processId,
                                long processInstanceId) {
        this.nodeName = nodeName;
        this.state = state;
        this.dateIn = dateIn;
        this.input = input;
        this.output = output;
        this.nodeType = nodeType;
        this.processId = processId;
        this.processInstanceId = processInstanceId;
        this.workItemId = workItemId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public long getRequestInfoId() {
        return requestInfoId;
    }

    public void setRequestInfoId(long requestInfoId) {
        this.requestInfoId = requestInfoId;
    }

    public String getRequestInfoStatus() {
        return requestInfoStatus;
    }

    public void setRequestInfoStatus(String requestInfoStatus) {
        this.requestInfoStatus = requestInfoStatus;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDescriptionProcess() {
        return descriptionProcess;
    }

    public void setDescriptionProcess(String descriptionProcess) {
        this.descriptionProcess = descriptionProcess;
    }
}
