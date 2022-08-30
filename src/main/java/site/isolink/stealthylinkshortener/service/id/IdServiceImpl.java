package site.isolink.stealthylinkshortener.service.id;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Default implementation of {@link IdService}.
 */
@Service
public class IdServiceImpl implements IdService {
    private static final long NUMBER_OF_COUNTERS = 10_000;

    private final IdConverter idConverter;
    private final MongoTemplate mongo;
    private final Random random = new Random();

    /**
     * Class constructor.
     * @param idConverter converter from number to id
     * @param mongo mongo operations
     */
    @Autowired
    public IdServiceImpl(IdConverter idConverter, MongoTemplate mongo) {
        this.idConverter = idConverter;
        this.mongo = mongo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull String generateId() {
        long counterId = randomCounter();
        Counter counter = mongo
            .update(Counter.class)
            .matching(Criteria.where("id").is(counterId))
            .apply(new Update().inc("value", 1))
            .withOptions(FindAndModifyOptions.options().upsert(true).returnNew(true))
            .findAndModify()
            .orElseThrow();

        return idConverter.getId(counter.getValue() * NUMBER_OF_COUNTERS + counter.getId());
    }

    private long randomCounter() {
        return random.nextLong(0, NUMBER_OF_COUNTERS);
    }
}
