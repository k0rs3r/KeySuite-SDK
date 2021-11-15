package it.kdm.orchestratore.exception;

import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.exception.SDKException;


@SuppressWarnings("serial")
public class KDMException extends SDKException {

    private final Integer DOCER_ERROR_CLASS = 10000;

    public KDMException(int errorCode) {
        this.errorCode = errorCode;
    }

    public KDMException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public KDMException(String message, Throwable cause) {
        super(message, cause);

        if (cause instanceof DocerApiException)
            this.errorCode = ((DocerApiException) cause).errorCode+DOCER_ERROR_CLASS;

        if (cause instanceof KDMException)
            this.errorCode = ((KDMException) cause).getErrorCode();
    }

    public KDMException(DocerApiException e) {
        super(e);
        this.errorCode = e.errorCode+DOCER_ERROR_CLASS;
    }

    public KDMException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
