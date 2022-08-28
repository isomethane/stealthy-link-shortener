package site.isolink.stealthylinkshortener.web.validation;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import site.isolink.stealthylinkshortener.web.attribute.ShortenLinkForm;

/**
 * Validates {@link ShortenLinkForm} ensuring it contains safe address.
 */
@Component
public class ShortenLinkFormValidator implements Validator {
    /**
     * Validates instances of {@link ShortenLinkForm} class.
     * @param clazz the {@link Class} that this {@link Validator} is
     * being asked if it can {@link #validate(Object, Errors) validate}
     * @return true if this {@link Validator} can indeed {@link #validate(Object, Errors)} instances of the supplied clazz
     */
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ShortenLinkForm.class.isAssignableFrom(clazz);
    }

    /**
     * Validates {@link ShortenLinkForm} ensuring it contains safe address.
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     */
    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ShortenLinkForm form = (ShortenLinkForm) target;
        if (ShortenLinkForm.CUSTOM_SAFE_OPTION.equals(form.getSafeAddressOption())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "customSafeAddress", "NotBlank"
            );
        }
    }
}
