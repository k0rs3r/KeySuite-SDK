import it.kdm.fatturaPA.FatturaPAUtils;
import it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1.FatturaElettronicaBodyType;
import it.kdm.fatturaPA.it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1.FatturaElettronicaType;
import it.kdm.fatturaPA.messagetype.EsitoCommittenteType;
import it.kdm.fatturaPA.messagetype.MetadatiInvioFileType;
import it.kdm.fatturaPA.messagetype.NotificaDecorrenzaTerminiType;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;

public class TestFatturaPA{

    @Test
    public void testByteArray() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(new HashMap<>());
            byte[] contentBytes = bos.toByteArray();
            String hex = Hex.encodeHexString(contentBytes);
            System.out.println(hex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseFattura(){
        System.out.println("Inizio Test: parseFattura");
        try {
//        HashMap<String, FatturaElettronicaType> fattElettroniche = FatturaPAUtils.parseFattura("file:///Users/francesco.maglitto/tmp/IT13435960151_0WTZM.xml.p7m", true, true);
        HashMap<String, FatturaElettronicaType> fattElettroniche = FatturaPAUtils.parseFattura("file:///Users/francesco.maglitto/tmp/IT13435960151_0WS1E.xml.p7m", true, true);

            if(fattElettroniche != null){
                for(String pathFile: fattElettroniche.keySet()){
                    System.out.println("Path: " + pathFile);
                    if(fattElettroniche.get(pathFile).getFatturaElettronicaBody() != null && fattElettroniche.get(pathFile).getFatturaElettronicaBody().size()>0){
                        for(FatturaElettronicaBodyType body: fattElettroniche.get(pathFile).getFatturaElettronicaBody()){
                            System.out.println("Numero Fattura: " + body.getDatiGenerali().getDatiGeneraliDocumento().getNumero());
                            System.out.println("Numero Allegati: " + body.getAllegati().size());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fine Test: parseFattura");
    }

    @Test
    public void fileUriToXML(){
        System.out.println("Inizio Test: fileUriToXML");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/IT00331380585_00001.xml");
            System.out.println("xml: " + xml);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: fileUriToXml");
    }
    @Test
    public void xmlToFattura(){
        System.out.println("Inizio Test: xmlToFattura");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/IT00331380585_00001.xml");
            FatturaElettronicaType fatt = FatturaPAUtils.xmlToFattura(xml);
            System.out.println("versione: " + fatt.getVersione());
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: xmlToFattura");
    }
    @Test
    public void fatturaToXml(){
        System.out.println("Inizio Test: fatturaToXml");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/IT00331380585_00001.xml");
            FatturaElettronicaType fatt = FatturaPAUtils.xmlToFattura(xml);
            String xmlNew = FatturaPAUtils.fatturaToXml(fatt);
            System.out.println("xml: " + xmlNew);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: fatturaToXml");
    }


    //SEZIONE METADATI INVIO

    @Test
    public void parseMetadatiInvio(){
        System.out.println("Inizio Test: parseMetadatiInvio");
        try {
            HashMap<String, MetadatiInvioFileType> metadatiinvio = FatturaPAUtils.parseMetadatiInvio("file:///Users/francesco.maglitto/tmp/IT02228810681_00007_MT_001.xml");

            if(metadatiinvio != null){
                for(String pathFile: metadatiinvio.keySet()){
                    System.out.println("Path: " + pathFile);
                    System.out.println("Codice Destinatario: " + metadatiinvio.get(pathFile).getCodiceDestinatario());
                    System.out.println("Numero Allegati: " + metadatiinvio.get(pathFile).getIdentificativoSdI());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fine Test: parseMetadatiInvio");
    }

    @Test
    public void fileUriMetadatiToXML(){
        System.out.println("Inizio Test: fileUriMetadatiToXML");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/1575369053726.xml");
            System.out.println("xml: " + xml);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: fileUriMetadatiToXML");
    }
    @Test
    public void xmlToMetadati(){
        System.out.println("Inizio Test: xmlToMetadati");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/1575369053726.xml");
            MetadatiInvioFileType metadati = FatturaPAUtils.xmlToMetadatiInvioFileType(xml);
            System.out.println("versione: " + metadati.getVersione());
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: xmlToMetadati");
    }
    //SEZIONE DecorrenzaTermini INVIO

    @Test
    public void parseDecorrenzaTermini(){
        System.out.println("Inizio Test: parseDecorrenzaTermini");
        try {
            HashMap<String, NotificaDecorrenzaTerminiType> decorrenzaTermini = FatturaPAUtils.parseDecorrenzaTermini("file:///Users/francesco.maglitto/tmp/IT01234567890_11111_DT_001.xml");

            if(decorrenzaTermini != null){
                for(String pathFile: decorrenzaTermini.keySet()){
                    System.out.println("Path: " + pathFile);
                    System.out.println("Codice Destinatario: " + decorrenzaTermini.get(pathFile).getDescrizione());
                    System.out.println("Numero Allegati: " + decorrenzaTermini.get(pathFile).getIdentificativoSdI());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fine Test: parseDecorrenzaTermini");
    }

    @Test
    public void fileUriDecorrenzaTerminiToXML(){
        System.out.println("Inizio Test: fileUriDecorrenzaTermini");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/1575371867133.xml");
            System.out.println("xml: " + xml);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: fileUriDecorrenzaTermini");
    }
    @Test
    public void xmlToDecorrenzaTermini(){
        System.out.println("Inizio Test: xmlToDecorrenzaTermini");
        try {
            String xml = FatturaPAUtils.fileUriToXML("file:///Users/francesco.maglitto/tmp/1575371867133.xml");
            NotificaDecorrenzaTerminiType metadati = FatturaPAUtils.xmlToNotificaDecorrenzaTerminiType(xml);
            System.out.println("versione: " + metadati.getVersione());
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fine Test: xmlToDecorrenzaTermini");
    }


    @Test
    public void testEsitoCommittente(){
        System.out.println("FATTURA_PA: INGRESSO SCRIPT CREO ESITO");
        it.kdm.fatturaPA.messagetype.NotificaEsitoCommittenteType notificaEsito = new  it.kdm.fatturaPA.messagetype.NotificaEsitoCommittenteType();
        notificaEsito.setIdentificativoSdI(new BigInteger("123456"));
        notificaEsito.setVersione("1.0");
    System.out.println("FATTURA_PA: settato identificativo: "+ notificaEsito.getIdentificativoSdI());
        //if(num_fattura != null && num_fattura != "" && anno_fattura != null && anno_fattura != ""){
            System.out.println("FATTURA_PA: setto riferimenti");
            it.kdm.fatturaPA.messagetype.RiferimentoFatturaType rifFattura = new it.kdm.fatturaPA.messagetype.RiferimentoFatturaType();
            rifFattura.setNumeroFattura("222");
            rifFattura.setAnnoFattura(new BigInteger("2019"));
            notificaEsito.setRiferimentoFattura(rifFattura);

            System.out.println("FATTURA_PA: settato riferimenti -  anno: "+ rifFattura.getAnnoFattura() + " numero: "+ rifFattura.getNumeroFattura());
        //}
        //if(esito_notifica != null){
          //  System.out.println("FATTURA_PA: verifico l'esito notifica");
           // if(esito_notifica =="EC01"){ //ACCETTAZIONE
                System.out.println("FATTURA_PA: esito notifica : ACCETAZIONE");
                notificaEsito.setEsito(EsitoCommittenteType.EC_01);
            //}
            //if(esito_notifica =="EC02"){//RIFIUTO
            //    notificaEsito.setEsito(EsitoCommittenteType.EC_02);
            //    System.out.println("FATTURA_PA: esito notifica : RIFUTO");
            //}
        //}
        //if(descrizione_notifica != null){
            notificaEsito.setDescrizione("descr");
            System.out.println("FATTURA_PA: setto il messaggio di notifica : "+ notificaEsito.getDescrizione());
        //}
        //System.out.println("FATTURA_PA: avvio la chiamata all'utility di creazione notifica : " + nome_file_esito);
        HashMap<String, String> aa = FatturaPAUtils.createTempFileNotificaCommittente(notificaEsito, "aaa.xml");
        System.out.println("dddd");

    }
}