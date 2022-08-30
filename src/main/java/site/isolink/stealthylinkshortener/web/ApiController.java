package site.isolink.stealthylinkshortener.web;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.web.api.GetStatisticsResponse;
import site.isolink.stealthylinkshortener.web.api.PostLinkRequest;
import site.isolink.stealthylinkshortener.web.api.PostLinkResponse;
import site.isolink.stealthylinkshortener.service.link.LinkService;

/**
 * Application API gateway.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {
    @NonNull
    private final LinkService linkService;

    /**
     * Constructs an {@link ApiController} with specified {@link LinkService}.
     * @param linkService link storage service
     */
    @Autowired
    public ApiController(@NonNull LinkService linkService) {
        this.linkService = linkService;
    }

    /**
     * Shorten link API method.
     * @param request request information
     * @return {@link PostLinkResponse} with created URL
     * @throws IllegalRequestException if any link in this request does not satisfy conditions
     */
    @PostMapping("/shorten")
    public ResponseEntity<PostLinkResponse> shortenLink(@RequestBody PostLinkRequest request) throws IllegalRequestException {
        String id = linkService.putLink(request.getTargetAddress(), request.getSafeAddress());
        String url = linkService.linkWithId(id);
        log.info("Created {}", url);
        return ResponseEntity.ok(new PostLinkResponse(url));
    }

    /**
     * Get statistics API method.
     * @param id link id
     * @return click statistics
     * @throws LinkNotFoundException if there is no such link
     */
    @GetMapping("/stats/{id}")
    public ResponseEntity<GetStatisticsResponse> getStatistics(@PathVariable("id") String id) throws LinkNotFoundException {
        Statistics statistics = linkService.getLinkStatistics(id);
        return ResponseEntity.ok(new GetStatisticsResponse(
            id,
            statistics.getTargetClicks(),
            statistics.getRestrictedClicks(),
            statistics.getUnknownClicks()
        ));
    }
}
