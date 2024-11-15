package com.example.service.login.repository;

import com.example.service.login.domain.UserAccountInformation;

import java.util.Map;
import java.util.Optional;

public class UserAccountRepositoryMock {
    private final Map<String, UserAccountInformation> userAccountInfos;

    public UserAccountRepositoryMock(Map<String, UserAccountInformation> userAccountInfos) {
        this.userAccountInfos = userAccountInfos;
    }

    public Optional<UserAccountInformation> findById(String id) {
        return Optional.ofNullable(userAccountInfos.get(id));
    }
}
