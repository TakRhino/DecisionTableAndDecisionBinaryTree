package com.example.decisiontree;

import java.util.Set;

public abstract class DecisionTree {
    private final DecisionNode<?> rootNode;

    protected DecisionTree() {
        this.rootNode = initialize();
        if (this.rootNode == null) {
            throw new IllegalArgumentException("Decision tree must have root node");
        }
    }

    protected abstract DecisionNode<?> initialize();

    public Result resolve(Set<Parameter> parameters) {
        var node = resolveInner(parameters, this.rootNode);
        return ((LeafNode<?>) node).result();
    }

    private Node resolveInner(Set<Parameter> parameters, DecisionNode<?> node) {
        var nextNode = parameters.stream()
                .filter(p -> p.getClass().isAssignableFrom(node.getParameterType()))
                .map(node::selectNextNode)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No such parameter: " + node.getParameterType()));
        if (nextNode.isLeafNode()) {
            return nextNode;
        } else {
            return resolveInner(parameters, (DecisionNode<?>) nextNode);
        }
    }
}
