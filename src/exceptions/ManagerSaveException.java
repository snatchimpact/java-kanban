package exceptions;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final Throwable cause) {
        super(cause);
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}


