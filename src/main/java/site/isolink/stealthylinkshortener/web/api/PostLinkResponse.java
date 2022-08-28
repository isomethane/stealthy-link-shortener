package site.isolink.stealthylinkshortener.web.api;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents API shorten link response.
 */
@Data
public class PostLinkResponse {
    @NonNull
    private String url;
}
