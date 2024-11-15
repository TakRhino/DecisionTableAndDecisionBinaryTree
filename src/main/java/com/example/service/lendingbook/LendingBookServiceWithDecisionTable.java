package com.example.service.lendingbook;

import com.example.decisiontable.impl.library.*;
import com.example.service.lendingbook.domain.Book;
import com.example.service.lendingbook.domain.BookCategory;
import com.example.service.lendingbook.domain.BorrowedBook;
import com.example.service.lendingbook.repository.UserBorrowedBookRepositoryMock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public class LendingBookServiceWithDecisionTable {
    private final UserBorrowedBookRepositoryMock userBorrowedBookRepository;
    private final LendingBookDecisionTable lendingBookDecisionTable;

    public LendingBookServiceWithDecisionTable(UserBorrowedBookRepositoryMock userBorrowedBookRepository, LendingBookDecisionTable lendingBookDecisionTable) {
        this.userBorrowedBookRepository = userBorrowedBookRepository;
        this.lendingBookDecisionTable = lendingBookDecisionTable;
    }

    public void lent(String userId, List<Book> lendingBooks) {
        var today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        lendingBooks.forEach(b -> checkAndLent(userId, b, today));
    }

    public void checkAndLent(String userId, Book lendingBook, Instant today) {
        var hasOverdueBooks = userBorrowedBookRepository.hasOverdueBooks(userId);
        var borrowingCount = userBorrowedBookRepository.countBooks(userId, today) + 1;
        var allBorrowedCount = userBorrowedBookRepository.countBooks(userId) + 1;
        var allBorrowedNewCategoryCount = userBorrowedBookRepository.countNewCategoryBooks(userId) + (lendingBook.bookCategory().equals(BookCategory.NEW) ? 1 : 0);
        var result = resolve(hasOverdueBooks, borrowingCount, allBorrowedCount, allBorrowedNewCategoryCount);
        if (result.canLend()) {
            userBorrowedBookRepository.addBook(userId, new BorrowedBook(lendingBook, today));
        } else {
            throw new RuntimeException(result.message());
        }
    }

    protected LendingBookResult resolve(boolean hasOverdueBooks, int borrowingCount, int allBorrowedCount, int allBorrowedNewCategoryCount) {
        return (LendingBookResult) lendingBookDecisionTable.resolve(Set.of(
                new HasOverdueParameter(hasOverdueBooks),
                new BorrowingBooksParameter(borrowingCount),
                new AllBorrowedBooksParameter(allBorrowedCount),
                new AllBorrowedNewCategoryBooksParameter(allBorrowedNewCategoryCount)
        ));
    }
}
