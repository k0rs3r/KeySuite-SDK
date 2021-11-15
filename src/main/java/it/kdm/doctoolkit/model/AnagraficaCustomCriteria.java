package it.kdm.doctoolkit.model;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 13/02/14
 * Time: 11.42
 * To change this template use File | Settings | File Templates.
 */
public class AnagraficaCustomCriteria extends AnagraficaCriteria {
    private String tipoAnagrafica = "";

    public String getTipoAnagrafica() {
        return tipoAnagrafica;
    }

    public void setTipoAnagrafica(String tipoAnagrafica) {
        this.tipoAnagrafica = tipoAnagrafica;
    }
}
