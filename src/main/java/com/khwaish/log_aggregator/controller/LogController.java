package com.khwaish.log_aggregator.controller;

import com.khwaish.log_aggregator.dto.LogEntryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LogController {

    private static final String TOPIC = "application-logs";

    private final KafkaTemplate<String, LogEntryRequest> kafkaTemplate;

    // Constructor injection: Spring automatically creates and hands us
    // a fully-configured KafkaTemplate bean here. We never call `new` ourselves.
    public LogController(KafkaTemplate<String, LogEntryRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/logs")
    public ResponseEntity<String> ingestLog(@RequestBody LogEntryRequest logEntry) {

        // Key = service name, so all logs from the same service
        // land in the same partition and stay ordered.
        kafkaTemplate.send(TOPIC, logEntry.getService(), logEntry);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Log accepted");
    }
}