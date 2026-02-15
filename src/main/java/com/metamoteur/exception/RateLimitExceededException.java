package com.metamoteur.exception;

/**
 * Exception levée quand la limite de taux est dépassée
 */
public class RateLimitExceededException extends MetaMoteurException {

    private final int limit;
    private final String timeWindow;

    public RateLimitExceededException(int limit, String timeWindow) {
        super(
                "RATE_LIMIT_EXCEEDED",
                String.format("Rate limit exceeded: %d requests per %s", limit, timeWindow)
        );
        this.limit = limit;
        this.timeWindow = timeWindow;
    }

    public int getLimit() {
        return limit;
    }

    public String getTimeWindow() {
        return timeWindow;
    }
}