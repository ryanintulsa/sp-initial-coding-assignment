package com.surepay.example.assignment.model.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.surepay.example.assignment.model.Transaction;

/**
 * Has this instance already processed a transaction with this reference?
 */ 
public class TransactionRuleIsDuplicate implements TransactionValidationRule {

    private static final String MSG_DUPE_FMT = "A transaction with reference %s has already been processed.";

    // all transaction references already processed
    private final Set<String> references = Collections.synchronizedSet(new HashSet<String>());

    @Override
    public String getValidationErrorMessage(Transaction transaction) {
        return references.add(transaction.getReference())
            ? null
            : String.format(MSG_DUPE_FMT, transaction.getReference());
    }
    
}
