package it.kdm.orchestratore.entity;

import java.util.ArrayList;
import java.util.List;

public class InstanceDetails {
	
	private String id;
	private String definitionId;
	private String startDate;
	private String endDate;
	private Boolean suspended;
	private List<RootToken> rootToken;
	
	
	private Boolean canBeSignaled;
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
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	public List<RootToken> getRootToken() {
		return rootToken;
	}
	public void setRootToken(ArrayList<RootToken> rootToken) {
		this.rootToken = rootToken;
	}
	public Boolean getCanBeSignaled() {
		return canBeSignaled;
	}
	public void setCanBeSignaled(Boolean canBeSignaled) {
		this.canBeSignaled = canBeSignaled;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	

}
