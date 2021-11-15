package it.kdm.orchestratore.beans;

import java.io.Serializable;
import java.util.List;

public class GenericResultSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames;
	private List<Object[]> valori;

	public GenericResultSet(){
		super();
	}

	public List<Object[]> getValori() {
		return valori;
	}

	public void setValori(List<Object[]> valori) {
		this.valori = valori;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
