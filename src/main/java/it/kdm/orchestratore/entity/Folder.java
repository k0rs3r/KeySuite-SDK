package it.kdm.orchestratore.entity;

public class Folder {

	private String label;
	private String id;
	private String parentId;
	
	/* field per web storage */
	private String nodeId;
	private String parentNodeId;
	
	private String type;
	private String tag;
	private Boolean load_on_demand;
	
	private String anno;
	
	public Folder()
	{
		
	}
	
	public Folder(String label, String id, String parentId, String nodeId, String parentNodeId, String type, String tag, Boolean load_on_demand, String anno) {
		this.label = label;
		this.id = id;
		this.parentId = parentId;
		this.nodeId = nodeId;
		this.parentNodeId = parentNodeId;
		this.type = type;
		this.tag = tag;
		this.load_on_demand = load_on_demand;
		this.anno = anno;
	}


	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(String parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Boolean getLoad_on_demand() {
		return load_on_demand;
	}

	public void setLoad_on_demand(Boolean load_on_demand) {
		this.load_on_demand = load_on_demand;
	}
	
	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}
}
