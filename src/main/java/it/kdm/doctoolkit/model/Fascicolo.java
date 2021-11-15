package it.kdm.doctoolkit.model;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.path.ICIFSObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

public class Fascicolo extends ICIFSObject {

    public static final String TYPE = "fascicolo";

    public String getDescrizione()  {
    	return this.getProperty("DES_FASCICOLO");
	}
	public void setDescrizione(String descrizione) {
		this.setProperty("DES_FASCICOLO", descrizione);
	}

    public String getAnno()  {
    	return this.getProperty("ANNO_FASCICOLO");
	}
	public void setAnno(String anno) {
		this.setProperty("ANNO_FASCICOLO", anno);
	}

    public String getEnte()  {
    	return this.getProperty("COD_ENTE");
	}
	public void setEnte(String ente) {
		this.setProperty("COD_ENTE", ente); 
	}

    public String getAoo()  {
    	 return this.getProperty("COD_AOO");
	}
	public void setAoo(String aoo) {
		 this.setProperty("COD_AOO", aoo);
	}

    public String getDescriptionTitolario() {
        return getDescription(DescriptionType.TITOLARIO);
    }

    @Override
    public String getFEAuthor() {
        return getProperty("CREATOR");
    }

    public String getProgressivoPadre()  {
    	return this.getProperty("PARENT_PROGR_FASCICOLO"); 
	}
	public void setProgressivoPadre(String progressivoPadre) {
		this.setProperty("PARENT_PROGR_FASCICOLO", progressivoPadre);
	}


    public String getSequence(){
        return this.getProperty("sequence");
    }

    public String getProgressivo()  {
    	return this.getProperty("PROGR_FASCICOLO"); 
	}
	public void setProgressivo(String progressivo) {
		this.setProperty("PROGR_FASCICOLO", progressivo);
	}

    public String getClassifica()  {
    	return this.getProperty("CLASSIFICA");
	}
	public void setClassifica(String classifica) {
		this.setProperty("CLASSIFICA", classifica);
	}
	
    public boolean getEnabled() {
		if (this.getProperty("ENABLED").equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}
	public void setEnabled(boolean enabled) {
		this.setProperty("ENABLED", enabled ? "true" : "false");
	}

    public String getCodFiscPersona() {
    	return this.getProperty("CF_PERSONA");
	}
	public void setCodFiscPersona(String cFPersona) {
		this.setProperty("CF_PERSONA", cFPersona); 
	}

    public String getCodFiscAzienda() {
    	 return this.getProperty("CF_AZIENDA"); 
	}
	public void setCodFiscAzienda(String cFAzienda) {
		this.setProperty("CF_AZIENDA", cFAzienda); 
	}

    public String getIdImmobile() {
    	 return this.getProperty("ID_IMMOBILE"); 
	}
	public void setIdImmobile(String idImmobile) {
		 this.setProperty("ID_IMMOBILE", idImmobile); 
	}

	public FascicoloPrimario toFascicoloPrimario()
    {
        FascicoloPrimario f = new FascicoloPrimario();
        f.properties = new HashMap<String, String>(this.properties);

        return f;
    }

    public FascicoloSecondario toFascicoloSecondario()
    {
        FascicoloSecondario f = new FascicoloSecondario();
        f.properties = new HashMap<String, String>(this.properties);

        return f;
    }
/*
    @Override
	public String getFEName()
	{
		return this.getDescrizione();
	}
*/
	@Override
	public String getFEId()
	{
		return this.getProgressivo();
	}

	@Override
	public String getFEDate()
	{
		return this.getProperty("MODIFIED");
	}
	
	@JsonIgnore
    public Element getXmlElement() 
    {
        Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("Fascicolo");

        Element CodiceAmministrazione = root.addElement("CodiceAmministrazione");
        CodiceAmministrazione.addText(getEnte());

        Element CodiceAOO = root.addElement("CodiceAOO");
        CodiceAOO.addText(getAoo());

        Element Classifica = root.addElement("Classifica");
        Classifica.addText(getClassifica());

        Element Anno = root.addElement("Anno");
        Anno.addText(getAnno());

        Element Progressivo = root.addElement("Progressivo");
        Progressivo.addText(getProgressivo());


        return (Element) root.clone();
    }

    public void loadFromXml(Document xml) throws DocerApiException 
    {
        try
        {
            setEnte(xml.selectSingleNode("//COD_ENTE").getText());
            setAoo(xml.selectSingleNode("//COD_AOO").getText());
            setClassifica(xml.selectSingleNode("//CLASSIFICA").getText());
            setAnno(xml.selectSingleNode("//ANNO_FASCICOLO").getText());
            setProgressivo(xml.selectSingleNode("//PROGR_FASCICOLO").getText());
            
            if (xml.selectSingleNode("//PARENT_PROGR_FASCICOLO")!=null) {
            	setProgressivoPadre(xml.selectSingleNode("//PARENT_PROGR_FASCICOLO").getText());
            } else {
            	setProgressivoPadre("");
			}

            setDescrizione(xml.selectSingleNode("//DES_FASCICOLO").getText());
            
            if (xml.selectSingleNode("//CF_PERSONA")!=null) {
				setCodFiscPersona(xml.selectSingleNode("//CF_PERSONA").getText());
			}	
            if (xml.selectSingleNode("//CF_AZIENDA")!=null) {
				setCodFiscAzienda(xml.selectSingleNode("//CF_AZIENDA").getText());
			}
			if (xml.selectSingleNode("//ID_IMMOBILE")!=null) {
				setIdImmobile(xml.selectSingleNode("//ID_IMMOBILE").getText());
			}            
            
            
            
        }
        catch (Exception e)
        {
            throw new DocerApiException("Formato dati non valido.", 513);
        }
    }
  

	@Override
	protected void initProperties() {
        this.setProperty("DES_FASCICOLO", "");
        this.setProperty("ENABLED", "true");
        this.setProperty("ANNO_FASCICOLO", "");
        this.setProperty("PARENT_PROGR_FASCICOLO", null);
        this.setProperty("COD_ENTE", "");
        this.setProperty("COD_AOO", "");
        this.setProperty("CF_PERSONA", "");
        this.setProperty("CF_AZIENDA", "");
        this.setProperty("ID_IMMOBILE", "");
        this.setProperty("PROGR_FASCICOLO", "");
        this.setProperty("CLASSIFICA", "");

	}

    @Override
    protected String getComputedName() {
        return getDescrizione();
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
        //TODO: Implementare
        return null;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBusinessType() {

        String businessType = this.getProperty("BUSINESS_TYPE");
        if (!Strings.isNullOrEmpty(businessType)) {
            return businessType;
        }

        return getType();
    }
    @Override
    public String getVersionID() {
        //TODO: Da implementare
        return null;
    }

    @Override
    public String getAbstract() {
        //TODO: E' giusto?
        return getDescrizione();
    }
    @Override
    public String getMimeTypeCSS(){
    	return "glyphicon glyphicon-folder-open";
    }
    
    @Override
    public String getWebURL() {
        return  webURLS+
        		TYPE+
        		"&COD_ENTE="+getEnte()+
        		"&COD_AOO="+getAoo()+
        		"&ANNO_FASCICOLO="+getAnno()+
        		"&PROGR_FASCICOLO="+getProgressivo()+
        		"&CLASSIFICA="+getClassifica()
        		;
    }
    
}
