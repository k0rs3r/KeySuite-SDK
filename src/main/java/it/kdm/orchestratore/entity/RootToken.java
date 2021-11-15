package it.kdm.orchestratore.entity;

import java.util.List;

public class RootToken {
	private String id;
	private String name;
	private String currentNodeName;
	private List<Children> children;
	private List<AvailableSignals> availableSignals;
	private Boolean canBeSignaled;
	
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
	public String getCurrentNodeName() {
		return currentNodeName;
	}
	public void setCurrentNodeName(String currentNodeName) {
		this.currentNodeName = currentNodeName;
	}
	public List<Children> getChildren() {
		return children;
	}
	public void setChildren(List<Children> children) {
		this.children = children;
	}
	public List<AvailableSignals> getAvailableSignals() {
		return availableSignals;
	}
	public void setAvailableSignals(List<AvailableSignals> availableSignals) {
		this.availableSignals = availableSignals;
	}
	public Boolean getCanBeSignaled() {
		return canBeSignaled;
	}
	public void setCanBeSignaled(Boolean canBeSignaled) {
		this.canBeSignaled = canBeSignaled;
	}
	
}
