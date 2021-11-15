package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.WSRegistrazioneStub;
import it.kdm.doctoolkit.clients.WSRegistrazioneStub.RegistraById;
import it.kdm.doctoolkit.clients.WSRegistrazioneStub.RegistraByIdResponse;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.Corrispondente;
import it.kdm.doctoolkit.model.PersonaFisica;
import it.kdm.doctoolkit.model.Registro;
import it.kdm.doctoolkit.model.Smistamento;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ServizioRegistrazione {

//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setRegistrazioneEpr(url);
//    }

    public enum tipoRichestaEnum {Entrata, Uscita, Interna}

//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setProtocollazioneEpr(url);
//  }

    public static Registro registraUnitaDocumentaria(String token, String id_unita_documentaria, String oggetto_registrazione, String registro_particolare, boolean forza_registrazione, List<PersonaFisica> firmatari, String tipoFirma,List<Corrispondente> mittenti, List<Corrispondente> destinatari, Smistamento smistamento, tipoRichestaEnum  tipo_richiesta) throws DocerApiException
    {
        try
        {
            String tipoRichiestaStr = tipo_richiesta.name().substring(0, 1);

            Element xmlInput = buildXmlInput(oggetto_registrazione, firmatari, tipoFirma, forza_registrazione, mittenti, destinatari,smistamento,tipoRichiestaStr);

            String xmlOutputParsed = xmlInput.asXML().replace("<SKIP>","").replace("</SKIP>","");
            Long docid = Long.parseLong(id_unita_documentaria);

            String sede = ToolkitConnector.extractSedeFromToken(token);
            WSRegistrazioneStub regServ = ClientManager.INSTANCE.getRegistrazioneClient(sede);
            //regServ.RequireMtom = true;

            RegistraById reg = new RegistraById();
            RegistraByIdResponse resp = null;
            reg.setToken(token);
            reg.setDatiRegistrazione(xmlOutputParsed);
            reg.setDocumentoId(docid);
            reg.setRegistroId(registro_particolare);

            resp = regServ.registraById(reg);
            String xmlRet = resp.get_return();

            Document xml =DocumentHelper.parseText(xmlRet);

            Node xcode = xml.selectSingleNode("//esito/codice");
            String esitoRegistrazione = "";
            if(xcode!=null){
                esitoRegistrazione = xcode.getText();
            }

            if (!esitoRegistrazione.equalsIgnoreCase("0") && (!esitoRegistrazione.equalsIgnoreCase("1") && tipoFirma.equalsIgnoreCase("F") ))
            {
                Node xdesc = xml.selectSingleNode("//esito/descrizione");
                throw new DocerApiException(xdesc.getText(), 511);
            }

            Registro dreg = new Registro();
            dreg.loadFromXml(xml);

            return dreg;
        }
        catch (Exception e)
        {
            throw new DocerApiException(e);
        }
    }

    private static Element buildXmlInput(String oggetto_registrazione, List<PersonaFisica> firmatari2, String tipoFirma, boolean forza_registrazione, List<Corrispondente> mittenti2, List<Corrispondente> destinatari2, Smistamento smistamento, String tipo_richiesta) throws DocerApiException
    {
        Document xml = DocumentHelper.createDocument();
        xml.setXMLEncoding("UTF-8");


        Element root = xml.addElement("Segnatura");
        root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        Element Intestazione = root.addElement("Intestazione");

        Element Oggetto = Intestazione.addElement("Oggetto");
        Oggetto.setText(oggetto_registrazione);

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
        Element Mittenti = Intestazione.addElement("Mittenti");

        Element Destinatari = Intestazione.addElement("Destinatari");

        Element Mittente = null;

        if (mittenti2 != null) {
            for (Corrispondente c : mittenti2) {

                Mittente = Mittenti.addElement("Mittente");
                Mittente.add(c.getXmlElement());

            }
        }
        Element Destinatario = null;

        if (destinatari2 != null) {
            for (Corrispondente d : destinatari2) {
                Destinatario = Destinatari.addElement("Destinatario");
                Destinatario.add(d.getXmlElement());
            }
        }


        if (smistamento != null)
        {
            Element Smistamento = Intestazione.addElement("Smistamento");

            if (smistamento.getUnitaOrganizzativa() != null)
            {
                Smistamento.add(smistamento.getUnitaOrganizzativa().getXmlElement());
            }

            if (smistamento.getPersonaFisica() != null)
            {

                Smistamento.add(smistamento.getPersonaFisica().getXmlElement());
            }


        }


        return (Element) root.clone();
    }


    @SuppressWarnings("unchecked")
    protected Element testBuildXml(HashMap<String,Object> map) throws DocerApiException  {

        return buildXmlInput(
                (String)map.get("oggetto_registrazione"),
                (ArrayList<PersonaFisica>)map.get("firmatari"),
                (String)map.get("tipoFirma"),
                (Boolean)map.get("forza_registrazione"),
                (ArrayList<Corrispondente>)map.get("mittenti"),
                (ArrayList<Corrispondente>)map.get("destinatari"),
                (Smistamento)map.get("smistamento"),
                (String)map.get("tipo_richiesta"));

    }



}
