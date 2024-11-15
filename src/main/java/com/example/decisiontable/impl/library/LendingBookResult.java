package com.example.decisiontable.impl.library;

import com.example.decisiontable.Result;

public record LendingBookResult(String message, boolean canLend) implements Result {
}
