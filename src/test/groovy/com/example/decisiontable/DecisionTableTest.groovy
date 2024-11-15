package com.example.decisiontable


import com.example.data.decisiontable.DecisionTableNumberParameter
import com.example.data.decisiontable.DecisionTableResult
import com.example.data.decisiontable.DecisionTableStringParameter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class DecisionTableTest extends Specification {

    @Shared
    def StringConditionStub = new ConditionStub<>(DecisionTableStringParameter.class, { it.value() == "CORRECT_VALUE" })
    @Shared
    def NumberConditionStub = new ConditionStub<>(DecisionTableNumberParameter.class, { it.value() < 10 })

    def "No condition error on creating instance of DecisionTable"() {
        when:
        new DecisionTable() {
            @Override
            protected Set<DecisionRule> initialize() {
                return Collections.EMPTY_SET
            }
        }

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Decision table must have more than one condition"
    }

    def "Duplicated error on creating instance of DecisionTable"() {
        when:
        new DecisionTable() {
            @Override
            protected Set<DecisionRule> initialize() {
                return Set.of(
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asMatch())
                                .then(new DecisionTableResult(true)),
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asMatch())
                                .then(new DecisionTableResult(false)),
                        new DecisionRule()
                                .when(stringConditionStub.asNotMatch())
                                .then(new DecisionTableResult(false))
                )
            }
        }

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Decision table has duplicated conditions"
    }

    def "No action error on creating instance of DecisionTable"() {
        when:
        new DecisionTable() {
            @Override
            protected Set<DecisionRule> initialize() {
                return Set.of(
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asMatch()),
                        new DecisionRule()
                                .when(stringConditionStub.asNotMatch())
                                .then(new DecisionTableResult(false))
                )
            }
        }

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage() == "Each decision entries must have one action"
    }

    def "Not found error on executing resolve() with not enough Entries"() {
        given:
        def decisionTable = new DecisionTable() {
            @Override
            protected Set<DecisionRule> initialize() {
                return Set.of(
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asMatch())
                                .then(new DecisionTableResult(true)),
                        new DecisionRule()
                                .when(stringConditionStub.asNotMatch())
                                .then(new DecisionTableResult(false))
                )
            }
        }

        when:
        decisionTable.resolve(Set.of(
                new DecisionTableStringParameter("CORRECT_VALUE"), new DecisionTableNumberParameter(10)
        ))

        then:
        def e = thrown(IllegalArgumentException)
        assert e.getMessage().startsWith("Not found any Decision Entry")
    }

    @Unroll
    def resolveTest() {
        given:
        def decisionTable = new DecisionTable() {
            @Override
            protected Set<DecisionRule> initialize() {
                return Set.of(
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asMatch())
                                .then(new DecisionTableResult(true)),
                        new DecisionRule()
                                .when(stringConditionStub.asMatch())
                                .when(numberConditionStub.asNotMatch())
                                .then(new DecisionTableResult(false)),
                        new DecisionRule()
                                .when(stringConditionStub.asNotMatch())
                                .then(new DecisionTableResult(false))
                )
            }
        }

        when:
        def actual = decisionTable.resolve(Set.of(
                new DecisionTableStringParameter(parameterString), new DecisionTableNumberParameter(parameterNumner)
        ))

        then:
        assert actual instanceof DecisionTableResult
        assert actual.ok() == expected

        where:
        ruleNo | parameterString   | parameterNumner || expected
        "R1"   | "CORRECT_VALUE"   | 9               || true
        "R2"   | "CORRECT_VALUE"   | 10              || false
        "R3"   | "INCORRECT_VALUE" | 9               || false
        "R4"   | "INCORRECT_VALUE" | 10              || false
    }
}
