
/**
 * RegistrazioneExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class RegistrazioneExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1401439824290L;
    
    private it.kdm.doctoolkit.clients.WSRegistrazioneStub.RegistrazioneExceptionE faultMessage;

    
        public RegistrazioneExceptionException() {
            super("RegistrazioneExceptionException");
        }

        public RegistrazioneExceptionException(java.lang.String s) {
           super(s);
        }

        public RegistrazioneExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public RegistrazioneExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSRegistrazioneStub.RegistrazioneExceptionE msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSRegistrazioneStub.RegistrazioneExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    