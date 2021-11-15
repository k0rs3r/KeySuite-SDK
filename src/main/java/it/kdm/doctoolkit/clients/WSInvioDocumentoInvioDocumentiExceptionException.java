
/**
 * WSInvioDocumentoInvioDocumentiExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class WSInvioDocumentoInvioDocumentiExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1435139378157L;
    
    private it.kdm.doctoolkit.clients.WSInvioDocumentoStub.WSInvioDocumentoInvioDocumentiException faultMessage;

    
        public WSInvioDocumentoInvioDocumentiExceptionException() {
            super("WSInvioDocumentoInvioDocumentiExceptionException");
        }

        public WSInvioDocumentoInvioDocumentiExceptionException(java.lang.String s) {
           super(s);
        }

        public WSInvioDocumentoInvioDocumentiExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public WSInvioDocumentoInvioDocumentiExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSInvioDocumentoStub.WSInvioDocumentoInvioDocumentiException msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSInvioDocumentoStub.WSInvioDocumentoInvioDocumentiException getFaultMessage(){
       return faultMessage;
    }
}
    