package com.surepay.example.assignment.model.parsers;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.surepay.example.assignment.model.Transaction;

/**
 * This class wraps an apache commons CSVParser, building a Transaction for each record.
 */
public class CSVTransactionParser implements Iterator<Transaction>, Closeable {

    private final CSVParser csvParser;
    private final Iterator<CSVRecord> csvIterator;

    public CSVTransactionParser(Reader reader) throws IOException {
        csvParser = new CSVParser(reader, CSVFormat.Builder.create()
            .setHeader().setSkipHeaderRecord(true)
            .build());
        csvIterator = csvParser.iterator();
    }

    @Override
    public void close() throws IOException {
        csvParser.close();
    }

    @Override
    public boolean hasNext() {
        return csvIterator.hasNext();
    }

    @Override
    public Transaction next() {
        final CSVRecord csvRecord = csvIterator.next();
        return new Transaction(
            csvRecord.get("Reference"),
            csvRecord.get("AccountNumber"),
            csvRecord.get("Description"),
            new BigDecimal(csvRecord.get("Start Balance")),
            new BigDecimal(csvRecord.get("Mutation")),
            new BigDecimal(csvRecord.get("End Balance")));
    }

}
