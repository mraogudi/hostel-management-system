package com.hostel.dto;

public class LoginResponse {
    
    private String token;
    private UserDto user;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
} 