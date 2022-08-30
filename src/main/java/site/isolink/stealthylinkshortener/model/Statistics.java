package site.isolink.stealthylinkshortener.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Click statistics for {@link Link}.
 *
 * Stores number of clicks from inside and outside the restricted area and from unknown locations.
 */
@Data
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class Statistics {
    @MongoId
    private String id;

    private long targetClicks;

    private long restrictedClicks;

    private long unknownClicks;
}
