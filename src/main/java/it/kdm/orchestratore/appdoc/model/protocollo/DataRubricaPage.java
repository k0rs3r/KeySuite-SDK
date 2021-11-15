package it.kdm.orchestratore.appdoc.model.protocollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by microchip on 28/08/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRubricaPage {
    private String codAoo;
    private String codice_fiscale;
    private String denominazione;
    private String id;
    private String name;
    private String partitaIva;
    private String type;
    private String email;
    private String indirizzo;
    private String telefono;
    private String peo;
    private String fax;


    public String getCodAoo() {
        return codAoo;
    }

    public void setCodAoo(String codAoo) {
        this.codAoo = codAoo;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPeo() {
        return peo;
    }

    public void setPeo(String peo) {
        this.peo = peo;
    }
}
