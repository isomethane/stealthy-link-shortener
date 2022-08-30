package site.isolink.stealthylinkshortener.web.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents API statistics response.
 */
@Data
@AllArgsConstructor
public class GetStatisticsResponse {
    @NonNull
    private String id;

    private long targetClicks;

    private long restrictedClicks;

    private long unknownClicks;
}
