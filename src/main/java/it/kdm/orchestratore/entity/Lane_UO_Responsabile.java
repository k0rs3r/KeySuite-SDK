package it.kdm.orchestratore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Lane_UO_Responsabile {

	@JsonProperty("IndirizzoTelematico")
	private String indirizzoTelematico;
	private String tipo;
	private String label;
	private String code;
	@JsonProperty("DES_ENTE")
	private String desEnte;
	@JsonProperty("COD_ENTE")
	private String codEnte;
	@JsonProperty("DES_AOO")
	private String desAoo;
	@JsonProperty("COD_AOO")
	private String codAoo;
	@JsonProperty("@type")
	private String type;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesEnte() {
		return desEnte;
	}

	public void setDesEnte(String desEnte) {
		this.desEnte = desEnte;
	}

	public String getCodEnte() {
		return codEnte;
	}

	public void setCodEnte(String codEnte) {
		this.codEnte = codEnte;
	}

	public String getDesAoo() {
		return desAoo;
	}

	public void setDesAoo(String desAoo) {
		this.desAoo = desAoo;
	}

	public String getCodAoo() {
		return codAoo;
	}

	public void setCodAoo(String codAoo) {
		this.codAoo = codAoo;
	}

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

	
	
	
	
	
}
