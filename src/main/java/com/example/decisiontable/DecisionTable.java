package com.example.decisiontable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class DecisionTable {
    private final Set<DecisionRule> rules;

    protected DecisionTable() {
        this.rules = initialize();
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("Decision table must have more than one condition");
        }
        if (!hasDuplicatedConditions()) {
            throw new IllegalArgumentException("Decision table has duplicated conditions");
        }
        if (!hasActionInAllEntries()) {
            throw new IllegalArgumentException("Each decision entries must have one action");
        }
    }

    protected abstract Set<DecisionRule> initialize();

    private boolean hasDuplicatedConditions() {
        return this.rules.stream().map(DecisionRule::getConditions).collect(uniqueElements());
    }

    private boolean hasActionInAllEntries() {
        return this.rules.stream().allMatch(DecisionRule::hasAction);
    }

    public Result resolve(Set<Parameter> parameters) {
        var decisionEntry = rules.stream()
                .filter(e -> e.getConditions().stream()
                        .allMatch(r -> {
                            var parameterTypeOfCondition = r.getParameterType();
                            return parameters.stream()
                                    .filter(p -> p.getClass().isAssignableFrom(parameterTypeOfCondition))
                                    .map(r::evaluate)
                                    .findFirst().orElse(false);
                        })).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Not found any Decision Entry: '%s'  in %s", parameters, rules)));
        return decisionEntry.getResult();
    }

    private static <T> Collector<T, ?, Boolean> uniqueElements() {
        Set<T> set = new HashSet<>();
        return Collectors.reducing(true, set::add, Boolean::logicalAnd);
    }
}
