package it.kdm.doctoolkit.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

public class Amministrazione extends Corrispondente {

	public Amministrazione() {

	}
	UO unitaOrganizzativa = new UO();
	AOO aoo = new AOO();

	public String getDenominazione() {
		return this.getProperty("DES_ENTE"); 
	}
	public void setDenominazione(String denominazione) {
		this.setProperty("DES_ENTE", denominazione);
	}

	public AOO getAOO() {
		return this.aoo;
	}
	public void setAOO(AOO aOO) {
		this.aoo = aOO; 
	}

	public String getCodiceAmministrazione() {
		return this.getProperty("COD_ENTE"); 
	}
	public void setCodiceAmministrazione(String codiceAmministrazione) {
		this.setProperty("COD_ENTE", codiceAmministrazione);
	}

	public UO getUnitaOrganizzativa() {
		return this.unitaOrganizzativa;
	}
	public void setUnitaOrganizzativa(UO unitaOrganizzativa) {
		this.unitaOrganizzativa = unitaOrganizzativa;
	}

	public String getIndirizzoPostale() {
		return getProperty("indirizzoPostale");
	}
	public void setIndirizzoPostale(String indirizzoPostale) {
		setProperty("indirizzoPostale", indirizzoPostale);
	}

	@Override
	@JsonIgnore
	public Element getXmlElement() {
        Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("SKIP");

        Element Amministrazione = root.addElement("Amministrazione");

        Element Denominazione = Amministrazione.addElement("Denominazione");
        Denominazione.addText(getDenominazione()!=null?getDenominazione():"");

        Element CodiceAmministrazione = Amministrazione.addElement("CodiceAmministrazione");
        CodiceAmministrazione.addText(getCodiceAmministrazione()!=null?getCodiceAmministrazione():"");

        Element IndirizzoTelematico = Amministrazione.addElement("IndirizzoTelematico");
        IndirizzoTelematico.addText(getIndirizzoTelematico()!=null?getIndirizzoTelematico():"");

		IndirizzoTelematico.addAttribute(new QName("note"),this.getMezzo()!=null?this.getMezzo():"");

		if (getUnitaOrganizzativa().getDenominazione()!=null) {
			Element UnitaOrganizzativa = Amministrazione.addElement("UnitaOrganizzativa");

			Element DenominazioneUO = UnitaOrganizzativa.addElement("Denominazione");
			DenominazioneUO.addText(getUnitaOrganizzativa().getDenominazione() != null ? getUnitaOrganizzativa().getDenominazione() : "");

			Element IdentificativoUO = UnitaOrganizzativa.addElement("Identificativo");
			IdentificativoUO.addText(getUnitaOrganizzativa().getCodiceUnitaOrganizzativa() != null ? getUnitaOrganizzativa().getCodiceUnitaOrganizzativa() : "");

			if (getUnitaOrganizzativa().getPersona()!=null) {
				Element persona = getUnitaOrganizzativa().getPersona().getXmlElement();
				UnitaOrganizzativa.add(persona);
			}

			Element IndirizzoPostale = UnitaOrganizzativa.addElement("IndirizzoPostale");

			Element DenominazionePostale = IndirizzoPostale.addElement("Denominazione");
			DenominazionePostale.addText(getUnitaOrganizzativa().getIndirizzoPostale()!=null?getUnitaOrganizzativa().getIndirizzoPostale():"");

		} else {
			Element IndirizzoPostale = Amministrazione.addElement("IndirizzoPostale");

			Element DenominazionePostale = IndirizzoPostale.addElement("Denominazione");
			DenominazionePostale.addText(getIndirizzoPostale()!=null?getIndirizzoPostale():"");
		}


		if (getAOO()!=null) {
			Element aoo = root.addElement("AOO");

			Element DenominazioneAoo = aoo.addElement("Denominazione");
			DenominazioneAoo.addText(getAOO().getDenominazione() != null ? getAOO().getDenominazione() : "");

			Element CodiceAOO = aoo.addElement("CodiceAOO");
			CodiceAOO.addText(getAOO().getCodiceAOO() != null ? getAOO().getCodiceAOO() : "");

			Element IndirizzoTelematicoAoo = aoo.addElement("IndirizzoTelematico");
			IndirizzoTelematicoAoo.addText(getAOO().getIndirizzoTelematico() != null ? getAOO().getIndirizzoTelematico() : "");
		}
		Element invioPec = Amministrazione.addElement("InvioPEC");
		String valueInvioPec = "NO";
		if ("PEC".equalsIgnoreCase(this.getMezzo()))
			valueInvioPec="SI";

//		Element Metadati = Amministrazione.addElement("Metadati");
//		if (this.metadati.size()>0) {
//			for(String meta : this.metadati.keySet()) {
//				Element e = Metadati.addElement("Parametro");
//				e.addAttribute("nome",meta);
//				e.addAttribute("valore",this.metadati.get(meta));
//				if("mezzo".equals(meta) && "PEC".equals(this.metadati.get(meta))){
//					valueInvioPec="SI";
//				}
//			}
//
//		}
		invioPec.setText(valueInvioPec);

        return (Element) root.clone();
	}


