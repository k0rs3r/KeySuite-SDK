package it.kdm.orchestratore.entity.object;

import it.kdm.orchestratore.entity.Definition;

import java.util.ArrayList;

public class DefinitionsObject {
	
	private String key = "definitions";
	private ArrayList<Definition> definitions;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ArrayList<Definition> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(ArrayList<Definition> definitions) {
		this.definitions = definitions;
	}
	
	
}
