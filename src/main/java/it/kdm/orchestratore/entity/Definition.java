package it.kdm.orchestratore.entity;

//TODO: Eliminare quando le nuove rest saranno implementate. La nuova classe è DefinitionItem
public class Definition {
	
	private String id;
	private String name;
	private String version;
	private String packageName;
	private String deploymentId;
	private Boolean suspended;
	private Boolean runnable;
	
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	public Boolean getRunnable() {
		return runnable;
	}
	public void setRunnable(Boolean runnable) {
		this.runnable = runnable;
	}
	
	
}
