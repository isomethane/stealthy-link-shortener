package site.isolink.stealthylinkshortener.web.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents full URL click statistics to show user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsInfo {
    @NotNull
    private String shortLink;

    private long targetClicks;

    private long restrictedClicks;

    private long unknownClicks;
}
