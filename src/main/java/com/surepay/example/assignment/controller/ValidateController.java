package com.surepay.example.assignment.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.surepay.example.assignment.model.Transaction;
import com.surepay.example.assignment.model.TransactionValidator;
import com.surepay.example.assignment.model.parsers.CSVTransactionParser;
import com.surepay.example.assignment.model.parsers.JSONTransactionParser;

@RestController
public class ValidateController {

	private static Logger logger = LoggerFactory.getLogger(ValidateController.class);
	
	/**
	 * This is setup for stream processing so in case of very large file it doesn't have to all be
	 * loaded into memory at once. Note that all files uploaded as form-data will be validated,
	 * and the form key for the files does not matter.
	 * @param request
	 * @return validation error messages
	 */
	@PostMapping("/validate")
	public ResponseEntity<List<String>> streamCSVUpload(final HttpServletRequest request) {
		List<String> validationErrorMessages = new ArrayList<>();
		ServletFileUpload upload = new ServletFileUpload();
		try {

			// loop through each file in the upload (in case multiple files were submitted at once)
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {

				// check if the item is a file
				FileItemStream item = iter.next();
				if (!item.isFormField()) {
					logger.info("processing " + item.getName());
					long startTime = System.currentTimeMillis();

					// check the file contents to detect json vs. csv
					Iterator<Transaction> parser = getParserFromContent(item.openStream());

					// iterate over all transactions in the file stream
					TransactionValidator validator = new TransactionValidator();
					while (parser.hasNext()) {
						Transaction transaction = parser.next();
						validationErrorMessages.addAll(validator.getValidationErrorMessages(transaction));
					}
					long endTime = System.currentTimeMillis();
					logger.info(String.format("finished processing %s in %d ms", item.getName(), endTime - startTime));
				} else {
					logger.warn("ignoring form field " + item.getName());
				}
			}
		} catch (IllegalArgumentException iae) {
			logger.error(iae.getMessage(), iae);
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		} catch (FileUploadException | IOException e) {
			logger.error("parsing error", e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(validationErrorMessages, HttpStatus.OK);
	}

	/**
	 * Choose the correct parser type from the first character of the stream.
	 * @param inputStream
	 * @return Iterator<Transaction>
	 */
	private Iterator<Transaction> getParserFromContent(InputStream inputStream) throws IOException {
		Iterator<Transaction> parser;
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		Reader reader = new InputStreamReader(bis);

		// prepare to reset the stream if the contents are not valid JSON
		bis.mark(0);
		try {
			// try JSONTransactionParser first, as the constructor fails immediately on first character mismatch
			parser = new JSONTransactionParser(reader);
		} catch (Exception jsonException) {
			try {
				// go back (one character) to the start of the stream
				bis.reset();

				// now try CSVTranactionParser, which reads the header row
				parser = new CSVTransactionParser(reader);
			} catch (Exception csvException) {
				throw new IllegalArgumentException("unexpected file content, should be csv or json", csvException);
			}
		}
		return parser;
	}

}