package it.kdm.doctoolkit.model;

import com.google.common.base.Strings;
import it.kdm.firma.bean.VerificaFirmaInt;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public abstract class Corrispondente extends GenericObject {

	protected Map<String,String> metadati = new HashMap<>();

	private static final String MV_SEP = "\\n";

	public Corrispondente() {
		this.setProperty("IndirizzoTelematico", "");
	}

	public String getMezzo() {
		return this.metadati.get("mezzo");
	}
	public void setMezzo(String mezzo) {
		this.metadati.put("mezzo", mezzo);
	}

	public String getConformita() {
		return this.metadati.get("conformita");
	}
	public void setConformita(String conformita) {
		this.metadati.put("conformita", conformita);
	}

	public void setControlloFirma(VerificaFirmaInt verificaFirmaInt) {

		this.metadati.put("codiceEsito", verificaFirmaInt.getCodiceEsito());
		this.metadati.put("controlloCatenaTrusted", verificaFirmaInt.getControlloCatenaTrusted());
		this.metadati.put("controlloCrittografico", verificaFirmaInt.getControlloCrittografico());
		this.metadati.put("controlloCertificato", verificaFirmaInt.getControlloCertificato());
		this.metadati.put("controlloCRL", verificaFirmaInt.getControlloCRL());
	}



	public String getCodiceEsito() {
		return this.metadati.get("codiceEsito");
	}
	public String getControlloCrittografico() {
		return this.metadati.get("controlloCrittografico");
	}
	public String getControlloCatenaTrusted() {
		return this.metadati.get("controlloCatenaTrusted");
	}
	public String getControlloCertificato() {
		return this.metadati.get("controlloCertificato");
	}
	public String getControlloCRL() {
		return this.metadati.get("controlloCRL");
	}



	public String getDataFirma() {
		return this.metadati.get("datafirma");
	}
	public void setDataFirma(String datafirma) {
		this.metadati.put("datafirma", datafirma);
	}

	public String[] getWarnings() {
		return this.metadati.containsKey("warnings")? this.metadati.get("warnings").split(MV_SEP): null;
	}
	public void setWarnings(String[] warnings) {
		this.metadati.put("warnings", StringUtils.join( warnings, MV_SEP));
	}

	public String[] getErrors() {
		return this.metadati.containsKey("errors")? this.metadati.get("errors").split(MV_SEP): null;
	}
	public void setErrors(String[] errors) {
		this.metadati.put("errors", StringUtils.join( errors, MV_SEP));
	}

//	public String getType() {
//		return this.metadati.get("type");
//	}
//	public void setType(String type) {
//		this.metadati.put("type", type);
//	}

	public String getIndirizzoTelematico(){
		return this.getProperty("IndirizzoTelematico");
	}
	public void setIndirizzoTelematico(String indirizzoTelematico) {
		this.setProperty("IndirizzoTelematico", indirizzoTelematico);
		this.setProperty("IndirizzoTelematicoUo", indirizzoTelematico);
	}

	public abstract Element getXmlElement();
	
	public abstract void parse(String xmlStr)throws DocumentException;
	
	public String getDenominazione(){
		
		if(this.getProperty("denominazione") != null && !"".equals(this.getProperty("denominazione"))){
			return this.getProperty("denominazione");
		}else if((this.getProperty("nome") != null && !"".equals(this.getProperty("nome"))) ||
				 (this.getProperty("cognome") != null && !"".equals(this.getProperty("cognome")))){
			String denom = this.getProperty("nome") + " " + this.getProperty("cognome");
            denom = denom.trim();
            return denom;
		}else{
			return this.getProperty("IndirizzoTelematico");
		}
	}


	private String getReferente() {

		String val = this.getDenominazione();
		UO uo1 = null;

		if (this instanceof Amministrazione
				&& ((Amministrazione) this).getUnitaOrganizzativa() != null) {

			uo1 = ((Amministrazione) this).getUnitaOrganizzativa();
		}
		else if(this instanceof UO) {

			uo1 = (UO)this;
		}

		if (uo1!=null) {

			val = uo1.getDenominazione();
			if (uo1.getPersona() != null && !"".equals(uo1.getPersona().getDenominazione())) {
				val = String.format("%s [ %s ]", uo1.getPersona().getDenominazione(), uo1.getDenominazione());
			}
		}

		return val;
	}

	public String toDisplayString() {
		String format = "%s %s\r";
		if (!Strings.isNullOrEmpty(this.getProperty("id")))
			return String.format(format,this.getDenominazione(),"("+this.getProperty("id")+")");
		else
			return String.format(format,this.getDenominazione(),"");
	}

}
