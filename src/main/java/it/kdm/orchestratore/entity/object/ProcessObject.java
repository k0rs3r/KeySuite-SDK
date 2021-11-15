package it.kdm.orchestratore.entity.object;

import it.kdm.orchestratore.entity.ProcessItem;
import it.kdm.orchestratore.entity.ResponseValidateBpmn;

import java.util.ArrayList;

public class ProcessObject {
	
	private String key = "definitions";
	private ArrayList<ProcessItem> definitions;
	private int pageSize;
	private int pageNumber;
	private int pageCount;
	private int recordCount;
	private String orderBy;
	private Boolean hasStartedPermission;
	
	private ResponseValidateBpmn responseValidateBpmn;
	
	
	
	
	public Boolean getHasStartedPermission() {
		return hasStartedPermission;
	}
	public void setHasStartedPermission(Boolean hasStartedPermission) {
		this.hasStartedPermission = hasStartedPermission;
	}
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
	public ArrayList<ProcessItem> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(ArrayList<ProcessItem> definitions) {
		this.definitions = definitions;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}





	

}
