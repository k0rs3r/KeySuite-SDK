package it.kdm.orchestratore.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <br></br>
 * La classe EmailBean rappresenta un messaggio generico.<br></br>
 * Viene utilizzata nel sistema di routing come struttura dei messaggi ricevuti. Pertanto un messaggio in uscita al blocco "imap-get" ha la struttura di un oggetto EmailBean.
 * @author KDM
 * @version 1.0
 */
public class EmailBean {

	private List<Map<String,String>> mittenti = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> destinatari = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> conoscenza = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> nascosta = new ArrayList<Map<String,String>>();
	private String sendDate;
	private String receivedDate;
	private String contentText;
	private String contentHtml;
	private String subject;
	private List<String> allegati = new ArrayList<String>();
	private String messageId;
	private String type;
	private String isRicevutaPec;
	private Boolean daControllare;

	public Boolean getDaControllare() {
		return daControllare;
	}

	public void setDaControllare(Boolean daControllare) {
		this.daControllare = daControllare;
	}

	/**
	 * Ritorna un valore che indica se il messaggio è una ricevuta PEC (true) oppure no (false)
	 * @return una stringa con valore true o false
	 */
	public String getIsRicevutaPec() {
		return isRicevutaPec;
	}

	/**
	 * Setta un valore per indicare se il messaggio è una ricevuta PEC (true) oppure no (false)
	 * @param isRicevutaPec stringa con valore true o false
	 */
	public void setIsRicevutaPec(String isRicevutaPec) {
		this.isRicevutaPec = isRicevutaPec;
	}

	/**
	 * Ritorna il type del messaggio (di solito impostato a "message")
	 * @return il tipo di messaggio
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setta il tipo di messaggio (impostato a "message")
	 * @param type stringa che indica il tipo di messaggio
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Ritorna il messageId del messaggio
	 * @return stringa che contiene il messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Setta il valore del messageId del messaggio (identificatore univoco del messaggio)
	 * @param messageId Stringa che contiene il valore del messageId
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * Ritorna una lista contenente i path degli allegati del messaggio
	 * @return Lista di stringhe
	 */
	public List<String> getAllegati() {
		return allegati;
	}

	/**
	 * Setta la lista degli allegati del messaggio
	 * @param allegati Lista di stringhe
	 */
	public void setAllegati(List<String> allegati) {
		this.allegati = allegati;
	}

	/**
	 * Ritorna la data di invio del messaggio
	 * @return Una Stringa
	 */
	public String getSendDate() {
		return sendDate;
	}

	/**
	 * Setta il valore della data di invio del messaggio
	 * @param sendDate Una stringa
	 */
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * Ritorna la data di ricezione del messaggio
	 * @return Una stringa
	 */
	public String getReceivedDate() {
		return receivedDate;
	}

	/**
	 * Setta la data di ricezione del messaggio
	 * @param receivedDate Una stringa
	 */
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	/**
	 * Ritorna il contenuto testuale del messaggio
	 * @return Una stringa
	 */
	public String getContentText() {
		return contentText;
	}

	/**
	 * Setta il contenuto testuale del messaggio
	 * @param contentText Una stringa
	 */
	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	/**
	 * Ritorna il contenuto HTML del messaggio
	 * @return Una stringa
	 */
	public String getContentHtml() {
		return contentHtml;
	}

	/**
	 * Setta il contenuto HTML del messaggio
	 * @param contentHtml Una stringa
	 */
	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}

	/**
	 * Ritorna il subject del messaggio
	 * @return Una stringa
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Setta il subject del messaggio
	 * @param subject Una stringa
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Ritorna la lista dei mittenti del messaggio
	 * @return Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public List<Map<String, String>> getMittenti() {
		return mittenti;
	}

	/**
	 * Setta la lista dei mittenti del messaggio
	 * @param mittenti Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public void setMittenti(List<Map<String, String>> mittenti) {
		this.mittenti = mittenti;
	}

	/**
	 * Ritorna la lista dei destinatari del messaggio
	 * @return Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public List<Map<String, String>> getDestinatari() {
		return destinatari;
	}

	/**
	 * Setta la lista dei destinatari del messaggio
	 * @param destinatari Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public void setDestinatari(List<Map<String, String>> destinatari) {
		this.destinatari = destinatari;
	}

	/**
	 * Ritorna la lista dei CC del messaggio
	 * @return Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public List<Map<String, String>> getConoscenza() {
		return conoscenza;
	}

	/**
	 * Setta la lista dei CC del messaggio
	 * @param conoscenza Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public void setConoscenza(List<Map<String, String>> conoscenza) {
		this.conoscenza = conoscenza;
	}

	/**
	 * Ritorna la lista dei BCC del messaggio
	 * @return Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public List<Map<String, String>> getNascosta() {
		return nascosta;
	}

	/**
	 * Setta la lista dei BCC del messaggio
	 * @param nascosta Una lista di Map<String,String>; ogni elemento contiene tre valori: "IndirizzoTelematico" "Denominazione" "Tipo"
	 */
	public void setNascosta(List<Map<String, String>> nascosta) {
		this.nascosta = nascosta;
	}
}
