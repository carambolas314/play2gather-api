package com.play2gather.logger.repository;

import com.play2gather.logger.model.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<LogEntry, String> {
    LogEntry findByTraceId(String s);
}
