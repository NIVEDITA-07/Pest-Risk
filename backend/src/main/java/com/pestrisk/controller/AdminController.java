package com.pestrisk.controller;

import com.pestrisk.dto.CreateStaffRequestDto;
import com.pestrisk.dto.UserResponseDto;
import com.pestrisk.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createStaffAccount(
            @Valid @RequestBody CreateStaffRequestDto request) {
        UserResponseDto created = authService.createStaffUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> listAllUsers() {
        List<UserResponseDto> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
