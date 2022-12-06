package net.cloudcentrik.autolink.testservice.repository;

import net.cloudcentrik.autolink.testservice.model.WikiPageViews;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiPageViewsRepository extends ReactiveMongoRepository<WikiPageViews, String> {
}
