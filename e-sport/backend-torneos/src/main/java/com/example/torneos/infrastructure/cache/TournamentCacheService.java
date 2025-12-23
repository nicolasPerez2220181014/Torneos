package com.example.torneos.infrastructure.cache;

import com.example.torneos.application.dto.response.TournamentResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TournamentCacheService {
    
    @Cacheable(value = "tournaments", key = "#id")
    public TournamentResponse getTournament(UUID id) {
        // This method signature is for cache annotation only
        // Actual implementation will be in TournamentService
        return null;
    }
    
    @CacheEvict(value = "tournaments", key = "#id")
    public void evictTournament(UUID id) {
        // Cache eviction when tournament is updated
    }
    
    @CacheEvict(value = "tournaments", allEntries = true)
    public void evictAllTournaments() {
        // Clear all tournament cache
    }
    
    @Cacheable(value = "tournament-details", key = "#id")
    public Object getTournamentDetails(UUID id) {
        return null;
    }
    
    @CacheEvict(value = "tournament-details", key = "#id")
    public void evictTournamentDetails(UUID id) {
        // Cache eviction for tournament details
    }
}