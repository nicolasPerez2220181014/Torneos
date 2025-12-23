package com.example.torneos.infrastructure.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;

@Component
public class ProblemDetailFactory {
    
    private static final String PROBLEM_BASE_URL = "https://api.torneos.com/problems/";
    
    public ProblemDetail createInsufficientCapacity(String message, int available, int requested, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
        problem.setType(URI.create(PROBLEM_BASE_URL + "insufficient-capacity"));
        problem.setTitle("Insufficient Capacity");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("availableCapacity", available);
        problem.setProperty("requestedQuantity", requested);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    public ProblemDetail createTournamentNotFound(String message, String tournamentId, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problem.setType(URI.create(PROBLEM_BASE_URL + "tournament-not-found"));
        problem.setTitle("Tournament Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("tournamentId", tournamentId);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    public ProblemDetail createValidationError(String message, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problem.setType(URI.create(PROBLEM_BASE_URL + "validation-error"));
        problem.setTitle("Validation Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    public ProblemDetail createUnauthorized(String message, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
        problem.setType(URI.create(PROBLEM_BASE_URL + "unauthorized"));
        problem.setTitle("Unauthorized Access");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
    
    public ProblemDetail createBusinessRuleViolation(String message, String rule, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, message);
        problem.setType(URI.create(PROBLEM_BASE_URL + "business-rule-violation"));
        problem.setTitle("Business Rule Violation");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("violatedRule", rule);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}