package com.example.service.lendingbook;

import com.example.decisiontree.impl.library.*;
import com.example.service.lendingbook.domain.Book;
import com.example.service.lendingbook.domain.BookCategory;
import com.example.service.lendingbook.domain.BorrowedBook;
import com.example.service.lendingbook.repository.UserBorrowedBookRepositoryMock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public class LendingBookServiceWithDecisionTree {
    private final UserBorrowedBookRepositoryMock userBorrowedBookRepository;
    private final LendingBookDecisionTree lendingBookDecisionTree;

    public LendingBookServiceWithDecisionTree(UserBorrowedBookRepositoryMock userBorrowedBookRepository, LendingBookDecisionTree lendingBookDecisionTree) {
        this.userBorrowedBookRepository = userBorrowedBookRepository;
        this.lendingBookDecisionTree = lendingBookDecisionTree;
    }

    public void lent(String userId, List<Book> lendingBooks) {
        var today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        lendingBooks.forEach(b -> checkAndLent(userId, b, today));
    }

    public void checkAndLent(String userId, Book lendingBook, Instant today) {
        var allBorrowedCount = userBorrowedBookRepository.countBooks(userId) + 1;
        var lendingCount = userBorrowedBookRepository.countBooks(userId, today) + 1;
        var allNewCategoryCount = userBorrowedBookRepository.countNewCategoryBooks(userId) + (lendingBook.bookCategory().equals(BookCategory.NEW) ? 1 : 0);
        var hasOverdueBooks = userBorrowedBookRepository.hasOverdueBooks(userId);
        var result = resolve(hasOverdueBooks, lendingCount, allBorrowedCount, allNewCategoryCount);
        if (result.canLend()) {
            userBorrowedBookRepository.addBook(userId, new BorrowedBook(lendingBook, today));
        } else {
            throw new RuntimeException(result.message());
        }
    }

    protected LendingResult resolve(boolean hasOverdueBooks, int lendingCount, int allBorrowedCount, int allNewCategoryCount) {
        return (LendingResult) lendingBookDecisionTree.resolve(Set.of(
                new HasOverdueParameter(hasOverdueBooks),
                new BorrowingBooksParameter(lendingCount),
                new AllBorrowedBooksParameter(allBorrowedCount),
                new AllBorrowedNewCategoryBooksParameter(allNewCategoryCount)
        ));
    }
}
