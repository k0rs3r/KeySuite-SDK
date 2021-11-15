package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.WSProtocollazioneStub;
import it.kdm.doctoolkit.clients.WSProtocollazioneStub.ProtocollaById;
import it.kdm.doctoolkit.clients.WSProtocollazioneStub.ProtocollaByIdResponse;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServizioProtocollazione {

    public enum tipoRichestaEunm {Entrata, Uscita, Interna}

//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setProtocollazioneEpr(url);
//    }

    public static Protocollo protocollaUnitaDocumentaria(String token, String id_unita_documentaria, String oggetto_protocollazione, tipoRichestaEunm tipo_richiesta, boolean forza_registrazione, ProtocolloMittente protocollo_mittente, List<PersonaFisica> firmatari, String tipoFirma, Fascicolo fascicolo_primario, List<Fascicolo> listSecondari, List<Corrispondente> listMittenti, List<Corrispondente> listDestinatari, Smistamento smistamento) throws DocerApiException {
        try {
            String tipoRichiestaStr = tipo_richiesta.name().substring(0, 1);

            Element xmlInput = buildXmlInput(oggetto_protocollazione, tipoRichiestaStr, firmatari, tipoFirma, forza_registrazione, protocollo_mittente, fascicolo_primario, listSecondari, listMittenti, listDestinatari, smistamento);

            String xmlOutputParsed = xmlInput.asXML().replace("<SKIP>", "").replace("</SKIP>", "");
            Long docid = Long.parseLong(id_unita_documentaria);

            String sede = ToolkitConnector.extractSedeFromToken(token);
            WSProtocollazioneStub protServ = ClientManager.INSTANCE.getProtocollazioneClient(sede);
            //protServ.RequireMtom = true;

            ProtocollaById prot = new ProtocollaById();
            ProtocollaByIdResponse resp = null;
            prot.setToken(token);
            prot.setDatiProtocollo(xmlOutputParsed);
            prot.setDocumentoId(docid);

            resp = protServ.protocollaById(prot);

            String xmlRet = resp.get_return();

            Document xml = DocumentHelper.parseText(xmlRet);

            Node xcode = xml.selectSingleNode("//esito/codice");
            if (!xcode.getText().equalsIgnoreCase("0")) {
                Node xdesc = xml.selectSingleNode("//esito/descrizione");
                throw new DocerApiException(xdesc.getText(), 511);
            }

            Protocollo dreg = new Protocollo();
            dreg.loadFromXml(xml);

            return dreg;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }


    private static Element buildXmlInput(String oggetto_protocollazione, String tipo_richiesta, List<PersonaFisica> firmatari2, String tipoFirma
            , boolean forza_registrazione, ProtocolloMittente protocollo_mittente, Fascicolo fascicolo_primario
            , List<Fascicolo> listSecondari, List<Corrispondente> listMittenti, List<Corrispondente> listDestinatari
            , Smistamento smistamento) throws DocerApiException {
        Document xml = DocumentHelper.createDocument();
        xml.setXMLEncoding("UTF-8");

        Element root = xml.addElement("Segnatura");

        root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        Element Intestazione = root.addElement("Intestazione");

        Element Oggetto = Intestazione.addElement("Oggetto");
        Oggetto.setText(oggetto_protocollazione);

        Element Flusso = Intestazione.addElement("Flusso");

        Element TipoRichiesta = Flusso.addElement("TipoRichiesta");
        TipoRichiesta.setText(tipo_richiesta);

        Element Firma = Flusso.addElement("Firma");
        Firma.setText(tipoFirma);

        Element ForzaRegistrazione = Flusso.addElement("ForzaRegistrazione");
        if (forza_registrazione)
            ForzaRegistrazione.setText("1");
        else
            ForzaRegistrazione.setText("0");

        Element Firmatari = Flusso.addElement("Firmatario");

        if (firmatari2 != null) {
            for (PersonaFisica p : firmatari2) {
                Firmatari.add(p.getXmlElement());
            }
        }

        if (protocollo_mittente != null) {
            Flusso.add(protocollo_mittente.getXmlElement());
        }

        Element Mittenti = Intestazione.addElement("Mittenti");

        Element Destinatari = Intestazione.addElement("Destinatari");

        Element Mittente = null;

        if (listMittenti != null) {
            for (Corrispondente c : listMittenti) {

                Mittente = Mittenti.addElement("Mittente");
                Mittente.add(c.getXmlElement());

            }
        }
        Element Destinatario = null;

        if (listDestinatari != null) {
            for (Corrispondente d : listDestinatari) {
                Destinatario = Destinatari.addElement("Destinatario");
                Destinatario.add(d.getXmlElement());
            }
        }

        if (fascicolo_primario != null) {
            Intestazione.add(fascicolo_primario.toFascicoloPrimario().getXmlElement());
        }

        Element fascicoliSecondari = null;

        if((listSecondari != null && listSecondari.size()>0 ) || !"E".equals(tipo_richiesta)  ) {
            fascicoliSecondari = Intestazione.addElement("FascicoliSecondari");
        }

        if (listSecondari != null && fascicoliSecondari!=null) {
            for (Fascicolo fs : listSecondari) {
                fascicoliSecondari.add(fs.toFascicoloSecondario().getXmlElement());

            }
        }


        //se si tratta di una protocollazione in entrata allora si aggiunge lo smistamento
        if(tipo_richiesta.equals("E")){

            Element Smistamento = Intestazione.addElement("Smistamento");

            if (smistamento != null) {
                if (smistamento.getUnitaOrganizzativa() != null) {
                    Smistamento.add(smistamento.getUnitaOrganizzativa().getXmlElement());

                }

                if (smistamento.getPersonaFisica() != null) {
                    Smistamento.add(smistamento.getPersonaFisica().getXmlElement());

                }

            }
        }

        return (Element) root.clone();
    }

    @SuppressWarnings("unchecked")
    protected Element testBuildXml(HashMap<String, Object> map) throws DocerApiException {


        return buildXmlInput(
                (String) map.get("oggetto_protocollazione"),
                (String) map.get("tipo_richiesta"),
                (ArrayList<PersonaFisica>) map.get("firmatari"),
                (String) map.get("tipoFirma"),
                (Boolean) map.get("forza_registrazione"),
                (ProtocolloMittente) map.get("protocollo_mittente"),
                (Fascicolo) map.get("fascicolo_primario"),
                (ArrayList<Fascicolo>) map.get("fascicoli_secondari"),
                (ArrayList<Corrispondente>) map.get("mittenti"),
                (ArrayList<Corrispondente>) map.get("destinatari"),
                (Smistamento) map.get("smistamento"));

    }


}
