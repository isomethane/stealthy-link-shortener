package site.isolink.stealthylinkshortener.service.id;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static site.isolink.stealthylinkshortener.util.Conditions.require;

/**
 * Default implementation of {@link IdConverter}.
 */
@Component
class IdConverterImpl implements IdConverter {
    private static final int MIN_ID_LENGTH = 4;
    private static final long MAX_VALUE = 1_000_000_000_000_000L;
    private static final List<Character> SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        .chars().mapToObj((s) -> (char) s).collect(Collectors.toList());
    private static final int BASE = SYMBOLS.size();
    private static final int OFFSET = (BASE + 2) * BASE + 3;

    static {
        Collections.shuffle(SYMBOLS, new Random(30));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String getId(long value) {
        require(value >= 0 && value <= MAX_VALUE,
            () -> new IllegalArgumentException("Invalid id out of range [0, " + MAX_VALUE + "]"));

        long number = value + OFFSET;
        var builder = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % BASE);
            builder.append(SYMBOLS.get(digit));
            number /= BASE;
        }
        builder.append(SYMBOLS.get(0).toString().repeat(Math.max(0, MIN_ID_LENGTH - builder.length())));
        return builder.reverse().toString();
    }
}
