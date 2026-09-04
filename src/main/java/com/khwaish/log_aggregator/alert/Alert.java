package com.khwaish.log_aggregator.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String service;

    @Column(nullable = false)
    private Instant triggeredAt;

    @Column(nullable = false)
    private int errorCount;

    @Column(nullable = false)
    private long windowMillis;

    public Alert() {
    }

    public Alert(String service, Instant triggeredAt, int errorCount, long windowMillis) {
        this.service = service;
        this.triggeredAt = triggeredAt;
        this.errorCount = errorCount;
        this.windowMillis = windowMillis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getWindowMillis() {
        return windowMillis;
    }

    public void setWindowMillis(long windowMillis) {
        this.windowMillis = windowMillis;
    }
}