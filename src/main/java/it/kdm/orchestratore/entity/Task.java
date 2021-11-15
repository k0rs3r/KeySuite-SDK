package it.kdm.orchestratore.entity;

public class Task  {
	
    private String  id;
    private String  start;
    private String  parentProcessInstanceId;
    private String  potOwner;
    private String  status;
    private String  previousStatus;
    private String  createdOn;
    private String  completedOn; 
    private String  activationTime;
    private Boolean skipable;
    private String  workItemId;
    private String  processInstanceId;
    private String  documentAccessType;
    private String  documentType;
    private String  documentContentId;
    private String  outputContentId;
    private String  faultContentId;
    private String  parentId;
    private String  processId;
    private String  processSessionId;
    private String  processVersion;
    private String  name;
    private String  text;
    private String  shortText;
    private String  language;
    private String  value;
    private String  variableId;
    private String  actualOwner;
    private String  outputAccessType;
    private String  outputType;
    private String expiration;
    private String end;
    private String processName;
	private String primaryProcessInstanceDescription;

	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

	public String getActualOwnerName() {
		return actualOwnerName;
	}

	public void setActualOwnerName(String actualOwnerName) {
		this.actualOwnerName = actualOwnerName;
	}

	private String actualOwnerName;

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}

	private String duration;
	private String wait;

	public boolean isPriority() {
		return isPriority;
	}

	public void setPriority(boolean priority) {
		isPriority = priority;
	}

	private boolean isPriority;

	public String getProcessVersion() {
		return processVersion;
	}
	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public String getOutputAccessType() {
		return outputAccessType;
	}
	public void setOutputAccessType(String outputAccessType) {
		this.outputAccessType = outputAccessType;
	}
	public String getActualOwner() {
		return actualOwner;
	}
	public void setActualOwner(String actualOwner) {
		this.actualOwner = actualOwner;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getParentProcessInstanceId() {
		return parentProcessInstanceId;
	}
	public void setParentProcessInstanceId(String parentProcessInstanceId) {
		this.parentProcessInstanceId = parentProcessInstanceId;
	}
	public String getPotOwner() {
		return potOwner;
	}
	public void setPotOwner(String potOwner) {
		this.potOwner = potOwner;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getActivationTime() {
		return activationTime;
	}
	public void setActivationTime(String activationTime) {
		this.activationTime = activationTime;
	}
	public Boolean getSkipable() {
		return skipable;
	}
	public void setSkipable(Boolean skipable) {
		this.skipable = skipable;
	}
	public String getWorkItemId() {
		return workItemId;
	}
	public void setWorkItemId(String workItemId) {
		this.workItemId = workItemId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getDocumentAccessType() {
		return documentAccessType;
	}
	public void setDocumentAccessType(String documentAccessType) {
		this.documentAccessType = documentAccessType;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getDocumentContentId() {
		return documentContentId;
	}
	public void setDocumentContentId(String documentContentId) {
		this.documentContentId = documentContentId;
	}
	public String getOutputContentId() {
		return outputContentId;
	}
	public void setOutputContentId(String outputContentId) {
		this.outputContentId = outputContentId;
	}
	public String getFaultContentId() {
		return faultContentId;
	}
	public void setFaultContentId(String faultContentId) {
		this.faultContentId = faultContentId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessSessionId() {
		return processSessionId;
	}
	public void setProcessSessionId(String processSessionId) {
		this.processSessionId = processSessionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getVariableId() {
		return variableId;
	}
	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}
	public String getCompletedOn() {
		return completedOn;
	}
	public void setCompletedOn(String completedOn) {
		this.completedOn = completedOn;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String end) {
		this.expiration = end;
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
    
    
    
}
