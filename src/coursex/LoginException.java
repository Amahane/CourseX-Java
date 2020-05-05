package coursex;

public class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Exception innerException) {
        super(message, innerException);
    }
}
