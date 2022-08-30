package site.isolink.stealthylinkshortener.data;

import lombok.NonNull;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.CrudRepository;
import site.isolink.stealthylinkshortener.model.Statistics;

/**
 * CRUD repository for click statistics (see {@link Statistics}).
 */
public interface StatisticsRepository extends CrudRepository<Statistics, String> {
    /**
     * Increment click counter from outside the restricted area.
     * @param id link id
     */
    @Update("{ '$inc' : { 'targetClicks' : 1 } }")
    void findAndIncrementTargetClicksById(@NonNull String id);

    /**
     * Increment click counter from the restricted area.
     * @param id link id
     */
    @Update("{ '$inc' : { 'restrictedClicks' : 1 } }")
    void findAndIncrementRestrictedClicksById(@NonNull String id);

    /**
     * Increment click counter from unknown location.
     * @param id link id
     */
    @Update("{ '$inc' : { 'unknownClicks' : 1 } }")
    void findAndIncrementUnknownClicksById(@NonNull String id);
}
