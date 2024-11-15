package com.example.decisiontree.impl.library;

import com.example.decisiontree.Result;

public record LendingResult(boolean canLend, String message) implements Result {
}
