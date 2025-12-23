package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.AssignSubAdminRequest;
import com.example.torneos.application.dto.response.TournamentAdminResponse;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.model.TournamentAdmin;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.TournamentAdminRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import com.example.torneos.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TournamentAdminService {

    private final TournamentAdminRepository tournamentAdminRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public TournamentAdminService(TournamentAdminRepository tournamentAdminRepository,
                                TournamentRepository tournamentRepository,
                                UserRepository userRepository) {
        this.tournamentAdminRepository = tournamentAdminRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }

    public TournamentAdminResponse assignSubAdmin(UUID tournamentId, AssignSubAdminRequest request, UUID organizerId) {
        // Validar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        // Validar que el usuario es el organizador del torneo
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw new IllegalArgumentException("Solo el organizador puede asignar subadministradores");
        }

        // Validar que el usuario a asignar existe
        User subAdmin = userRepository.findById(request.subAdminUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario subadministrador no encontrado"));

        // Validar que el usuario no es el mismo organizador
        if (subAdmin.getId().equals(organizerId)) {
            throw new IllegalArgumentException("El organizador no puede ser subadministrador de su propio torneo");
        }

        // Validar que no está ya asignado
        if (tournamentAdminRepository.existsByTournamentIdAndSubAdminUserId(tournamentId, request.subAdminUserId())) {
            throw new IllegalArgumentException("El usuario ya es subadministrador de este torneo");
        }

        // Regla de negocio: máximo 2 subadministradores por torneo
        long currentSubAdmins = tournamentAdminRepository.countByTournamentId(tournamentId);
        if (currentSubAdmins >= 2) {
            throw new IllegalArgumentException("Un torneo solo puede tener máximo 2 subadministradores");
        }

        TournamentAdmin tournamentAdmin = new TournamentAdmin(tournamentId, request.subAdminUserId());
        TournamentAdmin savedAdmin = tournamentAdminRepository.save(tournamentAdmin);

        return mapToResponse(savedAdmin);
    }

    @Transactional(readOnly = true)
    public List<TournamentAdminResponse> findByTournamentId(UUID tournamentId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        return tournamentAdminRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void removeSubAdmin(UUID tournamentId, UUID subAdminUserId, UUID organizerId) {
        // Validar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        // Validar que el usuario es el organizador del torneo
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw new IllegalArgumentException("Solo el organizador puede remover subadministradores");
        }

        // Buscar y eliminar la asignación
        List<TournamentAdmin> admins = tournamentAdminRepository.findByTournamentId(tournamentId);
        TournamentAdmin adminToRemove = admins.stream()
                .filter(admin -> admin.getSubAdminUserId().equals(subAdminUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El usuario no es subadministrador de este torneo"));

        tournamentAdminRepository.deleteById(adminToRemove.getId());
    }

    private TournamentAdminResponse mapToResponse(TournamentAdmin admin) {
        return new TournamentAdminResponse(
            admin.getId(),
            admin.getTournamentId(),
            admin.getSubAdminUserId(),
            admin.getCreatedAt()
        );
    }
}