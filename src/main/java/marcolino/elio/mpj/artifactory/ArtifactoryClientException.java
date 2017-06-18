package marcolino.elio.mpj.artifactory;

/**
 * Exception thrown by the artifactory client
 * @author elio
 *
 */
public class ArtifactoryClientException extends Exception {

    public ArtifactoryClientException() {
        super();
    }

    public ArtifactoryClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ArtifactoryClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtifactoryClientException(String message) {
        super(message);
    }

    public ArtifactoryClientException(Throwable cause) {
        super(cause);
    }
    
}
