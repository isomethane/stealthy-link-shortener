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
import java.util.stream.Stream;

/**
 * Implementation of {@link IPService} using <a href="https://ip-api.com/">ip-api</a> to obtain IP location.
 */
@Slf4j
@Service
public class IPServiceImpl implements IPService {
    private static final String IP_API_URL = "http://ip-api.com/json";
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
            return IPLocationStatus.UNKNOWN;
        }

        log.info("IP location: {} ({})", response.countryCode, ip);
        if (restrictedCountryCodes.contains(Objects.requireNonNull(response.countryCode))) {
            return IPLocationStatus.RESTRICTED;
        }
        return IPLocationStatus.FREE;
    }

    @NonNull
    private Optional<IPLocationResponse> requestIPLocation(@NonNull String ip) {
        final int MAX_RETRIES = 3;

        log.info("Trying to locate IP {}", ip);
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
                    ip, e.getClass().getSimpleName(), e.getMessage()
                );
            }
        }
        return Optional.empty();
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
