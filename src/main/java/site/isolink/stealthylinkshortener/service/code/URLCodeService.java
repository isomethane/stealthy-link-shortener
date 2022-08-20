package site.isolink.stealthylinkshortener.service.code;

import lombok.NonNull;
import site.isolink.stealthylinkshortener.exception.IllegalURLCodeException;
import site.isolink.stealthylinkshortener.model.Link;

/**
 * Service performs conversion between alphanumerical code which is a part of short URL
 * and integer id stored in database.
 */
public interface URLCodeService {
    /**
     * Converts {@link Link} id to URL code.
     * @param id link id
     * @return URL code
     */
    @NonNull
    String idToCode(long id);

    /**
     * Converts code to {@link Link} id.
     * @param code URL code
     * @return link id
     * @throws IllegalURLCodeException if code does not correspond to any valid id
     */
    long codeToId(@NonNull String code) throws IllegalURLCodeException;
}
