package com.example.decisiontable;

import java.util.HashSet;
import java.util.Set;

public class DecisionRule {
    private final Set<Condition<?>> conditions = new HashSet<>();
    private Action<?> action;

    public DecisionRule when(Condition<?> condition) {
        this.conditions.add(condition);
        return this;
    }

    public DecisionRule then(Result result) {
        this.action = new Action<>(result);
        return this;
    }

    Set<Condition<?>> getConditions() {
        return this.conditions;
    }

    public Result getResult() {
        return this.action.result();
    }

    public boolean hasAction() {
        return this.action != null;
    }
}
