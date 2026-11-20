package org.example.gun5.RateLimit.src.main.java.com.example.RateLimit;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimitService {

    private static final int MAX_REQUEST_PER_MINUTE = 5;
    private static final long REFILL_INTERVAL_MS = 60_000L;

    private final Map<String , TokenBucked> buckedMap = new ConcurrentHashMap<>();

    public boolean allowRequest(String customer_id){
        TokenBucked bucked = buckedMap.computeIfAbsent(customer_id,
                k-> new TokenBucked(MAX_REQUEST_PER_MINUTE,REFILL_INTERVAL_MS));

        return bucked.tryConsume();
    }
}
