package site.isolink.stealthylinkshortener.exception;

/**
 * Thrown if link code specified in URL cannot be mapped to any valid link id.
 */
public class IllegalURLCodeException extends Exception {
    /**
     * Constructs an {@link IllegalURLCodeException} with the specified detail message.
     * @param message the detail message
     */
    public IllegalURLCodeException(String message) {
        super(message);
    }
}
