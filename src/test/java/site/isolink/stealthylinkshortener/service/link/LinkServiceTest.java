package site.isolink.stealthylinkshortener.service.link;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import site.isolink.stealthylinkshortener.exception.IllegalRequestException;
import site.isolink.stealthylinkshortener.exception.LinkNotFoundException;
import site.isolink.stealthylinkshortener.model.Statistics;
import site.isolink.stealthylinkshortener.service.ip.IPLocationStatus;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class LinkServiceTest {
    private static final String[] TARGET_ADDRESS = {
        "https://zona.media/", "https://meduza.io/", "https://fbk.info/"
    };
    private static final String[] RESTRICTED_ADDRESS = {
        "https://youtu.be/dQw4w9WgXcQ", "https://youtu.be/iik25wqIuFo", "https://youtu.be/IAJeZwXk3cU"
    };

    private final LinkService linkService;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.11");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    LinkServiceTest(LinkService linkService) {
        this.linkService = linkService;
    }

    @Test
    void putLinkReturnsUniqueIds() throws IllegalRequestException {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = linkService.putLink(
                TARGET_ADDRESS[i % TARGET_ADDRESS.length], RESTRICTED_ADDRESS[i % RESTRICTED_ADDRESS.length]
            );
            assertFalse(ids.contains(id));
            ids.add(id);
        }
    }

    @Test
    void putLinkMalformedURL() {
        assertThrows(
            IllegalRequestException.class,
            () -> linkService.putLink("not url", RESTRICTED_ADDRESS[0])
        );
        assertThrows(
                IllegalRequestException.class,
            () -> linkService.putLink(TARGET_ADDRESS[0], "not url")
        );
    }

    @Test
    void getLink() throws LinkNotFoundException, IllegalRequestException {
        for (int i = 0; i < TARGET_ADDRESS.length; i++) {
            String id = linkService.putLink(TARGET_ADDRESS[i], RESTRICTED_ADDRESS[i]);
            assertEquals(TARGET_ADDRESS[i], linkService.getLink(id, IPLocationStatus.FREE));
            assertEquals(RESTRICTED_ADDRESS[i], linkService.getLink(id, IPLocationStatus.RESTRICTED));
            assertEquals(RESTRICTED_ADDRESS[i], linkService.getLink(id, IPLocationStatus.UNKNOWN));
        }
    }

    @Test
    void getLinkNotFound() {
        assertThrows(LinkNotFoundException.class, () -> linkService.getLink("", IPLocationStatus.FREE));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLink("A", IPLocationStatus.FREE));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLink("ABCDEF", IPLocationStatus.FREE));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLink(".", IPLocationStatus.FREE));
    }

    @Test
    void getLinkStatistics() throws LinkNotFoundException, IllegalRequestException {
        Random random = new Random(111);

        for (int i = 0; i < TARGET_ADDRESS.length; i++) {
            String id = linkService.putLink(TARGET_ADDRESS[i], RESTRICTED_ADDRESS[i]);

            int targetClicks = random.nextInt(20);
            for (int c = 0; c < targetClicks; c++) {
                linkService.getLink(id, IPLocationStatus.FREE);
            }

            int restrictedClicks = random.nextInt(20);
            for (int c = 0; c < restrictedClicks; c++) {
                linkService.getLink(id, IPLocationStatus.RESTRICTED);
            }

            int unknownClicks = random.nextInt(20);
            for (int c = 0; c < unknownClicks; c++) {
                linkService.getLink(id, IPLocationStatus.UNKNOWN);
            }

            Statistics statistics = linkService.getLinkStatistics(id);
            assertEquals(targetClicks, statistics.getTargetClicks());
            assertEquals(restrictedClicks, statistics.getRestrictedClicks());
            assertEquals(unknownClicks, statistics.getUnknownClicks());
        }
    }

    @Test
    void getLinkStatisticsNotFound() {
        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkStatistics(""));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkStatistics("A"));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkStatistics("ABCDEF"));
        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkStatistics("."));
    }
}
