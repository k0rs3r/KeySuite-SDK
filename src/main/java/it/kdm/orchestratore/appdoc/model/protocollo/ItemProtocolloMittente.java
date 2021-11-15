package it.kdm.orchestratore.appdoc.model.protocollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by microchip on 20/03/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemProtocolloMittente {
    private String numPgMittente = "";
    private String dataPgMittente = "";
    private String codEnteMittente = "";
    private String codAooMittente = "";
    private String classificaMittente = "";
    private String fascicoloMittente = "";

    public String getNumPgMittente() {
        return numPgMittente;
    }

    public void setNumPgMittente(String numPgMittente) {
        this.numPgMittente = numPgMittente;
    }

    public String getDataPgMittente() {
        return dataPgMittente;
    }

    public void setDataPgMittente(String dataPgMittente) {
        this.dataPgMittente = dataPgMittente;
    }

    public String getCodEnteMittente() {
        return codEnteMittente;
    }

    public void setCodEnteMittente(String codEnteMittente) {
        this.codEnteMittente = codEnteMittente;
    }

    public String getCodAooMittente() {
        return codAooMittente;
    }

    public void setCodAooMittente(String codAooMittente) {
        this.codAooMittente = codAooMittente;
    }

    public String getClassificaMittente() {
        return classificaMittente;
    }

    public void setClassificaMittente(String classificaMittente) {
        this.classificaMittente = classificaMittente;
    }

    public String getFascicoloMittente() {
        return fascicoloMittente;
    }

    public void setFascicoloMittente(String fascicoloMittente) {
        this.fascicoloMittente = fascicoloMittente;
    }
}
