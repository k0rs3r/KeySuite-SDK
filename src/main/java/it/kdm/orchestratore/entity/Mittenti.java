package it.kdm.orchestratore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mittenti {

	@JsonProperty("@type")
	private String type;
	private String tipo;
	@JsonProperty("IndirizzoTelematico")
	private String indirizzoTelematico;
	@JsonProperty("Nome")
	private String nome;
	@JsonProperty("Denominazione")
	private String denominazione;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getIndirizzoTelematico() {
		return indirizzoTelematico;
	}
	public void setIndirizzoTelematico(String indirizzoTelematico) {
		this.indirizzoTelematico = indirizzoTelematico;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDenominazione() {
		return denominazione;
	}
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
    
    
	
}
