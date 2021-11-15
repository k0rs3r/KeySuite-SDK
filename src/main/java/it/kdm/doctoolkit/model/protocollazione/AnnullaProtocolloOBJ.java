package it.kdm.doctoolkit.model.protocollazione;

/**
 * Created by antsic on 14/06/17.
 */
public class AnnullaProtocolloOBJ {

    private String docNum;
    private String annullatoPg;
    private String motivoAnnullaMentoPG;
    private String provvedimentoAnnullaPG;


    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getAnnullatoPg() {
        return annullatoPg;
    }

    public void setAnnullatoPg(String annullatoPg) {
        this.annullatoPg = annullatoPg;
    }

    public String getMotivoAnnullaMentoPG() {
        return motivoAnnullaMentoPG;
    }

    public void setMotivoAnnullaMentoPG(String motivoAnnullaMentoPG) {
        this.motivoAnnullaMentoPG = motivoAnnullaMentoPG;
    }

    public String getProvvedimentoAnnullaPG() {
        return provvedimentoAnnullaPG;
    }

    public void setProvvedimentoAnnullaPG(String provvedimentoAnnullaPG) {
        this.provvedimentoAnnullaPG = provvedimentoAnnullaPG;
    }
}
