package site.isolink.stealthylinkshortener.service.id;

import lombok.NonNull;

/**
 * Generates unique short {@link String} ids.
 */
public interface IdService {
    /**
     * Generates unique id.
     * @return id
     */
    @NonNull
    String generateId();
}
