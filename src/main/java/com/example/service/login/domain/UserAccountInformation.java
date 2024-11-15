package com.example.service.login.domain;

import java.util.Set;

public record UserAccountInformation(String id, String status, int pin, Set<String> roles) {
}
