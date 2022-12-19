package site.isolink.stealthylinkshortener.service.ip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Implementation of {@link IPService} using <a href="https://ip-api.com/">ip-api</a> to obtain IP location.
 */
@Slf4j
@Service
public class IPServiceImpl implements IPService {
    private static final String IP_API_URL = "http://ip-api.com/json";

    private static final Pattern IPV4_LAST_SECTIONS = Pattern.compile("(?:\\.\\d+){2}$");
    private static final Pattern IPV6_LAST_SECTIONS = Pattern.compile("(?::+\\w+){2}$");

    private final WebClient webClient = WebClient.create(IP_API_URL);

    @Value("${restricted-country-codes}")
    private List<String> restrictedCountryCodes;

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public IPLocationStatus locateClientIP(@NonNull HttpServletRequest request) {
        return getClientIP(request).map(this::locateIP).orElse(IPLocationStatus.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Optional<String> getClientIP(@NonNull HttpServletRequest request) {
        final String LOCALHOST_IPV4 = "127.0.0.1";
        final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

        String ipAddress = Stream.of(
            request.getHeader("X-Forwarded-For"),
            request.getHeader("Proxy-Client-IP"),
            request.getHeader("WL-Proxy-Client-IP"),
            request.getRemoteAddr()
        )
            .filter((ip) -> StringUtils.hasLength(ip) && !"unknown".equalsIgnoreCase(ip))
            .findFirst()
            .orElseThrow();

        if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
            try {
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return Optional.empty();
            }
        }

        return Optional.of(ipAddress.split(",")[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public IPLocationStatus locateIP(@NonNull String ip) {
        Optional<IPLocationResponse> maybeResponse = requestIPLocation(ip);
        if (maybeResponse.isEmpty()) {
            return IPLocationStatus.UNKNOWN;
        }

        IPLocationResponse response = maybeResponse.get();
        if (Objects.requireNonNull(response.status) == IPLocationResponse.Status.FAIL) {
            if (Objects.requireNonNull(response.message) == IPLocationResponse.Message.INVALID_QUERY) {
                throw new RuntimeException("Invalid query");
            }
            log.info("IP location: Unknown ({})", obfuscateIP(ip));
            return IPLocationStatus.UNKNOWN;
        }

        log.info("IP location: {} ({})", response.countryCode, obfuscateIP(ip));
        if (restrictedCountryCodes.contains(Objects.requireNonNull(response.countryCode))) {
            return IPLocationStatus.RESTRICTED;
        }
        return IPLocationStatus.FREE;
    }

    @NonNull
    private Optional<IPLocationResponse> requestIPLocation(@NonNull String ip) {
        final int MAX_RETRIES = 3;

        log.info("Trying to locate IP {}", obfuscateIP(ip));
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                IPLocationResponse response = webClient
                    .get()
                    .uri("/{ip}?fields=status,message,countryCode", ip)
                    .retrieve()
                    .bodyToMono(IPLocationResponse.class)
                    .block();
                return Optional.of(Objects.requireNonNull(response));
            } catch (Exception e) {
                log.warn(
                    "Failed to locate IP {}: got exception {}({})",
                    obfuscateIP(ip), e.getClass().getSimpleName(), e.getMessage()
                );
            }
        }
        return Optional.empty();
    }

    static @NonNull String obfuscateIP(@NonNull String ip) {
        if (ip.contains(".")) {
            return IPV4_LAST_SECTIONS.matcher(ip)
                .replaceFirst(matchResult -> matchResult.group().replaceAll("\\d", "*"));
        }
        return IPV6_LAST_SECTIONS.matcher(ip)
            .replaceFirst(matchResult -> matchResult.group().replaceAll("\\w", "*"));
    }

    @Data
    static class IPLocationResponse {
        private Status status;
        private Message message;
        private String countryCode;

        enum Status {
            @JsonProperty("success")
            SUCCESS,
            @JsonProperty("fail")
            FAIL
        }

        enum Message {
            @JsonProperty("private range")
            PRIVATE_RANGE,
            @JsonProperty("reserved range")
            RESERVED_RANGE,
            @JsonProperty("invalid query")
            INVALID_QUERY
        }
    }
}
