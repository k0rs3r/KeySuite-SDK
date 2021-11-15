package it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;

public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public String marshal(BigDecimal v) throws Exception {
        synchronized(v) {
            v = v.setScale(2, BigDecimal.ROUND_HALF_UP);
            return v.toString();
        }
    }

    @Override
    public BigDecimal unmarshal(String v) throws Exception {
        synchronized (v) {
            return new BigDecimal(v.trim());
        }
    }

}
