package it.kdm.orchestratore.entity.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.kdm.orchestratore.entity.Task;

import java.util.ArrayList;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TasksObject {
	
	private String key = "tasks";
	
	private int pageSize;
	private int pageNumber;
	private int pageCount;
	private int recordCount;
	private String orderBy;
	
	private ArrayList<Task> data;
	private ArrayList<Task> tasks;

	public Map<String, Map<String, Integer>> getFacets() {
		return facets;
	}

	public void setFacets(Map<String, Map<String, Integer>> facets) {
		this.facets = facets;
	}

	private Map<String,Map<String,Integer>> facets;


	public ArrayList<Task> getData() {
		return data;
	}
	public void setData(ArrayList<Task> data) {
		this.data = data;
	}
	public ArrayList<Task> getTasks() {
		return tasks;
	}
	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = "tasks";
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
