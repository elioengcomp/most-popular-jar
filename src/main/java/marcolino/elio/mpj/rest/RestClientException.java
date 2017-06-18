package marcolino.elio.mpj.rest;

/**
 * Exception thrown by a rest client request
 * @author elio
 *
 */
public class RestClientException extends Exception{

    private int statusCode;
    
    public RestClientException() {
        super();
    }
    
    public RestClientException(int statusCode) {
        this();
        this.statusCode = statusCode;
    }

    public RestClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestClientException(String message) {
        super(message);
    }

    public RestClientException(Throwable cause) {
        super(cause);
    }
    
    public RestClientException(int statusCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    public RestClientException(int statusCode, String message, Throwable cause) {
        this(message, cause);
        this.statusCode = statusCode;
    }

    public RestClientException(int statusCode, String message) {
        this(message);
        this.statusCode = statusCode;
    }

    public RestClientException(int statusCode, Throwable cause) {
        this(cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
    
        return statusCode;
    }

}
