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
     * Saves link and returns its unique id.
     * @param targetAddress address to redirect clients outside the restricted area
     * @param safeAddress address to redirect clients inside the restricted area
     * @return link id
     * @throws IllegalRequestException if any link in this request does not satisfy conditions
     */
    @NonNull
    String putLink(@NonNull String targetAddress, @NonNull String safeAddress) throws IllegalRequestException;

    /**
     * Returns redirect URL for specified link id depending on client's {@link IPLocationStatus}.
     * @param id link id
     * @param location client location status
     * @return redirect URL
     * @throws LinkNotFoundException if there is no link with the specified id
     */
    @NonNull
    String getLink(@NonNull String id, @NonNull IPLocationStatus location) throws LinkNotFoundException;

    /**
     * Returns click statistics for specified link id.
     * @param id link id
     * @return link click statistics
     * @throws LinkNotFoundException if there is no link with the specified id
     */
    @NonNull
    Statistics getLinkStatistics(@NonNull String id) throws LinkNotFoundException;

    /**
     * Returns URL with the specified id.
     * @param id link id
     * @return URL
     */
    @NonNull
    String linkWithId(@NonNull String id);
}
