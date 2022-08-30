package site.isolink.stealthylinkshortener.service.id;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdConverterTest {
    private final IdConverter idConverter;

    @Autowired
    IdConverterTest(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Test
    void getIdIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> idConverter.getId(-1));
        assertThrows(IllegalArgumentException.class, () -> idConverter.getId(-12345));
        assertThrows(IllegalArgumentException.class, () -> idConverter.getId(Long.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> idConverter.getId(Long.MAX_VALUE));
    }

    @Test
    void idsEqualToCanonical() throws IOException {
        try (var in = this.getClass().getClassLoader().getResourceAsStream("random_ids.txt")) {
            assert in != null;
            var scanner = new Scanner(in);
            while (scanner.hasNextLong()) {
                long value = scanner.nextLong();
                String id = scanner.next();
                assertEquals(id, idConverter.getId(value));
            }
        }
    }

    @Test
    void idsAreUnique() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000000; i++) {
            String id = idConverter.getId(i);
            assertFalse(ids.contains(id));
            ids.add(id);
        }
    }
}
