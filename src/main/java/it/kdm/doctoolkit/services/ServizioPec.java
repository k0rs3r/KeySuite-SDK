package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.WSPECStub;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ServizioPec
{

	public static String inviaPec (String token,  PecObject pec )throws DocerApiException{
		try {
			String docNum = pec.getDocumento().getDocNum();
			String sede = ToolkitConnector.extractSedeFromToken(token);
			WSPECStub	wspecStub = ClientManager.INSTANCE.getPecClient(sede);
			WSPECStub.InvioPEC invioPec = new WSPECStub.InvioPEC();

			Element elem = buildXmlInput(pec);

			invioPec.setDocumentoId(Long.parseLong(docNum));
			invioPec.setToken(token);
			invioPec.setDatiPec(elem.asXML().replace("<SKIP>","").replace("</SKIP>",""));
			WSPECStub.InvioPECResponse invioPECResponse = wspecStub.invioPEC(invioPec);

			return invioPECResponse.get_return();
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
		indirizzoTelematico.addAttribute("tipo","smtp");
		Element forzaIndirizzoTelematico = amministrazioneElem.addElement("ForzaIndirizzoTelematico");
		forzaIndirizzoTelematico.addText("1");

		Element unitaOrganizzativa = amministrazioneElem.addElement("UnitaOrganizzativa");
		unitaOrganizzativa.addAttribute("tipo","temporanea");
		Element denominazioneUO = unitaOrganizzativa.addElement("Denominazione");
		denominazioneUO.addText(amministrazione.getUnitaOrganizzativa().getDenominazione());

		Element identificativoUO = unitaOrganizzativa.addElement("Identificativo");
		identificativoUO.addText(amministrazione.getUnitaOrganizzativa().getTipo());


		if(amministrazione.getUnitaOrganizzativa().getIndirizzoTelematico() !=null &&  !"".equals(amministrazione.getUnitaOrganizzativa().getIndirizzoTelematico())){
			Element indirizzoTelematicoUo = unitaOrganizzativa.addElement("IndirizzoTelematico");
			indirizzoTelematicoUo.addText(amministrazione.getUnitaOrganizzativa().getIndirizzoTelematico());
			indirizzoTelematicoUo.addAttribute("tipo","smtp");
		}

		Element aoo = root.addElement("AOO");

		Element denominazioneAoo = aoo.addElement("Denominazione");
		denominazioneAoo.addText(amministrazione.getAOO().getDenominazione());

		Element codiceAOO = aoo.addElement("CodiceAOO");
		codiceAOO.addText(amministrazione.getAOO().getCodiceAOO());
		if(amministrazione.getAOO().getIndirizzoTelematico() !=null &&  !"".equals(amministrazione.getAOO().getIndirizzoTelematico())) {
			Element indirizzoTelematicoAoo = aoo.addElement("IndirizzoTelematico");
			indirizzoTelematicoAoo.addText(amministrazione.getAOO().getIndirizzoTelematico());
			indirizzoTelematicoAoo.addAttribute("tipo","smtp");
		}

		Element forzaIndirizzoTelematicoaoo = aoo.addElement("ForzaIndirizzoTelematico");
		forzaIndirizzoTelematicoaoo.addText("1");

		return (Element) root.clone();
	}

	private static Element getPersonaGiuridica(PersonaGiuridica personaGiuridica){
		Document xml = DocumentHelper.createDocument();

		Element root = xml.addElement("PersonaGiuridica");
		root.addAttribute("id",personaGiuridica.getId());
		root.addAttribute("tipo","Partita iva");

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
