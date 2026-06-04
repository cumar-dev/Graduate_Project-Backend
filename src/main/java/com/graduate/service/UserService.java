package com.graduate.service;

import com.graduate.dto.UserMapper;
import com.graduate.dto.UserResponse;
import com.graduate.exception.ResourceNotFoundException;
import com.graduate.model.Role;
import com.graduate.model.User;
import com.graduate.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public UserResponse getCurrentUserResponse() {
        return UserMapper.toResponse(getCurrentUser());
    }

    public List<UserResponse> getSupervisors() {
        return userRepository.findByRole(Role.SUPERVISOR).stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Optional<User> resolveSupervisor(String supervisorId, String email, String fullName) {
        if (supervisorId != null && !supervisorId.isBlank()) {
            Optional<User> byId = userRepository.findById(supervisorId)
                    .filter(user -> user.getRole() == Role.SUPERVISOR);
            if (byId.isPresent()) {
                return byId;
            }
        }

        if (email != null && !email.isBlank()) {
            Optional<User> byEmail = userRepository.findByEmail(email.trim().toLowerCase())
                    .filter(user -> user.getRole() == Role.SUPERVISOR);
            if (byEmail.isPresent()) {
                return byEmail;
            }
        }

        if (fullName != null && !fullName.isBlank()) {
            String normalizedName = fullName.trim();
            return userRepository.findByRole(Role.SUPERVISOR).stream()
                    .filter(user -> user.getFullName().equalsIgnoreCase(normalizedName))
                    .findFirst();
        }

        return Optional.empty();
    }
}
