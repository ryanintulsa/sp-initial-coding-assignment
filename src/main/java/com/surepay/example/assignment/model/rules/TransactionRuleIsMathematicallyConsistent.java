package com.surepay.example.assignment.model.rules;

import java.math.BigDecimal;

import com.surepay.example.assignment.model.Transaction;

/**
 * Does the endBalance match the startBalance plus the mutation?
 */ 
public class TransactionRuleIsMathematicallyConsistent implements TransactionValidationRule {

    private static final String MSG_MATH_FMT = "Expected end balance %s for transaction %s with starting balance %s and mutation %s, but was %s";

    @Override
    public String getValidationErrorMessage(Transaction transaction) {
        BigDecimal expected = transaction.getStartBalance().add(transaction.getMutation());
        // use compareTo for BigDecimals when a precision check is not needed
        return 0 == transaction.getEndBalance().compareTo(expected)
            ? null
            : String.format(MSG_MATH_FMT,
                expected,
                transaction.getReference(),
                transaction.getStartBalance().toString(),
                transaction.getMutation().toString(),
                transaction.getEndBalance().toString());
    }
    
}
