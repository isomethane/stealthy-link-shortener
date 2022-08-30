package site.isolink.stealthylinkshortener.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Represents shortened URL entity stored in database.
 *
 * Stores numerical link id and separate URL addresses for clients inside and outside the restricted area.
 */
@Data
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class Link {
    @MongoId
    @NonNull
    private String id;

    @NonNull
    private String targetAddress;

    @NonNull
    private String safeAddress;
}
