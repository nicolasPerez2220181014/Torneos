package com.example.torneos.infrastructure.config;

import com.example.torneos.application.service.IdempotencyService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class IdempotencyFilter implements Filter {
    
    private final IdempotencyService idempotencyService;
    private final Set<String> idempotentEndpoints = Set.of(
        "POST:/api/tournaments",
        "POST:/api/tournaments/.*/orders",
        "POST:/api/tournaments/.*/subadmins"
    );
    
    public IdempotencyFilter(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String idempotencyKey = httpRequest.getHeader("Idempotency-Key");
        
        if (isIdempotentEndpoint(httpRequest) && idempotencyKey != null) {
            var cached = idempotencyService.getResult(idempotencyKey);
            if (cached.isPresent()) {
                writeResponse(httpResponse, cached.get());
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isIdempotentEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        return idempotentEndpoints.stream()
                .anyMatch(endpoint -> {
                    String[] parts = endpoint.split(":");
                    return parts[0].equals(method) && path.matches(parts[1].replace(".*", "[^/]+"));
                });
    }
    
    private void writeResponse(HttpServletResponse response, IdempotencyService.IdempotencyResult result) 
            throws IOException {
        response.setStatus(result.getStatusCode());
        response.setContentType("application/json");
        response.getWriter().write(result.getResponse());
    }
}