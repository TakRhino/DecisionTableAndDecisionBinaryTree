package com.example.data.decisiontree;

import com.example.decisiontree.*;

import java.util.Arrays;
import java.util.Set;

public class UsingDecisionTree {

    // 1. Implement Parameter.class
    record SomeStringParameter(String value) implements Parameter {
    }

    record SomeNumberParameter(int value) implements Parameter {
    }

    // 2. Implemented Result.class
    record SomeResult(boolean ok) implements Result {
    }

    // 3. Implement the method initialize() in a class that inherits from DecisionTree.class.
    static public class SomeDecisionTree extends DecisionTree {
        @Override
        protected DecisionNode<?> initialize() {
            // 3-1. Create nodes tree. Each Decision Node has true node and false node
            return DecisionNode.builder(SomeStringParameter.class, (p) -> p.value().equals("CORRECT_STRING"))
                    .addFalseNode(LeafNode.create(new SomeResult(false)))
                    .addTrueNode(DecisionNode.builder(SomeNumberParameter.class, (p) -> p.value() < 10)
                            .addTrueNode(LeafNode.create(new SomeResult(true)))
                            .addFalseNode(LeafNode.create(new SomeResult(false)))
                            .build())
                    .build();
        }
    }

    public static void main(String[] args) {
        var decisionTree = new SomeDecisionTree();
        String[] strParams = {"CORRECT_STRING", "INCORRECT_STRING"};
        int[] numParams = {5, 9, 11, 15};
        Arrays.stream(strParams).forEach(str ->
                Arrays.stream(numParams).forEach(num -> {
                    // Pass parameter to the decision tree and get the result
                    var result = decisionTree.resolve(Set.of(new SomeStringParameter(str), new SomeNumberParameter(num)));
                    System.out.printf("strPram: %s, numPram: %d --> Result: %s\n", str, num, result.toString());
                }));
    }
}
