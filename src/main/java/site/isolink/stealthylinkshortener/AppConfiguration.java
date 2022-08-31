package site.isolink.stealthylinkshortener;

import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import site.isolink.stealthylinkshortener.model.Statistics;

import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.MongoActionOperation.UPDATE;

@Configuration
public class AppConfiguration {
    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory, MongoConverter mongoConverter) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory, mongoConverter);
        mongoTemplate.setWriteConcernResolver(action -> {
            if (action.getEntityType() == Statistics.class && action.getMongoActionOperation() == UPDATE) {
                return WriteConcern.UNACKNOWLEDGED.withJournal(false);
            }
            return WriteConcern.MAJORITY.withJournal(true).withWTimeout(3000, TimeUnit.MILLISECONDS);
        });
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);

        return mongoTemplate;
    }
}
