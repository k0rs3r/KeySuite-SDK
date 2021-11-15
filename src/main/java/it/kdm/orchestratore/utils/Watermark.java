package it.kdm.orchestratore.utils;

import com.google.common.base.Strings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import it.kdm.doctoolkit.model.AOO;
import it.kdm.doctoolkit.model.AOOCriteria;
import it.kdm.doctoolkit.model.DocerFile;
import it.kdm.doctoolkit.model.Documento;
import it.kdm.doctoolkit.services.DocerService;
import it.kdm.doctoolkit.services.SolrPathInterface;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.orchestratore.session.Session;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class Watermark {

    private static final Logger logger = LoggerFactory.getLogger(Watermark.class);

    public static PDDocument applyImageToPDF(InputStream fileStored, BufferedImage watermarkImage) throws IOException, WriterException {
        return applyImageToPDF(fileStored,watermarkImage,0);
    }

    public static PDDocument applyImageToPDF(InputStream stream,BufferedImage image, String position, boolean allpages) throws IOException, WriterException {

        PDDocument doc;
        doc = PDDocument.load(stream);
        int endPage = 0;

        if (allpages==true)
            endPage = doc.getPages().getCount();
        else
            endPage = 1;

        image = rotateImage(position,image);

        for (int i=0;i<endPage;i++) {
            PDPage page = doc.getPage(i);
            PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

            PDImageXObject ximage = LosslessFactory.createFromImage(doc, image);
            PDRectangle rect = page.getCropBox();

            Point offset = getPositionCoord(position,image,rect);
            cs.drawImage(ximage, (float)offset.getX(), (float)offset.getY(), (float)image.getWidth(), (float)image.getHeight());

            cs.close();
        }

        return doc;
    }

    public static BufferedImage rotateImage(String position, BufferedImage image) {
        AffineTransform tx;

        BufferedImage outImage = null;

        if ("alto".equalsIgnoreCase(position)) {
            return image;
        } else if ("basso".equalsIgnoreCase(position)) {
            return image;
        } else if ("sinistra".equalsIgnoreCase(position)) {
            outImage = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) outImage.getGraphics();
            graphics.setBackground(new Color(0, 0, 0, 0));
            tx = new AffineTransform();
            tx.rotate(-Math.PI/2, image.getWidth()/2, image.getHeight()/2);
            double offset = (image.getWidth()-image.getHeight())/2;
            tx.translate(-offset,-offset);
        } else if ("destra".equalsIgnoreCase(position)) {
            outImage = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) outImage.getGraphics();
            graphics.setBackground(new Color(0, 0, 0, 0));
            tx = new AffineTransform();
            tx.rotate(Math.PI/2, image.getWidth()/2, image.getHeight()/2);
            double offset = (image.getWidth()-image.getHeight())/2;
            tx.translate(offset,offset);
        } else {
            return image;
        }

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, outImage);

        return outImage;
    }

    private static Point getPositionCoord(String position, BufferedImage image, PDRectangle pageRect) {

        int marginDoc = 10;

        String stampigliaturaMargineBordo = ToolkitConnector.getGlobalProperty("stampigliatura.margineBordo");
        if (stampigliaturaMargineBordo != null)
        {
            try {
                marginDoc=Integer.parseInt( stampigliaturaMargineBordo );
            }
            catch( Exception e ) {
                logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (10). Parametro: 'stampigliatura.margineBordo'.");
            }
        }
        String stampigliaturaAllineamento = ToolkitConnector.getGlobalProperty("stampigliatura.allineamento");
        int posX = 0;
        int posY = 0;

        int pageWidth = (int)pageRect.getWidth();
        int pageHeight = (int)pageRect.getHeight();

        if ("basso".equalsIgnoreCase(position)) {

            posX = (pageWidth-image.getWidth())/2;
            posY = marginDoc;
            if (stampigliaturaAllineamento!=null){
                try{
                    posX=Integer.parseInt( stampigliaturaAllineamento );
                }
                catch (Exception e){
                    stampigliaturaAllineamento=stampigliaturaAllineamento.toUpperCase();
                    switch(stampigliaturaAllineamento){
                        case "LEFT":
                            posX=0;
                            break;
                        case "CENTER":
                            break;
                        case "RIGHT":
                            posX=pageWidth-image.getWidth();
                            break;
                         default: logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (CENTER). Parametro: 'stampigliatura.allineamento'.");
                    }

                }
            }
        } else if ("alto".equalsIgnoreCase(position)) {
            posX = (pageWidth-image.getWidth())/2;
            posY = pageHeight-image.getHeight()-marginDoc;
            if (stampigliaturaAllineamento!=null){
                try{
                    posX=Integer.parseInt( stampigliaturaAllineamento );
                }
                catch (Exception e){
                    stampigliaturaAllineamento=stampigliaturaAllineamento.toUpperCase();
                    switch(stampigliaturaAllineamento){
                        case "LEFT":
                            posX=0;
                            break;
                        case "CENTER":
                            break;
                        case "RIGHT":
                            posX=pageWidth-image.getWidth();
                            break;
                        default: logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (CENTER). Parametro: 'stampigliatura.allineamento'.");

                    }

                }
            }
        } else if ("sinistra".equalsIgnoreCase(position)) {
            posX = marginDoc;
            posY = (pageHeight-image.getHeight())/2;
            if (stampigliaturaAllineamento!=null){
                try{
                    posY=Integer.parseInt( stampigliaturaAllineamento );
                }
                catch (Exception e){
                    stampigliaturaAllineamento=stampigliaturaAllineamento.toUpperCase();
                    switch(stampigliaturaAllineamento){
                        case "LEFT":
                            posY=0;
                            break;
                        case "CENTER":
                            break;
                        case "RIGHT":
                            posY=pageHeight-image.getHeight();
                            break;
                        default: logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (CENTER). Parametro: 'stampigliatura.allineamento'.");

                    }

                }
            }
        } else if ("destra".equalsIgnoreCase(position)) {
            posX = pageWidth-image.getWidth()-marginDoc;
            posY = (pageHeight-image.getHeight())/2;
            if (stampigliaturaAllineamento!=null){
                try{
                    posY=pageHeight-image.getHeight()-Integer.parseInt( stampigliaturaAllineamento );
                }
                catch (Exception e){
                    stampigliaturaAllineamento=stampigliaturaAllineamento.toUpperCase();
                    switch(stampigliaturaAllineamento){
                        case "LEFT":
                            posY=pageHeight-image.getHeight();
                            break;
                        case "CENTER":
                            break;
                        case "RIGHT":
                            posY=0;
                            break;
                        default: logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (CENTER). Parametro: 'stampigliatura.allineamento'.");

                    }

                }
            }
        }

        return new Point(posX,posY);
    }

    public static PDDocument applyImageToPDF(InputStream fileStored, BufferedImage watermarkImage, int pageNumber) throws IOException, WriterException {
        return applyImageToPDF(fileStored,watermarkImage,pageNumber,20,20,210,70);
    }

    public static PDDocument applyImageToPDF(InputStream fileStored, BufferedImage watermarkImage, int pageNumber, int offsetTop, int offsetLeft, int width, int height) throws IOException, WriterException {
        PDDocument doc;
        doc = PDDocument.load(fileStored);

        PDPage page = doc.getPage(pageNumber);
        PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

        PDImageXObject ximage = LosslessFactory.createFromImage(doc,watermarkImage);
        PDRectangle rect =  page.getCropBox();

        cs.drawImage(ximage,offsetLeft,rect.getHeight()-offsetTop-height,width,height);

        cs.close();

        return doc;
    }

    public static InputStream convertPDDocumentToInputStream(PDDocument pdf) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdf.save(out);
        byte[] data = out.toByteArray();
        InputStream  is = new ByteArrayInputStream(data);

        return is;
    }

    public static PDDocument createStampigliaturaImage(String docnum, String token) throws Exception {
        try {

            String posizione = ToolkitConnector.getGlobalProperty("stampigliatura.pec.defaultPosizione");
            if (Strings.isNullOrEmpty(posizione)){
                posizione="sinistra";
            }
            else{
                if (!posizione.equalsIgnoreCase("sinistra")
                        &&!posizione.equalsIgnoreCase("destra")
                        &&!posizione.equalsIgnoreCase("alto")
                        &&!posizione.equalsIgnoreCase("basso")){
                    posizione="sinistra";
                }
            }

            Documento docProfile = DocerService.recuperaProfiloDocumento(token, docnum);
            DocerFile dFile =DocerService.downloadDocument(token, docnum);
            InputStream stream = dFile.getContent().getInputStream();
            String ente = docProfile.getEnte();
            String aoo =  docProfile.getAOO();
            if (docProfile.getExtension().equalsIgnoreCase("pdf")){

                }
            BufferedImage img = createStampigliaturaImage(stream,ente,aoo,docProfile);
            return applyImageToPDF(stream, img, posizione, true);
        }catch (Exception e){
            e.getStackTrace();
            throw e;
        }
    }




    public static BufferedImage createStampigliaturaImage(InputStream stream, String desEnte, String desAoo, Documento doc) throws Exception {

        int width = 250;
        int height = 60;
        int fontSize = 9;

        String riga1 = "";
        String riga2 = "";
        String riga3 = "";
        String riga4 = "";

        try {
            width = Integer.parseInt(ToolkitConnector.getGlobalProperty("stampigliatura.width"));
        } catch (Exception e) {
            logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default. Parametro: 'stampigliatura.width'.");
        }

        try {
            height = Integer.parseInt(ToolkitConnector.getGlobalProperty("stampigliatura.height"));
        } catch (Exception e) {
            logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default. Parametro: 'stampigliatura.height'.");
        }

//        try {
//            fontSize = Integer.parseInt(ToolkitConnector.getGlobalProperty("stampigliatura.fontSize"));
//        } catch (Exception e) {
//            logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default. Parametro: 'stampigliatura.fontSize'.");
//        }

        try {
            riga1 = ToolkitConnector.getGlobalProperty("stampigliatura.riga1");
            riga2 = ToolkitConnector.getGlobalProperty("stampigliatura.riga2");
            riga3 = ToolkitConnector.getGlobalProperty("stampigliatura.riga3");
            riga4 = ToolkitConnector.getGlobalProperty("stampigliatura.riga4");
        } catch (Exception e) {
            logger.warn("I parametri di configurazione delle righe per la stampigliatura non sono validi.");
        }

        if (Strings.isNullOrEmpty(riga1))
            riga1 = "";
        if (Strings.isNullOrEmpty(riga2))
            riga2 = "";
        if (Strings.isNullOrEmpty(riga3))
            riga3 = "";
        if (Strings.isNullOrEmpty(riga4))
            riga4 = "";

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Map<String,String> vals = doc.properties;
        vals.put("DES_ENTE", desEnte);
        vals.put("DES_AOO", desAoo);
        vals.put("DATA_PROTOCOLLO", formatter.format(doc.getPropertyDate("DATA_PG", Locale.ITALY)));
        vals.put("DATA_CREAZIONE", formatter.format(doc.getPropertyDate("CREATED", Locale.ITALY)));
        vals.put("DATA_MODIFICA", formatter.format(doc.getPropertyDate("MODIFIED", Locale.ITALY)));
        vals.put("VERSO_PROTOCOLLO",doc.getProperty("TIPO_PROTOCOLLAZIONE"));

        if (Strings.isNullOrEmpty(vals.get("CLASSIFICA")))
            vals.put("CLASSIFICA","");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));

        graphics.setColor(Color.BLUE);
        try{
            String stampigliaturaColor = ToolkitConnector.getGlobalProperty("stampigliatura.color");
            if (stampigliaturaColor != null)
            {
                graphics.setColor(Color.decode(stampigliaturaColor));
            }
        }
        catch(Exception e)
        {
            logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (Color.Blue). Parametro: 'stampigliatura.Color'.");
        }
        String stampigliaturaBox = ToolkitConnector.getGlobalProperty("stampigliatura.box");
        if (stampigliaturaBox != null)
        {
            stampigliaturaBox=stampigliaturaBox.toUpperCase();
            if (stampigliaturaBox.equalsIgnoreCase("NO")){
                stampigliaturaBox="NO";
            }
            else{
                if (!stampigliaturaBox.equalsIgnoreCase("SI")){
                    logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (SI). Parametro: 'stampigliatura.box'.");
                }
                stampigliaturaBox="SI";
            }

        }
        else{
            stampigliaturaBox="SI";
            logger.warn("Il parametro di configurazione per la stampigliatura non è valido. Assunto valore di default (SI). Parametro: 'stampigliatura.box'.");
        }

        //applica i template
        StrSubstitutor sub1 = new StrSubstitutor(vals);
        riga1 = sub1.replace(riga1);
        StrSubstitutor sub2 = new StrSubstitutor(vals);
        riga2 = sub2.replace(riga2);
        StrSubstitutor sub3 = new StrSubstitutor(vals);
        riga3 = sub3.replace(riga3);
        StrSubstitutor sub4 = new StrSubstitutor(vals);
        riga4 = sub4.replace(riga4);


        if (stampigliaturaBox.equalsIgnoreCase("SI")){
            //rettangolo
            graphics.drawRect(0, 0, width - 1, height - 1);
            //linea a metà
            graphics.drawLine(0, height / 2, width - 1, height / 2);
            //linea verticale per formare il quadreato in alto a destra
            graphics.drawLine(width-height / 2, 0, width-height / 2, height/2);
            graphics.setFont(new Font("Verdana", Font.PLAIN, 27));
            graphics.drawString(doc.getProperty("TIPO_PROTOCOLLAZIONE"),width-height / 2+5,25);
        }





        //contenuti scritte
        //tipo protocollazione
        // graphics.drawString(doc.getProperty("TIPO_PROTOCOLLAZIONE"),width-height / 2+5,25);

        graphics.setFont(new Font("Verdana", Font.PLAIN, fontSize));
        //riga 1
        graphics.drawString(riga1,5,12);
        //riga 2
        graphics.drawString(riga2,5, 12*2);
        //riga 3
        graphics.drawString(riga3,5, height / 2 + 12);
        //riga 4
        graphics.drawString(riga4,5, height / 2 + 12*2);

        //graphics.setColor(Color.BLACK);

        return image;
    }

    public static BufferedImage createQRImage( String docnum) throws Exception {
        SolrPathInterface solrInterface = new SolrPathInterface();
        String token = Session.getUserInfo().getJwtToken();

        Documento currDoc = solrInterface.openByDocnum(token, docnum);

        //recupera la descrizione dell'ENTE
        String desEnte = Session.getUserInfo().getEnte().getDesc();

        AOOCriteria criteria = new AOOCriteria();
        criteria.setAoo(currDoc.getAoo());
        java.util.List<AOO> results = DocerService.ricercaAOO(Session.getUserInfo().getJwtToken(), criteria);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Map<String,String> etichettaData = currDoc.properties;
        etichettaData.put("DES_ENTE", desEnte);
        etichettaData.put("DES_AOO", results.get(0).getName());
        etichettaData.put("DATA_PROTOCOLLO", formatter.format(currDoc.getPropertyDate("DATA_PG", Locale.ITALY)));
        etichettaData.put("DATA_CREAZIONE", formatter.format(currDoc.getPropertyDate("CREATED", Locale.ITALY)));
        etichettaData.put("DATA_MODIFICA", formatter.format(currDoc.getPropertyDate("MODIFIED", Locale.ITALY)));
        etichettaData.put("HASH", currDoc.getProperty("FILE_HASH"));

        if (Strings.isNullOrEmpty(etichettaData.get("CLASSIFICA")))
            etichettaData.put("CLASSIFICA","");

        String templateEtichetta = null;

        try {
            templateEtichetta = ToolkitConnector.getGlobalProperty("timbro.templateEtichetta");

        } catch (Exception e) {
            logger.error("I parametri di configurazione per il timbro non sono validi. Assunti i parametri di default.");
        }

        if (Strings.isNullOrEmpty(templateEtichetta))
            templateEtichetta = "Template non configurato.";

        return createQRImage(etichettaData,templateEtichetta);
    }

    public static BufferedImage createQRImage( Map<String,String> vals, String templateString) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.MARGIN,0);
        hintMap.put(EncodeHintType.QR_VERSION,9);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 53*3+5;

        String reg = "PG";

        if (vals.containsKey("REGISTRO_PG"))
            reg = vals.get("REGISTRO_PG");

        if(vals.containsKey("TIPO_PROTOCOLLAZIONE")) {
            String tipoProtocollazioneExt = vals.get("TIPO_PROTOCOLLAZIONE");
            switch (tipoProtocollazioneExt){
                case "E": tipoProtocollazioneExt = "Entrata";break;
                case "U": tipoProtocollazioneExt = "Uscita";break;
                case "I": tipoProtocollazioneExt = "Interna";break;
                default:tipoProtocollazioneExt = vals.get("TIPO_PROTOCOLLAZIONE");
            }

            vals.put("TIPO_PROTOCOLLAZIONE_EXT", tipoProtocollazioneExt);

        }


        String qrCodeTemplate = ToolkitConnector.getGlobalProperty("qrcode.template", "DN=${DOCNUM},ENTE=${COD_ENTE},AOO=${COD_AOO},DATA=${DATA_PG},NUM=${NUM_PG},HASH=${HASH}");
        StrSubstitutor sub1 = new StrSubstitutor(vals);
        String qrCodeText = sub1.replace(qrCodeTemplate);


//        String qrCodeText = String.format("DN=%s,ENTE=%s,AOO=%s,DATA=%s,NUM=%s,HASH=%s",
//                vals.get("DOCNUM"),vals.get("COD_ENTE"),vals.get("COD_AOO"),vals.get("DATA_PG"),vals.get("NUM_PG"),vals.get("HASH"));

        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth*3, matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth*3, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        int fontSize = 24;




        StrSubstitutor sub = new StrSubstitutor(vals);
        String text = sub.replace(templateString);
        //TODO: pulire gli eventuali parametri "${PAR}" rimasti

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

        String[] lines = text.split("\n");

        int top = (matrixWidth-(fontSize+2)*lines.length)/2;

        for (int i=0; i<lines.length;i++)
        {
            graphics.drawString(lines[i],matrixWidth+5,top+(fontSize+2)*(i+1) );
        }



        return image;
//        ImageIO.write(image, fileType, qrFile);
    }

}