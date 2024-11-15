package com.example.decisiontable;

import java.util.function.Predicate;

public class Condition<T extends Parameter> {
    private final Predicate<T> condition;
    private final Class<T> parameterType;
    private final boolean evaluation;

    Condition(Class<T> parameterType, Predicate<T> condition, boolean evaluation) {
        this.condition = condition;
        this.parameterType = parameterType;
        this.evaluation = evaluation;
    }

    @SuppressWarnings("unchecked")
    Boolean evaluate(Parameter parameter) {
        if (parameter.getClass().isAssignableFrom(this.getParameterType())) {
            return this.condition.test((T) parameter) == this.evaluation;
        }
        return false;
    }

    Class<?> getParameterType() {
        return this.parameterType;
    }
}
