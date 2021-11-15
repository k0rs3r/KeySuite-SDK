package it.kdm.orchestratore.appdoc.model.protocollo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by microchip on 19/06/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataEtichetta {
    private String docNum;
    private byte[] image;

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
