package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.TournamentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    List<TournamentEntity> findByOrganizerId(UUID organizerId);
    List<TournamentEntity> findByStatus(TournamentEntity.TournamentStatus status);
    Page<TournamentEntity> findAll(Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM TournamentEntity t WHERE t.organizerId = :organizerId AND t.isPaid = :isPaid AND t.status = :status")
    long countByOrganizerIdAndIsPaidAndStatus(@Param("organizerId") UUID organizerId, 
                                            @Param("isPaid") boolean isPaid, 
                                            @Param("status") TournamentEntity.TournamentStatus status);
    
    @Query("SELECT t FROM TournamentEntity t WHERE " +
           "(:isPaid IS NULL OR t.isPaid = :isPaid) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:categoryId IS NULL OR t.categoryId = :categoryId) AND " +
           "(:gameTypeId IS NULL OR t.gameTypeId = :gameTypeId) AND " +
           "(:organizerId IS NULL OR t.organizerId = :organizerId)")
    Page<TournamentEntity> findByFilters(@Param("isPaid") Boolean isPaid,
                                       @Param("status") TournamentEntity.TournamentStatus status,
                                       @Param("categoryId") UUID categoryId,
                                       @Param("gameTypeId") UUID gameTypeId,
                                       @Param("organizerId") UUID organizerId,
                                       Pageable pageable);
}