package it.kdm.orchestratore.entity;

import java.util.List;

public class Instances {

	private String id;
	private String definitionId;
	private String startDate;
	private String lastModifiedDate;
	private Boolean suspended;
	private String status;
    private String nodeRawId;
	private String state;
	private String statusST;
	private String stateST;
	private String processId;
	private String parentProcessInstanceId;
	private String parentProcessInstanceIdST;
    private String parentBlock;
	private String parentDesc;
	private String value;
	private String variableId;
	private String endDate;
	private List<Task> token;
	private List<Subproc> subproc;

	private String start;
	private String potOwner;
	private String previousStatus;
	private String createdOn;
	private String completedOn;
	private String activationTime;
	private String skipable;
	private String workItemId;
	private String workItemId2;
	private String processInstanceId;
	private String documentAccessType;
	private String documentType;
	private String documentContentId;
	private String outputContentId;
	private String faultContentId;
	private String parentId;
	private String processSessionId;
	private String name;
	private String text;
	private String shortText;
	private String language;
	private String actualOwner;
	private String outputAccessType;
	private String outputType;
	private String end;
	
	public String getPrimaryProcessId() {
		return primaryProcessId;
	}

	public void setPrimaryProcessId(String primaryProcessId) {
		this.primaryProcessId = primaryProcessId;
	}

	public String getPrimaryProcessInstanceId() {
		return primaryProcessInstanceId;
	}

	public void setPrimaryProcessInstanceId(String primaryProcessInstanceId) {
		this.primaryProcessInstanceId = primaryProcessInstanceId;
	}

	public String getPrimaryProcessInstanceDesc() {
		return primaryProcessInstanceDesc;
	}

	public void setPrimaryProcessInstanceDesc(String primaryProcessInstanceDesc) {
		this.primaryProcessInstanceDesc = primaryProcessInstanceDesc;
	}

	private String primaryProcessId;
	private String primaryProcessInstanceId;
	private String primaryProcessInstanceDesc;



	/* aggunte*/

	private Long duration;
	private String identity;
	private String processVersion;
	private String processName;
	private String externalId;
	private String outcome;

	private String connection;
	private String nodeId;
	private String nodeInstanceId;
	private String nodeName;
	private String nodeType;
	private String type;
	private String workitem;
	private Boolean conversation;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	private String creator;

	public String getBusinessState() {
		return businessState;
	}

	public void setBusinessState(String businessState) {
		this.businessState = businessState;
	}

	private String businessState;

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	private String businessKey;


    public String getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(String parentBlock) {
        this.parentBlock = parentBlock;
    }

    public String getNodeRawId() {
        return nodeRawId;
    }

    public void setNodeRawId(String nodeRawId) {
        this.nodeRawId = nodeRawId;
    }

    public String getConnection() {
		return connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeInstanceId() {
		return nodeInstanceId;
	}
	public void setNodeInstanceId(String nodeInstanceId) {
		this.nodeInstanceId = nodeInstanceId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWorkitem() {
		return workitem;
	}
	public void setWorkitem(String workitem) {
		this.workitem = workitem;
	}
	
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getProcessVersion() {
		return processVersion;
	}
	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDefinitionId() {
		return definitionId;
	}
	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatusST() {
		return statusST;
	}
	public void setStatusST(String statusST) {
		this.statusST = statusST;
	}
	public String getStateST() {
		return stateST;
	}
	public void setStateST(String stateST) {
		this.stateST = stateST;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getParentProcessInstanceId() {
		return parentProcessInstanceId;
	}
	public void setParentProcessInstanceId(String parentProcessInstanceId) {
		this.parentProcessInstanceId = parentProcessInstanceId;
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
	public String getParentDesc() {
		return parentDesc;
	}
	public void setParentDesc(String parentDesc) {
		this.parentDesc = parentDesc;
	}
	public List<Task> getToken() {
		return token;
	}
	public void setToken(List<Task> token) {
		this.token = token;
	}
	public List<Subproc> getSubproc() {
		return subproc;
	}
	public void setSubproc(List<Subproc> subproc) {
		this.subproc = subproc;
	}
	public String getParentProcessInstanceIdST() {
		return parentProcessInstanceIdST;
	}
	public void setParentProcessInstanceIdST(String parentProcessInstanceIdST) {
		this.parentProcessInstanceIdST = parentProcessInstanceIdST;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getPotOwner() {
		return potOwner;
	}
	public void setPotOwner(String potOwner) {
		this.potOwner = potOwner;
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
	public String getSkipable() {
		return skipable;
	}
	public void setSkipable(String skipable) {
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
	public String getActualOwner() {
		return actualOwner;
	}
	public void setActualOwner(String actualOwner) {
		this.actualOwner = actualOwner;
	}
	public String getOutputAccessType() {
		return outputAccessType;
	}
	public void setOutputAccessType(String outputAccessType) {
		this.outputAccessType = outputAccessType;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

	public Boolean getConversation() {
		return conversation;
	}

	public void setConversation(Boolean conversation) {
		this.conversation = conversation;
	}

	public String getWorkItemId2() {
		return workItemId2;
	}

	public void setWorkItemId2(String workItemId2) {
		this.workItemId2 = workItemId2;
	}
}
