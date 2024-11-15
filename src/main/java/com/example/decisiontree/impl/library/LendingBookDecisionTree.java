package com.example.decisiontree.impl.library;

import com.example.decisiontree.DecisionNode;
import com.example.decisiontree.DecisionTree;
import com.example.decisiontree.LeafNode;

import static com.example.service.lendingbook.domain.Constants.*;

public class LendingBookDecisionTree extends DecisionTree {
    @Override
    protected DecisionNode<?> initialize() {
        return DecisionNode.builder(HasOverdueParameter.class, HasOverdueParameter::hasOverdue)
                .addTrueNode(LeafNode.create(new LendingResult(false, MSG_OVERDUE)))
                .addFalseNode(DecisionNode.builder(BorrowingBooksParameter.class, (p) -> p.count() <= MAX_BORROWING_COUNT)
                        .addFalseNode(LeafNode.create(new LendingResult(false, MSG_OVER_MAX_BORROWING_COUNT)))
                        .addTrueNode(DecisionNode.builder(AllBorrowedBooksParameter.class, (p) -> p.count() <= MAX_BORROWED_COUNT)
                                .addFalseNode(LeafNode.create(new LendingResult(false, MSG_OVER_MAX_BORROWED_COUNT)))
                                .addTrueNode(DecisionNode.builder(AllBorrowedNewCategoryBooksParameter.class, (p) -> p.count() <= MAX_BORROWED_NEW_CATEGORY_COUNT)
                                        .addTrueNode(LeafNode.create(new LendingResult(true, MSG_BLANK)))
                                        .addFalseNode(LeafNode.create(new LendingResult(false, MSG_OVER_MAX_NEW_CATEGORY_COUNT)))
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
