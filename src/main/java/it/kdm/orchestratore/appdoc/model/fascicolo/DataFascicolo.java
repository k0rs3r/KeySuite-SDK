package it.kdm.orchestratore.appdoc.model.fascicolo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.kdm.orchestratore.appdoc.model.AutoCompletionItem;

/**
 * Created by microchip on 23/03/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFascicolo {
    private String docnum;
    private String codEnte;
    private String codAoo;
    private String anno;
    private String classifica;
    private AutoCompletionItem classificaFascicolo;
    private AutoCompletionItem fascicoloPadre;
    private String nomeFascicolo;
    private String eredita;
    private String copiaAcl;

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

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getClassifica() {
        return classifica;
    }

    public void setClassifica(String classifica) {
        this.classifica = classifica;
    }

    public String getNomeFascicolo() {
        return nomeFascicolo;
    }

    public void setNomeFascicolo(String nomeFascicolo) {
        this.nomeFascicolo = nomeFascicolo;
    }

    public AutoCompletionItem getFascicoloPadre() {
        return fascicoloPadre;
    }

    public void setFascicoloPadre(AutoCompletionItem fascicoloPadre) {
        this.fascicoloPadre = fascicoloPadre;
    }

    public String getEredita() {
        return eredita;
    }

    public void setEredita(String eredita) {
        this.eredita = eredita;
    }

    public AutoCompletionItem getClassificaFascicolo() {
        return classificaFascicolo;
    }

    public void setClassificaFascicolo(AutoCompletionItem classificaFascicolo) {
        this.classificaFascicolo = classificaFascicolo;
    }

    public String getCopiaAcl() {
        return copiaAcl;
    }

    public void setCopiaAcl(String copiaAcl) {
        this.copiaAcl = copiaAcl;
    }

    public String getDocnum() {
        return docnum;
    }

    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }
}
