
/**
 * ProtocollazioneExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class ProtocollazioneExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1401439791774L;
    
    private it.kdm.doctoolkit.clients.WSProtocollazioneStub.ProtocollazioneExceptionE faultMessage;

    
        public ProtocollazioneExceptionException() {
            super("ProtocollazioneExceptionException");
        }

        public ProtocollazioneExceptionException(java.lang.String s) {
           super(s);
        }

        public ProtocollazioneExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public ProtocollazioneExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSProtocollazioneStub.ProtocollazioneExceptionE msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSProtocollazioneStub.ProtocollazioneExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    