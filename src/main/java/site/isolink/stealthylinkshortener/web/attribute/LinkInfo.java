package site.isolink.stealthylinkshortener.web.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents full URL information to show user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkInfo {
    @NotNull
    private String shortLink;

    @NotNull
    private String targetLink;

    @NotNull
    private String safeLink;

    @NotNull
    private String statsLink;
}
