package it.kdm.doctoolkit.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocerApiException extends SDKException {

    /**
	 *
	 */
	private static final long serialVersionUID = -1128255833552271893L;

    private static final Pattern PATTERN = Pattern.compile("^(\\d\\d\\d)\\s\\-");

    Exception innerException = null;
    String errorMessage = "";
    //public Integer errorCode = -1;

    public DocerApiException() {
    	super();
    }

    public DocerApiException(String errorMessage, Integer errorCode)
    {
    	super(errorMessage);
    	this.errorMessage = errorMessage;
    	this.errorCode = errorCode;
    }

    public DocerApiException(Exception e)
    {
        super(e);
        this.innerException = e;
        this.errorMessage = e.getMessage();

        if (e instanceof DocerApiException) {
            this.errorCode = ((DocerApiException)e).errorCode;
        } else {
            if (this.errorMessage != null) {
                Matcher matcher = PATTERN.matcher(this.errorMessage);
                if (!matcher.find()) {
                    this.errorCode = 503;
                } else {
                    this.errorCode = Integer.parseInt(matcher.group(1));
                }
            } else {
                this.errorCode = 503;
            }
        }
    }

    public DocerApiException(Exception e, Integer errorCode)
    {
        super(e);
        this.innerException = e;
        this.errorMessage = e.getMessage();
        this.errorCode = errorCode;
    }

	@Override
	public String toString() {
		return "DocerApiException [innerException=" + innerException
				+ ", errorMessage=" + errorMessage + ", errorCode=" + errorCode
				+ "]";
	}
    
    
    
    
}
