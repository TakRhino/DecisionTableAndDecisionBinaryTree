package com.example.service.lendingbook.domain;

import java.time.Instant;

public record BorrowedBook(Book book, Instant lentDate) {
}
