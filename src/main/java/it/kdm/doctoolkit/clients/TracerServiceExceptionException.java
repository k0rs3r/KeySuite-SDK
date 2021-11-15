
/**
 * TracerServiceExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package it.kdm.doctoolkit.clients;

public class TracerServiceExceptionException extends Exception{

    private static final long serialVersionUID = 1458298625567L;

    private TracerServiceStub.TracerServiceException faultMessage;


        public TracerServiceExceptionException() {
            super("TracerServiceExceptionException");
        }

        public TracerServiceExceptionException(String s) {
           super(s);
        }

        public TracerServiceExceptionException(String s, Throwable ex) {
          super(s, ex);
        }

        public TracerServiceExceptionException(Throwable cause) {
            super(cause);
        }


    public void setFaultMessage(TracerServiceStub.TracerServiceException msg){
       faultMessage = msg;
    }

    public TracerServiceStub.TracerServiceException getFaultMessage(){
       return faultMessage;
    }
}
    