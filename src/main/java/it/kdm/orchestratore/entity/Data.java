package it.kdm.orchestratore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;



public class Data {

	private String dataOpen;
	private String dataClose;
	private String nomeIstanza;
	private String uscita;
	private Boolean esito;
	private String scelta;
	private String inoltroid;
	private String codiceFiscale;
	private String bodyMessage;
	private String test;
	private Lane_UO_Responsabile lane_UO_Responsabile;
	private ComIn comIn;
	
	private List<ComIn> collComm;
	private List<ColFasc> colFasc;
	
	
	@JsonProperty("labels")
	private Label label;
	

	public String getDataOpen() {
		return dataOpen;
	}

	public String getUscita() {
		return uscita;
	}

	public void setUscita(String uscita) {
		this.uscita = uscita;
	}

	public Boolean getEsito() {
		return esito;
	}

	public void setEsito(Boolean esito) {
		this.esito = esito;
	}

	public String getScelta() {
		return scelta;
	}

	public void setScelta(String scelta) {
		this.scelta = scelta;
	}

	public String getInoltroid() {
		return inoltroid;
	}

	public void setInoltroid(String inoltroid) {
		this.inoltroid = inoltroid;
	}

	public void setDataOpen(String dataOpen) {
		this.dataOpen = dataOpen;
	}

	public String getDataClose() {
		return dataClose;
	}

	public void setDataClose(String dataClose) {
		this.dataClose = dataClose;
	}

	public String getNomeIstanza() {
		return nomeIstanza;
	}

	public void setNomeIstanza(String nomeIstanza) {
		this.nomeIstanza = nomeIstanza;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getBodyMessage() {
		return bodyMessage;
	}

	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public Lane_UO_Responsabile getLane_UO_Responsabile() {
		return lane_UO_Responsabile;
	}

	public void setLane_UO_Responsabile(Lane_UO_Responsabile lane_UO_Responsabile) {
		this.lane_UO_Responsabile = lane_UO_Responsabile;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public ComIn getComIn() {
		return comIn;
	}

	public void setComIn(ComIn comIn) {
		this.comIn = comIn;
	}

	public List<ComIn> getCollComm() {
		return collComm;
	}

	public void setCollComm(List<ComIn> collComm) {
		this.collComm = collComm;
	}

	public List<ColFasc> getColFasc() {
		return colFasc;
	}

	public void setColFasc(List<ColFasc> colFasc) {
		this.colFasc = colFasc;
	}

	
	
	
}
