package it.kdm.orchestratore.appdoc.model.protocollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * Created by microchip on 28/02/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProtocolloPage {
    private String docNum;
    private String uo;
    private Object personaUO;
    private String verso;
    private String oggetto;
    private String tipoFirma;
    private String riservatezza;
    private Object classifica;
    private String dataDocumento;
    private Date dataVerificaFirma;
    private String dataPgEmergenza;
    private String numPgEmergenza;
    private String note;

    private List<ItemProtocolloMittente> protocolloMittente;
    private List<ItemProtocolloPage> mittenti;
    private List<ItemProtocolloPage> destinatari;
    private List<ItemProtocolloPage> firmatari;

    public String getUo() {
        return uo;
    }

    public void setUo(String uo) {
        this.uo = uo;
    }

    public Object getPersonaUO() {
        return personaUO;
    }

    public void setPersonaUO(Object personaUO) {
        this.personaUO = personaUO;
    }

    public String getVerso() {
        return verso;
    }

    public void setVerso(String verso) {
        this.verso = verso;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getTipoFirma() {
        return tipoFirma;
    }

    public void setTipoFirma(String tipoFirma) {
        this.tipoFirma = tipoFirma;
    }

    public List<ItemProtocolloPage> getMittenti() {
        return mittenti;
    }

    public void setMittenti(List<ItemProtocolloPage> mittenti) {
        this.mittenti = mittenti;
    }

    public List<ItemProtocolloPage> getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(List<ItemProtocolloPage> destinantari) {
        this.destinatari = destinantari;
    }

    public List<ItemProtocolloPage> getFirmatari() {
        return firmatari;
    }

    public void setFirmatari(List<ItemProtocolloPage> firmatari) {
        this.firmatari = firmatari;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public List<ItemProtocolloMittente> getProtocolloMittente() {
        return protocolloMittente;
    }

    public void setProtocolloMittente(List<ItemProtocolloMittente> protocolloMittente) {
        this.protocolloMittente = protocolloMittente;
    }

    public String getRiservatezza() {
        return riservatezza;
    }

    public void setRiservatezza(String riservatezza) {
        this.riservatezza = riservatezza;
    }

    public Object getClassifica() {
        return classifica;
    }

    public void setClassifica(Object classifica) {
        this.classifica = classifica;
    }

    public String getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(String dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public Date getDataVerificaFirma() {
        return dataVerificaFirma;
    }

    public void setDataVerificaFirma(Date dataVerificaFirma) {
        this.dataVerificaFirma = dataVerificaFirma;
    }

    public String getDataPgEmergenza() {
        return dataPgEmergenza;
    }

    public void setDataPgEmergenza(String dataPgEmergenza) {
        this.dataPgEmergenza = dataPgEmergenza;
    }

    public String getNumPgEmergenza() {
        return numPgEmergenza;
    }

    public void setNumPgEmergenza(String numPgEmergenza) {
        this.numPgEmergenza = numPgEmergenza;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
