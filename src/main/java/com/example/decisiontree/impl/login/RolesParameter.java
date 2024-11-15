package com.example.decisiontree.impl.login;

import com.example.decisiontree.Parameter;

import java.util.Set;

public record RolesParameter(Set<String> roles) implements Parameter {
}
