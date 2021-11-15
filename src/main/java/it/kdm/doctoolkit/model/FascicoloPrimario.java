package it.kdm.doctoolkit.model;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FascicoloPrimario extends Fascicolo {
	
	@Override
	@JsonIgnore
	public Element getXmlElement()
	{
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("FascicoloPrimario");

		Element CodiceAmministrazione = root.addElement("CodiceAmministrazione");
		CodiceAmministrazione.addText(getEnte());

		Element CodiceAOO = root.addElement("CodiceAOO");
		CodiceAOO.addText(getAoo());

		Element Classifica = root.addElement("Classifica");
		Classifica.addText(getClassifica()==null?"":getClassifica());

		Element Anno = root.addElement("Anno");
		Anno.addText(getAnno()==null?"":getAnno());

		Element Progressivo = root.addElement("Progressivo");
		Progressivo.addText(getProgressivo()==null?"":getProgressivo());

		return (Element) root.clone();
	}

}
