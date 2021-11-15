package it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateAdapter extends XmlAdapter<String, XMLGregorianCalendar> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(XMLGregorianCalendar v) throws Exception {
        synchronized (dateFormat) {
            return dateFormat.format(v.toGregorianCalendar().getTime());
        }
    }

    @Override
    public XMLGregorianCalendar unmarshal(String v) throws Exception {
        synchronized (dateFormat) {
        		Date d = dateFormat.parse(v);
        		GregorianCalendar c = new GregorianCalendar();
        		c.setTime(d);
        		XMLGregorianCalendar xmlGregorianCalendar =null;;
        		try {
        			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        		} catch (DatatypeConfigurationException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		return xmlGregorianCalendar;
        }
    }

}