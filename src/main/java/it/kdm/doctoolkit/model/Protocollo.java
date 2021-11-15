package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.exception.DocerApiException;

import org.dom4j.Document;

public class Protocollo extends GenericObject {

    public String getNumeroPG() {
    	return this.getProperty("NUM_PG");
	}

	public void setNumeroPG(String numeroPG) {
		this.setProperty("NUM_PG", numeroPG);
	}

    public String getDataPG() {
    	return this.getProperty("DATA_PG"); 
	}

	public void setDataPG(String dataPG) {
		this.setProperty("DATA_PG", dataPG);
	}

    public String getAnnoPG() {
    	return this.getProperty("ANNO_PG");
	}

	public void setAnnoPG(String annoPG) {
		this.setProperty("ANNO_PG", annoPG);
	}

    public String getOggettoPG() {
    	return this.getProperty("OGGETTO_PG");
	}

	public void setOggettoPG(String oggettoPG) {
		this.setProperty("OGGETTO_PG", oggettoPG);
	}

    public String getRegistroPG() {
    	return this.getProperty("REGISTRO_PG");
	}

	public void setRegistroPG(String registroPG) {
		this.setProperty("REGISTRO_PG", registroPG);
	}


    public void loadFromXml(Document xml) throws DocerApiException
    {
        try
        {

            setNumeroPG(xml.selectSingleNode("//NUM_PG").getText());
            setDataPG(xml.selectSingleNode("//DATA_PG").getText());
            setAnnoPG(xml.selectSingleNode("//ANNO_PG").getText());
            setOggettoPG(xml.selectSingleNode("//OGGETTO_PG").getText());
            setRegistroPG(xml.selectSingleNode("//REGISTRO_PG").getText());
        }
        catch (Exception e)
        {
            throw new DocerApiException("Formato dati non valido.", 513);
        }
    }

    @Override
	protected void initProperties() {
        this.setProperty("NUM_PG", "");
        this.setProperty("DATA_PG", "");
        this.setProperty("ANNO_PG", "");
        this.setProperty("OGGETTO_PG", "");
        this.setProperty("REGISTRO_PG", "");
	}

}
