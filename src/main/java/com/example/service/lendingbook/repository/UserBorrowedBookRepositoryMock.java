package com.example.service.lendingbook.repository;

import com.example.service.lendingbook.domain.BookCategory;
import com.example.service.lendingbook.domain.BorrowedBook;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class UserBorrowedBookRepositoryMock {
    private final Map<String, List<BorrowedBook>> books;

    public UserBorrowedBookRepositoryMock(Map<String, List<BorrowedBook>> books) {
        this.books = books;
    }

    public void addBook(String userId, BorrowedBook borrowedBook) {
        books.get(userId).add(borrowedBook);
    }

    public int countBooks(String userId) {
        return books.get(userId).size();
    }

    public int countBooks(String userId, Instant today) {
        return Math.toIntExact(books.get(userId).stream().filter(b -> b.lentDate().truncatedTo(ChronoUnit.DAYS).equals(today.truncatedTo(ChronoUnit.DAYS))).count());
    }

    public int countNewCategoryBooks(String userId) {
        return Math.toIntExact(books.get(userId).stream().filter(b -> b.book().bookCategory().equals(BookCategory.NEW)).count());
    }

    public boolean hasOverdueBooks(String userId) {
        return books.get(userId).stream().anyMatch(b -> b.lentDate().isBefore(Instant.now().minus(30, ChronoUnit.DAYS)));
    }
}
