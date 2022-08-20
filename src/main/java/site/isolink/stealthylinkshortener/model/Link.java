package site.isolink.stealthylinkshortener.model;

import lombok.*;

import javax.persistence.*;

/**
 * Represents shortened URL entity stored in database.
 *
 * Stores numerical link id and separate URL addresses for clients inside and outside the restricted area.
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String targetAddress;

    @NonNull
    private String safeAddress;
}
