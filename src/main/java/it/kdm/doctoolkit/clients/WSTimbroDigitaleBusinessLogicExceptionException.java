
/**
 * WSTimbroDigitaleBusinessLogicExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class WSTimbroDigitaleBusinessLogicExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1421165387008L;
    
    private it.kdm.doctoolkit.clients.WSTimbroDigitaleStub.WSTimbroDigitaleBusinessLogicException faultMessage;

    
        public WSTimbroDigitaleBusinessLogicExceptionException() {
            super("WSTimbroDigitaleBusinessLogicExceptionException");
        }

        public WSTimbroDigitaleBusinessLogicExceptionException(java.lang.String s) {
           super(s);
        }

        public WSTimbroDigitaleBusinessLogicExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public WSTimbroDigitaleBusinessLogicExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSTimbroDigitaleStub.WSTimbroDigitaleBusinessLogicException msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSTimbroDigitaleStub.WSTimbroDigitaleBusinessLogicException getFaultMessage(){
       return faultMessage;
    }
}
    