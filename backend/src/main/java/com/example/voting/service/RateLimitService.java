package com.example.voting.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private final StringRedisTemplate redis;

    public RateLimitService(StringRedisTemplate redis) {this.redis = redis;}

    public boolean incrementAndCheck(String key, int limit, Duration window) {
        Long v = redis.opsForValue().increment(key);
        if (v != null && v == 1) redis.expire(key, window);
        return v != null && v <= limit;
    }

    public void reset(String key) { redis.delete(key); }
}
