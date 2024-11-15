package com.example.decisiontree

import com.example.data.decisiontree.DecisionTreeBooleanParameter
import com.example.data.decisiontree.DecisionTreeNumberParameter
import com.example.data.decisiontree.DecisionTreeResult
import com.example.data.decisiontree.DecisionTreeStringParameter
import spock.lang.Specification
import spock.lang.Unroll

class DecisionTreeTest extends Specification {

    @Unroll
    def resolveTest() {
        given:
        def decisionTree = new DecisionTree() {
            @Override
            protected DecisionNode initialize() {
                return DecisionNode.builder(DecisionTreeStringParameter.class, { it.value() == "CORRECT_STRING" })
                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                        .addTrueNode(DecisionNode.builder(DecisionTreeBooleanParameter.class, { it.value() })
                                .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                                .addTrueNode(DecisionNode.builder(DecisionTreeNumberParameter.class, { it.value() < 10 })
                                        .addTrueNode(LeafNode.create(new DecisionTreeResult(true)))
                                        .addFalseNode(LeafNode.create(new DecisionTreeResult(false)))
                                        .build())
                                .build())
                        .build()
            }
        }

        when:
        def actual = decisionTree.resolve([
                new DecisionTreeNumberParameter(parameterNumber),
                new DecisionTreeBooleanParameter(parameterBoolean),
                new DecisionTreeStringParameter(parameterString)] as Set<Parameter>)

        then:
        noExceptionThrown()
        assert actual instanceof DecisionTreeResult
        assert actual.ok() == expected

        where:
        parameterString    | parameterBoolean | parameterNumber || expected
        "CORRECT_STRING"   | true             | 1               || true
        "CORRECT_STRING"   | true             | 9               || true
        "CORRECT_STRING"   | true             | 10              || false
        "CORRECT_STRING"   | false            | 2               || false
        "INCORRECT_STRING" | true             | 1               || false
    }

    def "Decision tree should have more than one node"() {
        when:
        new DecisionTree() {
            @Override
            protected DecisionNode initialize() {
                return null
            }
        }

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Decision tree must have root node"
    }
}
