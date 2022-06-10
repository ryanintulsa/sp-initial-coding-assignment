package com.surepay.example.assignment.model.parsers;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.surepay.example.assignment.model.Transaction;

/**
 * This class wraps a jackson JsonParser, building a Transaction for each record.
 * Note that org.springframework.boot.json does not support streaming, which is
 * necessary for processing large data sets without using too much memory.
 */
public class JSONTransactionParser implements Iterator<Transaction>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(JSONTransactionParser.class);

    private final JsonParser jsonParser;

    public JSONTransactionParser(Reader reader) throws IOException {
        jsonParser = new JsonFactory().createParser(reader);

        // Check the first token
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalArgumentException("Expected content to be an array");
        }
    }

    @Override
    public void close() throws IOException {
        jsonParser.close();
    }

    @Override
    public boolean hasNext() {
        try {
            return jsonParser.hasCurrentToken() && jsonParser.nextToken() != JsonToken.END_ARRAY;
        } catch (IOException e) {
            // If jsonParser.nextToken() fails, then we do not have a next Transaction.
            logger.warn("IOException during JSON parsing, hasNext()", e);
            return false;
        }
    }

    @Override
    public Transaction next() {
        // Check the first token
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected content to be an object");
        }

        // Iterate over the properties of the object, building the Transaction a field at a time
        final Transaction.Builder builder = new Transaction.Builder();
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                setTransactionField(builder);
            }
        } catch (IOException e) {
            // If the Transaction fails to build, return null
            logger.warn("IOException during JSON parsing, next()", e);
            return null;
        }
        return builder.build();
    }

    private void setTransactionField(Transaction.Builder builder) throws IOException {
        // Get the current property name
        String property = jsonParser.getCurrentName();

        // Move to the corresponding value
        jsonParser.nextToken();

        // Evaluate each property name and extract the value
        switch (property) {
            case "reference":
                builder.reference(jsonParser.getText());
                break;
            case "accountNumber":
                builder.accountNumber(jsonParser.getText());
                break;
            case "description":
                builder.description(jsonParser.getText());
                break;
            case "startBalance":
                builder.startBalance(new BigDecimal(jsonParser.getText()));
                break;
            case "mutation":
                builder.mutation(new BigDecimal(jsonParser.getText()));
                break;
            case "endBalance":
                builder.endBalance(new BigDecimal(jsonParser.getText()));
                break;
            // Unknown properties are ignored
        }
    }
}
