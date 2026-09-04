package com.khwaish.log_aggregator.consumer;

import com.khwaish.log_aggregator.alert.AlertEngine;
import com.khwaish.log_aggregator.buffer.LogBuffer;
import com.khwaish.log_aggregator.dto.LogEntryRequest;
import com.khwaish.log_aggregator.entity.Alert;
import com.khwaish.log_aggregator.entity.LogEntry;
import com.khwaish.log_aggregator.repository.AlertRepository;
import com.khwaish.log_aggregator.repository.LogEntryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class LogConsumer {

    private static final String ERROR_LEVEL = "ERROR";

    private final LogBuffer logBuffer;
    private final LogEntryRepository logEntryRepository;
    private final AlertEngine alertEngine;
    private final AlertRepository alertRepository;

    public LogConsumer(
            LogBuffer logBuffer,
            LogEntryRepository logEntryRepository,
            AlertEngine alertEngine,
            AlertRepository alertRepository
    ) {
        this.logBuffer = logBuffer;
        this.logEntryRepository = logEntryRepository;
        this.alertEngine = alertEngine;
        this.alertRepository = alertRepository;
    }

    @KafkaListener(topics = "application-logs", groupId = "log-aggregator-group")
    public void consume(LogEntryRequest logEntryRequest) {
        LogEntry logEntry = new LogEntry(
                logEntryRequest.getTimestamp(),
                logEntryRequest.getLevel(),
                logEntryRequest.getService(),
                logEntryRequest.getMessage()
        );
        logBuffer.add(logEntry);

        if (ERROR_LEVEL.equalsIgnoreCase(logEntryRequest.getLevel())) {
            checkForAlert(logEntryRequest);
        }
    }

    private void checkForAlert(LogEntryRequest logEntryRequest) {
        long timestampMillis = logEntryRequest.getTimestamp().toEpochMilli();

        boolean breached = alertEngine.recordErrorAndCheckBreach(
                logEntryRequest.getService(), timestampMillis
        );

        if (breached) {
            Alert alert = new Alert(
                    logEntryRequest.getService(),
                    Instant.now(),
                    alertEngine.getThreshold(),
                    alertEngine.getWindowMillis()
            );
            alertRepository.save(alert);
            System.out.println("ALERT: " + logEntryRequest.getService()
                    + " exceeded error threshold");
        }
    }

    @Scheduled(fixedRate = 5000)
    public void flushBuffer() {
        List<LogEntry> batch = logBuffer.drainAll();

        if (batch.isEmpty()) {
            return;
        }

        logEntryRepository.saveAll(batch);
        System.out.println("Flushed " + batch.size() + " logs to MySQL");
    }
}