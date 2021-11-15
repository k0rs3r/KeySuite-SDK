package it.kdm.orchestratore.entity.object.protocollo;

import java.util.Date;

/**
 * Created by antsic on 27/02/17.
 */
public class ProtocolloObject {
    private Long id;
    private String anno;
    private Date data;
    private String doc_id;
    private String ente;
    private Long numero;
    private String registro;
    private String tipo_richiesta;
    private String user;
    private String aoo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getTipo_richiesta() {
        return tipo_richiesta;
    }

    public void setTipo_richiesta(String tipo_richiesta) {
        this.tipo_richiesta = tipo_richiesta;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAoo() {
        return aoo;
    }

    public void setAoo(String aoo) {
        this.aoo = aoo;
    }
}
