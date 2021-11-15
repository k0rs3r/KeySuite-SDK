package it.kdm.orchestratore.entity.object.signDocument;

import it.kdm.orchestratore.entity.object.BpmDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antsic on 25/10/16.
 */
public class TaskDocumentCheckedUncheckedForm {

    private String  id;
    private String  status;
    private String  completedOn;
    private String  activationTime;
    private String  processInstanceId;
    private String  processId;
    private String  processVersion;
    private String  name;
    private String  value;
    private String processName;
    private String primaryProcessInstanceDescription;
    private String actualOwner;
    private List<BpmDocument> bpmDocumentChecked = new ArrayList<>();
    private List<BpmDocument> bpmDocumentUnchecked = new ArrayList<>();
    private String  checked;

    public String getChecked() {
        return checked;
    }

    public void setChecked(String isChecked) {
        this.checked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(String completedOn) {
        this.completedOn = completedOn;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getPrimaryProcessInstanceDescription() {
        return primaryProcessInstanceDescription;
    }

    public void setPrimaryProcessInstanceDescription(String primaryProcessInstanceDescription) {
        this.primaryProcessInstanceDescription = primaryProcessInstanceDescription;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public List<BpmDocument> getBpmDocumentChecked() {
        return bpmDocumentChecked;
    }

    public void setBpmDocumentChecked(List<BpmDocument> bpmDocumentChecked) {
        this.bpmDocumentChecked = bpmDocumentChecked;
    }

    public List<BpmDocument> getBpmDocumentUnchecked() {
        return bpmDocumentUnchecked;
    }

    public void setBpmDocumentUnchecked(List<BpmDocument> bpmDocumentUnchecked) {
        this.bpmDocumentUnchecked = bpmDocumentUnchecked;
    }
}
