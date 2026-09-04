package com.khwaish.log_aggregator.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertEngine {

    private final long windowMillis;
    private final int threshold;

    private final ConcurrentHashMap<String, Deque<Long>> serviceErrorTimestamps =
            new ConcurrentHashMap<>();

    public AlertEngine(
            @Value("${alert.window-ms:30000}") long windowMillis,
            @Value("${alert.threshold:5}") int threshold
    ) {
        this.windowMillis = windowMillis;
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public long getWindowMillis() {
        return windowMillis;
    }

    /**
     * Call this every time an ERROR-level log arrives for a given service.
     * Returns true if this call caused the threshold to be breached
     * (i.e., an alert should be raised right now).
     */
    public boolean recordErrorAndCheckBreach(String service, long timestampMillis) {
        Deque<Long> timestamps = serviceErrorTimestamps.computeIfAbsent(
                service, key -> new ArrayDeque<>()
        );

        synchronized (timestamps) {
            timestamps.addLast(timestampMillis);

            long cutoff = timestampMillis - windowMillis;
            while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
                timestamps.pollFirst();
            }

            return timestamps.size() >= threshold;
        }
    }
}