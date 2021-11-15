package it.kdm.orchestratore.entity.object;

/**
 * Created by antsic on 19/10/16.
 */
public class BpmDocument {

    private String docName;
    private String docNum;
    private String tipologiaDocumento;
    private String statoBusiness;

    public String getStatoBusiness() {
        return statoBusiness;
    }

    public void setStatoBusiness(String statoBusiness) {
        this.statoBusiness = statoBusiness;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getTipologiaDocumento() {
        return tipologiaDocumento;
    }

    public void setTipologiaDocumento(String tipologiaDocumento) {
        this.tipologiaDocumento = tipologiaDocumento;
    }
}
