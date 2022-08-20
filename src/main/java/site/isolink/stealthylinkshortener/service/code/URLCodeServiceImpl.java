package site.isolink.stealthylinkshortener.service.code;

import com.google.common.collect.Streams;
import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import site.isolink.stealthylinkshortener.exception.IllegalURLCodeException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static site.isolink.stealthylinkshortener.util.Conditions.require;

/**
 * Default implementation of {@link URLCodeService}.
 */
@Service
public class URLCodeServiceImpl implements URLCodeService {
    private static final int MIN_CODE_LENGTH = 4;
    private static final long MAX_ID = 1_000_000_000_000L;
    private static final List<Character> SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        .chars().mapToObj((s) -> (char) s).collect(Collectors.toList());
    private static final Map<Character, Long> SYMBOL_TO_NUMBER;
    private static final int BASE = SYMBOLS.size();
    private static final int OFFSET = (BASE + 2) * BASE + 3;

    static {
        Collections.shuffle(SYMBOLS, new Random(30));
        SYMBOL_TO_NUMBER = Streams
            .mapWithIndex(SYMBOLS.stream(), Pair::of)
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String idToCode(long id) {
        return idToString(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long codeToId(@NonNull String code) throws IllegalURLCodeException {
        return stringToId(code);
    }

    @NonNull
    private static String idToString(long id) {
        require(id >= 0 && id < MAX_ID,
            () -> new IllegalArgumentException("Invalid id out of range [0, " + MAX_ID + "]"));

        long number = id + OFFSET;
        var builder = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % BASE);
            builder.append(SYMBOLS.get(digit));
            number /= BASE;
        }
        builder.append(SYMBOLS.get(0).toString().repeat(Math.max(0, MIN_CODE_LENGTH - builder.length())));
        return builder.reverse().toString();
    }

    private static long stringToId(@NonNull String code) throws IllegalURLCodeException {
        require(code.length() >= MIN_CODE_LENGTH,
            () -> new IllegalURLCodeException("Code " + code + " is too short"));
        require(code.length() == MIN_CODE_LENGTH || code.charAt(0) != SYMBOLS.get(0),
            () -> new IllegalURLCodeException("Code " + code + " does not exist"));

        long result = 0;
        for (char symbol : code.toCharArray()) {
            Long digit = SYMBOL_TO_NUMBER.get(symbol);
            require(digit != null,
                () -> new IllegalURLCodeException("Code " + code + " contains illegal symbol '" + symbol + "'"));

            result = result * BASE + digit;
            require(result - OFFSET <= MAX_ID,
                () -> new IllegalURLCodeException("Code " + code + " does not exist"));
        }
        require(result - OFFSET >= 0,
            () -> new IllegalURLCodeException("Code " + code + " does not exist"));

        return result - OFFSET;
    }
}
