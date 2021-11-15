package it.kdm.doctoolkit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

public class PersonaFisica extends Corrispondente {

	public String Id;


	public String getId() {
		 return getProperty("id");
	}
	public void setId(String id) {
		 setProperty("id", id);
	}

    public String getNome() {
		return getProperty("nome");
	}
	public void setNome(String nome) {
		setProperty("nome", nome); 
	}

	public String getCodiceFiscale() {
		return getProperty("codicefiscale");
	}
	public void setCodiceFiscale(String codicefiscale) {
		setProperty("codicefiscale", codicefiscale);
	}

	public String getCognome() {
		return getProperty("cognome");
	}
	public void setCognome(String cognome) {
		setProperty("cognome", cognome);
	}

	public String getTitolo() {
		return getProperty("titolo"); 
	}
	public void setTitolo(String titolo) {
		setProperty("titolo", titolo);
	}
	public void setInvioPec(String invioPec) {
		setProperty("invioPec", invioPec);
	}
	public String getInvioPec() {
		return getProperty("invioPec");
	}
	public boolean getInvioPEC() {
		return Boolean.valueOf(getProperty("invioPEC"));
	}
	public void setInvioPEC(boolean cognome) {
		setProperty("invioPEC", Boolean.toString(cognome));
	}

	public String getIndirizzoPostale() {
		return getProperty("indirizzoPostale"); 
	}
	public void setIndirizzoPostale(String indirizzoPostale) {
		setProperty("indirizzoPostale", indirizzoPostale); 
	}
	public String getPEO() {
		return this.metadati.get("peo");
	}
	public void setPEO(String peo) {
		this.metadati.put("peo", peo);
	}

	public String getFAX() {
		return this.metadati.get("fax");
	}
	public void setFAX(String fax) {
		this.metadati.put("fax", fax);
	}


	@Override
	@JsonIgnore
	public Element getXmlElement() {
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("Persona");

		String id = getId();

		if (Strings.isNullOrEmpty(id))
			id = "";

		root.addAttribute("id",id);

		Element identificativo = root.addElement("Identificativo");
		identificativo.addText(getId()!=null?getId():"");

		Element Nome = root.addElement("Nome");
		Nome.addText(getNome()!=null?getNome():"");

		Element Cognome = root.addElement("Cognome");
		Cognome.addText(getCognome()!=null?getCognome():"");

		Element Titolo = root.addElement("Titolo");
		Titolo.addText(getTitolo()!=null?getTitolo():"");

		Element IndirizzoTelematico = root.addElement("IndirizzoTelematico");
		IndirizzoTelematico.addAttribute("tipo", "smtp");
		IndirizzoTelematico.addText(getIndirizzoTelematico()!=null?getIndirizzoTelematico():"");

		Element invioPec = root.addElement("InvioPEC");
		/*invioPec.addText(Boolean.toString(getInvioPEC()));
*/
		Element IndirizzoPostale = root.addElement("IndirizzoPostale");

		Element DenominazionePostale = IndirizzoPostale.addElement("Denominazione");
		DenominazionePostale.addText(getIndirizzoPostale()!=null?getIndirizzoPostale():"");

		Element denominazione = root.addElement("Denominazione");
		denominazione.addText(getDenominazione()!=null?getDenominazione():"");

		Element codiceFiscale = root.addElement("CodiceFiscale");
		codiceFiscale.addText(getCodiceFiscale()!=null?getCodiceFiscale():"");

		Element Metadati = root.addElement("Metadati");
		String valueInvioPec = "NO";
		if (this.metadati.size()>0) {
			for(String meta : this.metadati.keySet()) {
				if (this.metadati.get(meta)!=null) {
					Element e = Metadati.addElement("Parametro");
					e.addAttribute("nome",meta);
					e.addAttribute("valore",this.metadati.get(meta));
					if("mezzo".equals(meta) && "PEC".equals(this.metadati.get(meta))){
						valueInvioPec="SI";
					}
				}
			}
			invioPec.addText(valueInvioPec);

		}
		return (Element) root.clone();
	}
	@Override
	public void parse(String xmlStr)throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new InputSource(new StringReader(xmlStr)));
		Element rootElement = document.getRootElement();
		if(rootElement.attribute("id")!=null)
			this.setId(rootElement.attribute("id").getValue());
		if(rootElement.element("Denominazione")!=null)
			this.setProperty("denominazione",rootElement.element("Denominazione").getStringValue());
		if(rootElement.element("Nome")!=null)
			this.setNome(rootElement.element("Nome").getStringValue());
		if(rootElement.element("Cognome")!=null)
			this.setCognome(rootElement.element("Cognome").getStringValue());
		if(rootElement.element("Titolo")!=null)
			this.setTitolo(rootElement.element("Titolo").getStringValue());
		if(rootElement.element("IndirizzoTelematico")!=null)
			this.setIndirizzoTelematico(rootElement.element("IndirizzoTelematico").getStringValue());
		if(rootElement.element("CodiceFiscale")!=null)
			this.setCodiceFiscale(rootElement.element("CodiceFiscale").getStringValue());
		Element denomElem = rootElement.element("IndirizzoPostale");
		if(denomElem!=null && denomElem.element("Denominazione")!=null)
		this.setIndirizzoPostale(denomElem.element("Denominazione").getStringValue());
		if(rootElement.element("InvioPEC")!=null)
			this.setInvioPec(rootElement.element("InvioPEC").getStringValue());
		if(rootElement.element("Metadati")!=null) {
			List<Element> metadati = rootElement.element("Metadati").elements();
			String key = "";
			String value = "";
			for (Element e : metadati) {
				if (e.attribute("nome")!=null)
					key = e.attribute("nome").getValue();
				if (e.attribute("valore")!=null)
					value = e.attribute("valore").getValue();
				if (!Strings.isNullOrEmpty(key)) {
					this.metadati.put(key,value);
				}
			}
		}
	}

	@Override
	protected void initProperties() {
		this.setProperty("id", "");
		this.setProperty("nome", "");
		this.setProperty("cognome", "");
		this.setProperty("titolo", "");
		this.setProperty("indirizzoPostale", "");
	}

}
