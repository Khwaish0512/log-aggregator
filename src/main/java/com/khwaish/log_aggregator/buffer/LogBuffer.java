package com.khwaish.log_aggregator.buffer;

import com.khwaish.log_aggregator.entity.LogEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LogBuffer {

    private final ConcurrentLinkedQueue<LogEntry> queue = new ConcurrentLinkedQueue<>();

    public void add(LogEntry logEntry) {
        queue.add(logEntry);
    }

    public int size() {
        return queue.size();
    }

    /**
     * Atomically drains everything currently in the queue into a List,
     * removing each item as it's read. Safe to call while other threads
     * are concurrently adding new items — those new items simply won't
     * be included in this drain, they'll wait for the next one.
     */
    public List<LogEntry> drainAll() {
        List<LogEntry> drained = new ArrayList<>();
        LogEntry entry;
        while ((entry = queue.poll()) != null) {
            drained.add(entry);
        }
        return drained;
    }
}