package it.kdm.orchestratore.entity.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by maupet on 20/04/15.
 */
public class VisuraTaskObject {

    private String descrizione;
    private Date dataAttivita;

    public VisuraTaskObject(String descrizione, Date dataAttivita) {
        this.descrizione = descrizione;
        this.dataAttivita = dataAttivita;
    }

    public VisuraTaskObject() {
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataAttivita() {
        return dataAttivita;
    }

    public void setDataAttivita(Date dataAttivita) {
        this.dataAttivita = dataAttivita;
    }
}