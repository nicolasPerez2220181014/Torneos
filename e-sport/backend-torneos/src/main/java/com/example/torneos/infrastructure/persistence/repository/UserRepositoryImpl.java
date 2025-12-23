package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.UserRepository;
import com.example.torneos.infrastructure.persistence.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(JpaUserRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        var entityRole = com.example.torneos.infrastructure.persistence.entity.UserEntity.UserRole.valueOf(role.name());
        return jpaRepository.findByRole(entityRole).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public long countByRoleAndId(User.UserRole role, UUID excludeId) {
        var entityRole = com.example.torneos.infrastructure.persistence.entity.UserEntity.UserRole.valueOf(role.name());
        return jpaRepository.countByRoleAndIdNot(entityRole, excludeId);
    }
}