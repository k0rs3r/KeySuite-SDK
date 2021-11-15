
/**
 * FascicolazioneExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class FascicolazioneExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1401439750142L;
    
    private it.kdm.doctoolkit.clients.WSFascicolazioneStub.FascicolazioneExceptionE faultMessage;

    
        public FascicolazioneExceptionException() {
            super("FascicolazioneExceptionException");
        }

        public FascicolazioneExceptionException(java.lang.String s) {
           super(s);
        }

        public FascicolazioneExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public FascicolazioneExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(it.kdm.doctoolkit.clients.WSFascicolazioneStub.FascicolazioneExceptionE msg){
       faultMessage = msg;
    }
    
    public it.kdm.doctoolkit.clients.WSFascicolazioneStub.FascicolazioneExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    