package com.example.data.decisiontable;

import com.example.decisiontable.*;

import java.util.Arrays;
import java.util.Set;

public class UsingDecisionTable {

    // 1. Implement Parameter.class
    record SomeParameter(int value) implements Parameter {
    }

    // 2. Implemented Result.class
    record SomeResult(boolean ok) implements Result {
    }

    // 3. Implement the method initializeDecisionRules() in a class that inherits from DecisionTable.class.
    static public class SomeDecisionTable extends DecisionTable {
        @Override
        protected Set<DecisionRule> initialize() {
            // 3-1. Create an instance of ConditionStub.class with a class that is implemented Parameter.class
            var condition = new ConditionStub<>(SomeParameter.class, (p) -> p.value() < 10);

            // 3-2. Added rule sets which have the condition entries and the Action entry which represent a result.
            return Set.of(
                    new DecisionRule()
                            .when(condition.asMatch())
                            .then(new SomeResult(true)),
                    new DecisionRule()
                            .when(condition.asNotMatch())
                            .then(new SomeResult(false)));
        }
    }

    public static void main(String[] args) {
        var decisionTable = new SomeDecisionTable();
        int[] params = {5, 9, 11, 15};
        Arrays.stream(params).forEach(p -> {
            // Pass parameter to the decision table and get the result
            var result = decisionTable.resolve(Set.of(new SomeParameter(p)));
            System.out.println("Result: " + result);
        });
    }
}
