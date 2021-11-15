
/**
 * WSVerificaDocumentiVerificaDocumentoExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class WSVerificaDocumentiVerificaDocumentoExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1422005608528L;
    
    private it.kdm.doctoolkit.clients.WSVerificaDocumentiStub.WSVerificaDocumentiVerificaDocumentoException faultMessage;

    
        public WSVerificaDocumentiVerificaDocumentoExceptionException() {
            super("WSVerificaDocumentiVerificaDocumentoExceptionException");
        }

        public WSVerificaDocumentiVerificaDocumentoExceptionException(java.lang.String s) {
           super(s);
        }

        public WSVerificaDocumentiVerificaDocumentoExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public WSVerificaDocumentiVerificaDocumentoExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSVerificaDocumentiStub.WSVerificaDocumentiVerificaDocumentoException msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSVerificaDocumentiStub.WSVerificaDocumentiVerificaDocumentoException getFaultMessage(){
       return faultMessage;
    }
}
    