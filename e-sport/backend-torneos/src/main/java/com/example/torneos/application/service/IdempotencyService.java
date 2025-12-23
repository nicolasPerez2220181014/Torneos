package com.example.torneos.application.service;

import com.example.torneos.infrastructure.persistence.entity.IdempotencyKeyEntity;
import com.example.torneos.infrastructure.persistence.repository.JpaIdempotencyKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class IdempotencyService {
    
    private final JpaIdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;
    
    public IdempotencyService(JpaIdempotencyKeyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }
    
    public Optional<IdempotencyResult> getResult(String key) {
        return repository.findByKeyAndNotExpired(key, Instant.now())
                .map(entity -> new IdempotencyResult(entity.getResponse(), entity.getStatusCode()));
    }
    
    @Transactional
    public void storeResult(String key, Object result, int statusCode) {
        try {
            IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
            entity.setKey(key);
            entity.setResponse(objectMapper.writeValueAsString(result));
            entity.setStatusCode(statusCode);
            entity.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
            
            repository.save(entity);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to store idempotency result: " + e.getMessage());
        }
    }
    
    public static class IdempotencyResult {
        private final String response;
        private final int statusCode;
        
        public IdempotencyResult(String response, int statusCode) {
            this.response = response;
            this.statusCode = statusCode;
        }
        
        public String getResponse() { return response; }
        public int getStatusCode() { return statusCode; }
    }
}