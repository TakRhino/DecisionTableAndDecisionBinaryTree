package com.example.decisiontree.impl.library

import spock.lang.Specification
import spock.lang.Unroll

import static com.example.service.lendingbook.domain.Constants.*

class LendingBookDecisionTreeTest extends Specification {

    // All rules
    // | Stubs                                                        | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 | R9 | R10 | R11 | R12 | R13 | R14 | R15 | R16 |
    // |--------------------------------------------------------------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|
    // | ***IF (Condition stub)***                                    |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Borrowed for more than 30 days?                              | Y  | Y  | Y  | Y  | Y  | Y  | Y  | Y  | N  | N   | N   | N   | N   | N   | N   | N   |
    // | Borrowing more than 5 books at the same time?                | Y  | Y  | Y  | Y  | N  | N  | N  | N  | Y  | Y   | Y   | Y   | N   | N   | N   | N   |
    // | Borrowed more than 7 books at the same time?                 | Y  | Y  | N  | N  | Y  | Y  | N  | N  | Y  | Y   | N   | N   | Y   | Y   | N   | N   |
    // | Borrowed more than 2 New-category books at the same time?    | Y  | N  | Y  | N  | Y  | N  | Y  | N  | Y  | N   | Y   | N   | Y   | N   | Y   | N   |
    // | ***THEN (Action stub)***                                     |    |    |    |    |    |    |    |    |    |     |     |     |     |     |     |     |
    // | Message: "You have overdue books. Loan period 30 Days"       | X  | X  | X  | X  | X  | X  | X  | X  |    |     |     |     |     |     |     |     |
    // | Message: "Less than 6 books can be borrowed at once"         |    |    |    |    | X  | X  | X  | X  |    |     |     |     | X   | X   | X   | X   |
    // | Message: "Less than 8 books can be borrowed by a person"     |    |    | X  | X  |    |    | X  | X  |    |     | X   | X   |     |     | X   | X   |
    // | Message: "Less than 3 NEW books can be borrowed by a person" | X  |    |    | X  |    | X  |    | X  |    | X   |     | X   |     | X   |     | X   |
    // | Message: ""                                                  |    |    |    |    |    |    |    |    | X  |     |     |     |     |     |     |     |
    // | Can borrow                                                   |    |    |    |    |    |    |    |    | X  |     |     |     |     |     |     |     |
    @Unroll
    def resolveTest() {
        given:
        def decisionTree = new LendingBookDecisionTree()

        when:
        def actual = decisionTree.resolve(Set.of(
                new HasOverdueParameter(hasOverdue),
                new BorrowingBooksParameter(lendingCount),
                new AllBorrowedBooksParameter(allBorrowedCount),
                new AllBorrowedNewCategoryBooksParameter(allNewCategoryBorrowedCount)
        ))

        then:
        noExceptionThrown()
        assert actual instanceof LendingResult
        assert actual.message() == message
        assert actual.canLend() == canLend

        where:
        ruleNo | hasOverdue | lendingCount | allBorrowedCount | allNewCategoryBorrowedCount || message                         | canLend
        "R1"   | true       | 5            | 7                | 2                           || MSG_OVERDUE                     | false
        "R2"   | true       | 5            | 7                | 3                           || MSG_OVERDUE                     | false
        "R3"   | true       | 5            | 8                | 2                           || MSG_OVERDUE                     | false
        "R4"   | true       | 5            | 8                | 3                           || MSG_OVERDUE                     | false
        "R5"   | true       | 6            | 7                | 2                           || MSG_OVERDUE                     | false
        "R6"   | true       | 6            | 7                | 3                           || MSG_OVERDUE                     | false
        "R7"   | true       | 6            | 8                | 2                           || MSG_OVERDUE                     | false
        "R8"   | true       | 6            | 8                | 3                           || MSG_OVERDUE                     | false
        "R9"   | false      | 5            | 7                | 2                           || MSG_BLANK                       | true
        "R10"  | false      | 5            | 7                | 3                           || MSG_OVER_MAX_NEW_CATEGORY_COUNT | false
        "R11"  | false      | 5            | 8                | 2                           || MSG_OVER_MAX_BORROWED_COUNT     | false
        "R12"  | false      | 5            | 8                | 3                           || MSG_OVER_MAX_BORROWED_COUNT     | false
        "R13"  | false      | 6            | 7                | 2                           || MSG_OVER_MAX_BORROWING_COUNT    | false
        "R14"  | false      | 6            | 7                | 3                           || MSG_OVER_MAX_BORROWING_COUNT    | false
        "R15"  | false      | 6            | 8                | 2                           || MSG_OVER_MAX_BORROWING_COUNT    | false
        "R16"  | false      | 6            | 8                | 3                           || MSG_OVER_MAX_BORROWING_COUNT    | false
    }
}
