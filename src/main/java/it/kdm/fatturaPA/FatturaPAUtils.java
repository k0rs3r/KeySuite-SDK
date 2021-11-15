package it.kdm.fatturaPA;

import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1.FatturaElettronicaBodyType;
import it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1.FatturaElettronicaType;
import it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1.ObjectFactory;
import it.kdm.fatturaPA.messagetype.MetadatiInvioFileType;
import it.kdm.fatturaPA.messagetype.NotificaDecorrenzaTerminiType;
import it.kdm.fatturaPA.messagetype.NotificaEsitoCommittenteType;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.util.Store;

import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class FatturaPAUtils {

    public static FatturaElettronicaType xmlToFattura(String xmlText){
        FatturaElettronicaType res = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            res = (FatturaElettronicaType) unmarshaller.unmarshal(new StringReader(xmlText));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del testo xm");
        }

        return res;
    }

    public static String fatturaToXml(FatturaElettronicaType fatt){
        String xmlFattura = null;

        ObjectFactory o = new ObjectFactory();
        JAXBElement<FatturaElettronicaType> a = o.createFatturaElettronica(fatt);

        xmlFattura = jaxbFatturaToXML(a);

        return xmlFattura;
    }


    public static String metadatiToXml(MetadatiInvioFileType metadati){
        String xmlMetadati = null;

        it.kdm.fatturaPA.messagetype.ObjectFactory o = new it.kdm.fatturaPA.messagetype.ObjectFactory();
        JAXBElement<MetadatiInvioFileType> a = o.createMetadatiInvioFile(metadati);

        xmlMetadati = jaxbMessageTypeToXML(a);

        return xmlMetadati;
    }

    public static MetadatiInvioFileType xmlToMetadatiInvioFileType(String xmlText){
        MetadatiInvioFileType res = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MetadatiInvioFileType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            res = (MetadatiInvioFileType) unmarshaller.unmarshal(new StringReader(xmlText));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del testo di metadati xml");
        }

        return res;
    }


    public static String decorrenzaTerminiToXml(NotificaDecorrenzaTerminiType decorrenzaTermini){
        String xmlDecorrenzaTermini = null;

        it.kdm.fatturaPA.messagetype.ObjectFactory o = new it.kdm.fatturaPA.messagetype.ObjectFactory();
        JAXBElement<NotificaDecorrenzaTerminiType> a = o.createNotificaDecorrenzaTermini(decorrenzaTermini);

        xmlDecorrenzaTermini = jaxbMessageTypeToXML(a);

        return xmlDecorrenzaTermini;
    }

    public static String esitoCommittenteToXml(NotificaEsitoCommittenteType esitoCommittente){
        String xmlEsitoCommittente = null;

        it.kdm.fatturaPA.messagetype.ObjectFactory o = new it.kdm.fatturaPA.messagetype.ObjectFactory();
        JAXBElement<NotificaEsitoCommittenteType> a = o.createNotificaEsitoCommittente(esitoCommittente);

        xmlEsitoCommittente = jaxbMessageTypeToXML(a);

        return xmlEsitoCommittente;
    }

    public static NotificaDecorrenzaTerminiType xmlToNotificaDecorrenzaTerminiType(String xmlText){
        NotificaDecorrenzaTerminiType res = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(NotificaDecorrenzaTerminiType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Source source = new StreamSource(new StringReader(xmlText));
            JAXBElement<NotificaDecorrenzaTerminiType> root = unmarshaller.unmarshal(source, NotificaDecorrenzaTerminiType.class);
            res = root.getValue();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del testo di notifica decorrenza termini xml");
        }

        return res;
    }




    public static HashMap<String, FatturaElettronicaType> parseFattura(String fileUri, boolean split, boolean removeAttachements) throws IOException {
        HashMap<String,FatturaElettronicaType> result = new HashMap<String, FatturaElettronicaType>();
        FatturaElettronicaType resultOne = null;

        byte[] byteArrayFattura = null;
        InputStream fileInputStream = null;

        if(fileUri != null){
            String suffix = (fileUri.substring(fileUri.lastIndexOf("."))).toLowerCase();

            if(suffix.endsWith("xml")){
                try {
                    fileInputStream = new URL(fileUri).openStream();
                } catch (FileNotFoundException | MalformedURLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERRORE FATURAPA - Errore trasformazione file: "+ fileUri + "in FileInputStream");
                }
            }else if(suffix.endsWith("p7m")){
                //Extract xml from p7m
                try{
                    byteArrayFattura = extractXMLFROMP7M(fileUri);
                    fileInputStream = new ByteArrayInputStream(byteArrayFattura);
                }catch (IOException io){
                    io.printStackTrace();
                    throw new RuntimeException("Errore nella lettura del file FatturaPA: " + fileUri);
                }catch (CMSException cm){
                    cm.printStackTrace();
                    throw new RuntimeException("Errore nell'estrazione del file xml della FatturaPA: " + fileUri);
                }
            }

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                resultOne = (FatturaElettronicaType) unmarshaller.unmarshal(fileInputStream);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del file xml: " + fileUri + "in bean FatturaElettronicaType");
            }
            if(resultOne != null){

                if(split){
                    if(resultOne.getFatturaElettronicaBody() != null && resultOne.getFatturaElettronicaBody().size()>0) {
                        for(FatturaElettronicaBodyType body : resultOne.getFatturaElettronicaBody()) {
                            FatturaElettronicaType fatturaIesima = new FatturaElettronicaType();
                            fatturaIesima.setFatturaElettronicaHeader(resultOne.getFatturaElettronicaHeader());
                            fatturaIesima.setSignature(resultOne.getSignature());
                            fatturaIesima.setVersione(resultOne.getVersione());
                            List<FatturaElettronicaBodyType> bodies = new ArrayList<FatturaElettronicaBodyType>();
                            bodies.add(body);
                            fatturaIesima.setFatturaElettronicaBody(bodies);

                            String pathTemp = createTempFileFattura(fatturaIesima);

                            pathTemp = optimizePathDownload(pathTemp);


                            result.put(pathTemp, fatturaIesima);
                        }

                    }
                }else{
                    String pathTemp = createTempFileFattura(resultOne);
                    pathTemp = optimizePathDownload(pathTemp);
                    result.put(pathTemp ,resultOne);
                }


                if(removeAttachements){

                    for(String filePath: result.keySet()){
                        for(int i =0; i< result.get(filePath).getFatturaElettronicaBody().size(); i++){
                            result.get(filePath).getFatturaElettronicaBody().get(i).getAllegati().clear();
                        }
                    }
                }

            }

        }

        return result;
    }

    private static String optimizePathDownload(String realPath){
        String url = ToolkitConnector.loadConfigFile("bpm-server-config").getProperty("url.download.file","file://");
        if(!url.endsWith("/"))
            url = url+"/";

        if(realPath.startsWith("/")){
            realPath = realPath.substring(1);
        }
        realPath = url + realPath;
        return realPath;
    }
    public static HashMap<String, MetadatiInvioFileType> parseMetadatiInvio(String fileUri) throws IOException {
        HashMap<String,MetadatiInvioFileType> result = new HashMap<String, MetadatiInvioFileType>();
        MetadatiInvioFileType resultOne = null;

        byte[] byteArrayFattura = null;
        InputStream fileInputStream = null;

        if(fileUri != null){
            try {
                fileInputStream = new URL(fileUri).openStream();
            } catch (FileNotFoundException | MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("ERRORE FATURAPA - Errore nell'apertura del file metadati:: "+ fileUri + "in FileInputStream");
            }


            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(MetadatiInvioFileType.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                resultOne = (MetadatiInvioFileType) unmarshaller.unmarshal(fileInputStream);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del file metadati: " + fileUri + "in bean MetadatiInvioFileType");
            }

            if(resultOne != null){
                String pathTemp = createTempFileMetadati(resultOne);
                pathTemp = optimizePathDownload(pathTemp);
                result.put(pathTemp ,resultOne);
            }
        }

        return result;
    }

    public static HashMap<String, NotificaDecorrenzaTerminiType> parseDecorrenzaTermini(String fileUri) throws IOException {
        HashMap<String,NotificaDecorrenzaTerminiType> result = new HashMap<String, NotificaDecorrenzaTerminiType>();
        NotificaDecorrenzaTerminiType resultOne = null;

        InputStream fileInputStream = null;

        if(fileUri != null){
            try {
                fileInputStream = new URL(fileUri).openStream();
            } catch (FileNotFoundException | MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("ERRORE FATURAPA - Errore nell'apertura del file decorrenza termini: "+ fileUri + "in FileInputStream");
            }


            try {

                JAXBContext jaxbContext = JAXBContext.newInstance(NotificaDecorrenzaTerminiType.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                Source source = new StreamSource(fileInputStream);
                JAXBElement<NotificaDecorrenzaTerminiType> root = unmarshaller.unmarshal(source, NotificaDecorrenzaTerminiType.class);
                resultOne = root.getValue();

            } catch (JAXBException e) {
                e.printStackTrace();
                throw new RuntimeException("ERRORE FATTURAPA - Errore nel parse del file metadati: " + fileUri + "in bean MetadatiInvioFileType");
            }

            if(resultOne != null){
                String pathTemp = createTempFileDecorrenzaTemini(resultOne);
                pathTemp = optimizePathDownload(pathTemp);
                result.put(pathTemp ,resultOne);
            }
        }

        return result;
    }



    public static String createTempFileFattura(FatturaElettronicaType fatt){
        String xml1 = fatturaToXml(fatt);

        String tempFile=null;
        try {
            String path = ToolkitConnector.getGlobalProperty("fatturaPa.dir", "/Users/francesco.maglitto/tmp");
            File f = new File(path, new Date().getTime() + ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));

            bw.write(xml1);
            bw.flush();
            bw.close();
            tempFile = f.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempFile;
    }

    public static String createTempFileMetadati(MetadatiInvioFileType metadati){

        String xml1 = metadatiToXml(metadati);

        String tempFile=null;
        try {
            String path = ToolkitConnector.getGlobalProperty("fatturaPa.dir", "/Users/francesco.maglitto/tmp");
            File f = new File(path, new Date().getTime() + ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));

            bw.write(xml1);
            bw.flush();
            bw.close();
            tempFile = f.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempFile;
    }

    public static String createTempFileDecorrenzaTemini(NotificaDecorrenzaTerminiType decorrenzaTermini){

        String xml1 = decorrenzaTerminiToXml(decorrenzaTermini);

        String tempFile=null;
        try {
            String path = ToolkitConnector.getGlobalProperty("fatturaPa.dir", "/Users/francesco.maglitto/tmp");
            File f = new File(path, new Date().getTime() + ".xml");

            BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));

            bw.write(xml1);
            bw.flush();
            bw.close();
            tempFile = f.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempFile;
    }


    public static HashMap<String, String> createTempFileNotificaCommittente(NotificaEsitoCommittenteType esitoCommittente, String fileName){

        String xml1 = esitoCommittenteToXml(esitoCommittente);

        HashMap<String,String> tempFile=null;
        try {
            String path = ToolkitConnector.getGlobalProperty("fatturaPa.dir", "/Users/francesco.maglitto/tmp");
            String fname = fileName!= null && !fileName.equalsIgnoreCase("") ? fileName : new Date().getTime() + ".xml";
            File f = new File(path, fname);

            BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));

            bw.write(xml1);
            bw.flush();
            bw.close();
            tempFile = new HashMap<String, String>();
            tempFile.put(f.getAbsolutePath(),optimizePathDownload(f.getAbsolutePath()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return tempFile;
    }



    public static String fileUriToXML(String uri){
        String xml= null;
        try{
            InputStream inputStream = new URL(uri).openStream();
            byte[] buffer = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            if(buffer != null){
                xml = new String(buffer, "UTF-8");
            }

        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Errore nella lettura del file FatturaPA: " + uri);
        }
        return xml;
    }
    public static byte[] extractXMLFROMP7M(String fileName) throws IOException, CMSException{
        // Loading the file first

        InputStream inputStream = new URL(fileName).openStream();
        //File f = new File(fileName);
//        byte[] buffer = new byte[(int) f.length()];
        byte[] buffer = org.apache.commons.io.IOUtils.toByteArray(inputStream);

        return extractXMLFROMP7M(buffer);
    }

    public static byte[] extractXMLFROMP7M(byte[] buffer) throws IOException, CMSException{

//        DataInputStream in = new DataInputStream(new FileInputStream(f));
//        DataInputStream in = new DataInputStream(inputStream);
//        in.readFully(buffer);
//        in.close();

        if(Base64.isBase64(buffer)){
            buffer = Base64.decodeBase64(buffer);
        }

        //Corresponding class of signed_data is CMSSignedData
        CMSSignedData signature = new CMSSignedData(buffer);
        Store cs = signature.getCertificates();
        SignerInformationStore signers = signature.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();

        //the following array will contain the content of xml document
        byte[] data = null;

        while (it.hasNext()) {
            SignerInformation signer = (SignerInformation) it.next();
            Collection certCollection = cs.getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder cert = (X509CertificateHolder) certIt.next();

            CMSProcessable sc = signature.getSignedContent();
            data = (byte[]) sc.getContent();
        }
            return data;
    }

    private static String jaxbFatturaToXML(JAXBElement<FatturaElettronicaType> customer) {
        String xmlString = "";
        try {
            JAXBContext context = JAXBContext.newInstance("it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1");
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter sw = new StringWriter();
            m.marshal(customer, sw);
            xmlString = sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlString;
    }

    private static String jaxbMessageTypeToXML(JAXBElement<?> metadati) {
        String xmlString = "";
        try {
            JAXBContext context = JAXBContext.newInstance("it.kdm.fatturaPA.messagetype");
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter sw = new StringWriter();
            m.marshal(metadati, sw);
            xmlString = sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlString;
    }




}
