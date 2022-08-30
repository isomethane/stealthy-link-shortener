package site.isolink.stealthylinkshortener.service.link;

import lombok.NonNull;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.isolink.stealthylinkshortener.data.LinkRepository;
import site.isolink.stealthylinkshortener.data.StatisticsRepository;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Link;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.service.id.IdService;
import site.isolink.stealthylinkshortener.service.ip.IPLocationStatus;

import static site.isolink.stealthylinkshortener.util.Conditions.require;

/**
 * Default implementation of {@link LinkService}.
 */
@Service
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final StatisticsRepository statisticsRepository;
    private final IdService idService;

    @Value("${server.url}")
    private String serverUrl;

    /**
     * Class constructor.
     * @param linkRepository Link CRUD repository
     * @param statisticsRepository Link click statistics CRUD repository
     * @param idService id generator
     */
    @Autowired
    public LinkServiceImpl(@NonNull LinkRepository linkRepository,
                           @NonNull StatisticsRepository statisticsRepository,
                           @NonNull IdService idService) {
        this.linkRepository = linkRepository;
        this.statisticsRepository = statisticsRepository;
        this.idService = idService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String putLink(@NonNull String targetAddress, @NonNull String safeAddress) throws IllegalRequestException {
        require(UrlValidator.getInstance().isValid(targetAddress),
            () -> new IllegalRequestException("Malformed URL: " + targetAddress));
        require(UrlValidator.getInstance().isValid(safeAddress),
            () -> new IllegalRequestException("Malformed URL: " + safeAddress));

        String id = idService.generateId();
        linkRepository.save(new Link(id, targetAddress, safeAddress));
        statisticsRepository.save(new Statistics(id, 0L, 0L, 0L));
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getLink(@NonNull String id, @NonNull IPLocationStatus location) throws LinkNotFoundException {
        Link link = linkRepository.findById(id).orElseThrow(LinkNotFoundException::new);
        return switch (location) {
            case FREE -> {
                statisticsRepository.findAndIncrementTargetClicksById(id);
                yield link.getTargetAddress();
            }
            case RESTRICTED -> {
                statisticsRepository.findAndIncrementRestrictedClicksById(id);
                yield link.getSafeAddress();
            }
            case UNKNOWN -> {
                statisticsRepository.findAndIncrementUnknownClicksById(id);
                yield link.getSafeAddress();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Statistics getLinkStatistics(@NonNull String id) throws LinkNotFoundException {
        return statisticsRepository.findById(id).orElseThrow(LinkNotFoundException::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull String linkWithId(@NonNull String id) {
        return serverUrl + "/" + id;
    }
}
