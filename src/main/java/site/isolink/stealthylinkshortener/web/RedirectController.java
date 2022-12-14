package site.isolink.stealthylinkshortener.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.service.ip.IPService;
import site.isolink.stealthylinkshortener.service.link.LinkService;

/**
 * Controller that resolves short links and redirects
 * to target address if client is outside the restricted area
 * and to safe address otherwise.
 */
@Slf4j
@Controller
public class RedirectController {
    private final LinkService linkService;
    private final IPService ipService;

    /**
     * Constructs a {@link RedirectController} with the specified {@link LinkService} and {@link IPService}.
     * @param linkService link storage service
     * @param ipService ip locating service
     */
    @Autowired
    public RedirectController(@NonNull LinkService linkService, @NonNull IPService ipService) {
        this.linkService = linkService;
        this.ipService = ipService;
    }

    /**
     * Resolves URL with specified id and redirects
     * to target address if client is outside the restricted area
     * and to safe address otherwise.
     * @param id link id
     * @param request request information
     * @return redirect destination
     * @throws LinkNotFoundException if there is no such link
     */
    @GetMapping("/{id}")
    public String getLink(@PathVariable("id") String id, HttpServletRequest request) throws LinkNotFoundException {
        return "redirect:" + linkService.getLink(id, ipService.locateClientIP(request));
    }
}
