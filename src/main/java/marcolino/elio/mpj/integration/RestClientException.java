package marcolino.elio.mpj.integration;

/**
 * Exception thrown by a rest client request
 * @author elio
 *
 */
public class RestClientException extends Exception{

    public RestClientException() {
        super();
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

}
