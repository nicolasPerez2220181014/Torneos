package com.example.torneos.infrastructure.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String USER_ID_HEADER = "X-USER-ID";
    private static final String USER_ID_KEY = "userId";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate or extract correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Extract user ID if present
        String userId = request.getHeader(USER_ID_HEADER);
        
        // Add to MDC for structured logging
        MDC.put(CORRELATION_ID_KEY, correlationId);
        if (userId != null && !userId.isEmpty()) {
            MDC.put(USER_ID_KEY, userId);
        }
        
        // Add correlation ID to response
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // Clean up MDC
        MDC.clear();
    }
}