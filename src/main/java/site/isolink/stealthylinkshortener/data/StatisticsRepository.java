package site.isolink.stealthylinkshortener.data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import site.isolink.stealthylinkshortener.model.Statistics;

/**
 * CRUD repository for click statistics (see {@link Statistics}).
 */
public interface StatisticsRepository extends CrudRepository<Statistics, Long> {
    /**
     * Increment click counter from outside the restricted area.
     * @param id link id
     */
    @Transactional
    @Modifying
    @Query("UPDATE Statistics s SET s.targetClicks = s.targetClicks + 1 WHERE s.linkId = :id")
    void addTargetClick(@Param("id") Long id);

    /**
     * Increment click counter from the restricted area.
     * @param id link id
     */
    @Transactional
    @Modifying
    @Query("UPDATE Statistics s SET s.restrictedClicks = s.restrictedClicks + 1 WHERE s.linkId = :id")
    void addRestrictedClick(@Param("id") Long id);

    /**
     * Increment click counter from unknown location.
     * @param id link id
     */
    @Transactional
    @Modifying
    @Query("UPDATE Statistics s SET s.unknownClicks = s.unknownClicks + 1 WHERE s.linkId = :id")
    void addUnknownClick(@Param("id") Long id);
}
