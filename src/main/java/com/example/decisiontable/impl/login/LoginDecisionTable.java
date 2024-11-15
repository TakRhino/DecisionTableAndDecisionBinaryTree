package com.example.decisiontable.impl.login;

import com.example.decisiontable.ConditionStub;
import com.example.decisiontable.DecisionRule;
import com.example.decisiontable.DecisionTable;
import com.example.service.login.domain.LoginMessage;
import com.example.service.login.domain.LoginPermission;

import java.util.Set;

import static com.example.service.login.domain.Constants.*;

/**
 * All rules
 * <pre>
 * | Stubs                     | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 | R10 | R11 | R12 | R13 | R14 | R15 | R16 |
 * |---------------------------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|
 * | ***IF (Condition stub)*** |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Existing ID?              | Y  | Y  | Y  | Y  | Y  | Y  | Y  | Y  | N  | N   | N   | N   | N   | N   | N   | N   |
 * | PIN Correct?              | Y  | Y  | Y  | Y  | N  | N  | N  | N  | Y  | Y   | Y   | Y   | N   | N   | N   | N   |
 * | User Status is "VALID"?   | Y  | Y  | N  | N  | Y  | Y  | N  | N  | Y  | Y   | N   | N   | Y   | Y   | N   | N   |
 * | Roles contain "ADMIN"?    | Y  | N  | Y  | N  | Y  | N  | Y  | N  | Y  | N   | Y   | N   | Y   | N   | Y   | N   |
 * | ***THEN (Action stub)***  |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Not found ID message      |    |    |    |    |    |    |    |    | X  | X   | X   | X   | X   | X   | X   | X   |
 * | Invalid PIN message       |    |    |    |    | X  | X  | X  | X  |    |     |     |     |     |     |     |     |
 * | Invalid Status message    |    |    | X  | X  |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Invalid Role message      |    | X  |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Successful admin login    | X  |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * </pre>
 *
 * Analyzed and compressed rules
 * <pre>
 * | Stubs                            | R1   | R2             | R3               | R4            | ELSE           |
 * |----------------------------------|------|----------------|------------------|---------------|----------------|
 * | ***IF (Condition stub)***        |      |                |                  |               |                |
 * | isExisting as boolean == true    | Y    | Y              | Y                | Y             | N              |
 * | PIN as int == value@DB           | Y    | Y              | Y                | N             | I              |
 * | User Status as String == "VALID" | Y    | Y              | N                | I             | I              |
 * | Role as ∋ "ADMIN"                | Y    | N              | I                | I             | I              |
 * | ***THEN (Action)***              |      |                |                  |               |                |
 * | Error message (String)           | ""   | "Invalid Role" | "Invalid Status" | "Invalid PIN" | "Not found ID" |
 * | Can admin login (boolean)        | true | false          | true             | false         | false          |
 * </pre>
 *
 */
public class LoginDecisionTable extends DecisionTable {

    @Override
    protected Set<DecisionRule> initialize() {
        var idExistingCondition = new ConditionStub<>(ExistIdParameter.class, ExistIdParameter::existing);
        var pinCondition = new ConditionStub<>(PinParameter.class, (p) -> p.pin() == VALID_PIN);
        var statusCondition = new ConditionStub<>(StatusParameter.class, (p) -> p.status().equals(VALID_STATUS));
        var roleCondition = new ConditionStub<>(RolesParameter.class, (p) -> p.roles().contains(VALID_ROLE));

        return Set.of(
                new DecisionRule()
                        .when(idExistingCondition.asMatch())
                        .when(pinCondition.asMatch())
                        .when(statusCondition.asMatch())
                        .when(roleCondition.asMatch())
                        .then(new LoginResult(LoginMessage.of(MSG_SUCCESS), LoginPermission.of(true))),
                new DecisionRule()
                        .when(idExistingCondition.asMatch())
                        .when(pinCondition.asMatch())
                        .when(statusCondition.asMatch())
                        .when(roleCondition.asNotMatch())
                        .then(new LoginResult(LoginMessage.of(MSG_INVALID_ROLE), LoginPermission.of(false))),
                new DecisionRule()
                        .when(idExistingCondition.asMatch())
                        .when(pinCondition.asMatch())
                        .when(statusCondition.asNotMatch())
                        .then(new LoginResult(LoginMessage.of(MSG_INVALID_STATUS), LoginPermission.of(false))),
                new DecisionRule()
                        .when(idExistingCondition.asMatch())
                        .when(pinCondition.asNotMatch())
                        .then(new LoginResult(LoginMessage.of(MSG_INVALID_PIN), LoginPermission.of(false))),
                new DecisionRule()
                        .when(idExistingCondition.asNotMatch())
                        .then(new LoginResult(LoginMessage.of(MSG_NOT_FOUND), LoginPermission.of(false)))
        );
    }
}
