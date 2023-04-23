package exception;

public class ManagerException extends RuntimeException{

    public ManagerException(String textErr){
        super(textErr);
    }

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }
    public ManagerException(Throwable cause) {
        super(cause);
    }
}
