package it.kdm.orchestratore.appdoc.model.registrazione;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by microchip on 24/07/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRegistrazionePage {
    private String registro;
    private String docNum;
    private String ente;
    private String aoo;
    private String oggetto;
    private String tipoRichiesta;

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getAoo() {
        return aoo;
    }

    public void setAoo(String aoo) {
        this.aoo = aoo;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getTipoRichiesta() {
        return tipoRichiesta;
    }

    public void setTipoRichiesta(String tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }
}
