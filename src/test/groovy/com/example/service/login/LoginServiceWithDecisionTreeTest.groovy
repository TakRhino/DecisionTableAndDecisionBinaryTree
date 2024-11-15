package com.example.service.login

import com.example.decisiontree.impl.login.LoginDecisionTree
import com.example.service.login.domain.UserAccountInformation
import com.example.service.login.repository.UserAccountRepositoryMock
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.example.service.login.domain.Constants.*

class LoginServiceWithDecisionTreeTest extends Specification {
    @Shared
    def repository = new UserAccountRepositoryMock([
            "id_01": new UserAccountInformation("id_01", VALID_STATUS, VALID_PIN, Set.of(VALID_ROLE, "SYSTEM")),
            "id_02": new UserAccountInformation("id_02", "INVALID", 4321, Set.of(VALID_ROLE, "SYSTEM")),
            "id_03": new UserAccountInformation("id_03", VALID_STATUS, VALID_PIN, Set.of("USER")),
    ])
    @Shared
    def service = new LoginServiceWithDecisionTree(new LoginDecisionTree(), repository)

    @Unroll
    def checkAdminAccountTest() {
        when:
        def actual = service.checkAdminAccount(existingId, pin, status, roles)

        then:
        noExceptionThrown()

        where:
        ruleNo | existingId | pin  | status    | roles            || expectedMessage  | expectedCanLogin
        "R1"   | true       | 1234 | "VALID"   | ["ADMIN"] as Set || ""               | true
        "R2"   | true       | 1234 | "VALID"   | [] as Set        || "Invalid Role"   | false
        "R3"   | true       | 1234 | "EXPIRED" | ["ADMIN"] as Set || "Invalid Status" | false
        "R4"   | true       | 4321 | "VALID"   | ["ADMIN"] as Set || "Invalid PIN"    | false
        "ELSE" | false      | 1234 | "VALID"   | ["ADMIN"] as Set || ""               | false
    }

    def "loginByAdminRole should return no error with correct user"() {
        when:
        service.loginByAdminRole("id_01")

        then:
        noExceptionThrown()
    }

    def "loginByAdminRole should throw exception with incorrect user"() {
        when:
        service.loginByAdminRole(id)

        then:
        def e = thrown(RuntimeException)
        assert e.getMessage() == expectedMessage

        where:
        id      || expectedMessage
        "id_02" || "Invalid PIN"
        "id_03" || "Invalid Role"
        "id_03" || "Invalid Role"
        "id_99" || "Not found Id"
    }
}
