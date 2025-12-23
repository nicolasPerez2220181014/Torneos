package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.infrastructure.persistence.entity.TournamentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OptimizedTournamentRepository {
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        LEFT JOIN FETCH t.category c 
        LEFT JOIN FETCH t.gameType g 
        WHERE t.status = 'PUBLISHED' 
        ORDER BY t.createdAt DESC
        """)
    List<TournamentEntity> findPublishedTournamentsWithDetails();
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        LEFT JOIN FETCH t.ticketSaleStages s 
        WHERE t.id = :tournamentId
        """)
    TournamentEntity findByIdWithStages(@Param("tournamentId") UUID tournamentId);
    
    @Query("""
        SELECT COUNT(t) FROM TournamentEntity t 
        WHERE t.organizerId = :organizerId 
        AND t.status = :status
        """)
    Long countByOrganizerAndStatus(@Param("organizerId") UUID organizerId, 
                                  @Param("status") String status);
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        WHERE t.organizerId = :organizerId 
        AND (:status IS NULL OR t.status = :status)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        ORDER BY t.createdAt DESC
        """)
    Page<TournamentEntity> findOptimizedByFilters(
        @Param("organizerId") UUID organizerId,
        @Param("status") String status,
        @Param("categoryId") UUID categoryId,
        Pageable pageable
    );
}