package site.isolink.stealthylinkshortener.model.api;

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
    private String code;

    private long targetClicks;

    private long restrictedClicks;

    private long unknownClicks;
}
