package it.kdm.orchestratore.entity;

import java.util.Set;

public class ProcessInstanceDetail {
	private Long processInstanceId;
	private String processId;
	private String version;
	private String name;
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
	private String parentProcessDescription;
	private Set eventTypes;
	private String ente;
	private String aoo;
	private String deploymentId;
	private Boolean conversation;

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

	public String getParentProcessDescription() {
		return parentProcessDescription;
	}

	public void setParentProcessDescription(String parentProcessDescription) {
		this.parentProcessDescription = parentProcessDescription;
	}

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public String getAoo() {
		return aoo;
	}

	public void setAoo(String aoo) {
		this.aoo = aoo;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
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
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public Long getParentProcessInstanceid() {
		return parentProcessInstanceid;
	}
	public void setParentProcessInstanceid(Long parentProcessInstanceid) {
		this.parentProcessInstanceid = parentProcessInstanceid;
	}
	public String getLastModificationDate() {
		return lastModificationDate;
	}
	public void setLastModificationDate(String lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}
	public Set getEventTypes() {
		return eventTypes;
	}
	public void setEventTypes(Set eventTypes) {
		this.eventTypes = eventTypes;
	}

	public Boolean getConversation() {
		return conversation;
	}

	public void setConversation(Boolean conversation) {
		this.conversation = conversation;
	}
}