	@Override
	public String toDisplayString() {
		String outString = "";

		String desAoo = this.getAOO()==null?"":this.getAOO().getProperty("DES_AOO");
		String desUo = this.getUnitaOrganizzativa()==null?"":this.getUnitaOrganizzativa().getProperty("DES_UO");


		if (!Strings.isNullOrEmpty(desUo)) {
			if (this.getUnitaOrganizzativa().getPersona()!=null && !Strings.isNullOrEmpty(this.getUnitaOrganizzativa().getPersona().getProperty("id"))) {
				//UO+Persona
				outString = this.getUnitaOrganizzativa().getProperty("DES_UO") + " (" + this.getUnitaOrganizzativa().getPersona().getProperty("id") + ")";
			} else {
				//UO
				outString = this.getUnitaOrganizzativa().getProperty("DES_UO") + " (" + this.getProperty("DES_ENTE") + ")";
			}

		} else if (!Strings.isNullOrEmpty(desAoo)) {
			//AOO
			outString = this.getAOO().getProperty("DES_AOO") + " (" + this.getProperty("DES_ENTE") + ")";
		} else {
			//ENTE
			outString = this.getProperty("DES_ENTE");
		}

		return outString;
	}

	@Override
	public void parse(String xmlStr)throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new InputSource(new StringReader(xmlStr)));
		Element rootElement = document.getRootElement();



		if(rootElement.element("Denominazione")!=null) {
			this.setDenominazione(rootElement.element("Denominazione").getStringValue());
		}else{
			this.setDenominazione("");
		}

		if(rootElement.element("CodiceAmministrazione")!=null) {
			this.setCodiceAmministrazione(rootElement.element("CodiceAmministrazione").getStringValue());
		}else{
			this.setCodiceAmministrazione("");
		}

		if(rootElement.element("IndirizzoTelematico")!=null) {
			this.setIndirizzoTelematico(rootElement.element("IndirizzoTelematico").getStringValue());
			this.setMezzo(rootElement.element("IndirizzoTelematico").attributeValue(new QName("note")));
		}else{
			this.setIndirizzoTelematico("");
		}



		Element unitaOrganizzativa = rootElement.element("UnitaOrganizzativa");
		UO uo = new UO();
		if(unitaOrganizzativa!=null){
			uo.parse(unitaOrganizzativa.asXML());
		} else {
			Element denomElem = rootElement.element("IndirizzoPostale");
			if(denomElem!=null && denomElem.element("Denominazione")!=null)
				this.setIndirizzoPostale(denomElem.element("Denominazione").getStringValue());
		}

		this.setUnitaOrganizzativa(uo);


//		if(rootElement.element("Metadati")!=null) {
//			List<Element> metadati = rootElement.element("Metadati").elements();
//			String key = "";
//			String value = "";
//			for (Element e : metadati) {
//				if (e.attribute("nome")!=null)
//					key = e.attribute("nome").getValue();
//				if (e.attribute("valore")!=null)
//					value = e.attribute("valore").getValue();
//				if (!Strings.isNullOrEmpty(key)) {
//					this.metadati.put(key,value);
//				}
//			}
//		}
	}

	@Override
	protected void initProperties() {
		this.setProperty("COD_ENTE", "");
		this.setProperty("DES_ENTE", "");
		this.setProperty("COD_AOO", "");
		this.setProperty("DES_AOO", "");
		this.setProperty("COD_UO", "");
		this.setProperty("DES_UO", "");

	}



}
