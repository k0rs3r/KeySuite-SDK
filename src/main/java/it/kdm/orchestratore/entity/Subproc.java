package it.kdm.orchestratore.entity;

public class Subproc {

	private String id;
	private String processInstanceId;
	private String processId;
	private String start;
	private String state;
	private String status;
	private String parentProcessInstanceId;
	private String value;
	private String variableId;
	private String modified;
	private String parentDesc;
	private String parentProcessInstanceIdST;
	private String end;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getParentProcessInstanceId() {
		return parentProcessInstanceId;
	}
	public void setParentProcessInstanceId(String parentProcessInstanceId) {
		this.parentProcessInstanceId = parentProcessInstanceId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getVariableId() {
		return variableId;
	}
	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getParentDesc() {
		return parentDesc;
	}
	public void setParentDesc(String parentDesc) {
		this.parentDesc = parentDesc;
	}
	public String getParentProcessInstanceIdST() {
		return parentProcessInstanceIdST;
	}
	public void setParentProcessInstanceIdST(String parentProcessInstanceIdST) {
		this.parentProcessInstanceIdST = parentProcessInstanceIdST;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	
	
}
