package site.isolink.stealthylinkshortener.web.attribute;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

/**
 * Represents shorten URL form.
 */
@Data
public class ShortenLinkForm {
    /**
     * Option for user-defined safe address.
     */
    public static final String CUSTOM_SAFE_OPTION = "Custom";

    @NotBlank
    @URL
    private String targetAddress;

    @NotBlank
    private String safeAddressOption;

    @URL
    private String customSafeAddress;
}
