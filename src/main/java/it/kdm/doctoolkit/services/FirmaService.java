package it.kdm.doctoolkit.services;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.firma.WSFirmaFirmaExceptionException;
import it.kdm.doctoolkit.clients.firma.WSFirmaStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FirmaService {
    private static final Logger log = LoggerFactory.getLogger(FirmaService.class);
    public static final String DELETED_FORMAT = "DELETED-%s-%s";
//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setDocerServicesEpr(url);
//    }

    public static void setAttachDir(String path) {
        ClientManager.INSTANCE.setCacheDir(new File(path));
    }

    public static String requestOTP(String token, String alias, String pin) throws WSFirmaFirmaExceptionException, IOException {
        String sede = ToolkitConnector.extractSedeFromToken(token);
        WSFirmaStub serv = ClientManager.INSTANCE.getFirmaClient(sede);
        WSFirmaStub.RequestOTP  requestOtp = new WSFirmaStub.RequestOTP();
        requestOtp.setToken(token);
        requestOtp.setAlias(alias);
        requestOtp.setPin(pin);

       WSFirmaStub.RequestOTPResponse response = serv.requestOTP(requestOtp);
        String otp = response.get_return();

        if(Strings.isNullOrEmpty(otp)){
            throw new WSFirmaFirmaExceptionException("request OTP error");
        }

        return otp;
    }




    public static Map firmaRemota(String token, String alias, String pin, String tipo, String otp, String documenti []) throws WSFirmaFirmaExceptionException, IOException {
        String sede = ToolkitConnector.extractSedeFromToken(token);
        WSFirmaStub serv = ClientManager.INSTANCE.getFirmaClient(sede);
        WSFirmaStub.FirmaRemota  firmaRemota = new WSFirmaStub.FirmaRemota();
        firmaRemota.setPin(pin);
        log.info("pin Firma remota "+firmaRemota.getPin().toString());
        firmaRemota.setAlias(alias);
        log.info("alias Firma remota "+firmaRemota.getAlias().toString());
        firmaRemota.setToken(token);
        log.info("token Firma remota "+firmaRemota.getToken().toString());
        firmaRemota.setDocumenti(documenti);
        log.info("documenti Firma remota "+ firmaRemota.getDocumenti().toString());
        firmaRemota.setTipo(tipo);
        log.info("tipo Firma remota"+firmaRemota.getTipo().toString());
        firmaRemota.setOTP(otp);
        log.info("otp Firma remota"+ firmaRemota.getOTP().toString());

        WSFirmaStub.FirmaRemotaResponse firmaRemotaResponse = serv.firmaRemota(firmaRemota);

        //temporaneamente commento la chiamata al ws firma e quindi preservo la variabile firmaRemotaResponse valorizzandola a null
      //  WSFirmaStub.FirmaRemotaResponse firmaRemotaResponse=null;

        WSFirmaStub.StreamDescriptor [] streamDescriptor=firmaRemotaResponse.get_return();
        Map returnType= new HashMap();
        if(streamDescriptor!=null){
            for(WSFirmaStub.StreamDescriptor tmp:streamDescriptor){

                log.info("costruisco la mappa mapdoc");
                Map mapDoc= new HashMap();
                log.info("recupero il docnum");
                String docnum=tmp.getDocNum();
                log.info("recupero il nomedel documento");
                String name= tmp.getName();
                log.info("recupero il nome originale");
                String originalName= tmp.getOriginalName();
                log.info("datahandler");
                DataHandler dm = tmp.getDataHandler();
                //tmp.getDataHandler().getOutputStream()
                log.info("getInputStream");
                InputStream io = dm.getInputStream();

                log.info("inserisco nella mappa il docnum");
                mapDoc.put("docnum",docnum);
                log.info("inserisco nella mappa il name");
                mapDoc.put("name",name);
                log.info("inserisco nella mappa il nome originale");
                mapDoc.put("originalName",originalName);
                log.info("inserisco nella mappa lo stream file");
                mapDoc.put("streamFile",io);
                log.info("ritorno la mappa con lo stream file");

                returnType.put(docnum,mapDoc);
            }
        }else{
            throw new WSFirmaFirmaExceptionException("firma remota error");
        }

        return returnType;
    }

    public static Map firmaAutomatica(String token, String alias, String pin, String tipo, String documenti []) throws WSFirmaFirmaExceptionException, IOException {
        String sede = ToolkitConnector.extractSedeFromToken(token);
        WSFirmaStub serv = ClientManager.INSTANCE.getFirmaClient(sede);
        WSFirmaStub.FirmaAutomatica  firmaAutomatica = new WSFirmaStub.FirmaAutomatica();
        firmaAutomatica.setPin(pin);
        log.info("pin Firma remota "+firmaAutomatica.getPin().toString());
        firmaAutomatica.setAlias(alias);
        log.info("alias Firma remota "+firmaAutomatica.getAlias().toString());
        firmaAutomatica.setToken(token);
        log.info("token Firma remota "+firmaAutomatica.getToken().toString());
        firmaAutomatica.setDocumenti(documenti);
        log.info("documenti Firma remota "+ firmaAutomatica.getDocumenti().toString());
        firmaAutomatica.setTipo(tipo);
        log.info("tipo Firma remota"+firmaAutomatica.getTipo().toString());

        WSFirmaStub.FirmaAutomaticaResponse firmaRemotaResponse = serv.firmaAutomatica(firmaAutomatica);


        //temporaneamente commento la chiamata al ws firma e quindi preservo la variabile firmaRemotaResponse valorizzandola a null
        //  WSFirmaStub.FirmaRemotaResponse firmaRemotaResponse=null;

        WSFirmaStub.StreamDescriptor [] streamDescriptor=firmaRemotaResponse.get_return();
        Map returnType= new HashMap();
        if(streamDescriptor!=null){
            for(WSFirmaStub.StreamDescriptor tmp:streamDescriptor){

                log.info("costruisco la mappa mapdoc");
                Map mapDoc= new HashMap();
                log.info("recupero il docnum");
                String docnum=tmp.getDocNum();
                log.info("recupero il nomedel documento");
                String name= tmp.getName();
                log.info("recupero il nome originale");
                String originalName= tmp.getOriginalName();
                log.info("datahandler");
                DataHandler dm = tmp.getDataHandler();
                //tmp.getDataHandler().getOutputStream()
                log.info("getInputStream");
                InputStream io = dm.getInputStream();

                log.info("inserisco nella mappa il docnum");
                mapDoc.put("docnum",docnum);
                log.info("inserisco nella mappa il name");
                mapDoc.put("name",name);
                log.info("inserisco nella mappa il nome originale");
                mapDoc.put("originalName",originalName);
                log.info("inserisco nella mappa lo stream file");
                mapDoc.put("streamFile",io);
                log.info("ritorno la mappa con lo stream file");

                returnType.put(docnum,mapDoc);
            }
        }else{
            throw new WSFirmaFirmaExceptionException("firma remota error");
        }

        return returnType;
    }



}