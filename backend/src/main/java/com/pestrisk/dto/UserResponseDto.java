package com.pestrisk.dto;

import com.pestrisk.entity.UserAccount;
import com.pestrisk.enums.UserRole;
import java.time.Instant;

public class UserResponseDto {

    private Long id;
    private String username;
    private UserRole role;
    private Instant createdAt;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String username, UserRole role, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponseDto fromEntity(UserAccount account) {
        return new UserResponseDto(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
