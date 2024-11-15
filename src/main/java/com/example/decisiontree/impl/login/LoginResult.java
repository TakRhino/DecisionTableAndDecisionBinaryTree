package com.example.decisiontree.impl.login;

import com.example.decisiontree.Result;
import com.example.service.login.domain.LoginMessage;
import com.example.service.login.domain.LoginPermission;

public record LoginResult(LoginMessage message, LoginPermission permission) implements Result {
}
