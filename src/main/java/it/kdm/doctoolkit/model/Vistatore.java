package it.kdm.doctoolkit.model;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Vistatore extends Corrispondente {

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

	public String getCognome() {
		return getProperty("cognome");
	}
	public void setCognome(String cognome) {
		setProperty("cognome", cognome);
	}

	public String getDataVisto(){
		return getProperty("dataVisto");
	}

	public Date getDataVisto( Locale locale) {

		String strout = getProperty("dataVisto");

		if(strout==null)
			return null;

		boolean localized = (strout.length() >= 24);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale);

		if(!localized) {
//			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

			String padding = "0000-00-00T00:00:00.000Z";

			// se la data non contiene la timezone nel formato ISO si considera ora locale aggiungendo la parte di formato mancante
			if (strout.length() < padding.length()) {

				int offset = (TimeZone.getDefault().getOffset(new Date().getTime()) / 1000 / 60 / 60);

				String zone = String.format("%02d", offset);
				String sign = offset > 0 ? "+" : "-";
				padding = "0000-00-00T00:00:00.000" + sign + zone + ":00";

				strout = StringUtils.rightPad(strout, padding.length(), StringUtils.substring(padding, strout.length()));
			}
		}


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

	public void setDataVisto(String dataVisto){
		setProperty("dataVisto",dataVisto);
	}
	public void setVisto(String visto){
		setProperty("visto",visto);
	}
	public String getVisto(){
		return getProperty("visto");
	}

	@Override
	@JsonIgnore
	public Element getXmlElement() {
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("Persona");
		root.addAttribute("id",getId());
		Element Nome = root.addElement("Nome");
		Nome.addText(getNome());
		Element Cognome = root.addElement("Cognome");
		Cognome.addText(getCognome());
		Element dataVisto = root.addElement("DataVisto");
		if(getDataVisto()!=null){
			dataVisto.addText(getDataVisto());
		}
		
		Element visto = root.addElement("Visto");
		visto.addText(getVisto());
		visto.addAttribute("id",getId());
		return (Element) root.clone();
	}
	@Override
	public void parse(String xmlStr)throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(new InputSource(new StringReader(xmlStr)));
		Element rootElement = document.getRootElement();
		if(rootElement.attribute("id")!=null)
			this.setId(rootElement.attribute("id").getValue());
		if(rootElement.element("Nome")!=null)
			this.setNome(rootElement.element("Nome").getStringValue());
		if(rootElement.element("Cognome")!=null)
			this.setCognome(rootElement.element("Cognome").getStringValue());
		if(rootElement.element("DataVisto")!=null)
			this.setDataVisto(rootElement.element("DataVisto").getStringValue());
		if(rootElement.element("Visto")!=null)
			this.setVisto(rootElement.element("Visto").getStringValue());
	}

	@Override
	protected void initProperties() {
		this.setProperty("id", "");
		this.setProperty("nome", "");
		this.setProperty("cognome", "");
		this.setProperty("dataVisto", "");
		this.setProperty("Visto", "");
	}

}
