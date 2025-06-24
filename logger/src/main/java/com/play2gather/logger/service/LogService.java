package com.play2gather.logger.service;

import com.play2gather.logger.model.LogEntry;
import com.play2gather.logger.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository repository;

    public void saveLog(LogEntry request) {
        repository.save(request);
    }
}

