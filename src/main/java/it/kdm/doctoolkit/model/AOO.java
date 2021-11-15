package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;


public class AOO extends ICIFSObject {

    public static final String TYPE = "aoo";

    public AOO()
    { 
    
    }
    
    public AOO(String denominazione, String codice, String indirizzoTelematico)
    {
        setDenominazione(denominazione);
        setCodiceAOO(codice);
        setIndirizzoTelematico(indirizzoTelematico);
    }


    public String getDenominazione() {
    	return this.getProperty("DES_AOO");
	}
	public void setDenominazione(String denominazione) {
		this.setProperty("DES_AOO", denominazione); 
	}

    public boolean getEnabled()
    {
        if (this.getProperty("ENABLED").equalsIgnoreCase("true"))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.setProperty("ENABLED", enabled ? "true" : "false");
    }

    public String getCodiceAOO() {
    	return this.getProperty("COD_AOO");
	}
	public void setCodiceAOO(String codiceAOO) {
		this.setProperty("COD_AOO", codiceAOO);
	}


    public String getIndirizzoTelematico() {
    	return this.getProperty("indirizzoTelematico"); 
	}
	public void setIndirizzoTelematico(String indirizzoTelematico) {
		this.setProperty("indirizzoTelematico", indirizzoTelematico);
	}
	public void parse(String xmlStr)throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new InputSource(new StringReader(xmlStr)));
        Element rootElement = document.getRootElement();

        if (rootElement.element("Denominazione") != null){
            this.setDenominazione(rootElement.element("Denominazione").getStringValue());
        }else{
        this.setDenominazione("");
        }

        if(rootElement.element("CodiceAOO")!=null){
            this.setCodiceAOO(rootElement.element("CodiceAOO").getStringValue());
        }else{
            this.setCodiceAOO("");
        }

		if(rootElement.element("IndirizzoTelematico")!=null){
            this.setIndirizzoTelematico(rootElement.element("IndirizzoTelematico").getStringValue());
        }else{
            this.setIndirizzoTelematico("");
        }


	}
	@Override
	protected void initProperties() {
	
		this.setProperty("COD_AOO", "");
        this.setProperty("DES_AOO", "");
        this.setProperty("indirizzoTelematico", "");
	}

    @Override
    public String getFEId()
    {
        return this.getCodiceAOO();
    }



    @Override
    public String getFEDate()
    {
        return this.getProperty("MODIFIED");
    }

    @Override
    protected String getComputedName() {
        return getDenominazione();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String getID() {
        return getCodiceAOO();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getVersionID() {
        //TODO: Da implementare
        return null;
    }

    @Override
    public String getAbstract() {
        //TODO: E' giusto?
        return getDenominazione();
    }
    @Override
    public String getMimeTypeCSS(){
        return "glyphicon glyphicon-folder-close";
    }

}
