package it.kdm.doctoolkit.services;

import com.google.common.base.Optional;
import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.WSFascicolazioneStub;
import it.kdm.doctoolkit.clients.WSFascicolazioneStub.*;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.utils.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.lang.Exception;
import java.util.*;
import java.util.Map.Entry;

public class ServizioFascicolazione
{

//    public static void setWSURL(String url)
//	{
//		ClientManager.INSTANCE.setFascicolazioneEpr(url);
//	}
	
	public static boolean verificaFascicolo(String token, Fascicolo fascicolo) throws DocerApiException
	{
		return DocerService.verificaFascicolo(token, fascicolo);
	}
	
	public static Fascicolo aggiornaFascicolo(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException
	{
		return DocerService.aggiornaFascicolo(token, fascicolo, diritti);
	}

    public static void fascicolaUnitaDocumentaria(String token, UnitaDocumentaria unitaDocumentaria, Fascicolo fascicolo_primario, Fascicolo... fascicoli_secondari) throws DocerApiException {
        fascicolaUnitaDocumentaria(token, unitaDocumentaria, fascicolo_primario, Arrays.asList(fascicoli_secondari));
    }

	public static void fascicolaUnitaDocumentaria(String token, UnitaDocumentaria unitaDocumentaria, Fascicolo fascicolo_primario, List<Fascicolo> fascicoli_secondari) throws DocerApiException
	{
		try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
			WSFascicolazioneStub serv = ClientManager.INSTANCE.getFascicolazioneClient(sede);
			FascicolaById fascicola = new FascicolaById();
			FascicolaByIdResponse resp = null;
			
//			if (fascicolo_primario == null) throw new DocerApiException("Argomento 'fascicolo_primario non valido.", 570);

			//annullamento fascicolazione
			if (fascicolo_primario == null) {
				fascicolo_primario = new FascicoloPrimario();
				fascicolo_primario.setAoo(unitaDocumentaria.getDocumentoPrincipale().getAoo());
				fascicolo_primario.setEnte(unitaDocumentaria.getDocumentoPrincipale().getEnte());
				fascicolo_primario.setClassifica(unitaDocumentaria.getDocumentoPrincipale().getClassifica());
//				fascicolo_primario.setAnno(unitaDocumentaria.getDocumentoPrincipale().getAnno());
				fascicolo_primario.setAnno(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
				fascicolo_primario.setProgressivo("");
			}
			Element xmlInput = buildXmlInput(fascicolo_primario, fascicoli_secondari);
			String xmlOutputParsed = xmlInput.asXML().replace("<SKIP>", "").replace("</SKIP>", "");
			
			fascicola.setDatiFascicolo(xmlOutputParsed);
			fascicola.setDocumentId(Long.parseLong(unitaDocumentaria.DocumentoPrincipale.getDocNum()));
			fascicola.setToken(token);
			
			resp = serv.fascicolaById(fascicola);
			
			String xmlRet = resp.get_return();
			
			Document xml = DocumentHelper.parseText(xmlRet);
			
			Node xcode = xml.selectSingleNode("//esito/codice");
			if (!xcode.getText().equalsIgnoreCase("0")) {
				Node xdesc = xml.selectSingleNode("//esito/descrizione");
				throw new DocerApiException(xdesc.getText(), 511);
			}
			
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
	}
	public static Fascicolo creaFascicoloForzaNuovo(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException
	{
		WSFascicolazioneStub serv;
		try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
			serv = ClientManager.INSTANCE.getFascicolazioneClient(sede);
			
			if (fascicolo.getProgressivoPadre() == null) throw new DocerApiException("Valore 'null' non ammesso per il campo: 'ProgressivoPadre'", 560);
			
			// serv.RequireMtom = true;
			ForzaNuovoFascicolo fasc = new ForzaNuovoFascicolo();
			ForzaNuovoFascicoloResponse resp = null;
			
			KeyValuePair[] metadati = convertMetadati(fascicolo.properties);
			
			fasc.setToken(token);
			fasc.setMetadati(metadati);
			
			resp = serv.forzaNuovoFascicolo(fasc);
			String xmlRet = resp.get_return();
			
			Document xml = DocumentHelper.parseText(xmlRet);
			Node xcode = xml.selectSingleNode("//esito/codice");
			if (!xcode.getText().equalsIgnoreCase("0")) {
				Node xdesc = xml.selectSingleNode("//esito/descrizione");
				throw new DocerApiException(xdesc.getText(), 511);
			}
			
			
			
			Fascicolo dreg = new Fascicolo();
			dreg.loadFromXml(xml);
			FascicoloCriteria fcriteria = new FascicoloCriteria();
			fcriteria.copyFrom(dreg);
			Fascicolo fout=  DocerService.recuperaFascicolo(token, fcriteria);
			
			
			if (diritti != null) {
				DocerService.impostaDirittiFascicolo(token, dreg, diritti);
			}
			return fout;
			
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
		
	}
	public static Fascicolo creaFascicolo(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException
	{
        //Utils.validateFolderName(fascicolo.getDescrizione());

		WSFascicolazioneStub serv;
		try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
			serv = ClientManager.INSTANCE.getFascicolazioneClient(sede);
			
			if (fascicolo.getProgressivoPadre() == null) throw new DocerApiException("Valore 'null' non ammesso per il campo: 'ProgressivoPadre'", 560);
			
			// serv.RequireMtom = true;
			CreaFascicolo fasc = new CreaFascicolo();
			CreaFascicoloResponse resp = null;
			
			KeyValuePair[] metadati = convertMetadati(fascicolo.properties);
			
			fasc.setToken(token);
			fasc.setMetadati(metadati);
			
			resp = serv.creaFascicolo(fasc);
			String xmlRet = resp.get_return();
			
			Document xml = DocumentHelper.parseText(xmlRet);
			Node xcode = xml.selectSingleNode("//esito/codice");
			if (!xcode.getText().equalsIgnoreCase("0")) {
				Node xdesc = xml.selectSingleNode("//esito/descrizione");
				throw new DocerApiException(xdesc.getText(), 511);
			}
			
			
			
			Fascicolo dreg = new Fascicolo();
			dreg.loadFromXml(xml);
			FascicoloCriteria fcriteria = new FascicoloCriteria();
			fcriteria.copyFrom(dreg);
			Fascicolo fout=  DocerService.recuperaFascicolo(token, fcriteria);
			
			
			if (diritti != null) {
				DocerService.impostaDirittiFascicolo(token, dreg, diritti);
			}
			return fout;
			
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
		
	}

	public static Fascicolo creaFascicoloCifs(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException
	{
		//Utils.validateFolderName(fascicolo.getDescrizione());

		WSFascicolazioneStub serv;
		try {
			String sede = ToolkitConnector.extractSedeFromToken(token);
			serv = ClientManager.INSTANCE.getFascicolazioneClient(sede);

			if (fascicolo.getProgressivoPadre() == null) throw new DocerApiException("Valore 'null' non ammesso per il campo: 'ProgressivoPadre'", 560);

			// serv.RequireMtom = true;
			CreaFascicolo fasc = new CreaFascicolo();
			CreaFascicoloResponse resp = null;

			KeyValuePair[] metadati = convertMetadati(fascicolo.properties);

			fasc.setToken(token);
			fasc.setMetadati(metadati);

			resp = serv.creaFascicolo(fasc);
			String xmlRet = resp.get_return();

			Document xml = DocumentHelper.parseText(xmlRet);
			Node xcode = xml.selectSingleNode("//esito/codice");
			if (!xcode.getText().equalsIgnoreCase("0")) {
				Node xdesc = xml.selectSingleNode("//esito/descrizione");
				throw new DocerApiException(xdesc.getText(), 511);
			}



			Fascicolo dreg = new Fascicolo();
			dreg.properties.clear();
			dreg.loadFromXml(xml);
			FascicoloCriteria fcriteria = new FascicoloCriteria();
			// fcriteria.copyFrom(dreg);
			fcriteria.getSolrModelFrom(dreg); // converto i metadati del fascicolo nei corrispondenti metadati solr (es. stringa vuota diventa null)

			if (diritti != null) {
				DocerService.impostaDirittiFascicolo(token, dreg, diritti);
			}

			SOLRClient client = new SOLRClient();
			Optional<Fascicolo> ret = client.openBySearch(token, fcriteria, Fascicolo.class, true);

			if (!ret.isPresent()) {
				throw new DocerApiException("Fascicolo non trovato dopo la create", 500);
			}

			return ret.get();
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}

	}

	public static List<Fascicolo> ricercaFascicoli(String token, Fascicolo parametri_ricerca) throws DocerApiException
	{
		return DocerService.ricercaFascicoli(token, parametri_ricerca);
	}
	
	private static KeyValuePair[] convertMetadati(HashMap<String, String> metadati)
	{
		List<KeyValuePair> listaMetadati = new ArrayList<KeyValuePair>();
		KeyValuePair docerPair = null;
		
		for (Entry<String, String> pair : metadati.entrySet()) {
		
			// Object intero = (Object)pair.getValue();
			// if(intero instanceof Integer){
			// Integer i = (Integer)intero;
			// docerPair.setValue(i.toString());
			// }else{
	
			if (pair.getValue() != null) {
				docerPair = new KeyValuePair();
				docerPair.setKey(pair.getKey());
				String value = String.valueOf(pair.getValue());
				docerPair.setValue(value);
			}
			
			// }
			
			listaMetadati.add(docerPair);
		}
		KeyValuePair[] arr = listaMetadati.toArray(new KeyValuePair[listaMetadati.size()]);
		return arr;
		
	}
	
	@SuppressWarnings("unused")
	private static KeyValuePair[] convertACL(List<Acl> acls)
	{
		List<KeyValuePair> listaAcl = new ArrayList<KeyValuePair>();
		KeyValuePair docerPair = null;
		
		for (Acl a : acls) {
			docerPair = new KeyValuePair();
			docerPair.setKey(a.getUtenteGruppo());
			docerPair.setValue(String.valueOf(a.getDiritti()));
			listaAcl.add(docerPair);
		}
		
		KeyValuePair[] arr = listaAcl.toArray(new KeyValuePair[listaAcl.size()]);
		return arr;
	}
	
	private static Element buildXmlInput(Fascicolo fascicolo_primario, List<Fascicolo> fascicoli_secondari) throws DocerApiException
	{
		Document xml = DocumentHelper.createDocument();
		xml.setXMLEncoding("UTF-8");
		
		Element root = xml.addElement("Segnatura");
		
		root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		Element Intestazione = root.addElement("Intestazione");
		
		if (fascicolo_primario == null) throw new DocerApiException("Argomento 'fascicolo_primario' non valid.", 580);
		if (fascicolo_primario != null)
			Intestazione.add(fascicolo_primario.toFascicoloPrimario().getXmlElement());

		if (fascicoli_secondari != null) {
			Element FascicoliSecondari = Intestazione.addElement("FascicoliSecondari");
			for (Fascicolo fs : fascicoli_secondari) {
				FascicoliSecondari.add(fs.toFascicoloSecondario().getXmlElement());
			}
		}
		
		return (Element) root.clone();
	}
	
}
