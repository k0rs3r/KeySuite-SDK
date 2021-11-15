package it.kdm.orchestratore.entity;

public class Comments {

	private String id;
	private String addedAtSt;
	private String addedBy;
	private String text;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddedAtSt() {
		return addedAtSt;
	}
	public void setAddedAtSt(String addedAtSt) {
		this.addedAtSt = addedAtSt;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

}
