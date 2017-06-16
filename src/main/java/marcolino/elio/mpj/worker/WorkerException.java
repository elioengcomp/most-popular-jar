package marcolino.elio.mpj.worker;

/**
 * Runtime exception thrown by background workers
 * @author elio
 *
 */
public class WorkerException extends RuntimeException{

    public WorkerException() {
        super();
    }

    public WorkerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerException(String message) {
        super(message);
    }

    public WorkerException(Throwable cause) {
        super(cause);
    }

}
