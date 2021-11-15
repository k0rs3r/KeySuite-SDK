package it.kdm.orchestratore.appdoc.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cronologia implements Serializable{
	
	private String docNum;
	private String date;
	private String extraData;
	private String message;
	private String methodName;
	private String user;
	private String displayString;

	public String getDocNum() {
		return docNum;
	}
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getExtraData() {
		return (this.extraData != null) ? extraData : "";
	}
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}


	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}
}
