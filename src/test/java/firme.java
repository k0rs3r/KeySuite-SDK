import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.model.DSSDocument;
import it.kdm.firma.SignatureValidator;
import it.kdm.firma.SignerFactory;
import it.kdm.firma.bean.Firmatario;
import it.kdm.firma.bean.VerificaFirma;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class firme {
    // aggiungere -Djavax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema=com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory

    @Before
    public void setup(){
        System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
    }

    @Test
    public void testPADESSempliceErrato() throws Exception {

        InputStream stream;
        SignatureValidator validator;
        List<Firmatario> firmatarioList;

        stream = this.getClass().getResourceAsStream("testfiles/PADES.pdf");
        validator = new SignatureValidator(stream);
        validator.validateDocument();

        firmatarioList = validator.getFirmatari();

        assert (firmatarioList.size()==1);

        assert !"POSITIVO".equals(firmatarioList.get(0).getEsitoFirma().toString());
        assert "Patrizia Bramini".equals(firmatarioList.get(0).getCognomeNome().toString());

    }

    @Test
    public void testMatrioska() throws Exception {

        InputStream stream;
        SignatureValidator validator;
        List<Firmatario> firmatarioList;

        stream = this.getClass().getResourceAsStream("testfiles/serie.pdf.p7m.p7m");
        validator = new SignatureValidator(stream);
        validator.validateDocument();

        firmatarioList = validator.getFirmatari();

        assert (firmatarioList.size()==2);

        assert "POSITIVO".equals(firmatarioList.get(0).getEsitoFirma().toString());
        assert "Dante Ciantra".equals(firmatarioList.get(0).getCognomeNome().toString());

        assert "POSITIVO".equals(firmatarioList.get(1).getEsitoFirma().toString());
        assert "Maresca Lea".equals(firmatarioList.get(1).getCognomeNome().toString());

    }

    @Test
    public void testPADESDoppio() throws Exception {

        InputStream stream;
        SignatureValidator validator;
        List<Firmatario> firmatarioList;

        stream = this.getClass().getResourceAsStream("testfiles/PADES2.pdf");
        validator = new SignatureValidator(stream);
        validator.validateDocument();

        firmatarioList = validator.getFirmatari();

        assert (firmatarioList.size()==2);

        assert "WARNING".equals(firmatarioList.get(0).getEsitoFirma().toString());
        assert "Patrizia Bramini".equals(firmatarioList.get(0).getCognomeNome().toString());

        assert "POSITIVO".equals(firmatarioList.get(1).getEsitoFirma().toString());
        assert "Dante Ciantra".equals(firmatarioList.get(1).getCognomeNome().toString());
    }

    @Test
    public void testSigillo() throws Exception {

        InputStream stream;
        SignatureValidator validator;
        List<Firmatario> firmatarioList;

        stream = this.getClass().getResourceAsStream("testfiles/marcato.pdf");
        validator = new SignatureValidator(stream);
        validator.validateDocument();

        firmatarioList = validator.getFirmatari();

        assert (firmatarioList.size()==2);

        assert "POSITIVO".equals(firmatarioList.get(0).getEsitoFirma().toString());
        assert "FRANCESCO MAFFEI".equals(firmatarioList.get(0).getCognomeNome().toString());

        assert "POSITIVO".equals(firmatarioList.get(1).getEsitoFirma().toString());
        assert "Cdc - Registro Informatico".equals(firmatarioList.get(1).getCognomeNome().toString());
        assert "QESeal".equals(firmatarioList.get(1).getXmlSignature().getSignatureLevel().getValue().getReadable());
    }

    @Test
    public void testCADESControfirma() throws Exception {

        InputStream stream;
        SignatureValidator validator;
        List<Firmatario> firmatarioList;

        stream = this.getClass().getResourceAsStream("testfiles/controfirma.pdf.p7m");
        validator = new SignatureValidator(stream);
        validator.validateDocument();

        firmatarioList = validator.getFirmatari();

        assert (firmatarioList.size()==2);

        assert "POSITIVO".equals(firmatarioList.get(0).getEsitoFirma().toString());
        assert "SCOZZOLI ANDREA".equals(firmatarioList.get(0).getCognomeNome().toString());

        assert "POSITIVO".equals(firmatarioList.get(1).getEsitoFirma().toString());
        assert "Paolo Rapisarda".equals(firmatarioList.get(1).getCognomeNome().toString());
    }

    @Test
    public void testCADESsign() throws Exception {

        DSSDocument doc = SignerFactory.get(SignatureForm.CAdES)
                .withKeyStore("user_a_rsa","password")
                .signDocument(this.getClass().getResourceAsStream("testfiles/nonfirmato.pdf"));

        VerificaFirma verifica = new SignatureValidator(doc.openStream()).validateDocument();

        assert "SignerFake".equals(verifica.getFirmatari().get(0).getCognomeNome());
    }

    @Test
    public void testPADESsign() throws Exception {

        DSSDocument doc = SignerFactory.get(SignatureForm.PAdES)
                .withKeyStore("user_a_rsa","password")
                .signDocument(this.getClass().getResourceAsStream("testfiles/nonfirmato.pdf"));

        VerificaFirma verifica = new SignatureValidator(doc.openStream()).validateDocument();

        assert "SignerFake".equals(verifica.getFirmatari().get(0).getCognomeNome());
    }

    @Test
    public void testXADESsign() throws Exception {


        DSSDocument doc = SignerFactory.get(SignatureForm.XAdES)
                .withKeyStore("user_a_rsa","password")
                .signDocument("<test>aaa</test>".getBytes());

        VerificaFirma verifica = new SignatureValidator(doc.openStream()).validateDocument();

        assert "SignerFake".equals(verifica.getFirmatari().get(0).getCognomeNome());
    }
}
