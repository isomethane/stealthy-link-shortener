package site.isolink.stealthylinkshortener.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.service.link.LinkService;
import site.isolink.stealthylinkshortener.web.attribute.LinkInfo;
import site.isolink.stealthylinkshortener.web.attribute.SafeLinkOption;
import site.isolink.stealthylinkshortener.web.attribute.ShortenLinkForm;
import site.isolink.stealthylinkshortener.web.attribute.StatisticsInfo;
import site.isolink.stealthylinkshortener.web.validation.ShortenLinkFormValidator;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application frontend controller.
 */
@Slf4j
@Controller
@SessionAttributes("linkInfo")
public class WebsiteController {
    private final LinkService linkService;
    private final ShortenLinkFormValidator validator;
    @Value("#{${safe-links}}")
    private Map<String, String> safeLinks = new LinkedHashMap<>();
    @Value("${restricted-country-codes}")
    private List<String> restrictedCountryCodes;

    /**
     * Constructs a {@link WebsiteController} with the specified
     * {@link LinkService} and {@link ShortenLinkFormValidator}.
     * @param linkService link storage service
     * @param validator {@link ShortenLinkForm} validator
     */
    @Autowired
    public WebsiteController(LinkService linkService,
                             ShortenLinkFormValidator validator) {
        this.linkService = linkService;
        this.validator = validator;
    }

    /**
     * Shows shorten URL form.
     * @return shorten URL form page
     */
    @GetMapping
    public String showShortenLinkForm() {
        return "shorten";
    }

    /**
     * Handles shorten URL request and redirects to information page.
     * @param linkInfo link info to fill
     * @param shortenLinkForm request form info
     * @param errors form validation errors
     * @return information page redirect if successful
     * @throws IllegalRequestException if request is still incorrect after validation (should not happen normally)
     */
    @PostMapping
    public String postLink(@ModelAttribute("linkInfo") LinkInfo linkInfo,
                           @Valid ShortenLinkForm shortenLinkForm,
                           Errors errors) throws IllegalRequestException {
        validator.validate(shortenLinkForm, errors);
        if (errors.hasErrors()) {
            return "shorten";
        }

        String targetAddress = shortenLinkForm.getTargetAddress();
        String safeAddress = ShortenLinkForm.CUSTOM_SAFE_OPTION.equals(shortenLinkForm.getSafeAddressOption()) ?
                shortenLinkForm.getCustomSafeAddress() : safeLinks.get(shortenLinkForm.getSafeAddressOption());
        String code = linkService.putLink(targetAddress, safeAddress);
        String shortLink = linkService.codeToLink(code);

        linkInfo.setShortLink(shortLink);
        linkInfo.setTargetLink(targetAddress);
        linkInfo.setSafeLink(safeAddress);
        linkInfo.setStatsLink("/stats/" + code);

        return "redirect:/url";
    }

    /**
     * Shows shortened URL full information.
     * @param linkInfo link info
     * @param errors link info validation errors
     * @param sessionStatus session status to set complete
     * @return link info page
     */
    @GetMapping("/url")
    public String showLinkInfo(@Valid @ModelAttribute("linkInfo") LinkInfo linkInfo,
                               Errors errors,
                               SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "redirect:/";
        }
        sessionStatus.setComplete();

        return "linkInfo";
    }

    /**
     * Shows URL click statistics.
     * @param model model to save statistics
     * @param code URL short code
     * @return statistics page
     * @throws LinkNotFoundException if there is no such link
     */
    @GetMapping("/stats/{code}")
    public String showLinkStatistics(Model model, @PathVariable String code) throws LinkNotFoundException {
        Statistics stats = linkService.getLinkStatistics(code);
        model.addAttribute(
            "stats",
            new StatisticsInfo(
                linkService.codeToLink(code),
                stats.getTargetClicks(),
                stats.getRestrictedClicks(),
                stats.getUnknownClicks()
            )
        );
        return "stats";
    }

    /**
     * Returns favicon location.
     * @return favicon location
     */
    @GetMapping("/favicon.ico")
    public String getFavicon() {
        return "forward:/icons/favicon.ico";
    }

    @ModelAttribute(name = "shortenLinkForm")
    private ShortenLinkForm shortenLinkForm(@ModelAttribute("safeLinks") List<SafeLinkOption> safeLinks) {
        var form = new ShortenLinkForm();
        form.setSafeAddressOption(safeLinks.get(0).name());
        return form;
    }

    @ModelAttribute(name = "linkInfo")
    private LinkInfo linkInfo() {
        return new LinkInfo();
    }

    @ModelAttribute(name = "safeLinks")
    private List<SafeLinkOption> safeLinks() {
        return safeLinks
            .entrySet()
            .stream()
            .map(entry -> new SafeLinkOption(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @ModelAttribute(name = "restrictedCountries")
    private String restrictedCountries() {
        return restrictedCountryCodes
            .stream()
            .map(code -> new Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.ENGLISH))
            .collect(Collectors.joining(", "));
    }

}
