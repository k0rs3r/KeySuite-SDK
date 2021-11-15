package it.kdm.doctoolkit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class UO extends Corrispondente {

	PersonaFisica persona;

	public UO() {

	}
	
	public UO(String denominazione, String codice)
    {
        setDenominazione(denominazione);
        setCodiceUnitaOrganizzativa(codice);
    }


    public String getDenominazione() {
    	return this.getProperty("DES_UO");
	}
	public void setDenominazione(String denominazione) {
		this.setProperty("DES_UO", denominazione); 
	}
	
    public String getTipo() {
    	return this.getProperty("tipo");
	}
	public void setTipo(String tipo) {
		this.setProperty("tipo", tipo); 
	}

	public String getIndirizzoPostale() {
		return this.getProperty("indirizzoPostale");
	}
	public void setIndirizzoPostale(String indirizzoPostale) {
		this.setProperty("indirizzoPostale", indirizzoPostale);
	}
    public String getCodiceUnitaOrganizzativa() {
    	return this.getProperty("COD_UO"); 
	}
	public void setCodiceUnitaOrganizzativa(String codiceUnitaOrganizzativa) {
		this.setProperty("COD_UO", codiceUnitaOrganizzativa); 
	}

	public PersonaFisica getPersona() {
		return this.persona;
	}
	public void setPersona(PersonaFisica persona) {
		this.persona=persona;
	}

	@JsonIgnore
    public Element getXmlElement()
    {
    	Document xml = DocumentHelper.createDocument();
    	Element root = xml.addElement("UnitaOrganizzativa");
    	root.addAttribute("tipo", getTipo());


    	Element Denominazione = root.addElement("Denominazione");
    	Denominazione.addText(getDenominazione()!=null?getDenominazione():"");

    	Element Identificativo = root.addElement("Identificativo");
    	Identificativo.addText(getCodiceUnitaOrganizzativa()!=null?getCodiceUnitaOrganizzativa():"");


		if (getPersona()!=null) {
			Element persona = this.getPersona().getXmlElement();
			root.add(persona);
//			Element persona = root.addElement("Persona");
//			Element name = persona.addElement("Denominazione");
//			Element id = persona.addElement("Identificativo");
//			name.addText(getPersona().getDenominazione()!=null?getPersona().getDenominazione():"");
//			id.addText(getPersona().getId()!=null?getPersona().getId():"");
		}

//		if (getRuolo()!=null) {
//			Element ruolo = root.addElement("Ruolo");
//			Element name = ruolo.addElement("Denominazione");
//			Element id = ruolo.addElement("Identificativo");
//			name.addText(getRuolo().getDenominazione());
//			id.addText(getRuolo().getId());
//		}

		Element IndirizzoPostale = root.addElement("IndirizzoPostale");

		Element DenominazionePostale = IndirizzoPostale.addElement("Denominazione");
		DenominazionePostale.addText(getIndirizzoPostale()!=null?getIndirizzoPostale():"");

		return (Element) root.clone();
    }
	public void parse(String xmlStr)throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new InputSource(new StringReader(xmlStr)));
		Element rootElement = document.getRootElement();

		if(rootElement.element("Denominazione")!=null) {
			this.setDenominazione(rootElement.element("Denominazione").getStringValue());
		}else{
			this.setDenominazione("");
		}

		if(rootElement.element("Identificativo")!=null) {
			this.setCodiceUnitaOrganizzativa(rootElement.element("Identificativo").getStringValue());
		}else{
			this.setCodiceUnitaOrganizzativa("");
		}

		Element denomElem = rootElement.element("IndirizzoPostale");
		if(denomElem!=null && denomElem.element("Denominazione")!=null)
			this.setIndirizzoPostale(denomElem.element("Denominazione").getStringValue());

		if(rootElement.element("Persona")!=null) {
			this.persona = new PersonaFisica();
			this.getPersona().setId(rootElement.element("Persona").element("Identificativo").getStringValue());
			this.getPersona().setProperty("denominazione", rootElement.element("Persona").element("Denominazione").getStringValue());
		}
	}

	@Override
	protected void initProperties() {
		this.setProperty("tipo", "temporanea");
		this.setProperty("COD_UO", "");
        this.setProperty("DES_UO", "");
        
		
	}

}
