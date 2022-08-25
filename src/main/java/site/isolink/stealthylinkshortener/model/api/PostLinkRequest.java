package site.isolink.stealthylinkshortener.model.api;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents API shorten link request.
 */
@Data
public class PostLinkRequest {
    @NonNull
    private String targetAddress;

    @NonNull
    private String restrictedAddress;
}
