package com.example.decisiontree;

import java.util.function.Predicate;

public class DecisionNode<T extends Parameter> implements Node {
    private final Class<T> parameterType;
    private final Predicate<T> condition;
    private final Node trueNode;
    private final Node falseNode;

    private DecisionNode(Builder<T> builder) {
        this.parameterType = builder.parameterType;
        this.condition = builder.condition;
        this.trueNode = builder.trueNode;
        this.falseNode = builder.falseNode;
        if (this.trueNode == null || this.falseNode == null) {
            throw new IllegalArgumentException("Two nodes must not be null");
        }
    }

    @Override
    public boolean isDecisionNode() {
        return true;
    }

    @Override
    public boolean isLeafNode() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Node selectNextNode(Parameter parameter) {
        if (parameter.getClass().isAssignableFrom(this.parameterType)) {
            return this.condition.test((T) parameter) ? trueNode : falseNode;
        }
        throw new IllegalArgumentException("Unsupported parameter type");
    }

    Class<?> getParameterType() {
        return this.parameterType;
    }

    public static <T extends Parameter> Builder<T> builder(Class<T> parameterType, Predicate<T> condition) {
        return new Builder<>(parameterType, condition);
    }

    public static class Builder<T extends Parameter> {
        private final Class<T> parameterType;
        private final Predicate<T> condition;
        private Node trueNode;
        private Node falseNode;

        Builder(Class<T> parameterType, Predicate<T> condition) {
            this.parameterType = parameterType;
            this.condition = condition;
        }

        public Builder<T> addTrueNode(Node node) {
            this.trueNode = node;
            return this;
        }

        public Builder<T> addFalseNode(Node node) {
            this.falseNode = node;
            return this;
        }

        public DecisionNode<T> build() {
            return new DecisionNode<>(this);
        }
    }
}
