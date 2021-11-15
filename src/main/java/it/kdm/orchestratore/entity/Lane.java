package it.kdm.orchestratore.entity;

import java.util.ArrayList;
import java.util.List;

public class Lane {


	private String tipo;
	private String id;
	private String denominazione;
	private String indirizzo;
	private String structure;
	private List<Actor> actor = new ArrayList();
	
	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public List<Actor> getActor() {
		return actor;
	}
	public void setActor(List<Actor> actor) {
		this.actor = actor;
	}
	
	
	
}
