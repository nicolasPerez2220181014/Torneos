package com.example.torneos.infrastructure.config;

import com.example.torneos.application.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {
    
    private final IdempotencyService idempotencyService;
    
    public IdempotencyInterceptor(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String idempotencyKey = request.getHeader("Idempotency-Key");
        if (idempotencyKey != null && isIdempotentEndpoint(request)) {
            var cached = idempotencyService.getResult(idempotencyKey);
            if (cached.isPresent()) {
                response.setStatus(cached.get().getStatusCode());
                response.setContentType("application/json");
                response.getWriter().write(cached.get().getResponse());
                return false;
            }
            request.setAttribute("idempotencyKey", idempotencyKey);
        }
        
        return true;
    }
    
    private boolean isIdempotentEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        return "POST".equals(method) && (
            path.matches("/api/tournaments") ||
            path.matches("/api/tournaments/[^/]+/orders") ||
            path.matches("/api/tournaments/[^/]+/subadmins")
        );
    }
}