package it.kdm.doctoolkit.model;

public class AOOCriteria extends GenericCriteria {

    public AOOCriteria()
    {
    	

    }
    
    public void setOrderBy(String field, Ordinamento.orderByEnum direction) {
    	if (field.equalsIgnoreCase("name"))
        	field = "DES_AOO";
    	
    	super.setOrderBy(field,direction);
    }

   



}
