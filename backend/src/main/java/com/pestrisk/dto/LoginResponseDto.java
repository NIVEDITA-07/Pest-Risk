package com.pestrisk.dto;

import com.pestrisk.enums.UserRole;

public class LoginResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private UserRole role;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String username, UserRole role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
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
}
