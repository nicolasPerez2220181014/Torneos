package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.StreamLinkControl;
import java.util.Optional;
import java.util.UUID;

public interface StreamLinkControlRepository {
    StreamLinkControl save(StreamLinkControl streamLinkControl);
    Optional<StreamLinkControl> findById(UUID id);
    Optional<StreamLinkControl> findByTournamentId(UUID tournamentId);
    void deleteById(UUID id);
}