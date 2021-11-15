package it.kdm.orchestratore.beans;

import java.math.BigInteger;
import java.util.Date;


public class NodeInstanceLogKDM {
	private Integer id;
	private String connection;
	private Date endDate;
	private Date startDate;
	private BigInteger workItemId;
	private String nodeId;
	private Integer nodeInstanceId;
	private String processId;
	private String nodeName;
	private String nodeType;
	private Integer processInstanceId;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getConnection() {
		return connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public BigInteger getWorkItemId() {
		return workItemId;
	}
	public void setWorkItemId(BigInteger workItemId) {
		this.workItemId = workItemId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public Integer getNodeInstanceId() {
		return nodeInstanceId;
	}
	public void setNodeInstanceId(Integer nodeInstanceId) {
		this.nodeInstanceId = nodeInstanceId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public Integer getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(Integer processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
}
