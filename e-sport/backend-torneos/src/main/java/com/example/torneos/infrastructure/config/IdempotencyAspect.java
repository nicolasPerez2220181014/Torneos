package com.example.torneos.infrastructure.config;

import com.example.torneos.application.service.IdempotencyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class IdempotencyAspect {
    
    private final IdempotencyService idempotencyService;
    
    public IdempotencyAspect(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }
    
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) && " +
            "execution(* com.example.torneos.infrastructure.controller.*Controller.*(..))")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        String idempotencyKey = (String) request.getAttribute("idempotencyKey");
        
        if (idempotencyKey == null) {
            return joinPoint.proceed();
        }
        
        Object result = joinPoint.proceed();
        
        // Store result for future requests
        if (result instanceof ResponseEntity<?> responseEntity) {
            idempotencyService.storeResult(
                idempotencyKey, 
                responseEntity.getBody(), 
                responseEntity.getStatusCode().value()
            );
        }
        
        return result;
    }
}