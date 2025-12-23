package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.StreamLinkControlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaStreamLinkControlRepository extends JpaRepository<StreamLinkControlEntity, UUID> {
    Optional<StreamLinkControlEntity> findByTournamentId(UUID tournamentId);
}