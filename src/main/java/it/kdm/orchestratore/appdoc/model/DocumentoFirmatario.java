package it.kdm.orchestratore.appdoc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antsic on 28/06/17.
 */
public class DocumentoFirmatario {

    private String docname;
    private List<FirmatarioPojo> firmatarioPojoList = new ArrayList<>();

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public List<FirmatarioPojo> getFirmatarioPojoList() {
        return firmatarioPojoList;
    }

    public void setFirmatarioPojoList(List<FirmatarioPojo> firmatarioPojoList) {
        this.firmatarioPojoList = firmatarioPojoList;
    }
}




