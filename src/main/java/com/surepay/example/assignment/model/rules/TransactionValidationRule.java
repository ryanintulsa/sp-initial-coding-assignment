package com.surepay.example.assignment.model.rules;

import com.surepay.example.assignment.model.Transaction;

/**
 * Instances if this interface check a given transaction for validity,
 * returning null if the transaction is valid, or an error message
 * (including the transaction reference) if the transaction is invalid.
 */
public interface TransactionValidationRule {

    String getValidationErrorMessage(Transaction transaction);
    
}
