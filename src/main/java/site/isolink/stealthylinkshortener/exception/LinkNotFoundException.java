package site.isolink.stealthylinkshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown if specified link does not exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LinkNotFoundException extends Exception {
    /**
     * Constructs a {@link LinkNotFoundException} with no detail message.
     */
    public LinkNotFoundException() {}

    /**
     * Constructs a {@link LinkNotFoundException} with the specified detail message.
     * @param message the detail message
     */
    public LinkNotFoundException(String message) {
        super(message);
    }
}
