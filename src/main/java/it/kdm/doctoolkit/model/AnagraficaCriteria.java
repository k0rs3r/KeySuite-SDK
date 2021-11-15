package it.kdm.doctoolkit.model;

/**
 * Created with IntelliJ IDEA.
 * User: stefano.vigna
 * Date: 14/03/14
 * Time: 19.02
 * To change this template use File | Settings | File Templates.
 */
public class AnagraficaCriteria extends GenericCriteria {
    @Override
    protected void initProperties() {
        this.setProperty("$MAX_RESULTS","100");
    }
}
