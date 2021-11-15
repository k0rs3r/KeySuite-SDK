package it.kdm.orchestratore.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseCorrispondente {

    public static String AMMINISTRAZIONE="Amministrazione";

    public static String PERSONAFISICA="PersonaFisica";

    public static String PERSONAGIURIDICA="PersonaGiuridica";


    public static String toXML(List<Map<String,Object>> corrispondente,String tipoCorrispondente) throws ParserConfigurationException, TransformerException {
        String xmlDocer=null;



for(Map tmp:corrispondente) {

    if (AMMINISTRAZIONE.equalsIgnoreCase(tipoCorrispondente)) {


        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element mittenti = doc.createElement("Mittenti");
        doc.appendChild(mittenti);

        // staff elements
        Element mittente = doc.createElement("Mittente");
        mittenti.appendChild(mittente);

        Element amministrazione = doc.createElement("Amministrazione");
        mittente.appendChild(amministrazione);

        Element denominazione = doc.createElement("Denominazione");
        amministrazione.appendChild(denominazione);
        //denominazione.setNodeValue((String) tmp.get("DES_ENTE"));
        denominazione.appendChild((doc.createTextNode((String) tmp.get("DES_ENTE"))));

        Element codiceAmministrazione = doc.createElement("CodiceAmministrazione");
        amministrazione.appendChild(codiceAmministrazione);
        codiceAmministrazione.setNodeValue((String) tmp.get("COD_ENTE"));

        codiceAmministrazione.appendChild((doc.createTextNode((String) tmp.get("COD_ENTE"))));

        Element indirizzoTelematico = doc.createElement("IndirizzoTelematico");
        amministrazione.appendChild(indirizzoTelematico);
        //indirizzoTelematico.setNodeValue((String) tmp.get("IndirizzoTelematicoAMM"));
        if(tmp.containsKey("IndirizzoTelematicoAMM") && tmp.get("IndirizzoTelematicoAMM")!=null)
        indirizzoTelematico.appendChild((doc.createTextNode((String) tmp.get("IndirizzoTelematicoAMM"))));

        Attr note = doc.createAttribute("note");
        note.setValue("");
        indirizzoTelematico.setAttributeNode(note);


        Element unitaOrganizzativa = doc.createElement("UnitaOrganizzativa");
        amministrazione.appendChild(unitaOrganizzativa);

//inizio la fase di retrocompativbilità con il formato impostato sul save protocollo
        Element denominazioneUo = doc.createElement("Denominazione");
        unitaOrganizzativa.appendChild(denominazioneUo);
        Element identificativoUo = doc.createElement("Identificativo");
        unitaOrganizzativa.appendChild(identificativoUo);

        Element indirizzoPostale = doc.createElement("IndirizzoPostale");
        unitaOrganizzativa.appendChild(indirizzoPostale);
        Element denominazioneIndirizzoPostaleUO = doc.createElement("Denominazione");
        indirizzoPostale.appendChild(denominazioneIndirizzoPostaleUO);
        Element indirizzoTelematicoUo = doc.createElement("IndirizzoTelematico");
        unitaOrganizzativa.appendChild(indirizzoTelematicoUo);
        if(tmp.containsKey("UO")){
            Map<String,String> uo=(HashMap)tmp.get("UO");
            if(uo.containsKey("indirizzoPostale")){
                if(uo.containsKey("indirizzoPostale") && uo.get("indirizzoPostale")!=null) {
                    //indirizzoPostale.setNodeValue((String) uo.get("indirizzoPostale"));
                    indirizzoPostale.appendChild((doc.createTextNode((String) uo.get("indirizzoPostale"))));
                }
            }
            if(uo.containsKey("IndirizzoTelematico")){
                if(uo.containsKey("IndirizzoTelematico") && uo.get("IndirizzoTelematico")!=null) {
                    //indirizzoTelematicoUo.setNodeValue((String) uo.get("IndirizzoTelematico"));
                    indirizzoTelematicoUo.appendChild((doc.createTextNode((String) uo.get("IndirizzoTelematico"))));
                }
            }
            if(uo.containsKey("COD_UO")){
                if(uo.containsKey("COD_UO") && uo.get("COD_UO")!=null) {
                    //identificativoUo.setNodeValue((String) tmp.get("COD_UO"));
                    identificativoUo.appendChild((doc.createTextNode((String) uo.get("COD_UO"))));
                }
            }
            if(uo.containsKey("DES_UO")){
                if(uo.containsKey("DES_UO") && uo.get("DES_UO")!=null) {
                    //denominazioneUo.setNodeValue((String) uo.get("DES_UO"));
                    denominazioneUo.appendChild((doc.createTextNode((String) uo.get("DES_UO"))));
                }
            }
        }else{
            if(tmp.containsKey("DenominazioneUO") && tmp.get("DenominazioneUO")!=null) {
                // denominazioneUo.setNodeValue((String) tmp.get("DenominazioneUO"));
                denominazioneUo.appendChild((doc.createTextNode((String) tmp.get("DenominazioneUO"))));
            }
            if(tmp.containsKey("identificativoUO") && tmp.get("identificativoUO")!=null) {
                //identificativoUo.setNodeValue((String) tmp.get("identificativoUO"));
                identificativoUo.appendChild((doc.createTextNode((String) tmp.get("identificativoUO"))));
            }
            if(tmp.containsKey("denominazioneIndirizzoPostaleUO") && tmp.get("denominazioneIndirizzoPostaleUO")!=null) {
                //indirizzoPostale.setNodeValue((String) tmp.get("denominazioneIndirizzoPostaleUO"));
                 indirizzoPostale.appendChild((doc.createTextNode((String) tmp.get("denominazioneIndirizzoPostaleUO"))));
            }
            if(tmp.containsKey("indirizzoTelematicoUo") && tmp.get("indirizzoTelematicoUo")!=null) {
                //indirizzoTelematicoUo.setNodeValue((String) tmp.get("indirizzoTelematicoUo"));
                 indirizzoTelematicoUo.appendChild((doc.createTextNode((String) tmp.get("indirizzoTelematicoUo"))));
            }
        }


        Element invioPec = doc.createElement("InvioPEC");
        amministrazione.appendChild(invioPec);
        //invioPec.setNodeValue((String) tmp.get("inviopec"));
        if(tmp.containsKey("InvioPEC") && tmp.get("InvioPEC")!=null) {
            invioPec.appendChild((doc.createTextNode((String) tmp.get("InvioPEC"))));
        }



        Element aoo = doc.createElement("AOO");
        mittente.appendChild(aoo);
        Element denominazioneAoo = doc.createElement("Denominazione");
        aoo.appendChild(denominazioneAoo);
        Element codiceAOO = doc.createElement("CodiceAOO");
        aoo.appendChild(codiceAOO);
        Element indirizzoTelematicoAoo = doc.createElement("IndirizzoTelematico");
        aoo.appendChild(indirizzoTelematicoAoo);
        if(tmp.containsKey("AOO")){
            Map<String,String> aooHash=(HashMap)tmp.get("AOO");

            if(aooHash.containsKey("COD_AOO")  && aooHash.get("COD_AOO")!=null){
                //identificativoUo.setNodeValue((String) aooHash.get("COD_AOO"));
                identificativoUo.appendChild((doc.createTextNode((String) aooHash.get("COD_AOO"))));
            }
            if(aooHash.containsKey("DES_AOO")  && aooHash.get("DES_AOO")!=null){
                //denominazioneUo.setNodeValue((String) aooHash.get("DES_AOO"));
                denominazioneUo.appendChild((doc.createTextNode((String) aooHash.get("DES_AOO"))));
            }
            if(aooHash.containsKey("indirizzoTelematico")  && aooHash.get("indirizzoTelematico")!=null){
               // indirizzoTelematicoAoo.setNodeValue((String) aooHash.get("indirizzoTelematico"));
                indirizzoTelematicoAoo.appendChild((doc.createTextNode((String) aooHash.get("indirizzoTelematico"))));
            }
        }else{
            if(tmp.containsKey("DenominazioneAOO")  && tmp.get("DenominazioneAOO")!=null) {
                indirizzoTelematicoAoo.appendChild((doc.createTextNode((String) tmp.get("DenominazioneAOO"))));
            }
            if(tmp.containsKey("codiceAOO")  && tmp.get("codiceAOO")!=null) {
                indirizzoTelematicoAoo.appendChild((doc.createTextNode((String) tmp.get("codiceAOO"))));
            }
            if(tmp.containsKey("IndirizzoTelematicoAOO")  && tmp.get("IndirizzoTelematicoAOO")!=null) {
                indirizzoTelematicoAoo.appendChild((doc.createTextNode((String) tmp.get("IndirizzoTelematicoAOO"))));
            }
        }

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        xmlDocer= writer.toString();

    } else if (PERSONAFISICA.equalsIgnoreCase(tipoCorrispondente)) {

    } else if (PERSONAGIURIDICA.equalsIgnoreCase(tipoCorrispondente)) {

    } else {
        throw new RuntimeException("Valore tipoCorrispondente non supportato, valori ammissi" +
                ":Amministrazione,PersonaFisica,PersonaGiuridica; valore inserito" + tipoCorrispondente);
    }
}
        return xmlDocer;
    }

    public List<Map<String,Object>> parseToHashMap(String xml,String tipoCorrispondente){


        return null;
    }




}
