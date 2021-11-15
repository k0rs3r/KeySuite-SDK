package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.DocerServicesStub;
import it.kdm.doctoolkit.clients.WSInvioDocumentoStub;
import it.kdm.doctoolkit.clients.WSPECStub;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

public class InvioDocumento
{

	public static Map<String, String> invioDocumento (String token,  long documentId, PecObject datiPec )throws DocerApiException{
		try {
			Map<Corrispondente,Boolean> esitoReturn = new HashMap<Corrispondente,Boolean>();
			String sede = ToolkitConnector.extractSedeFromToken(token);
			WSInvioDocumentoStub wsInvioDocumentoStub = ClientManager.INSTANCE.getInvioDocumentoClient(sede);
			WSInvioDocumentoStub.InviaDocumento inviaDocumento = new WSInvioDocumentoStub.InviaDocumento();
			inviaDocumento.setToken(token);
			inviaDocumento.setDocumentoId(documentId);
			Map <String,String> returnMap = new HashMap<String,String>();

			Element elem = buildXmlInput(datiPec);


			inviaDocumento.setDatiPec(elem.asXML().replace("<SKIP>", "").replace("</SKIP>", ""));
			WSInvioDocumentoStub.InviaDocumentoResponse inviaDocumentoResponse = wsInvioDocumentoStub.inviaDocumento(inviaDocumento);

			WSInvioDocumentoStub.EsitoWS[] esito = inviaDocumentoResponse.get_return();
			for(WSInvioDocumentoStub.EsitoWS tmp:esito) {
				WSInvioDocumentoStub.KeyValuePair[] met = tmp.getDestinatario().getMetadata();

				for (WSInvioDocumentoStub.KeyValuePair tmpK : met) {
					String key = tmpK.getKey();
					String value = tmpK.getValue();
					returnMap.put(key,value);

				}
			}
			return returnMap;

		}catch(Exception e){
			throw new DocerApiException(e);
		}
		}
	private static Element buildXmlInput(PecObject pec) throws DocerApiException
	{

		Document xml = DocumentHelper.createDocument();
		xml.setXMLEncoding("UTF-8");

		Element root = xml.addElement("Segnatura");

		root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element intestazione = root.addElement("Intestazione");

		Element Oggetto = intestazione.addElement("Oggetto");
		Oggetto.setText(pec.getOggetto());

		Element flusso = intestazione.addElement("Flusso");


		Element modalitaInvio = flusso.addElement("ModalitaInvio");
		modalitaInvio.setText(pec.getModalitaInvio());

		Element tipoRichiesta = flusso.addElement("TipoRichiesta");
		tipoRichiesta.setText(pec.getTipoRichiesta());

		Element forzaInvio = flusso.addElement("ForzaInvio");
		forzaInvio.setText(pec.getForzaInvio());

		Element destinatari = intestazione.addElement("Destinatari");

		for (Corrispondente c : pec.getDestinatari()) {
			Element elem=null;

			if(c instanceof  Amministrazione){
				elem=getAmministrazione((Amministrazione)c);
			}else if (c instanceof  PersonaFisica){
				elem=getPersonaFisica((PersonaFisica)c);
			}else if(c instanceof PersonaGiuridica){
				elem=getPersonaGiuridica((PersonaGiuridica)c);
			}
			Element destinatario = destinatari.addElement("Destinatario");
			destinatario.add(elem);

		}


		Element documenti = root.addElement("Documenti");
		Element documento = documenti.addElement("Documento");
		documento.addAttribute("uri","string");
		documento.addAttribute("id","100");
		documento.addElement("Metadati");
		documento.addElement("Acl");
		documenti.addElement("Allegati");
		documenti.addElement("Annessi");
		documenti.addElement("Annotazioni");

		return (Element) root.clone();
	}

	private static Element getPersonaFisica(PersonaFisica personaFisica){
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("Persona");
		root.addAttribute("id",personaFisica.getId());

		Element Nome = root.addElement("Nome");
		Nome.addText(personaFisica.getNome());

		Element Cognome = root.addElement("Cognome");
		Cognome.addText(personaFisica.getCognome());

		Element Titolo = root.addElement("Titolo");
		Titolo.addText(personaFisica.getTitolo());

		Element IndirizzoTelematico = root.addElement("IndirizzoTelematico");
		IndirizzoTelematico.addAttribute("tipo", "smtp");
		IndirizzoTelematico.addText(personaFisica.getIndirizzoTelematico());
		Element codiceFiscale = root.addElement("CodiceFiscale");
		codiceFiscale.addText(personaFisica.getId());

		return (Element) root.clone();
	}

	private static Element getAmministrazione(Amministrazione amministrazione){
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("SKIP");

		Element amministrazioneElem = root.addElement("Amministrazione");

		Element denominazione = amministrazioneElem.addElement("Denominazione");
		denominazione.addText(amministrazione.getDenominazione());

		Element codiceAmministrazione = amministrazioneElem.addElement("CodiceAmministrazione");
		codiceAmministrazione.addText(amministrazione.getCodiceAmministrazione());

		Element indirizzoTelematico = amministrazioneElem.addElement("IndirizzoTelematico");
		indirizzoTelematico.addText(amministrazione.getIndirizzoTelematico());

		Element forzaIndirizzoTelematico = amministrazioneElem.addElement("ForzaIndirizzoTelematico");
		forzaIndirizzoTelematico.addText("1");

		Element unitaOrganizzativa = amministrazioneElem.addElement("UnitaOrganizzativa");
		unitaOrganizzativa.addAttribute("tipo","temporanea");
		Element denominazioneUO = unitaOrganizzativa.addElement("Denominazione");
		denominazioneUO.addText(amministrazione.getUnitaOrganizzativa().getDenominazione());

		Element identificativoUO = unitaOrganizzativa.addElement("Identificativo");
		identificativoUO.addText(amministrazione.getUnitaOrganizzativa().getTipo());

		Element aoo = root.addElement("AOO");

		Element denominazioneAoo = aoo.addElement("Denominazione");
		denominazioneAoo.addText(amministrazione.getAOO().getDenominazione());

		Element codiceAOO = aoo.addElement("CodiceAOO");
		codiceAOO.addText(amministrazione.getAOO().getCodiceAOO());

		Element forzaIndirizzoTelematicoaoo = aoo.addElement("ForzaIndirizzoTelematico");
		forzaIndirizzoTelematico.addText("1");

		return (Element) root.clone();
	}

	private static Element getPersonaGiuridica(PersonaGiuridica personaGiuridica){
		Document xml = DocumentHelper.createDocument();

		Element root = xml.addElement("PersonaGiuridica");
		root.addAttribute("id",personaGiuridica.getId());
		root.addAttribute("tipo","da capire doce viene settato");

		Element denominazione = root.addElement("Denominazione");
		denominazione.addText(personaGiuridica.getDenominazione());

		Element indirizzoTelematico = root.addElement("IndirizzoTelematico");
		indirizzoTelematico.addAttribute("tipo","smtp");
		indirizzoTelematico.addText(personaGiuridica.getIndirizzoTelematico());

		Element forzaIndirizzoTelematico = root.addElement("ForzaIndirizzoTelematico");
		forzaIndirizzoTelematico.addText("1");

		return (Element) root.clone();
	}

}
