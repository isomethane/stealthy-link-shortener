package site.isolink.stealthylinkshortener.service.link;

import lombok.NonNull;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.service.ip.IPLocationStatus;

/**
 * Short links storage service.
 */
public interface LinkService {
    /**
     * Saves link and obtains unique short code for it.
     * @param targetAddress address to redirect clients outside the restricted area
     * @param safeAddress address to redirect clients inside the restricted area
     * @return alphanumerical short code
     * @throws IllegalRequestException if any link in this request does not satisfy conditions
     */
    @NonNull
    String putLink(@NonNull String targetAddress, @NonNull String safeAddress) throws IllegalRequestException;

    /**
     * Returns redirect URL for specified code depending on client's {@link IPLocationStatus}.
     * @param code link code
     * @param location client location status
     * @return redirect URL
     * @throws LinkNotFoundException if there is no link with specified code
     */
    @NonNull
    String getLink(@NonNull String code, @NonNull IPLocationStatus location) throws LinkNotFoundException;

    /**
     * Returns click statistics for specified code.
     * @param code link code
     * @return link click statistics
     * @throws LinkNotFoundException if there is no link with specified code
     */
    @NonNull
    Statistics getLinkStatistics(@NonNull String code) throws LinkNotFoundException;

    /**
     * Returns URL with the specified short code;
     * @param code link code
     * @return URL
     */
    @NonNull
    String codeToLink(@NonNull String code);
}
