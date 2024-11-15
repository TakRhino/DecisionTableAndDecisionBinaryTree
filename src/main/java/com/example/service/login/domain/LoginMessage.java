package com.example.service.login.domain;

public record LoginMessage(String message) {
    public static LoginMessage of(String message) {
        return new LoginMessage(message);
    }
}
