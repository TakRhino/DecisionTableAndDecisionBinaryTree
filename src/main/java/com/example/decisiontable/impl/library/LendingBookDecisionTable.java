package com.example.decisiontable.impl.library;

import com.example.decisiontable.ConditionStub;
import com.example.decisiontable.DecisionRule;
import com.example.decisiontable.DecisionTable;

import java.util.Set;

import static com.example.service.lendingbook.domain.Constants.*;

/**
 *
 * All rules
 * <pre>
 * | Stubs                                                        | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 | R10 | R11 | R12 | R13 | R14 | R15 | R16 |
 * |--------------------------------------------------------------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|
 * | ***IF (Condition stub)***                                    |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Borrowed for more than 30 days?                              | Y  | Y  | Y  | Y  | Y  | Y  | Y  | Y  | N  | N   | N   | N   | N   | N   | N   | N   |
 * | Borrowing more than 5 books at the same time?                | Y  | Y  | Y  | Y  | N  | N  | N  | N  | Y  | Y   | Y   | Y   | N   | N   | N   | N   |
 * | Borrowed more than 7 books at the same time?                 | Y  | Y  | N  | N  | Y  | Y  | N  | N  | Y  | Y   | N   | N   | Y   | Y   | N   | N   |
 * | Borrowed more than 2 New-category books at the same time?    | Y  | N  | Y  | N  | Y  | N  | Y  | N  | Y  | N   | Y   | N   | Y   | N   | Y   | N   |
 * | ***THEN (Action stub)***                                     |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
 * | Message: "You have overdue books. Loan period 30 Days"       | X  | X  | X  | X  | X  | X  | X  | X  |    |     |     |     |     |     |     |     |
 * | Message: "Less than 6 books can be borrowed at once"         |    |    |    |    | X  | X  | X  | X  |    |     |     |     | X   | X   | X   | X   |
 * | Message: "Less than 8 books can be borrowed by a person"     |    |    | X  | X  |    |    | X  | X  |    |     | X   | X   |     |     | X   | X   |
 * | Message: "Less than 3 NEW books can be borrowed by a person" | X  |    |    | X  |    | X  |    | X  |    | X   |     | X   |     | X   |     | X   |
 * | Message: ""                                                  |    |    |    |    |    |    |    |    | X  |     |     |     |     |     |     |     |
 * | Can borrow                                                   |    |    |    |    |    |    |    |    | X  |     |     |     |     |     |     |     |
 * </pre>
 *
 * Analyzed and compressed rules
 * <pre>
 * | Stubs                                                     | R1 | R2                                                            | R3                                                         | R4                                               | ELSE                                                              |
 * |-----------------------------------------------------------|----|---------------------------------------------------------------|------------------------------------------------------------|--------------------------------------------------|-------------------------------------------------------------------|
 * | ***IF (Condition stub)***                                 |    |                                                               |                                                            |                                                  |                                                                   |
 * | Borrowed for more than 30 days?                           | N  | N                                                             | N                                                          | N                                                | Y                                                                 |
 * | Borrowing more than 5 books at the same time?             | Y  | Y                                                             | Y                                                          | N                                                | I                                                                 |
 * | Borrowed more than 7 books at the same time?              | Y  | Y                                                             | N                                                          | I                                                | I                                                                 |
 * | Borrowed more than 2 New-category books at the same time? | Y  | N                                                             | I                                                          | I                                                | I                                                                 |
 * | ***THEN (Action stub)***                                  |    |                                                               |                                                            |                                                  |                                                                   |
 * | Message                                                   | "" | "Can have max 2 NEW-category books borrowed at the same time" | "Can have a maximum of 7 books borrowed at the same time." | "Can borrow a maximum 5 books at the same time." | "You have overdue books. Must be returned no later than 30 days." |
 * | Can lend                                                  | X  |                                                               |                                                            |                                                  |                                                                   |
 * </pre>
 *
 */
public class LendingBookDecisionTable extends DecisionTable {

    @Override
    protected Set<DecisionRule> initialize() {
        var hasOverdueCondition = new ConditionStub<>(HasOverdueParameter.class, HasOverdueParameter::hasOverdue);
        var borrowingBooksCondition = new ConditionStub<>(BorrowingBooksParameter.class, (p) -> p.count() <= MAX_BORROWING_COUNT);
        var allBorrowedBooksCondition = new ConditionStub<>(AllBorrowedBooksParameter.class, (p) -> p.count() <= MAX_BORROWED_COUNT);
        var allBorrowedNewCategoryBooksCondition = new ConditionStub<>(AllBorrowedNewCategoryBooksParameter.class, (p) -> p.count() <= MAX_BORROWED_NEW_CATEGORY_COUNT);
        return Set.of(
                new DecisionRule()
                        .when(hasOverdueCondition.asNotMatch())
                        .when(borrowingBooksCondition.asMatch())
                        .when(allBorrowedBooksCondition.asMatch())
                        .when(allBorrowedNewCategoryBooksCondition.asMatch())
                        .then(new LendingBookResult(MSG_BLANK, true)),
                new DecisionRule()
                        .when(hasOverdueCondition.asNotMatch())
                        .when(borrowingBooksCondition.asMatch())
                        .when(allBorrowedBooksCondition.asMatch())
                        .when(allBorrowedNewCategoryBooksCondition.asNotMatch())
                        .then(new LendingBookResult(MSG_OVER_MAX_NEW_CATEGORY_COUNT, false)),
                new DecisionRule()
                        .when(hasOverdueCondition.asNotMatch())
                        .when(borrowingBooksCondition.asMatch())
                        .when(allBorrowedBooksCondition.asNotMatch())
                        .then(new LendingBookResult(MSG_OVER_MAX_BORROWED_COUNT, false)),
                new DecisionRule()
                        .when(hasOverdueCondition.asNotMatch())
                        .when(borrowingBooksCondition.asNotMatch())
                        .then(new LendingBookResult(MSG_OVER_MAX_BORROWING_COUNT, false)),
                new DecisionRule()
                        .when(hasOverdueCondition.asMatch())
                        .then(new LendingBookResult(MSG_OVERDUE, false))
        );
    }
}
