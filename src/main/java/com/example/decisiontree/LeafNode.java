package com.example.decisiontree;

public record LeafNode<T extends Result>(T result) implements Node {

    public static <T extends Result> LeafNode<T> create(T result) {
        return new LeafNode<>(result);
    }

    @Override
    public boolean isDecisionNode() {
        return false;
    }

    @Override
    public boolean isLeafNode() {
        return true;
    }
}
