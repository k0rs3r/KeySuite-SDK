package it.kdm.doctoolkit.model;

/**
 * Created by lorenxs on 26/03/14.
 */
public class CartellaCriteria extends GenericCriteria {


    private Ordinamento ordinamento;

    public void setOrderBy(String field, Ordinamento.orderByEnum direction) {
    	if (field.equalsIgnoreCase("name"))
        	field = "FOLDER_NAME";

        this.ordinamento = new Ordinamento();
        ordinamento.setNomeCampo(field);
        ordinamento.setTipo(direction);
        super.setOrderBy(field,direction);
    }

    public Ordinamento getOrdinamento() {
        return ordinamento;
    }

   
    @Override
    protected void initProperties() {
        this.setProperty("$MAX_RESULTS","100");
    }
}
