package it.kdm.doctoolkit.services;

import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.WSTimbroDigitaleStub;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.utils.XMLHelper;

import javax.activation.DataHandler;
import java.lang.Exception;
import org.apache.axiom.attachments.ByteArrayDataSource;

public class ServizioTimbro
{

	public static Timbro getTimbroFromDocument(String token, Timbro timbro, Documento documento) throws DocerApiException
	{
		try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
			DocerFile df = DocerService.downloadDocument(token, documento);
			WSTimbroDigitaleStub serv = ClientManager.INSTANCE.getTimbroDigitaleClient(sede);
			WSTimbroDigitaleStub.GetTimbro getTimbro = new WSTimbroDigitaleStub.GetTimbro();
			getTimbro.setImgDPI(timbro.getDpi()); //600
			getTimbro.setImgFormat(timbro.getFormat()); // jpeg
			getTimbro.setImgMaxH(timbro.getMaxh()); //60
			getTimbro.setImgMaxW(timbro.getMaxw()); //60
			getTimbro.setData(df.getContent());
			WSTimbroDigitaleStub.GetTimbroResponse response = serv.getTimbro(getTimbro);
			DataHandler dh = response.get_return();
			timbro.setTimbroImg(dh);

			return timbro;
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
	}

	//TODO da completare
	public static Timbro getTimbroFromMetadati(String token, Timbro timbro, Documento documento) throws DocerApiException
	{
		try {

			String sede = ToolkitConnector.extractSedeFromToken(token);
			String xmlMetadata  = DocerService.recuperaXmlProfiloDocumento(token, documento.getDocNum());
			//trasformo il profilo del documento in xml
			String XmlForTimbro = XMLHelper.transformXML("xsl",xmlMetadata);

			WSTimbroDigitaleStub serv = ClientManager.INSTANCE.getTimbroDigitaleClient(sede);
			WSTimbroDigitaleStub.GetTimbro getTimbro = new WSTimbroDigitaleStub.GetTimbro();
			getTimbro.setImgDPI(timbro.getDpi()); //600
			getTimbro.setImgFormat(timbro.getFormat()); // jpeg
			getTimbro.setImgMaxH(timbro.getMaxh()); //60
			getTimbro.setImgMaxW(timbro.getMaxw()); //60

			ByteArrayDataSource source = new ByteArrayDataSource(XmlForTimbro.getBytes());
			getTimbro.setData(new DataHandler(source));

			WSTimbroDigitaleStub.GetTimbroResponse response = serv.getTimbro(getTimbro);
			serv._getServiceClient().getLastOperationContext();
			DataHandler dh = response.get_return();
			timbro.setTimbroImg(dh);
			return timbro;
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
	}


	public static DocerFile applicaTimbro(String token, Timbro timbro, DocerFile file, TimbroCoordinates position) throws DocerApiException
	{
		try {
			String sede = ToolkitConnector.extractSedeFromToken(token);
			//trasformo il profilo del documento in xml
			WSTimbroDigitaleStub serv = ClientManager.INSTANCE.getTimbroDigitaleClient(sede);
			WSTimbroDigitaleStub.ApplicaTimbro applicaTimbro = new WSTimbroDigitaleStub.ApplicaTimbro();
			applicaTimbro.setPagina(position.getPagina());
			applicaTimbro.setPdf(file.getContent());
			applicaTimbro.setTimbro(timbro.getTimbroImg());
			applicaTimbro.setX(position.getX());
			applicaTimbro.setY(position.getY());
			WSTimbroDigitaleStub.ApplicaTimbroResponse response = serv.applicaTimbro(applicaTimbro);
			DataHandler dh = response.get_return();
			DocerFile df = new DocerFile();
			df.setContent(dh);
			return df;
		}
		catch (Exception e) {
			throw new DocerApiException(e);
		}
	}


}
