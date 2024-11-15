package com.example.service.lendingbook

import com.example.data.TestData
import com.example.decisiontable.impl.library.LendingBookDecisionTable
import com.example.decisiontable.impl.library.LendingBookResult
import com.example.service.lendingbook.domain.Book
import com.example.service.lendingbook.domain.BookCategory
import com.example.service.lendingbook.domain.BorrowedBook
import com.example.service.lendingbook.repository.UserBorrowedBookRepositoryMock
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.temporal.ChronoUnit

import static com.example.service.lendingbook.domain.Constants.*

class LendingBookServiceWithDecisionTableTest extends Specification {

    @Unroll
    def resolveTest() {
        given:
        def service = new LendingBookServiceWithDecisionTable(new UserBorrowedBookRepositoryMock([(TestData.USER_ID_01): []]), new LendingBookDecisionTable())

        when:
        def actual = service.resolve(hasOverdueBooks, lendingCount, allBorrowedCount, allNewCategoryCount)

        then:
        noExceptionThrown()
        assert actual instanceof LendingBookResult
        assert actual.message() == expectedMessage
        assert actual.canLend() == expectedCanLend

        where:
        ruleNo | hasOverdueBooks | lendingCount | allBorrowedCount | allNewCategoryCount || expectedMessage                 | expectedCanLend
        "R1"   | false           | 1            | 1                | 1                   || ""                              | true
        "R2"   | false           | 1            | 1                | 3                   || MSG_OVER_MAX_NEW_CATEGORY_COUNT | false
        "R3"   | false           | 1            | 10               | 1                   || MSG_OVER_MAX_BORROWED_COUNT     | false
        "R4"   | false           | 10           | 1                | 1                   || MSG_OVER_MAX_BORROWING_COUNT    | false
        "R5"   | true            | 1            | 1                | 1                   || MSG_OVERDUE                     | false
    }

    @Unroll
    def "check should return no error with valid lending books"() {
        given:
        def repository = new UserBorrowedBookRepositoryMock([(TestData.USER_ID_01): [
                new BorrowedBook(new Book("ISBN_01", BookCategory.NEW), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_02", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(29, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_03", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(29, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_04", BookCategory.CLASSIC), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS))]
        ])
        def service = new LendingBookServiceWithDecisionTable(repository, new LendingBookDecisionTable())

        when:
        service.lent("user_id_01", lendingBooks)

        then:
        noExceptionThrown()

        where:
        lendingBooks << [
                [
                        new Book("ISBN_11", BookCategory.NEW)
                ] as List<Book>,
                [
                        new Book("ISBN_11", BookCategory.NEW),
                        new Book("ISBN_12", BookCategory.CLASSIC),
                        new Book("ISBN_13", BookCategory.STANDARD)
                ] as List<Book>,
        ]
    }

    def "check should throw exception when have overdue books"() {
        given:
        def repository = new UserBorrowedBookRepositoryMock([(TestData.USER_ID_01): [
                new BorrowedBook(new Book("ISBN_01", BookCategory.NEW), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_02", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(29, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_03", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(LEND_PERIOD, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_04", BookCategory.CLASSIC), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS))]
        ])
        def service = new LendingBookServiceWithDecisionTable(repository, new LendingBookDecisionTable())

        def lendingBooks = [new Book("ISBN_11", BookCategory.NEW)]

        when:
        service.lent("user_id_01", lendingBooks)

        then:
        def e = thrown(RuntimeException)
        assert e.getMessage() == MSG_OVERDUE
    }

    @Unroll
    def "check should throw error with invalid lending books"() {
        given:
        def repository = new UserBorrowedBookRepositoryMock([(TestData.USER_ID_01): [
                new BorrowedBook(new Book("ISBN_01", BookCategory.NEW), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS))]
        ])
        def service = new LendingBookServiceWithDecisionTable(repository, new LendingBookDecisionTable())

        when:
        service.lent("user_id_01", lendingBooks)

        then:
        def e = thrown(RuntimeException)
        assert e.getMessage() == expectedMessage

        where:
        lendingBooks << [
                [
                        new Book("ISBN_11", BookCategory.NEW),
                        new Book("ISBN_12", BookCategory.CLASSIC),
                        new Book("ISBN_13", BookCategory.STANDARD),
                        new Book("ISBN_14", BookCategory.NEW),
                ] as List<Book>,
                [
                        new Book("ISBN_11", BookCategory.NEW),
                        new Book("ISBN_12", BookCategory.CLASSIC),
                        new Book("ISBN_13", BookCategory.STANDARD),
                        new Book("ISBN_14", BookCategory.STANDARD),
                        new Book("ISBN_15", BookCategory.STANDARD),
                        new Book("ISBN_16", BookCategory.STANDARD),

                ] as List<Book>,
        ]
        expectedMessage << [
                MSG_OVER_MAX_NEW_CATEGORY_COUNT,
                MSG_OVER_MAX_BORROWING_COUNT
        ]
    }

    def "check should throw error with over limit"() {
        given:
        def repository = new UserBorrowedBookRepositoryMock([(TestData.USER_ID_01): [
                new BorrowedBook(new Book("ISBN_01", BookCategory.NEW), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_02", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(29, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_03", BookCategory.STANDARD), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(29, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_04", BookCategory.CLASSIC), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS)),
                new BorrowedBook(new Book("ISBN_05", BookCategory.CLASSIC), Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS))]
        ])
        def service = new LendingBookServiceWithDecisionTable(repository, new LendingBookDecisionTable())

        when:
        service.lent("user_id_01",
                [new Book("ISBN_11", BookCategory.NEW),
                 new Book("ISBN_12", BookCategory.CLASSIC),
                 new Book("ISBN_13", BookCategory.STANDARD)])

        then:
        def e = thrown(RuntimeException)
        assert e.getMessage() == MSG_OVER_MAX_BORROWED_COUNT
    }
}
