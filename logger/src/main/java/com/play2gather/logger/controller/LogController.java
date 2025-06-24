package com.play2gather.logger.controller;

import com.play2gather.logger.model.LogEntry;
import com.play2gather.logger.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @PostMapping
    public ResponseEntity<Void> createLog(@RequestBody LogEntry log) {
        logService.saveLog(log);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<String> verifyLogger() {
        return new ResponseEntity<String>("Logger Rodando", HttpStatus.OK);
    }
}
