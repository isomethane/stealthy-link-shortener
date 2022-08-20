package site.isolink.stealthylinkshortener.model;

import lombok.*;

import javax.persistence.*;

/**
 * Click statistics for {@link Link}.
 *
 * Stores number of clicks from inside and outside the restricted area and from unknown locations.
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class Statistics {
    @Id
    @NonNull
    private Long linkId;

    @NonNull
    private Long targetClicks;

    @NonNull
    private Long restrictedClicks;

    @NonNull
    private Long unknownClicks;
}
