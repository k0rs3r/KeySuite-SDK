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

public class PersonaGiuridica extends Corrispondente {
	
    public String getId() {
    	return this.getProperty("id"); 
	}
	public void setId(String id) {
		 this.setProperty("id", id);
	}

    public String getDenominazione() {
    	return this.getProperty("denominazione");
	}
	public void setDenominazione(String denominazione) {
		this.setProperty("denominazione", denominazione); 
	}

    public String getIndirizzoPostale() {
    	return this.getProperty("indirizzoPostale");
	}
	public void setIndirizzoPostale(String indirizzoPostale) {
		this.setProperty("indirizzoPostale", indirizzoPostale);
	}

	public String getDesUo() {
		return this.metadati.get("desUo");
	}
	public void setDesUo(String desUo) {
		this.metadati.put("desUo", desUo);
	}
	public void setInvioPec(String invioPec) {
		setProperty("invioPec", invioPec);
	}
	public String getInvioPec() {
		return getProperty("invioPec");
	}
	@Override
	@JsonIgnore
	public Element getXmlElement() {
		String valueInvioPec="NO";
		Document xml = DocumentHelper.createDocument();
		
        Element root = xml.addElement("PersonaGiuridica");
        root.addAttribute("id",getId()==null?"":getId());
//        root.addAttribute("tipo","CodiceFiscalePG");
        root.addAttribute("tipo","partita_iva");

        Element Denominazione = root.addElement("Denominazione");
        Denominazione.addText(getDenominazione()!=null?getDenominazione():"");

        Element IndirizzoPostale = root.addElement("IndirizzoPostale");

        Element DenominazionePostale = IndirizzoPostale.addElement("Denominazione");
        DenominazionePostale.addText(getIndirizzoPostale()!=null?getIndirizzoPostale():"");

        Element IndirizzoTelematico = root.addElement("IndirizzoTelematico");
        IndirizzoTelematico.addAttribute("tipo","smtp");
        IndirizzoTelematico.addText(getIndirizzoTelematico()!=null?getIndirizzoTelematico():"");
		Element invioPec = root.addElement("InvioPEC");
		Element Metadati = root.addElement("Metadati");

		if (this.metadati.size()>0) {
			for(String meta : this.metadati.keySet()) {
				Element e = Metadati.addElement("Parametro");
				e.addAttribute("nome",meta);
				e.addAttribute("valore",this.metadati.get(meta));
				if("mezzo".equals(meta) && "PEC".equals(this.metadati.get(meta))){
					valueInvioPec="SI";
				}
			}

		}
		invioPec.setText(valueInvioPec);
        return (Element) root.clone();
	}
	
	@Override
	public void parse(String xmlStr)throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new InputSource(new StringReader(xmlStr)));
		Element rootElement = document.getRootElement();
		if(rootElement.attribute("id")!=null) {
			this.setId(rootElement.attribute("id").getValue());
		}else{
			this.setId("");
		}

		if(rootElement.element("Denominazione")!=null){
			this.setDenominazione(rootElement.element("Denominazione").getStringValue());
		} else
			this.setDenominazione("");

		if(rootElement.element("IndirizzoTelematico")!=null){
			this.setIndirizzoTelematico(rootElement.element("IndirizzoTelematico").getStringValue());
		} else
			this.setIndirizzoTelematico("");

		if(rootElement.element("IndirizzoPostale")!=null){
			if (rootElement.element("IndirizzoPostale").element("Denominazione")!=null)
				this.setIndirizzoPostale(rootElement.element("IndirizzoPostale").element("Denominazione").getStringValue());
		} else
			this.setIndirizzoPostale("");

		if(rootElement.element("InvioPEC")!=null){
			this.setInvioPec(rootElement.element("InvioPEC").getStringValue());
		} else
		this.setInvioPec("NO");

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
		/*Element denomElem = rootElement.element("IndirizzoPostale");
		this.setIndirizzoPostale(denomElem.element("Denominazione").getStringValue());*/
	}
	@Override
	protected void initProperties() {
        this.setProperty("id", "");
        this.setProperty("denominazione", "");
        this.setProperty("IndirizzoPostale", "");

	}

}
