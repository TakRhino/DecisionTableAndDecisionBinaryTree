package com.example.decisiontree.impl.login;

import com.example.decisiontree.DecisionNode;
import com.example.decisiontree.DecisionTree;
import com.example.decisiontree.LeafNode;
import com.example.service.login.domain.LoginMessage;
import com.example.service.login.domain.LoginPermission;

import static com.example.service.login.domain.Constants.*;

public class LoginDecisionTree extends DecisionTree {
    @Override
    protected DecisionNode<?> initialize() {
        return DecisionNode.builder(ExistIdParameter.class, ExistIdParameter::existing)
                .addFalseNode(LeafNode.create(new LoginResult(LoginMessage.of(MSG_NOT_FOUND), LoginPermission.of(false))))
                .addTrueNode(DecisionNode.builder(PinParameter.class, (p) -> p.pin() == VALID_PIN)
                        .addFalseNode(LeafNode.create(new LoginResult(LoginMessage.of(MSG_INVALID_PIN), LoginPermission.of(false))))
                        .addTrueNode(DecisionNode.builder(StatusParameter.class, (p) -> p.status().equals(VALID_STATUS))
                                .addFalseNode(LeafNode.create(new LoginResult(LoginMessage.of(MSG_INVALID_STATUS), LoginPermission.of(false))))
                                .addTrueNode(DecisionNode.builder(RolesParameter.class, (p) -> p.roles().contains(VALID_ROLE))
                                        .addTrueNode(LeafNode.create(new LoginResult(LoginMessage.of(MSG_SUCCESS), LoginPermission.of(true))))
                                        .addFalseNode(LeafNode.create(new LoginResult(LoginMessage.of(MSG_INVALID_ROLE), LoginPermission.of(false))))
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
