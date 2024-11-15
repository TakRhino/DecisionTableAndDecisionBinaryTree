package com.example.decisiontable.impl.login;

import com.example.decisiontable.Parameter;

import java.util.Set;

public record RolesParameter(Set<String> roles) implements Parameter {
}
