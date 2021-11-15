package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.exception.DocerApiException;

import org.dom4j.Document;

public class Registro extends GenericObject {

    public String getDataRegistrazione() {
    	return this.getProperty("DataRegistrazione");
	}
	public void setDataRegistrazione(String dataRegistrazione) {
		this.setProperty("DataRegistrazione", dataRegistrazione);
	}

    public String getNumeroRegistrazione() {
    	return this.getProperty("NumeroRegistrazione");
	}
	public void setNumeroRegistrazione(String numeroRegistrazione) {
		this.setProperty("NumeroRegistrazione", numeroRegistrazione);
	}

    public String getOggettoRegistrazione() {
    	return this.getProperty("OggettoRegistrazione"); 
	}
	public void setOggettoRegistrazione(String oggettoRegistrazione) {
		this.setProperty("OggettoRegistrazione", oggettoRegistrazione);
	}

    public String getIDRegistro() {
    	return this.getProperty("IDRegistro");
	}
	public void setIDRegistro(String iDRegistro) {
		this.setProperty("IDRegistro", iDRegistro);
	}


    public void loadFromXml(Document xml) throws DocerApiException
    {
        try
        {
            setDataRegistrazione(xml.selectSingleNode("//DataRegistrazione").getText());
            setNumeroRegistrazione(xml.selectSingleNode("//NumeroRegistrazione").getText());
            setOggettoRegistrazione(xml.selectSingleNode("//OggettoRegistrazione").getText());
            setIDRegistro(xml.selectSingleNode("//IDRegistro").getText());
        }
        catch (Exception e)
        {
            throw new DocerApiException("Formato dati non valido.", 513);
        }
    }

	@Override
	protected void initProperties() {
        this.setProperty("DataRegistrazione", "");
        this.setProperty("NumeroRegistrazione", "");
        this.setProperty("OggettoRegistrazione", "");
        this.setProperty("IDRegistro", "");
	}

}
