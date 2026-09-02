package com.khwaish.log_aggregator.consumer;

import com.khwaish.log_aggregator.buffer.LogBuffer;
import com.khwaish.log_aggregator.dto.LogEntryRequest;
import com.khwaish.log_aggregator.entity.LogEntry;
import com.khwaish.log_aggregator.repository.LogEntryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogConsumer {

    private final LogBuffer logBuffer;
    private final LogEntryRepository logEntryRepository;

    public LogConsumer(LogBuffer logBuffer, LogEntryRepository logEntryRepository) {
        this.logBuffer = logBuffer;
        this.logEntryRepository = logEntryRepository;
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
    }

    // Runs every 5000ms (5 seconds), flushing whatever is currently buffered.
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