package it.kdm.orchestratore.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComIn {

    private String sendDate;
    @JsonProperty("MessageDirection")
    private String messageDirection;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("@type")
    private String type;
    private String codiceFiscale;
    private String nomeIstanza;
    @JsonProperty("Attachments")
    private List<String> attachments;
    @JsonProperty("Mittenti")
    private List<Mittenti> mittenti;
    @JsonProperty("Body")
    private String body;
    private List<Mittenti> cc;
    
    private List<Mittenti> to;
    private Mittenti from;
	
    public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getMessageDirection() {
		return messageDirection;
	}
	public void setMessageDirection(String messageDirection) {
		this.messageDirection = messageDirection;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public String getNomeIstanza() {
		return nomeIstanza;
	}
	public void setNomeIstanza(String nomeIstanza) {
		this.nomeIstanza = nomeIstanza;
	}
	public List<Mittenti> getMittenti() {
		return mittenti;
	}
	public void setMittenti(List<Mittenti> mittenti) {
		this.mittenti = mittenti;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public List<String> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	public List<Mittenti> getTo() {
		return to;
	}
	public void setTo(List<Mittenti> to) {
		this.to = to;
	}
	public Mittenti getFrom() {
		return from;
	}
	public void setFrom(Mittenti from) {
		this.from = from;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public List<Mittenti> getCc() {
		return cc;
	}
	public void setCc(List<Mittenti> cc) {
		this.cc = cc;
	}
    
    
	
}
