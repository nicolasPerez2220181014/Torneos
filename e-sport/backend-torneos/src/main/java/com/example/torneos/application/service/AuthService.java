package com.example.torneos.application.service;

import com.example.torneos.application.dto.response.UserResponse;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getCurrentUser(String userIdHeader, String roleHeader) {
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Header X-USER-ID es requerido");
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Validar que el rol del header coincida con el rol del usuario
            if (roleHeader != null && !roleHeader.equals(user.getRole().name())) {
                throw new IllegalArgumentException("El rol del header no coincide con el usuario");
            }

            return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                UserResponse.UserRole.valueOf(user.getRole().name()),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Header X-USER-ID inválido: " + e.getMessage());
        }
    }

    public void validateRole(String userIdHeader, User.UserRole requiredRole) {
        UserResponse user = getCurrentUser(userIdHeader, null);
        User.UserRole userRole = User.UserRole.valueOf(user.role().name());
        
        if (userRole != requiredRole) {
            throw new IllegalArgumentException("Acceso denegado. Se requiere rol: " + requiredRole);
        }
    }

    public void validateRoleAnyOf(String userIdHeader, User.UserRole... allowedRoles) {
        UserResponse user = getCurrentUser(userIdHeader, null);
        User.UserRole userRole = User.UserRole.valueOf(user.role().name());
        
        for (User.UserRole allowedRole : allowedRoles) {
            if (userRole == allowedRole) {
                return;
            }
        }
        
        throw new IllegalArgumentException("Acceso denegado. Roles permitidos: " + java.util.Arrays.toString(allowedRoles));
    }
}