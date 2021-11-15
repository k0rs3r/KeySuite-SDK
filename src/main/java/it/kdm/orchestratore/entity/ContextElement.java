package it.kdm.orchestratore.entity;

import it.kdm.orchestratore.utils.EncodeUtils;

public class ContextElement {

	private String tag;
	
	private String id;
	
	private String name;
	
	private String type;

	public ContextElement()
	{
		
	}
	
	public ContextElement(String tag, String id, String name, String type) {
		this.tag = tag;
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public String getEncodeName() {
		return EncodeUtils.encodeURIComponent(name);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
