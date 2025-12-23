package com.example.torneos.infrastructure.web.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            rateLimit = handlerMethod.getBeanType().getAnnotation(RateLimit.class);
        }
        
        if (rateLimit == null) {
            return true;
        }
        
        String key = generateKey(request, rateLimit);
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - rateLimit.timeUnit().toMillis(rateLimit.window());
        
        // Clean old entries
        info.requests.removeIf(timestamp -> timestamp < windowStart);
        
        if (info.requests.size() >= rateLimit.requests()) {
            // Rate limit exceeded
            response.setStatus(429); // Too Many Requests
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.requests()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(
                currentTime + rateLimit.timeUnit().toMillis(rateLimit.window())));
            return false;
        }
        
        // Add current request
        info.requests.add(currentTime);
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.requests()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimit.requests() - info.requests.size()));
        
        return true;
    }
    
    private String generateKey(HttpServletRequest request, RateLimit rateLimit) {
        String keyTemplate = rateLimit.key();
        
        // Simple key generation - can be enhanced with SpEL
        if (keyTemplate.contains("#{request.remoteAddr}")) {
            return "rate_limit:" + request.getRemoteAddr() + ":" + request.getRequestURI();
        }
        
        return "rate_limit:" + keyTemplate + ":" + request.getRequestURI();
    }
    
    private static class RateLimitInfo {
        private final java.util.List<Long> requests = new java.util.concurrent.CopyOnWriteArrayList<>();
    }
}