package com.example.service.login;

import com.example.decisiontree.impl.login.*;
import com.example.service.login.domain.UserAccountInformation;
import com.example.service.login.repository.UserAccountRepositoryMock;

import java.util.Collections;
import java.util.Set;

/**
 * This class is example of using Decision Tree
 */
public class LoginServiceWithDecisionTree {
    private final LoginDecisionTree loginDecisionTree;
    private final UserAccountRepositoryMock userAccountRepository;

    public LoginServiceWithDecisionTree(LoginDecisionTree loginDecisionTree, UserAccountRepositoryMock userAccountRepository) {
        this.loginDecisionTree = loginDecisionTree;
        this.userAccountRepository = userAccountRepository;
    }

    public void loginByAdminRole(String id) {
        var userAccountInfo = userAccountRepository.findById(id);

        var loginResult = checkAdminAccount(
                userAccountInfo.isPresent(),
                userAccountInfo.map(UserAccountInformation::pin).orElse(0),
                userAccountInfo.map(UserAccountInformation::status).orElse(""),
                userAccountInfo.map(UserAccountInformation::roles).orElse(Collections.emptySet()));
        if (!loginResult.permission().permitted()) {
            throw new RuntimeException(loginResult.message().message());
        }
    }

    protected LoginResult checkAdminAccount(boolean existingId, int pin, String status, Set<String> roles) {
        return ((LoginResult) loginDecisionTree.resolve(
                Set.of(new ExistIdParameter(existingId), new StatusParameter(status), new PinParameter(pin), new RolesParameter(roles))));
    }
}
