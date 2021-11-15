package it.kdm.doctoolkit.model;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FascicoloSecondario extends Fascicolo {
	
	@Override
	@JsonIgnore
	public Element getXmlElement() 
	{
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("FascicoloSecondario");

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

}
