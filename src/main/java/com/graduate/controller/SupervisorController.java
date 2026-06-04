package com.graduate.controller;

import com.graduate.dto.UserResponse;
import com.graduate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisors")
public class SupervisorController {

    private final UserService userService;

    public SupervisorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getSupervisors() {
        return ResponseEntity.ok(userService.getSupervisors());
    }
}
