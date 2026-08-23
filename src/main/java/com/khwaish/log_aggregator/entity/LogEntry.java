package com.khwaish.log_aggregator.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "logs",
        indexes = {
                @Index(name = "idx_timestamp", columnList = "timestamp"),
                @Index(name = "idx_level", columnList = "level")
        }
)
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 20)
    private String level;

    @Column(nullable = false, length = 100)
    private String service;

    @Column(nullable = false, length = 2000)
    private String message;

    public LogEntry() {
    }

    public LogEntry(Instant timestamp, String level, String service, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.service = service;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
