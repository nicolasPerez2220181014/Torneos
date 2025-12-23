package com.example.torneos.infrastructure.web.versioning;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    
    private final String version;
    
    public ApiVersionCondition(String version) {
        this.version = version;
    }
    
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.version);
    }
    
    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        String versionHeader = request.getHeader("API-Version");
        
        // Check API-Version header first
        if (versionHeader != null && versionHeader.equals(version)) {
            return this;
        }
        
        // Check Accept header for version (application/vnd.api+json;version=v1)
        if (acceptHeader != null && acceptHeader.contains("version=" + version)) {
            return this;
        }
        
        // Default to v1 if no version specified
        if ((versionHeader == null || versionHeader.isEmpty()) && "v1".equals(version)) {
            return this;
        }
        
        return null;
    }
    
    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return other.version.compareTo(this.version);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ApiVersionCondition that = (ApiVersionCondition) obj;
        return Objects.equals(version, that.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}