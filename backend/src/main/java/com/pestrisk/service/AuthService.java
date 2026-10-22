package com.pestrisk.service;

import com.pestrisk.dto.CreateStaffRequestDto;
import com.pestrisk.dto.LoginRequestDto;
import com.pestrisk.dto.LoginResponseDto;
import com.pestrisk.dto.UserResponseDto;
import com.pestrisk.entity.UserAccount;
import com.pestrisk.enums.UserRole;
import com.pestrisk.exception.ResourceNotFoundException;
import com.pestrisk.repository.UserAccountRepository;
import com.pestrisk.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        UserAccount user = userAccountRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
        return new LoginResponseDto(token, user.getUsername(), user.getRole());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(String username) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto createStaffUser(CreateStaffRequestDto request) {
        String cleanUsername = request.getUsername().trim();
        if (userAccountRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username is already in use: " + cleanUsername);
        }

        UserAccount newStaff = new UserAccount(
                cleanUsername,
                passwordEncoder.encode(request.getPassword()),
                UserRole.STAFF
        );

        UserAccount saved = userAccountRepository.save(newStaff);
        return UserResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .toList();
    }
}
