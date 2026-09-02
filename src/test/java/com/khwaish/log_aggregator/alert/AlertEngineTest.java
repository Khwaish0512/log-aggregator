package com.khwaish.log_aggregator.alert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertEngineTest {

    @Test
    void doesNotBreachBelowThreshold() {
        // window = 30s, threshold = 5
        AlertEngine engine = new AlertEngine(30_000, 5);

        long now = System.currentTimeMillis();

        // Only 4 errors — should NOT breach (threshold is 5)
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 1000));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 2000));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 3000));
    }

    @Test
    void breachesAtThreshold() {
        AlertEngine engine = new AlertEngine(30_000, 5);

        long now = System.currentTimeMillis();

        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 1000));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 2000));
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 3000));
        // 5th error within the window — should breach now
        assertTrue(engine.recordErrorAndCheckBreach("payment-service", now + 4000));
    }

    @Test
    void oldTimestampsGetEvictedOutsideWindow() {
        AlertEngine engine = new AlertEngine(30_000, 5);

        long now = System.currentTimeMillis();

        // 4 errors right at the start
        engine.recordErrorAndCheckBreach("payment-service", now);
        engine.recordErrorAndCheckBreach("payment-service", now + 1000);
        engine.recordErrorAndCheckBreach("payment-service", now + 2000);
        engine.recordErrorAndCheckBreach("payment-service", now + 3000);

        // 5th error arrives 40 seconds later — the first 4 should have
        // "aged out" of the 30-second window, so this should NOT breach.
        assertFalse(engine.recordErrorAndCheckBreach("payment-service", now + 40_000));
    }

    @Test
    void differentServicesAreTrackedIndependently() {
        AlertEngine engine = new AlertEngine(30_000, 5);

        long now = System.currentTimeMillis();

        // 4 errors for payment-service — not enough to breach on its own
        engine.recordErrorAndCheckBreach("payment-service", now);
        engine.recordErrorAndCheckBreach("payment-service", now + 1000);
        engine.recordErrorAndCheckBreach("payment-service", now + 2000);
        engine.recordErrorAndCheckBreach("payment-service", now + 3000);

        // auth-service's own errors should NOT count toward payment-service's total
        assertFalse(engine.recordErrorAndCheckBreach("auth-service", now));
        assertFalse(engine.recordErrorAndCheckBreach("auth-service", now + 1000));
    }
}