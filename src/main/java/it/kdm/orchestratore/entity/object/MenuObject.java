package it.kdm.orchestratore.entity.object;

import it.kdm.orchestratore.entity.DefinitionItem;
import it.kdm.orchestratore.entity.ResponseValidateBpmn;

import java.util.ArrayList;

public class MenuObject {
	
	private String key = "definitions";
	private ArrayList<DefinitionItem> definitions;
	private ResponseValidateBpmn responseValidateBpmn;
	
	
	
	public ResponseValidateBpmn getResponseValidateBpmn() {
		return responseValidateBpmn;
	}
	public void setResponseValidateBpmn(ResponseValidateBpmn responseValidateBpmn) {
		this.responseValidateBpmn = responseValidateBpmn;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = "definitions";
	}
	public ArrayList<DefinitionItem> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(ArrayList<DefinitionItem> definitions) {
		this.definitions = definitions;
	}





	

}
