
/**
 * DocerServicesDocerExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class DocerServicesDocerExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1401446748648L;
    
    private it.kdm.doctoolkit.clients.DocerServicesStub.DocerServicesDocerException faultMessage;

    
        public DocerServicesDocerExceptionException() {
            super("DocerServicesDocerExceptionException");
        }

        public DocerServicesDocerExceptionException(java.lang.String s) {
           super(s);
        }

        public DocerServicesDocerExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public DocerServicesDocerExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.DocerServicesStub.DocerServicesDocerException msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.DocerServicesStub.DocerServicesDocerException getFaultMessage(){
       return faultMessage;
    }
}
    