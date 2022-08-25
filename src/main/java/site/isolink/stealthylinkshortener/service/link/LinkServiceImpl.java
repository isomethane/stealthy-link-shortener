package site.isolink.stealthylinkshortener.service.link;

import lombok.NonNull;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.isolink.stealthylinkshortener.data.LinkRepository;
import site.isolink.stealthylinkshortener.data.StatisticsRepository;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.IllegalURLCodeException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Link;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.service.code.URLCodeService;
import site.isolink.stealthylinkshortener.service.ip.IPLocationStatus;

import static site.isolink.stealthylinkshortener.util.Conditions.require;

/**
 * Default implementation of {@link LinkService}.
 */
@Service
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final StatisticsRepository statisticsRepository;
    private final URLCodeService urlCodeService;

    @Value("${server.url}")
    private String serverUrl;

    /**
     * Class constructor.
     * @param linkRepository Link CRUD repository
     * @param statisticsRepository Link click statistics CRUD repository
     * @param urlCodeService converter between id and URL code
     */
    @Autowired
    public LinkServiceImpl(@NonNull LinkRepository linkRepository,
                           @NonNull StatisticsRepository statisticsRepository,
                           @NonNull URLCodeService urlCodeService) {
        this.linkRepository = linkRepository;
        this.statisticsRepository = statisticsRepository;
        this.urlCodeService = urlCodeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String putLink(@NonNull String targetAddress, @NonNull String restrictedAddress) throws IllegalRequestException {
        require(!targetAddress.startsWith(serverUrl) && !restrictedAddress.startsWith(serverUrl),
            () -> new IllegalRequestException("Link must not redirect to " + serverUrl));

        require(UrlValidator.getInstance().isValid(targetAddress),
            () -> new IllegalRequestException("Malformed URL: " + targetAddress));
        require(UrlValidator.getInstance().isValid(restrictedAddress),
            () -> new IllegalRequestException("Malformed URL: " + restrictedAddress));

        Link link = linkRepository.save(new Link(targetAddress, restrictedAddress));
        statisticsRepository.save(new Statistics(link.getId(), 0L, 0L, 0L));
        return urlCodeService.idToCode(link.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getLink(@NonNull String code, @NonNull IPLocationStatus location) throws LinkNotFoundException {
        try {
            Link link = linkRepository.findById(urlCodeService.codeToId(code)).orElseThrow(LinkNotFoundException::new);
            return switch (location) {
                case FREE -> {
                    statisticsRepository.addTargetClick(link.getId());
                    yield link.getTargetAddress();
                }
                case RESTRICTED -> {
                    statisticsRepository.addRestrictedClick(link.getId());
                    yield link.getSafeAddress();
                }
                case UNKNOWN -> {
                    statisticsRepository.addUnknownClick(link.getId());
                    yield link.getSafeAddress();
                }
            };
        } catch (IllegalURLCodeException e) {
            throw new LinkNotFoundException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Statistics getLinkStatistics(@NonNull String code) throws LinkNotFoundException {
        try {
            return statisticsRepository.findById(urlCodeService.codeToId(code)).orElseThrow(LinkNotFoundException::new);
        } catch (IllegalURLCodeException e) {
            throw new LinkNotFoundException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull String codeToLink(@NonNull String code) {
        return serverUrl + "/" + code;
    }
}
