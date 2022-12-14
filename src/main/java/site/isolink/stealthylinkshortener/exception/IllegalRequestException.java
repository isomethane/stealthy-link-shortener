package site.isolink.stealthylinkshortener.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown if API request is illegal.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRequestException extends Exception {
    /**
     * Constructs an {@link IllegalRequestException} with the specified detail message.
     * @param message the detail message
     */
    public IllegalRequestException(@NonNull String message) {
        super(message);
    }
}
