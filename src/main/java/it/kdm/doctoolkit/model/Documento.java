package it.kdm.doctoolkit.model;


import com.google.common.base.Strings;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.doctoolkit.model.path.ICIFSObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.InputSource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Classe bean di nterscambio Documento
 */
@SuppressWarnings("unused")
public class Documento extends ICIFSObject {

    public static final String TYPE = "documento";

    enum setFileTypeEnum { None, Stream, filePath }

	private String filePath = "";
	private String fileName = "";
	private InputStream file = null;

	private Boolean estraiFirmatari = false;

	public Boolean getEstraiFirmatari() {
		return estraiFirmatari;
	}
	public void setEstraiFirmatari(Boolean value) {
		estraiFirmatari = value;
	}

    public String getDirection() {
		return this.getProperty("TIPO_PROTOCOLLAZIONE");
	}
	public void setDirection(String direction) {
		this.setProperty("TIPO_PROTOCOLLAZIONE",direction);
	}
	public void setPublicVersion(String publicVersion) {
		this.setProperty("PUBLIC_VERSION",publicVersion);
	}
	public String getPublicVersion() {
		return this.getProperty("PUBLIC_VERSION");
	}

    private String dateTimeString(DateTime dateTime) {
        return ISODateTimeFormat.dateTime().print(dateTime);
    }

	public String getDescriptionTitolario() {
		return getDescription(DescriptionType.TITOLARIO);
	}

	public String getDescriptionFascicolo() {
		return getDescription(DescriptionType.FASCICOLO);
	}

	public String getDocumentVersion() {
		return this.getProperty("DOC_VERSION");
	}

    @Override
    public long getSize() {
        return getContentSize();
    }

    @Override
    public String getID() {
        return getDocNum();
    }

    @Override
    public String getType() {
        return getDocType();
    }

    @Override
    public String getVersionID() {

//        return properties.get("VERSION_ID");

		String version;

		if (properties.containsKey("VERSION_ID")) {
			version = properties.get("VERSION_ID");
		} else {
			version = properties.get(getID() + "~content_modified_on");
		}

		return version;
    }


