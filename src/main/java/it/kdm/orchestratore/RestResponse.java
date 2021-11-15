package it.kdm.orchestratore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponse {
	
	//record per pagina
	private Integer pageSize;
	
	//numero pagina attuale
	private Integer pageNumber;
	
	//numero di pagine
	private Integer pageCount;
	
	//numero di pagine
	private Integer recordCount;
		
	private String orderBy;
	private Object data;

	public Integer getElapsed() {
		return elapsed;
	}

	public void setElapsed(Integer elapsed) {
		this.elapsed = elapsed;
	}

	private Integer elapsed;

	private Map<String,String> parameters;

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	private List<String> columns;

	public Map<String, Map<String, Integer>> getFacets() {
		return facets;
	}

	public void setFacets(Map<String, Map<String, Integer>> facets) {
		this.facets = facets;
	}

	private Map<String,Map<String,Integer>> facets;

	public Map<String, List<Object>> getSeries() {
		return series;
	}

	public void setSeries(Map<String, List<Object>> series) {
		this.series = series;
	}

	private Map<String,List<Object>> series;
	
	public RestResponse(Object data) {
		this.data = data;
	}

	public RestResponse(){

	}
	
	public RestResponse(Object data, Integer pageSize, Integer pageNumber, Integer pageCount, Integer recordCount, String order) {
		this.data = data;
		this.orderBy=order;
		this.pageNumber=pageNumber;
		this.pageSize=pageSize;
		this.pageCount=pageCount;
		this.recordCount=recordCount;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}
	
	
	
}
