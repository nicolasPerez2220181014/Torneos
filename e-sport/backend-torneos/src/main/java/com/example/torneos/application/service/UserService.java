package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateUserRequest;
import com.example.torneos.application.dto.request.UpdateUserRequest;
import com.example.torneos.application.dto.response.UserResponse;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.email());
        }

        User.UserRole domainRole = User.UserRole.valueOf(request.role().name());
        User user = new User(request.email(), request.fullName(), domainRole);
        User savedUser = userRepository.save(user);
        
        return mapToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
        
        return mapToResponse(user);
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // Verificar si el nuevo email ya existe (excepto para el mismo usuario)
        userRepository.findByEmail(request.email())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.email());
                    }
                });

        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setRole(User.UserRole.valueOf(request.role().name()));
        
        User updatedUser = userRepository.save(user);
        
        return mapToResponse(updatedUser);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            UserResponse.UserRole.valueOf(user.getRole().name()),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}