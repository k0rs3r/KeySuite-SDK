package it.kdm.orchestratore.entity;

public class AutocompletionProperty {
	private int limit;
	private String q;
	
	public AutocompletionProperty(){
		
	}

	public AutocompletionProperty(int limit, String q) {
		super();
		this.limit = limit;
		this.q = q;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getQuery() {
		return q;
	}

	public void setQuery(String q) {
		this.q = q;
	}
}
