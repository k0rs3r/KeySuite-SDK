package it.kdm.doctoolkit.utils;

import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Element;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import java.io.ByteArrayInputStream;

import java.io.StringReader;
import java.io.ByteArrayOutputStream;

public class XMLHelper {

	@SuppressWarnings("unchecked")
	public static String getContent(Element element) {
		  StringBuilder builder = new StringBuilder();
		  for (Iterator<Element> i = element.elementIterator(); i.hasNext();) {
		    Element e = i.next();
		    builder.append(e.asXML());
		  }
		  return builder.toString();
	}


	public static  String transformXML(String xslFilename, String xmlString) throws Exception {
		String formattedOutput = "";
		try {

			InputStream in = XMLHelper.class.getResourceAsStream(xslFilename);

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(in));

			StreamSource xmlSource = new StreamSource(new StringReader(xmlString));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			transformer.transform(xmlSource, new StreamResult(baos));

			formattedOutput = baos.toString();

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return formattedOutput;
	}


	public static  String transformXML(InputStream in, String xmlString) throws Exception {
		String formattedOutput = "";
		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(in));

			StreamSource xmlSource = new StreamSource(new StringReader(xmlString));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			transformer.transform(xmlSource, new StreamResult(baos));

			formattedOutput = baos.toString();

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return formattedOutput;
	}


}