	public Date getPropertyDate( String PropertyName, Locale locale) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale);
		String strout = properties.get(PropertyName);

		if(strout==null)
			return null;

		Date date = new Date();

			try {

				if ( strout!=null )
					date = sdf.parse(strout);
				else
					date = sdf.parse("1970-01-01");

			}
			catch(Exception exc){
				return null;
			}

		return date;
	}
    @Override
    public String getAbstract() {
        return getProperty("ABSTRACT");
    }

    public void setCreationDate(DateTime creationDate) {
		this.setProperty("CREATION_DATE", dateTimeString(creationDate));
	}

    @Override
    protected String getComputedName() {
        return getDocName();
    }

    @Override
    public String getExtension() {
        //TODO: Verify
        String name = getDocName();
        int idx = name.lastIndexOf('.');
        if (idx != -1) {
            return name.substring(idx + 1);
        }

        return "";
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    public void setModifiedDate(DateTime creationDate) {
        this.setProperty("modified_on", dateTimeString(creationDate));
    }
	
    public String getStatoArchivistico() {
		return this.getProperty("STATO_ARCHIVISTICO");
	}
	public void setStatoArchivistico(String statoArchivistico) {
		this.setProperty("STATO_ARCHIVISTICO",statoArchivistico);
	}
	
    public String getDocNum() {
		return this.getProperty("DOCNUM");
	}
	public void setDocNum(String docNum) {
		this.setProperty("DOCNUM",docNum);
	}

	public String getDocName() {
		return this.getProperty("DOCNAME");
	}
	public void setDocName(String docName) {
		this.setProperty("DOCNAME", docName);
	}

    public String getEnte() {
		return this.getProperty("COD_ENTE");
	}
	public void setEnte(String ente) {
		this.setProperty("COD_ENTE", ente); 
	}

    public String getAoo() {
		return this.getProperty("COD_AOO");
	}
	public void setAoo(String aoo) {
		this.setProperty("COD_AOO", aoo);
	}

	public String getDescrizione() {
		return this.getProperty("ABSTRACT"); 
	}
	public void setDescrizione(String descrizione) {
		this.setProperty("ABSTRACT", descrizione);
	}

    public String getDocType() {
		return this.getProperty("TYPE_ID");
	}
	public void setDocType(String docType) {
		this.setProperty("TYPE_ID", docType);
	}

    public String getTipoComponente() {
		return this.getProperty("TIPO_COMPONENTE");
	}
	public void setTipoComponente(String tipoComponente) {
		this.setProperty("TIPO_COMPONENTE", tipoComponente);
	}

    public String getClassifica() {
        return this.getProperty("CLASSIFICA");
    }

    public void setClassifica(String classifica) {
        this.setProperty("CLASSIFICA", classifica);
    }

    public String getProgressivo() {
        return this.getProperty("PROGR_FASCICOLO");
    }

    public void setProgressivo(String progressivo) {
        this.setProperty("PROGR_FASCICOLO", progressivo);
    }

    public String getAnno() {
        return this.getProperty("ANNO_FASCICOLO");
    }

    public void setAnno(String anno) {
        this.setProperty("ANNO_FASCICOLO", anno);
    }

	public int getLivelloRiservatezza() {
		return Integer.parseInt(this.getProperty("RISERVATEZZA"));
	}
	public void setLivelloRiservatezza(int livello) {
		this.setProperty("RISERVATEZZA",String.valueOf(livello));
	}

    public long getContentSize() {
        String length;
        if (properties.containsKey("content_size")) {
            length = properties.get("content_size");
        } else {
            length = properties.get("CONTENT_SIZE");
        }

        try {
            if (!Strings.isNullOrEmpty(length)) {
                long l = Long.parseLong(length);
                if (l == 1) {
                    return 0;
                }

                return l;
            }
        } catch (NumberFormatException e) {
            return 0;
        }

        return 0;
    }

    /*
    @Override
   	public String getFEName() {
   		return this.getDocName();
   	}
   	*/
   	@Override
   	public String getBusinessType() {
   		return getDocType();
   	}
   	
   	@Override
   	public String getFEId() {
   		return this.getDocNum();
   	}
   	
   	@Override
   	public String getFEAuthor() {
   		return this.getProperty("AUTHOR_ID");
   	}
   	
   	@Override
   	public String getFEDate() {
   		return this.getProperty("MODIFIED");
   	}

    @Override
    public HashMap<String,Object> toFlowObject() throws Exception{
        //converte i mittenti e destinatari
        List<Corrispondente> mittenti = new ArrayList<Corrispondente>();
        List<Corrispondente> destinatari = new ArrayList<Corrispondente>();
        List<Corrispondente> firmatari = new ArrayList<Corrispondente>();
        List<Vistatore> vistatore = new ArrayList<Vistatore>();
        try {
            mittenti = this.getMittenti();
            destinatari = this.getDestinatari();
            firmatari = this.getFirmatari();
            vistatore = this.getVistatore();
        }
        catch(Exception e) {
            throw e;
        }

        //pulisce e filtra le fields
        HashMap<String,Object> flowObj = super.toFlowObject();
        flowObj.put("VISTO", documentToRuntimeVistatore(vistatore));
        flowObj.put("MITTENTI", documentToRuntime(mittenti));
        flowObj.put("DESTINATARI",documentToRuntime(destinatari));
        flowObj.put("FIRMATARIO",documentToRuntime(firmatari));

        return flowObj;
    }

    @Override
    public void fromFlowObject(HashMap<String,Object> flowObject) throws Exception {
        List<Corrispondente> mittenti = runtimeToDocument((List<HashMap>)flowObject.get("MITTENTI"));
        List<Corrispondente> destinatari = runtimeToDocument((List<HashMap>)flowObject.get("DESTINATARI"));
        List<Corrispondente> firmatari = runtimeToDocument((List<HashMap>)flowObject.get("FIRMATARIO"));
        List<Vistatore> vistatori = runtimeToDocumentVistatore((List<HashMap>)flowObject.get("VISTO"));
        
        flowObject.remove("MITTENTI");
        flowObject.remove("DESTINATARI");
        flowObject.remove("FIRMATARIO");
        flowObject.remove("VISTO");

        super.fromFlowObject(flowObject);

        setFirmatari(firmatari);
        setMittenti(mittenti);
        setDestinatari(destinatari);
        setVistatore(vistatori);

    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Vistatore> getVistatore() throws DocumentException{
		
		List<Vistatore> result = new ArrayList<Vistatore>();
		Object objVisto = this.properties.get("VISTO");
		if(objVisto instanceof ArrayList<?>){
			List<HashMap<String, String>> list = (List<HashMap<String, String>>)objVisto;
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hash = list.get(i);
					Vistatore visto = new Vistatore();
					visto.properties = hash;
					result.add(visto);
			}
		}else{
			String xmlStr = this.getProperty("VISTO");
			if(xmlStr != null && xmlStr.length() != 0 ){
				xmlStr = xmlStr.replaceAll("<SKIP>", "");
				xmlStr = xmlStr.replaceAll("</SKIP>", "");
				SAXReader reader = new SAXReader();
				Document document = reader.read(new InputSource(new StringReader(xmlStr)));

				//RootElement
				Element rootElement = document.getRootElement();
				
				for (Iterator i = rootElement.elementIterator("Persona"); i.hasNext();) {
					
					Element vistElem = (Element)i.next();	
					//Vistatore
//					Element perFisElem = firmElem.element("Persona");
					if(vistElem.getName()!=null && vistElem.getName().equals("Persona")){
						Vistatore vistator = new Vistatore();
						String vistatoreXML =  vistElem.asXML();
						vistator.parse(vistatoreXML);
						result.add(vistator);
					}
				
				}
			}
		}
		return result;
	}
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Corrispondente> getFirmatari() throws DocumentException{
		
		List<Corrispondente> result = new ArrayList<Corrispondente>();
		Object objFirm = this.properties.get("FIRMATARIO");
		if(objFirm instanceof ArrayList<?>){
			List<HashMap<String, String>> list = (List<HashMap<String, String>>)objFirm;
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hash = list.get(i);
				if("PersonaGiuridica".equals(hash.get("@type"))){
					PersonaGiuridica firmatario = new PersonaGiuridica();
					firmatario.properties = hash;
					result.add(firmatario);
				}else if("PersonaFisica".equals(hash.get("@type"))){
					PersonaFisica firmatario = new PersonaFisica();
					firmatario.properties = hash;
					result.add(firmatario);
				}else if("Amministrazione".equals(hash.get("@type"))){
					Amministrazione firmatario = new Amministrazione();
					firmatario.properties = hash;
					result.add(firmatario);
				}else if("UnitaOrganizzativa".equals(hash.get("@type"))){
					UO mittente = new UO();
					mittente.properties = hash;
					result.add(mittente);
				}
			}
		}else{
			String xmlStr = this.getProperty("FIRMATARIO");
			if(xmlStr != null && xmlStr.length() != 0 ){
				xmlStr = xmlStr.replaceAll("<SKIP>", "");
				xmlStr = xmlStr.replaceAll("</SKIP>", "");
				SAXReader reader = new SAXReader();
				Document document = reader.read(new InputSource(new StringReader(xmlStr)));

				//RootElement
				Element rootElement = document.getRootElement();
				
				for (Iterator i = rootElement.elementIterator("Persona"); i.hasNext();) {
					//firmatario
					Element firmElem = (Element)i.next();	
					
					//Amministrazione
//					Element ammElem = firmElem.element("Amministrazione");
					if(firmElem.getName()!=null && firmElem.getName().equals("Amministrazione")){
						Amministrazione amministrazione = new Amministrazione();
						String amministrazioneXML =  firmElem.asXML();
						amministrazione.parse(amministrazioneXML);

						if (firmElem.element("AOO")!=null) {
							Element aooElem = firmElem.element("AOO");
							String aooXML = aooElem.asXML();
							AOO aoo = new AOO();
							aoo.parse(aooXML);
							amministrazione.setAOO(aoo);
						}

						result.add(amministrazione);
					}
					//PersonaFisica
//					Element perFisElem = firmElem.element("Persona");
					if(firmElem.getName()!=null && firmElem.getName().equals("Persona")){
						PersonaFisica personaFisica = new PersonaFisica();
						String personaFisicaXML =  firmElem.asXML();
						personaFisica.parse(personaFisicaXML);
						result.add(personaFisica);
					}
					//PersonaGiuridica
//					Element perGiuElem = firmElem.element("PersonaGiuridica");
					if(firmElem.getName()!=null && firmElem.getName().equals("PersonaGiuridica")){
						PersonaGiuridica personaGiuridica = new PersonaGiuridica();
						String personaGiuridicaXML =  firmElem.asXML();
						personaGiuridica.parse(personaGiuridicaXML);
						result.add(personaGiuridica);
					}
					//UO
					Element uoElem = firmElem.element("UnitaOrganizzativa");
					if(uoElem != null){
						UO uo = new UO();
						String uoXML =  uoElem.asXML();
						uo.parse(uoXML);
						result.add(uo);
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Corrispondente> getDestinatari() throws DocumentException{
		
		List<Corrispondente> result = new ArrayList<Corrispondente>();
		Object objMitt = this.properties.get("DESTINATARI");
		if(objMitt instanceof ArrayList<?>){
			List<HashMap<String, String>> list = (List<HashMap<String, String>>)objMitt;
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hash = list.get(i);
				if("PersonaGiuridica".equals(hash.get("@type"))){
					PersonaGiuridica mittente = new PersonaGiuridica();
					mittente.properties = hash;
					result.add(mittente);
				}else if("PersonaFisica".equals(hash.get("@type"))){
					PersonaFisica mittente = new PersonaFisica();
					mittente.properties = hash;
					result.add(mittente);
				}else if("Amministrazione".equals(hash.get("@type"))){
					Amministrazione mittente = new Amministrazione();
					mittente.properties = hash;
					result.add(mittente);
				}else if("UnitaOrganizzativa".equals(hash.get("@type"))){
					UO mittente = new UO();
					mittente.properties = hash;
					result.add(mittente);
				}
			}
		}else{
			String xmlStr = this.getProperty("DESTINATARI");
			if(xmlStr != null && xmlStr.length() != 0 ){
				xmlStr = xmlStr.replaceAll("<SKIP>", "");
				xmlStr = xmlStr.replaceAll("</SKIP>", "");
				SAXReader reader = new SAXReader();
				Document document = reader.read(new InputSource(new StringReader(xmlStr)));

				//RootElement
				Element rootElement = document.getRootElement();
				
				for (Iterator i = rootElement.elementIterator("Destinatario"); i.hasNext();) {
					//Mittente
					Element mittElem = (Element)i.next();	
					
					//Amministrazione
					Element ammElem = mittElem.element("Amministrazione");
					if(ammElem != null){
						Amministrazione amministrazione = new Amministrazione();
						String amministrazioneXML =  ammElem.asXML();
						amministrazione.parse(amministrazioneXML);

						if (mittElem.element("AOO")!=null) {
							Element aooElem = mittElem.element("AOO");
							String aooXML = aooElem.asXML();
							AOO aoo = new AOO();
							aoo.parse(aooXML);
							amministrazione.setAOO(aoo);
						}

						result.add(amministrazione);
					}
					//PersonaFisica
					Element perFisElem = mittElem.element("Persona");
					if(perFisElem != null){
						PersonaFisica personaFisica = new PersonaFisica();
						String personaFisicaXML =  perFisElem.asXML();
						personaFisica.parse(personaFisicaXML);
						result.add(personaFisica);
					}
					//PersonaGiuridica
					Element perGiuElem = mittElem.element("PersonaGiuridica");
					if(perGiuElem != null){
						PersonaGiuridica personaGiuridica = new PersonaGiuridica();
						String personaGiuridicaXML =  perGiuElem.asXML();
						personaGiuridica.parse(personaGiuridicaXML);
						result.add(personaGiuridica);
					}
					//UO
					Element uoElem = mittElem.element("UnitaOrganizzativa");
					if(uoElem != null){
						UO uo = new UO();
						String uoXML =  uoElem.asXML();
						uo.parse(uoXML);
						result.add(uo);
					}
				}
			}
		}
		return result;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Corrispondente> getMittenti() throws DocumentException{
		
		List<Corrispondente> result = new ArrayList<Corrispondente>();
		Object objMitt = this.properties.get("MITTENTI");
		if(objMitt instanceof ArrayList<?>){
			List<HashMap<String, String>> list = (List<HashMap<String, String>>)objMitt;
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> hash = list.get(i);
				if("PersonaGiuridica".equals(hash.get("@type"))){
					PersonaGiuridica mittente = new PersonaGiuridica();
					mittente.properties = hash;
					result.add(mittente);
				}else if("PersonaFisica".equals(hash.get("@type"))){
					PersonaFisica mittente = new PersonaFisica();
					mittente.properties = hash;
					result.add(mittente);
				}else if("Amministrazione".equals(hash.get("@type"))){
					Amministrazione mittente = new Amministrazione();
					mittente.properties = hash;
					result.add(mittente);
				}else if("UnitaOrganizzativa".equals(hash.get("@type"))){
					UO mittente = new UO();
					mittente.properties = hash;
					result.add(mittente);
				}
			}
		}else{
			String xmlStr = this.getProperty("MITTENTI");
			if(xmlStr != null && xmlStr.length() != 0 ){
				xmlStr = xmlStr.replaceAll("<SKIP>", "");
				xmlStr = xmlStr.replaceAll("</SKIP>", "");
				SAXReader reader = new SAXReader();
				Document document = reader.read(new InputSource(new StringReader(xmlStr)));

				//RootElement
				Element rootElement = document.getRootElement();
				
				for (Iterator i = rootElement.elementIterator("Mittente"); i.hasNext();) {
					//Mittente
					Element mittElem = (Element)i.next();	
					
					//Amministrazione
					Element ammElem = mittElem.element("Amministrazione");
					if(ammElem != null){
						Amministrazione amministrazione = new Amministrazione();
						String amministrazioneXML =  ammElem.asXML();
						amministrazione.parse(amministrazioneXML);

						if (mittElem.element("AOO")!=null) {
							Element aooElem = mittElem.element("AOO");
							String aooXML = aooElem.asXML();
							AOO aoo = new AOO();
							aoo.parse(aooXML);
							amministrazione.setAOO(aoo);
						}

						result.add(amministrazione);
					}
					//PersonaFisica
					Element perFisElem = mittElem.element("Persona");
					if(perFisElem != null){
						PersonaFisica personaFisica = new PersonaFisica();
						String personaFisicaXML =  perFisElem.asXML();
						personaFisica.parse(personaFisicaXML);
						result.add(personaFisica);
					}
					//PersonaGiuridica
					Element perGiuElem = mittElem.element("PersonaGiuridica");
					if(perGiuElem != null){
						PersonaGiuridica personaGiuridica = new PersonaGiuridica();
						String personaGiuridicaXML =  perGiuElem.asXML();
						personaGiuridica.parse(personaGiuridicaXML);
						result.add(personaGiuridica);
					}
					//UO
					Element uoElem = mittElem.element("UnitaOrganizzativa");
					if(uoElem != null){
						UO uo = new UO();
						String uoXML =  uoElem.asXML();
						uo.parse(uoXML);
						result.add(uo);
					}
				}
			}
		}
		return result;
	}
	public void setMittenti(List<Corrispondente> listMittenti) {
		
		Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("Mittenti");
        Element Mittente = null;
        if (listMittenti != null) {
			for (Corrispondente c : listMittenti) {
				Mittente = root.addElement("Mittente");
				Mittente.add(c.getXmlElement());
			}
		}
        String xmlStr = xml.asXML();
		xmlStr = xmlStr.replaceAll("(<SKIP>|</SKIP>)","");
        this.setProperty("MITTENTI", xmlStr);
	}
	public void setDestinatari(List<Corrispondente> listDestinatari) {
		
		Document xml = DocumentHelper.createDocument();
        Element root = xml.addElement("Destinatari");
        Element Mittente = null;
        if (listDestinatari != null) {
			for (Corrispondente c : listDestinatari) {
				Mittente = root.addElement("Destinatario");
				Mittente.add(c.getXmlElement());
			}
		}
        String xmlStr = xml.asXML();
		xmlStr = xmlStr.replaceAll("(<SKIP>|</SKIP>)","");
		this.setProperty("DESTINATARI", xmlStr);
	}
	
	public void setFirmatari(List<Corrispondente> listFirmatari) {
		
		Document xml = DocumentHelper.createDocument();
        Element firmatarioRoot = xml.addElement("Firmatario");
        if (listFirmatari != null) {
			for (Corrispondente c : listFirmatari) {
				firmatarioRoot.add(c.getXmlElement());
			}
		}
        String xmlStr = xml.asXML();
		xmlStr = xmlStr.replaceAll("(<SKIP>|</SKIP>)","");
        this.setProperty("FIRMATARIO", xmlStr);
	}
	
	
	
	public void setVistatore(List<Vistatore> visto) {
		 String xmlStr = null;
		Document xml = DocumentHelper.createDocument();
       
        if (visto != null && visto.size()>0) {
        	 Element vistoRoot = xml.addElement("Vistatori");
			for (Corrispondente c : visto) {
				vistoRoot.add(c.getXmlElement());
			}
			xmlStr = xml.asXML();
			xmlStr = xmlStr.replaceAll("(<SKIP>|</SKIP>)","");
		}
        this.setProperty("VISTO", xmlStr);
	}

	public void setFile(InputStream file, String fileName)
	{
		this.file = file;
		this.fileName = fileName;
	}

	public void setFile(String filePath)
	{
		this.filePath = filePath;
	}

	/**
	 * Restituisce il file
	 * @return InputStream
	 * @throws DocerApiException 
	 * @
	 * @throws IOException 
	 */
	public InputStream getFileStream() throws DocerApiException 
	{
		if (this.file != null)
			return this.file;

		if (StringUtils.isNotBlank(this.filePath))
		{
			File doc=new File(this.filePath);
			try {
				this.file = new ByteArrayInputStream(FileUtils.readFileToByteArray(doc));
				
			} catch (IOException e) {
				
				throw new DocerApiException("Errore in apertura file:" + e.getMessage(), 520);
			}

			return this.file;
		}

		throw new DocerApiException("File documento non impostato.", 520);
	}

    private List<Corrispondente> runtimeToDocument(List<HashMap> corrispondenti)throws DocumentException{

        List<Corrispondente> mittentiLista = new ArrayList<Corrispondente>();
        List <HashMap> mitt = corrispondenti;
        if(mitt!=null && mitt.size()>0){
            for (HashMap temp:mitt){
                if(temp.get("@type").equals("PersonaFisica")){
                    PersonaFisica c = new PersonaFisica();
                    c.properties = (HashMap<String,String>)temp.clone();
                    mittentiLista.add(c);
                }else if(temp.get("@type").equals("Amministrazione")){
                    Amministrazione c = new Amministrazione();
                    c.properties = (HashMap<String,String>)temp.clone();
                    mittentiLista.add(c);
                }else if(temp.get("@type").equals("PersonaGiuridica")){
                    PersonaGiuridica c = new PersonaGiuridica();
                    c.properties = (HashMap<String,String>)temp.clone();
                    mittentiLista.add(c);
                }
            }

        }

        return mittentiLista;
    }
    
    
    private List<Vistatore> runtimeToDocumentVistatore(List<HashMap> vistatore)throws DocumentException{

        List<Vistatore> vistatoreLista = new ArrayList<Vistatore>();
        List <HashMap> mitt = vistatore;
        if(mitt!=null && mitt.size()>0){
            for (HashMap temp:mitt){
                
                    Vistatore c = new Vistatore();
                    c.properties = (HashMap<String,String>)temp.clone();
                    vistatoreLista.add(c);                
            }
        }
        return vistatoreLista;
    }
    
    
    private List<HashMap<String, Object>> documentToRuntimeVistatore(List<Vistatore> listVistatore)throws DocumentException{
        List<HashMap<String, Object>> docVistatore = new ArrayList<HashMap<String, Object>>();
        for (Vistatore c : listVistatore) {
                docVistatore.add(new HashMap< String , Object>(c.properties));
        }
        return docVistatore;
    }


    private List<HashMap<String, Object>> documentToRuntime(List<Corrispondente> listCorrispondenti)throws DocumentException{


        List<HashMap<String, Object>> docMittenti = new ArrayList<HashMap<String, Object>>();

        for (Corrispondente c : listCorrispondenti) {
            if(c instanceof Amministrazione){
                HashMap<String, Object> hashAmm = new HashMap< String , Object>(c.properties);
                HashMap<String, Object> aoo = new HashMap<String,Object>(((Amministrazione) c).getAOO().properties);
                hashAmm.put("AOO", aoo);
                hashAmm.put("UO", ((Amministrazione) c).getUnitaOrganizzativa().properties);
                hashAmm.put("@type" , "Amministrazione");
                docMittenti.add(new HashMap< String , Object>(hashAmm));
            }else if(c instanceof PersonaFisica){
                c.properties.put("@type" , "PersonaFisica");
                docMittenti.add(new HashMap< String , Object>(c.properties));
            }else if(c instanceof PersonaGiuridica){
                c.properties.put("@type" , "PersonaGiuridica");
                docMittenti.add(new HashMap< String , Object>(c.properties));
            }

        }
        return docMittenti;
    }

//    public void setFullName(String name) {
//        setDocName(name);
//        super.setName(name);
//    }

    public String getParentFolderId() {
        return properties.get("PARENT_FOLDER_ID");
    }

    @Override
	protected void initProperties() {
		this.setProperty("DOCNAME", "");
		this.setProperty("COD_ENTE", "");
		this.setProperty("COD_AOO", "");
		this.setProperty("ABSTRACT", "");
		this.setProperty("TYPE_ID", "");
		this.setProperty("TIPO_COMPONENTE", "");
//		TODO campi direction,statoArchivistico
//		this.setProperty("DOCUMENTDIRECTION", "internal");
//		this.setProperty("STATO_ARCHIVISTICO", "");
		

	}

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Documento))return false;
        Documento otherMyClass = (Documento)other;
        if(this.getDocNum().equals(otherMyClass.getDocNum())) return true;
        else return false;
    }
    
    @Override
    public String getWebURL() {
        return webURLS+
        		TYPE+
        		"&DOCNUM="+getDocNum()
        		;
    }


	public String getMailField(String fieldName, Boolean isAddressList) {

		String val = (this.getProperty(fieldName) != null
				&& !"".equals(this.getProperty(fieldName))) ?
				this.getProperty(fieldName) : "";

		if (isAddressList) {
			val = val.replace("[", "");
			val = val.replace("]", "");
			val = val.replace("\"", "");
//			val = val.replace("<", "&lt;");
//			val = val.replace(">", "&gt;");
		}

		return val;
	}
    
}
