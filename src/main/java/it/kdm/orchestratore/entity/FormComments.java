package it.kdm.orchestratore.entity;

public class FormComments {
	
	private String id;
	private String formString;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormString() {
		return formString;
	}
	public void setFormString(String formString) {
		this.formString = formString;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("FormComments model: " + this.id );
		sb.append(", " + this.formString );
		
		return sb.toString();
	}


}
