package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentRepository {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
    Page<Tournament> findAll(Pageable pageable);
    List<Tournament> findByOrganizerId(UUID organizerId);
    List<Tournament> findByStatus(Tournament.TournamentStatus status);
    Page<Tournament> findByFilters(Boolean isPaid, Tournament.TournamentStatus status, 
                                  UUID categoryId, UUID gameTypeId, UUID organizerId, Pageable pageable);
    void deleteById(UUID id);
    long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, boolean isPaid, Tournament.TournamentStatus status);
}