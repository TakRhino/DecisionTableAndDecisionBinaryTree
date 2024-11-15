package com.example.service.lendingbook.domain;

public interface Constants {
    int MAX_BORROWING_COUNT = 5;
    int LEND_PERIOD = 30;
    int MAX_BORROWED_COUNT = 7;
    int MAX_BORROWED_NEW_CATEGORY_COUNT = 2;

    String MSG_OVER_MAX_BORROWING_COUNT = String.format("Can borrow a maximum %d books at the same time.", MAX_BORROWING_COUNT);
    String MSG_OVERDUE = String.format("You have overdue books. Must be returned no later than %d days.", LEND_PERIOD);
    String MSG_OVER_MAX_BORROWED_COUNT = String.format("Can have a maximum of %d books borrowed at the same time.", MAX_BORROWED_COUNT);
    String MSG_OVER_MAX_NEW_CATEGORY_COUNT = String.format("Can have max %d NEW-category books borrowed at the same time.", MAX_BORROWED_NEW_CATEGORY_COUNT);
    String MSG_BLANK = "";
}
