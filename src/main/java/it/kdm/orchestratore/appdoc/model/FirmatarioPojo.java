package it.kdm.orchestratore.appdoc.model;

/**
 * Created by antsic on 28/06/17.
 */
public class FirmatarioPojo {

    private String denominazione;
    private String codiceFiscale;
    private String dataFirma;
    private String conformita;
    private String codiceEsito;
    private String controlloCrittografico;
    private String controlloCatenaTrusted;
    private String controlloCertificato;
    private String controlloCRL;



    public static final String POSITIVE = "POSITIVO";
    public static final String NEGATIVE = "NEGATIVO";
    public static final String WARNING = "WARNING";


    public boolean getIsError(){
        return NEGATIVE.equals(controlloCrittografico) && NEGATIVE.equals(controlloCatenaTrusted) &&
                NEGATIVE.equals(controlloCertificato) && NEGATIVE.equals(controlloCRL);
    }


    public boolean getIsPositive(){

            if(POSITIVE.equals(conformita)){
                return true;
            }

        return POSITIVE.equals(controlloCrittografico) && POSITIVE.equals(controlloCatenaTrusted) &&
                POSITIVE.equals(controlloCertificato) && POSITIVE.equals(controlloCRL);
    }



    public boolean getIsWArning(){
        return !getIsError() && !getIsPositive();
    }



    public String getControlloCRL() {
        return controlloCRL;
    }

    public void setControlloCRL(String controlloCRL) {
        this.controlloCRL = controlloCRL;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getDataFirma() {
        return dataFirma;
    }

    public void setDataFirma(String dataFirma) {
        this.dataFirma = dataFirma;
    }

    public String getConformita() {
        return conformita;
    }

    public void setConformita(String conformita) {
        this.conformita = conformita;
    }

    public String getCodiceEsito() {
        return codiceEsito;
    }

    public void setCodiceEsito(String codiceEsito) {
        this.codiceEsito = codiceEsito;
    }

    public String getControlloCrittografico() {
        return controlloCrittografico;
    }

    public void setControlloCrittografico(String controlloCrittografico) {
        this.controlloCrittografico = controlloCrittografico;
    }

    public String getControlloCatenaTrusted() {
        return controlloCatenaTrusted;
    }

    public void setControlloCatenaTrusted(String controlloCatenaTrusted) {
        this.controlloCatenaTrusted = controlloCatenaTrusted;
    }

    public String getControlloCertificato() {
        return controlloCertificato;
    }

    public void setControlloCertificato(String controlloCertificato) {
        this.controlloCertificato = controlloCertificato;
    }
}
