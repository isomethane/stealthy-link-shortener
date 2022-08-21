package site.isolink.stealthylinkshortener.service.ip;

/**
 * Indicates whether specified IP is located inside or outside the restricted area.
 */
public enum IPLocationStatus {
    /**
     * IP out of the restricted area
     */
    FREE,
    /**
     * IP in the restricted area
     */
    RESTRICTED,
    /**
     * IP location is unknown
     */
    UNKNOWN
}
