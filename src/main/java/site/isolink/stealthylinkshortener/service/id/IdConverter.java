package site.isolink.stealthylinkshortener.service.id;

import lombok.NonNull;

/**
 * Performs conversion from number into short {@link String} id.
 */
interface IdConverter {
    /**
     * Returns a unique short {@link String} representation of the specified value.
     * @param value numeric id representation
     * @return {@link String} id representation
     */
    @NonNull
    String getId(long value);
}
