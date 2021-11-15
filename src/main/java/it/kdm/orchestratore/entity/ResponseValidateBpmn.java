package it.kdm.orchestratore.entity;

import java.util.HashMap;
import java.util.Map;

public class ResponseValidateBpmn {

	private Boolean isError=false;
	private Boolean isWarn=false;
	private Map<String, String> errors = new HashMap<String,String>();
	private Map<String, String> info = new HashMap<String,String>();
	private Map<String, String> warns = new HashMap<String,String>();
	
	public Boolean getIsError() {
		return isError;
	}
	public void setIsError(Boolean isError) {
		this.isError = isError;
	}
	public Map<String, String> getErrors() {
		return errors;
	}
	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public Boolean getIsWarning() {
		return isWarn;
	}
	public void setIsWarning(Boolean isWarning) {
		this.isWarn = isWarning;
	}
	public Map<String, String> getWarnings() {
		return warns;
	}
	public void setWarnings(Map<String, String> warns) {
		this.warns = warns;
	}

	public Map<String, String> getInfos() {
		return info;
	}
	public void setInfos(Map<String, String> info) {
		this.info = info;
	}





}
