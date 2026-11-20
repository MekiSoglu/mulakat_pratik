package org.example.gun5.RateLimit.src.main.java.com.example.RateLimit;

public class TokenBucked {
    private final int capacity;
    private final long refillIntervalMilis;
    private  int tokens;
    private  long lastRefillTimestamp;

    public TokenBucked(final int capacity, final long refillIntervalMilis) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillIntervalMilis = refillIntervalMilis;
        lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume(){
        refill();
        if(tokens > 0) {
            tokens --;
            return true;
        }
        return false;
    }


    private void refill(){
        long now = System.currentTimeMillis();
        if(now - lastRefillTimestamp >= refillIntervalMilis){
            tokens = capacity;
            lastRefillTimestamp = now;
        }
    }
}
