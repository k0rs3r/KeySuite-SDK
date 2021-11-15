package it.kdm.doctoolkit.model;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 13/02/14
 * Time: 11.40
 * To change this template use File | Settings | File Templates.
 */
public class FascicoloCriteria extends AnagraficaCriteria {

	public void setOrderBy(String field, Ordinamento.orderByEnum direction) {
    	if (field.equalsIgnoreCase("name"))
        	field = "DES_FASCICOLO";
    	
    	super.setOrderBy(field,direction);
    }

   
}
