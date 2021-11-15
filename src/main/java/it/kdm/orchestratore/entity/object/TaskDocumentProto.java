package it.kdm.orchestratore.entity.object;

import java.util.List;

/**
 * Created by antsic on 13/02/17.
 */
public class TaskDocumentProto {

    private String taskName;
    private String taskStatus;
    private String activationTaskTime;
    private long idTask;
    private String istanceName;
    private List<String> docNum;
    private List<String> peopleAssignments;
    private String owner;
    private String expirationTaskTime;
    private long processInstanceId;
    private int priority;
    private String subject;
    private String note;
    private String owner_viewName;
    private String rootInstanceName;


    public TaskDocumentProto() {
    }

    public TaskDocumentProto(String taskName, String taskStatus, String activationTaskTime, Integer idTask, List<String> docNum, String expirationTaskTime, String owner,
                             List<String> peopleAssignments, long processInstanceId) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.activationTaskTime = activationTaskTime;
        this.idTask = idTask;
        this.docNum = docNum;
        this.expirationTaskTime = expirationTaskTime;
        this.owner = owner;
        this.peopleAssignments = peopleAssignments;
    }

    public String getRootInstanceName() {
        return rootInstanceName;
    }

    public void setRootInstanceName(String rootInstanceName) {
        this.rootInstanceName = rootInstanceName;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getExpirationTaskTime() {
        return expirationTaskTime;
    }

    public void setExpirationTaskTime(String expirationTaskTime) {
        this.expirationTaskTime = expirationTaskTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getActivationTaskTime() {
        return activationTaskTime;
    }

    public void setActivationTaskTime(String activationTaskTime) {
        this.activationTaskTime = activationTaskTime;
    }

    public long getIdTask() {
        return idTask;
    }

    public void setIdTask(long idTask) {
        this.idTask = idTask;
    }

    public String getIstanceName() {
        return istanceName;
    }

    public void setIstanceName(String istanceName) {
        this.istanceName = istanceName;
    }

    public List<String> getDocNum() {
        return docNum;
    }

    public void setDocNum(List<String> docNum) {
        this.docNum = docNum;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(List<String> peopleAssignments) {
        this.peopleAssignments = peopleAssignments;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOwner_viewName() { return owner_viewName; }

    public void setOwner_viewName(String owner_viewName) { this.owner_viewName = owner_viewName; }
}
