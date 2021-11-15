package it.kdm.orchestratore.appdoc.model.protocollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by microchip on 28/02/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemProtocolloPage {
    private String codAoo;
    private String type;
    private String denominazione;
    private String codice_fiscale;
    private String partitaIva;
    private String indirizzo;
    private String desUo;
    private String email;
    private String peo;
    private String fax;
    private String mezzo;
    private Object uo;
    private Object persona;
    private Object ipa;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getUo() {
        return uo;
    }

    public void setUo(Object uo) {
        this.uo = uo;
    }

    public Object getPersona() {
        return persona;
    }

    public void setPersona(Object personaUO) {
        this.persona = personaUO;
    }

    public String getCodAoo() {
        return codAoo;
    }

    public void setCodAoo(String codAoo) {
        this.codAoo = codAoo;
    }

    public String getPeo() {
        return peo;
    }

    public void setPeo(String peo) {
        this.peo = peo;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDesUo() {
        return desUo;
    }

    public void setDesUo(String desUo) {
        this.desUo = desUo;
    }

    public String getMezzo() {
        return mezzo;
    }

    public void setMezzo(String mezzo) {
        this.mezzo = mezzo;
    }


    public Object getIpa() {
        return ipa;
    }

    public void setIpa(Object ipa) {
        this.ipa = ipa;
    }
}