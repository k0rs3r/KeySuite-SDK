package it.kdm.orchestratore.entity;

public class DefinitionItem {
	
	private String id;
	private String name;
	private String runnable;
	private String version;
	private String pubblished;
	private ResponseValidateBpmn responseValidateBpmn;
	private Boolean hasStartedPermission;
	private String roles;
	private String description;
	private String category;
	private String processId;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Boolean getHasStartedPermission() {
		return hasStartedPermission;
	}
	public void setHasStartedPermission(Boolean hasStartedPermission) {
		this.hasStartedPermission = hasStartedPermission;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ResponseValidateBpmn getResponseValidateBpmn() {
		return responseValidateBpmn;
	}
	public void setResponseValidateBpmn(ResponseValidateBpmn responseValidateBpmn) {
		this.responseValidateBpmn = responseValidateBpmn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRunnable() {
		return runnable;
	}
	public void setRunnable(String runnable) {
		this.runnable = runnable;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPubblished() {
		return pubblished;
	}
	public void setPubblished(String pubblished) {
		this.pubblished = pubblished;
	}

	
}
