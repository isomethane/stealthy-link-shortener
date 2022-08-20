package site.isolink.stealthylinkshortener.data;

import org.springframework.data.repository.CrudRepository;
import site.isolink.stealthylinkshortener.model.Link;

/**
 * CRUD repository for shortened URLs (see {@link Link}).
 */
public interface LinkRepository extends CrudRepository<Link, Long> {
}
