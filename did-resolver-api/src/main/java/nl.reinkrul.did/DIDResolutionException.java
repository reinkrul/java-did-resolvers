package nl.reinkrul.did;

public class DIDResolutionException extends Exception {

    public DIDResolutionException(String message) {
        super(message);
    }

    public DIDResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
