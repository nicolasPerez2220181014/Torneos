package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Page<User> findAll(Pageable pageable);
    List<User> findByRole(User.UserRole role);
    void deleteById(UUID id);
    boolean existsByEmail(String email);
    long countByRoleAndId(User.UserRole role, UUID excludeId);
}