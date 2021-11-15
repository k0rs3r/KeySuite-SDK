package it.kdm.orchestratore.entity;

import java.util.ArrayList;

public class DefinitionObject {

	private ArrayList<DefinitionItem> definitions;
	private ResponseValidateBpmn responseValidateBpmn = new ResponseValidateBpmn();
	
	public ArrayList<DefinitionItem> getDefinitions() {
		return definitions;
	}
	
	public void setDefinitions(ArrayList<DefinitionItem> definitions) {
		this.definitions = definitions;
	}
	
	public ResponseValidateBpmn getResponseValidateBpmn() {
		return responseValidateBpmn;
	}
	
	public void setResponseValidateBpmn(ResponseValidateBpmn responseValidateBpmn) {
		this.responseValidateBpmn = responseValidateBpmn;
	}
	
}
