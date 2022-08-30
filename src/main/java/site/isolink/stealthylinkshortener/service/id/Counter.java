package site.isolink.stealthylinkshortener.service.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
class Counter {
    @MongoId
    private long id;

    private long value;
}
