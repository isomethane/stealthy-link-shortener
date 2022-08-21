package site.isolink.stealthylinkshortener.service.ip;

import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * IP locating service.
 */
public interface IPService {
    /**
     * Extracts client public IP from {@link HttpServletRequest} and determines whether it is located inside
     * or outside the restricted area.
     * @param request request information
     * @return location status
     */
    @NonNull
    IPLocationStatus locateClientIP(@NonNull HttpServletRequest request);

    /**
     * Extracts client public IP from {@link HttpServletRequest} if possible.
     * @param request request information
     * @return an {@link Optional} with client public IP if located successfully
     */
    @NonNull
    Optional<String> getClientIP(@NonNull HttpServletRequest request);

    /**
     * Determines whether IP is located inside or outside the restricted area.
     * @param ip IPv4/IPv6 address
     * @return location status
     */
    @NonNull
    IPLocationStatus locateIP(@NonNull String ip);
}
