package it.kdm.doctoolkit.model;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProtocolloMittente extends GenericObject {

    public String getCodiceAmministrazione() {
    	return this.getProperty("CodiceAmministrazione"); 
	}
	public void setCodiceAmministrazione(String codiceAmministrazione) {
		this.setProperty("CodiceAmministrazione", codiceAmministrazione);
	}

    public String getCodiceAOO() {
    	return this.getProperty("CodiceAOO");
	}
	public void setCodiceAOO(String codiceAOO) {
		this.setProperty("CodiceAOO", codiceAOO);
	}

    public String getClassifica() {
    	return this.getProperty("Classifica"); 
	}
	public void setClassifica(String classifica) {
		this.setProperty("Classifica", classifica);
	}

    public String getData() {
    	return this.getProperty("Data");
	}
	public void setData(String data) {
		this.setProperty("Data", data);
	}

    public String getFascicolo() {
    	return this.getProperty("Fascicolo");
	}
	public void setFascicolo(String fascicolo) {
		this.setProperty("Fascicolo", fascicolo);
	}

    public String getNumero() {
    	 return this.getProperty("Numero");
	}
	public void setNumero(String numero) {
		this.setProperty("Numero", numero);
	}

	@JsonIgnore
	public Element getXmlElement() 
    {
    	Document xml = DocumentHelper.createDocument();
         
        Element root = xml.addElement("ProtocolloMittente");

        Element CodiceAmministrazione = root.addElement("CodiceAmministrazione");
        CodiceAmministrazione.addText(getCodiceAmministrazione());

        Element CodiceAOO = root.addElement("CodiceAOO");
        CodiceAOO.addText(getCodiceAOO());

        Element Classifica = root.addElement("Classifica");
        Classifica.addText(getClassifica());

        Element Anno = root.addElement("Data");
        Anno.addText(getData());

        Element Progressivo = root.addElement("Fascicolo");
        Progressivo.addText(getFascicolo());

        Element Numero = root.addElement("Numero");
        Numero.addText(getNumero());

        return (Element) root.clone();
    }
    
	@Override
	protected void initProperties() {
        this.setProperty("CodiceAmministrazione", "");
        this.setProperty("CodiceAOO", "");
        this.setProperty("Classifica", "");
        this.setProperty("Data", "");
        this.setProperty("Fascicolo", "");
        this.setProperty("Numero", "");
	}


}
