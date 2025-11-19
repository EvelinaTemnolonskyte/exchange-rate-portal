package lt.fxportal.ApplicationServices.Exceptions;


public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

