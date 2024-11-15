package com.example.decisiontable.impl.login

import com.example.decisiontable.Parameter
import spock.lang.Specification
import spock.lang.Unroll

class LoginDecisionTableTest extends Specification {

    // All rules
    // | Stubs                     | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 | R10 | R11 | R12 | R13 | R14 | R15 | R16 |
    // |---------------------------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|
    // | ***IF (Condition stub)*** |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Existing ID?              | Y  | Y  | Y  | Y  | Y  | Y  | Y  | Y  | N  | N   | N   | N   | N   | N   | N   | N   |
    // | PIN Correct?              | Y  | Y  | Y  | Y  | N  | N  | N  | N  | Y  | Y   | Y   | Y   | N   | N   | N   | N   |
    // | User Status is "VALID"?   | Y  | Y  | N  | N  | Y  | Y  | N  | N  | Y  | Y   | N   | N   | Y   | Y   | N   | N   |
    // | Roles contain "ADMIN"?    | Y  | N  | Y  | N  | Y  | N  | Y  | N  | Y  | N   | Y   | N   | Y   | N   | Y   | N   |
    // | ***THEN (Action stub)***  |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Not found ID message      |    |    |    |    |    |    |    |    | X  | X   | X   | X   | X   | X   | X   | X   |
    // | Invalid PIN message       |    |    |    |    | X  | X  | X  | X  |    |     |     |     |     |     |     |     |
    // | Invalid Status message    |    |    | X  | X  |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Invalid Role message      |    | X  |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Successful admin login    | X  |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    @Unroll
    def resolveTest() {
        given:
        def decisionTable = new LoginDecisionTable()
        def parameters = [new ExistIdParameter(existingId), new StatusParameter(status), new PinParameter(pin), new RolesParameter(roles)] as Set<Parameter>

        when:
        def actual = decisionTable.resolve(parameters)

        then:
        assert actual instanceof LoginResult
        assert ((LoginResult) actual).message().message() == expectedMessage
        assert ((LoginResult) actual).permission().permitted() == expectedCanLogin

        where:
        ruleNo | existingId | pin  | status    | roles                     || expectedMessage  | expectedCanLogin
        "R1"   | true       | 1234 | "VALID"   | ["ADMIN"] as Set          || ""               | true
        "R2"   | true       | 1234 | "VALID"   | ["SYSTEM"] as Set         || "Invalid Role"   | false
        "R3"   | true       | 1234 | "EXPIRED" | ["ADMIN"] as Set          || "Invalid Status" | false
        "R4"   | true       | 1234 | "EXPIRED" | ["SYSTEM"] as Set         || "Invalid Status" | false
        "R5"   | true       | 4321 | "VALID"   | ["ADMIN"] as Set          || "Invalid PIN"    | false
        "R6"   | true       | 4321 | "VALID"   | ["SYSTEM"] as Set         || "Invalid PIN"    | false
        "R7"   | true       | 4321 | "EXPIRED" | ["ADMIN"] as Set          || "Invalid PIN"    | false
        "R8"   | true       | 4321 | "EXPIRED" | ["SYSTEM"] as Set         || "Invalid PIN"    | false
        "R9"   | false      | 1234 | "VALID"   | ["ADMIN"] as Set          || "Not found Id"   | false
        "R10"  | false      | 1234 | "VALID"   | ["SYSTEM"] as Set         || "Not found Id"   | false
        "R11"  | false      | 1234 | "EXPIRED" | ["ADMIN"] as Set          || "Not found Id"   | false
        "R12"  | false      | 1234 | "EXPIRED" | ["SYSTEM"] as Set         || "Not found Id"   | false
        "R13"  | false      | 4321 | "VALID"   | ["ADMIN"] as Set          || "Not found Id"   | false
        "R14"  | false      | 4321 | "VALID"   | ["SYSTEM"] as Set         || "Not found Id"   | false
        "R15"  | false      | 4321 | "EXPIRED" | ["ADMIN"] as Set          || "Not found Id"   | false
        "R16"  | false      | 4321 | "EXPIRED" | ["SYSTEM"] as Set         || "Not found Id"   | false
        "R1a"  | true       | 1234 | "VALID"   | ["USER", "ADMIN"] as Set  || ""               | true
        "R2a"  | true       | 1234 | "VALID"   | ["USER", "SYSTEM"] as Set || "Invalid Role"   | false
    }
}
