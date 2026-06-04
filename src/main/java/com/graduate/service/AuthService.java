package com.graduate.service;

import com.graduate.dto.LoginRequest;
import com.graduate.dto.RegisterRequest;
import com.graduate.dto.UserResponse;
import com.graduate.exception.BadRequestException;
import com.graduate.model.Role;
import com.graduate.model.User;
import com.graduate.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       UserService userService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (request.getStudentId() != null && !request.getStudentId().isBlank()
                && userRepository.existsByStudentId(request.getStudentId())) {
            throw new BadRequestException("Student ID is already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setDepartment(request.getDepartment());
        user.setStudentId(request.getStudentId());
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userService.getCurrentUserResponse();
    }

    public UserResponse login(LoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Invalid email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userService.getCurrentUserResponse();
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
