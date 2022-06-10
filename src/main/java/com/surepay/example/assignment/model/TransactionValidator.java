package com.surepay.example.assignment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.surepay.example.assignment.model.rules.TransactionRuleIsDuplicate;
import com.surepay.example.assignment.model.rules.TransactionRuleIsMathematicallyConsistent;
import com.surepay.example.assignment.model.rules.TransactionValidationRule;

/**
 * Checks that all transaction references are unique
 * and start/end balances are consistent with the muation.
 * 
 * Instance State: all processed references
 * Threadsafe
 */
@Component("transactionValidator")
public class TransactionValidator {
    
    private final List<TransactionValidationRule> rules = new ArrayList<>();

    /**
     * The list of rules is specified here rather than injected, because it is
     * easy this way (where else would you look for what rules are used?), and
     * if this class were any more generic it would just be another rules engine.
     */
    public TransactionValidator() {
        // Note that we want to create new instances each time (don't make these static)
        // because some rules are stateful.
        rules.add(new TransactionRuleIsDuplicate());
        rules.add(new TransactionRuleIsMathematicallyConsistent());
    }

    /**
     * @param transaction
     * @return a list of error messages, since it is possible for a single transaction
     * to have multiple validation errors. A valid transaction will return an empty list.
     */
    public List<String> getValidationErrorMessages(Transaction transaction) {
        return rules.stream()
            .map(rule -> rule.getValidationErrorMessage(transaction))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
