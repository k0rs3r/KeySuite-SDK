
/**
 * WSFirmaFirmaExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients.firma;

public class WSFirmaFirmaExceptionException extends Exception{

    private static final long serialVersionUID = 1461663513444L;

    private it.kdm.doctoolkit.clients.firma.WSFirmaStub.WSFirmaFirmaException faultMessage;


        public WSFirmaFirmaExceptionException() {
            super("WSFirmaFirmaExceptionException");
        }

        public WSFirmaFirmaExceptionException(String s) {
           super(s);
        }

        public WSFirmaFirmaExceptionException(String s, Throwable ex) {
          super(s, ex);
        }

        public WSFirmaFirmaExceptionException(Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.firma.WSFirmaStub.WSFirmaFirmaException msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.firma.WSFirmaStub.WSFirmaFirmaException getFaultMessage(){
       return faultMessage;
    }
}
    