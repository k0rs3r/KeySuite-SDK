package it.kdm.doctoolkit.model;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 13/02/14
 * Time: 11.46
 * To change this template use File | Settings | File Templates.
 */
public class DocumentoCriteria extends GenericCriteria {
    private String keywords = "";
    private int maxElementi = 100;
    private Ordinamento ordinamento;

    public void setOrderBy(String field, Ordinamento.orderByEnum direction) {
    	if (field.equalsIgnoreCase("name")) {
            field = "DOCNAME";
        }

        this.ordinamento = new Ordinamento();
        ordinamento.setNomeCampo(field);
        ordinamento.setTipo(direction);
    	super.setOrderBy(field,direction);
    }

    public Ordinamento getOrdinamento() {
        return ordinamento;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
        setFullTextQuery(keywords);
    }

    public int getMaxElementi() {
        return maxElementi;
    }

    public void setMaxElementi(int maxElementi) {
        this.maxElementi = maxElementi;
    }
}
