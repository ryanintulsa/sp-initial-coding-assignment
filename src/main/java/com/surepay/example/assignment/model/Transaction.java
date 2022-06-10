package com.surepay.example.assignment.model;

import java.math.BigDecimal;

/**
 * Struct that holds the values for each transaction record
 * 
 * BigDecimals are used instead of float/double because these values are all currency,
 * and floating point error is not acceptable. String type is avoided for these fields
 * as well, because if they were used it would not be obvious the values are numeric.
 */
public class Transaction {
    private final String reference, accountNumber, description;
    private final BigDecimal startBalance, mutation, endBalance;
    
    public Transaction(String reference, String accountNumber, String description, BigDecimal startBalance,
            BigDecimal mutation, BigDecimal endBalance) {
        this.reference = reference;
        this.accountNumber = accountNumber;
        this.description = description;
        this.startBalance = startBalance;
        this.mutation = mutation;
        this.endBalance = endBalance;
    }

    public String getReference() {
        return reference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public BigDecimal getMutation() {
        return mutation;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    /**
     * We supply a Builder so we can set a field at a time (useful when
     * parsing streaming input) but keep each Transaction immutable.
     */
    public static class Builder {
        private String reference, accountNumber, description;
        private BigDecimal startBalance, mutation, endBalance;
    
        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }
    
        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startBalance(BigDecimal startBalance) {
            this.startBalance = startBalance;
            return this;
        }
        
        public Builder mutation(BigDecimal mutation) {
            this.mutation = mutation;
            return this;
        }
        
        public Builder endBalance(BigDecimal endBalance) {
            this.endBalance = endBalance;
            return this;
        }
        
        public Transaction build() {
            return new Transaction(reference, accountNumber, description, startBalance, mutation, endBalance);
        }
    }
}
