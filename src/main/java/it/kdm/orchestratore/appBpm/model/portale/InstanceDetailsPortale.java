package it.kdm.orchestratore.appBpm.model.portale;

/**
 * Created by maupet on 30/10/15.
 */
public class InstanceDetailsPortale {

    private String cod_ente;
    private String cod_aoo;
    private String idIstanzaBPM;
    private int stato;

    public String getCod_ente() {
        return cod_ente;
    }

    public void setCod_ente(String cod_ente) {
        this.cod_ente = cod_ente;
    }

    public String getCod_aoo() {
        return cod_aoo;
    }

    public void setCod_aoo(String cod_aoo) {
        this.cod_aoo = cod_aoo;
    }

    public String getIdIstanzaBPM() {
        return idIstanzaBPM;
    }

    public void setIdIstanzaBPM(String idIstanzaBPM) {
        this.idIstanzaBPM = idIstanzaBPM;
    }

    public int getStato() {
        return stato;
    }

    public void setStato(int stato) {
        this.stato = stato;
    }
}
