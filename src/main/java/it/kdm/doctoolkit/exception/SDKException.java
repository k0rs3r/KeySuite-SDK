package it.kdm.doctoolkit.exception;

import com.google.common.base.Throwables;

public abstract class SDKException extends Exception {
    public Integer errorCode = -1;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public SDKException(){
        super();
    }

    public SDKException(String message) {
        super(message);
    }

    public SDKException(String message, Throwable cause) {
        super(message, cause);
    }

    public SDKException(Throwable cause) {
        super(cause);
    }

    public String getStackTraceAsString(){
        if(getCause() != null)
            return Throwables.getStackTraceAsString(getCause());
        else{
            return "Non ci sono ulteriori dettagli.";
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s]:%s",this.getClass().getSimpleName(), errorCode,getMessage());
    }

}
