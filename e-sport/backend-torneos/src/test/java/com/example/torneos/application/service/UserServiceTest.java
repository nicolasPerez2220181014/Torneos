package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateUserRequest;
import com.example.torneos.application.dto.request.UpdateUserRequest;
import com.example.torneos.application.dto.response.UserResponse;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@example.com", "Test User", User.UserRole.USER);
        user.setId(userId);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void create_ShouldCreateUser_WhenEmailDoesNotExist() {
        // Given
        CreateUserRequest request = new CreateUserRequest("new@example.com", "New User", CreateUserRequest.UserRole.USER);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse response = userService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getFullName(), response.fullName());
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest("existing@example.com", "Existing User", CreateUserRequest.UserRole.USER);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.create(request)
        );
        assertEquals("Ya existe un usuario con el email: existing@example.com", exception.getMessage());
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserResponse response = userService.findById(userId);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getFullName(), response.fullName());
        assertEquals(UserResponse.UserRole.USER, response.role());
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.findById(userId)
        );
        assertEquals("Usuario no encontrado con ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void update_ShouldUpdateUser_WhenValidRequest() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest("updated@example.com", "Updated User", UpdateUserRequest.UserRole.ORGANIZER);
        User updatedUser = new User("updated@example.com", "Updated User", User.UserRole.ORGANIZER);
        updatedUser.setId(userId);
        updatedUser.setCreatedAt(user.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse response = userService.update(userId, request);

        // Then
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("updated@example.com", response.email());
        assertEquals("Updated User", response.fullName());
        assertEquals(UserResponse.UserRole.ORGANIZER, response.role());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserResponse response = userService.findByEmail(email);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getEmail(), response.email());
        verify(userRepository).findByEmail(email);
    }
}