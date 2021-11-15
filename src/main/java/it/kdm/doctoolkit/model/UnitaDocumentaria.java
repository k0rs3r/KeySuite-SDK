package it.kdm.doctoolkit.model;

import java.util.ArrayList;
import java.util.List;

public class UnitaDocumentaria {

	public UnitaDocumentaria() {
		this.DocumentoPrincipale = new Documento();
	}

	public Documento DocumentoPrincipale = null;

	public List<Documento> Allegati = new ArrayList<Documento>();
	public List<Documento> Annessi = new ArrayList<Documento>();
	public List<Documento> Annotazioni = new ArrayList<Documento>();


    public Documento getDocumentByDocNum(String docnum) {
        if (docnum.equals(this.getDocumentoPrincipale().getDocNum()))
            return this.getDocumentoPrincipale();

        for (Documento d : this.getAllegati())
            if (d.getDocNum().equals(docnum))
                return d;

        for (Documento d : this.getAnnotazioni())
            if (d.getDocNum().equals(docnum))
                return d;

        for (Documento d : this.getAnnessi())
            if (d.getDocNum().equals(docnum))
                return d;

        return null;
    }

	public Documento getDocumentoPrincipale() {
		return DocumentoPrincipale;
	}
	public void setDocumentoPrincipale(Documento documentoPrincipale) {
		DocumentoPrincipale = documentoPrincipale;
	}
	public List<Documento> getAllegati() {
		return Allegati;
	}
	public void setAllegati(List<Documento> allegati) {
		Allegati = allegati;
	}
	public List<Documento> getAnnessi() {
		return Annessi;
	}
	public void setAnnessi(List<Documento> annessi) {
		Annessi = annessi;
	}
	public List<Documento> getAnnotazioni() {
		return Annotazioni;
	}
	public void setAnnotazioni(List<Documento> annotazioni) {
		Annotazioni = annotazioni;
	}

	public boolean isEmpty(){
		int i = Annotazioni.size()+Annessi.size()+Allegati.size();
		return (i==0 && (DocumentoPrincipale==null || "".equals(DocumentoPrincipale.getDocName())) );
	}

     
}
