package site.isolink.stealthylinkshortener.service.code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.isolink.stealthylinkshortener.exception.IllegalURLCodeException;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class URLCodeServiceTest {
    private final URLCodeService urlCodeService;

    @Autowired
    URLCodeServiceTest(URLCodeService urlCodeService) {
        this.urlCodeService = urlCodeService;
    }

    @Test
    void idToCodeToIdRemainsTheSame() throws IllegalURLCodeException {
        for (int i = 0; i < 10000; i++) {
            assertEquals(i, urlCodeService.codeToId(urlCodeService.idToCode(i)));
        }

        var random = new Random(30);
        for (int i = 0; i < 10000; i++) {
            long id = random.nextLong(0, 1_000_000_000_000L);
            assertEquals(id, urlCodeService.codeToId(urlCodeService.idToCode(id)));
        }
    }

    @Test
    void idToCodeIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> urlCodeService.idToCode(-1));
        assertThrows(IllegalArgumentException.class, () -> urlCodeService.idToCode(-12345));
        assertThrows(IllegalArgumentException.class, () -> urlCodeService.idToCode(Long.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> urlCodeService.idToCode(Long.MAX_VALUE));
    }

    @Test
    void codeToIdIllegalArgument() {
        assertThrows(IllegalURLCodeException.class, () -> urlCodeService.codeToId("0"));
        assertThrows(IllegalURLCodeException.class, () -> urlCodeService.codeToId("123"));
        assertThrows(IllegalURLCodeException.class, () -> urlCodeService.codeToId("123-"));
        assertThrows(IllegalURLCodeException.class, () -> urlCodeService.codeToId("-123"));
    }

    @Test
    void codesEqualToCanonical() throws IOException, IllegalURLCodeException {
        try (var in = this.getClass().getClassLoader().getResourceAsStream("random_codes.txt")) {
            assert in != null;
            var scanner = new Scanner(in);
            while (scanner.hasNextLong()) {
                long id = scanner.nextLong();
                String code = scanner.next();

                assertEquals(id, urlCodeService.codeToId(code));
                assertEquals(code, urlCodeService.idToCode(id));
            }
        }
    }
}
