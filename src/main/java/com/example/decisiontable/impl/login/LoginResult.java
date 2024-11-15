package com.example.decisiontable.impl.login;

import com.example.decisiontable.Result;
import com.example.service.login.domain.LoginMessage;
import com.example.service.login.domain.LoginPermission;

public record LoginResult(LoginMessage message, LoginPermission permission) implements Result {
}
