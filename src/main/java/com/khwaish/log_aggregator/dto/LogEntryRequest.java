package com.khwaish.log_aggregator.dto;

import java.time.Instant;

public class LogEntryRequest {

    private Instant timestamp;
    private String level;      // e.g. "INFO", "ERROR", "WARN"
    private String service;    // which app/service sent this log
    private String message;

    // Spring/Jackson needs a no-args constructor to build this object
    // from incoming JSON before filling in the fields.
    public LogEntryRequest() {
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

    @Override
    public String toString() {
        return "LogEntryRequest{" +
                "timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", service='" + service + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}