package it.kdm.doctoolkit.model;

import java.util.List;

/**
 * Created by antsic on 30/01/18.
 */
public class ProtoDocerServicesObj {
    private String numPG;
    private String dataPG;
    private String oggettoPG;
    private String registroPG;
    private String tipoFirma;
    private String codEnte;
    private String codAoo;

    public String getCodEnte() {
        return codEnte;
    }

    public void setCodEnte(String codEnte) {
        this.codEnte = codEnte;
    }

    public String getCodAoo() {
        return codAoo;
    }

    public void setCodAoo(String codAoo) {
        this.codAoo = codAoo;
    }

    private List<String> mittenti;
    private List<String> destinatari;

    public String getNumPG() {
        return numPG;
    }

    public void setNumPG(String numPG) {
        this.numPG = numPG;
    }

    public String getDataPG() {
        return dataPG;
    }

    public void setDataPG(String dataPG) {
        this.dataPG = dataPG;
    }

    public String getOggettoPG() {
        return oggettoPG;
    }

    public void setOggettoPG(String oggettoPG) {
        this.oggettoPG = oggettoPG;
    }

    public List<String> getMittenti() {
        return mittenti;
    }

    public void setMittenti(List<String> mittenti) {
        this.mittenti = mittenti;
    }

    public List<String> getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(List<String> destinatari) {
        this.destinatari = destinatari;
    }

    public String getRegistroPG() {
        return registroPG;
    }

    public void setRegistroPG(String registroPG) {
        this.registroPG = registroPG;
    }

    public String getTipoFirma() {
        return tipoFirma;
    }

    public void setTipoFirma(String tipoFirma) {
        this.tipoFirma = tipoFirma;
    }
}
