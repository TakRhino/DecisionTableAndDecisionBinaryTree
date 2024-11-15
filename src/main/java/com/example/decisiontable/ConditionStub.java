package com.example.decisiontable;

import java.util.function.Predicate;

public class ConditionStub<T extends Parameter> {
    private final Condition<T> match;
    private final Condition<T> notMatch;

    public ConditionStub(Class<T> parameterType, Predicate<T> condition) {
        this.match = new Condition<>(parameterType, condition, true);
        this.notMatch = new Condition<>(parameterType, condition, false);
    }

    public Condition<T> asMatch() {
        return this.match;
    }

    public Condition<T> asNotMatch() {
        return this.notMatch;
    }
}
