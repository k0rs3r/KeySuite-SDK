package it.kdm.orchestratore.appdoc.model;

import java.io.Serializable;

public class DocActionItem implements Serializable{

	private String actionId;
	private String label;
	private boolean enabled;
	
	public DocActionItem(String actionId,String label,boolean enabled){
		this.actionId = actionId;
		this.enabled = enabled;
		this.label = label;
	}
	
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
