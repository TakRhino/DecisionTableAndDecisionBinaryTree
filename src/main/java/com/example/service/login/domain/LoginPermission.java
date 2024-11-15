package com.example.service.login.domain;

public record LoginPermission(boolean permitted) {
    public static LoginPermission of(boolean permitted) {
        return new LoginPermission(permitted);
    }
}
