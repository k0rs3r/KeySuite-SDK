package it.kdm.orchestratore.entity;

public class NewInstance {

	private String PID;
	private String TID;
	private String nomeIstanza;
	private String descrizione; 
	private String dataOpen; 
	private String dataClose;
	

	public String getPID() {
		return PID;
	}
	public void setPID(String pID) {
		PID = pID;
	}	
	public String getTID() {
		return TID;
	}
	public void setTID(String tID) {
		TID = tID;
	}
	public String getNomeIstanza() {
		return nomeIstanza;
	}
	public void setNomeIstanza(String nomeIstanza) {
		this.nomeIstanza = nomeIstanza;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getDataOpen() {
		return dataOpen;
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

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("NewInstance model: PID:" + this.PID );
		sb.append(", TID:" + this.TID );
		sb.append(", " + this.nomeIstanza );
		sb.append(", " + this.descrizione );
		sb.append(", " + this.dataOpen );
		sb.append(", " + this.dataClose );
		
		return sb.toString();
	}



	
}
