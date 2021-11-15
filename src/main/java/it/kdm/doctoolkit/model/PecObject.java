package it.kdm.doctoolkit.model;

import java.util.List;

/**
 * Created by antsic on 20/01/15.
 */
public class PecObject {

    private String oggetto;
    private String modalitaInvio;
    private String tipoRichiesta;
    private String forzaInvio;
    private List<Corrispondente> destinatari;
    private Documento documento;

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getModalitaInvio() {
        return modalitaInvio;
    }

    public void setModalitaInvio(String modalitaInvio) {
        this.modalitaInvio = modalitaInvio;
    }

    public String getTipoRichiesta() {
        return tipoRichiesta;
    }

    public void setTipoRichiesta(String tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }

    public String getForzaInvio() {
        return forzaInvio;
    }

    public void setForzaInvio(String forzaInvio) {
        this.forzaInvio = forzaInvio;
    }

    public List<Corrispondente> getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(List<Corrispondente> destinatari) {
        this.destinatari = destinatari;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

}
