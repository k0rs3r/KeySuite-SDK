package it.kdm.orchestratore.appdoc.model.fascicolo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by microchip on 15/06/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFascicoloPage {
    private String docNum;
    private List<ItemFascicolo> fascicoli;

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public List<ItemFascicolo> getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(List<ItemFascicolo> fascicoli) {
        this.fascicoli = fascicoli;
    }


}
