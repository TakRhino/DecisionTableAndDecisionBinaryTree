package com.example.decisiontree

import com.example.data.decisiontree.DecisionTreeBooleanParameter
import com.example.data.decisiontree.DecisionTreeNumberParameter
import com.example.data.decisiontree.DecisionTreeResult
import com.example.data.decisiontree.DecisionTreeStringParameter
import spock.lang.Specification

class NodeTest extends Specification {

    def "Create Node without error"() {
        when:
        def actual =
                DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                        .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                        .build()

        then:
        noExceptionThrown()
        assert actual.isDecisionNode()
        assert !actual.isLeafNode()
    }

    def "getParameterType should return parameter type of Decision Node"() {
        given:
        def decisionNode =
                DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                        .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                        .build()

        when:
        def actual = decisionNode.getParameterType()

        then:
        noExceptionThrown()
        assert actual == DecisionTreeNumberParameter.class
    }

    def "Create Decision Node with error"() {
        when:
        DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                .addTrueNode(LeafNode.create(new DecisionTreeResult(false))).build()
        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Two nodes must not be null"

        when:
        DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                .addFalseNode(LeafNode.create(new DecisionTreeResult(false))).build()
        then:
        e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Two nodes must not be null"

        when:
        DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 }).build()
        then:
        e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Two nodes must not be null"
    }

    def "selectNextNode should return next node"() {
        given:
        def decisionNode = DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                .addFalseNode(DecisionNode.builder(DecisionTreeStringParameter.class, { it.value() == "CORRECT_STRING" })
                        .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                        .build())
                .build()

        expect:
        assert decisionNode.selectNextNode(new DecisionTreeNumberParameter(11)).isDecisionNode()
        assert !decisionNode.selectNextNode(new DecisionTreeNumberParameter(11)).isLeafNode()
        assert !decisionNode.selectNextNode(new DecisionTreeNumberParameter(9)).isDecisionNode()
        assert decisionNode.selectNextNode(new DecisionTreeNumberParameter(9)).isLeafNode()
    }

    def "selectNextNode should throw Exception"() {
        given:
        def decisionNode = DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                .addFalseNode(DecisionNode.builder(DecisionTreeStringParameter.class, { it.value() == "CORRECT_STRING" })
                        .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                        .build())
                .build()

        when:
        decisionNode.selectNextNode(new DecisionTreeBooleanParameter(false))

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Unsupported parameter type"
    }
}
