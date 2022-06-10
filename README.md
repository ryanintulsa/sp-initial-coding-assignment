## Install (requires maven and a JDK)
`./mvnw clean install`

## Only Run Tests (Install, above, also runs the tests)
`./mvnw test`

## Run Server
`./mvnw spring-boot:run`

## Upload
POST any number of csv and/or json files as form-data to, e.g., `localhost:8080/validate`. Key name makes no difference. 

## Design Choices
- All uploads and processors are streaming so memory won't blow up with large uploads.
- If a record has multiple validation failures (duplicate and mathematically inconsistent), all failures are
returned instead of just the first. This prevents having downstream failures after the first round of data fixes.

## Notes on Performance
Since the entire microservice is designed with streaming input in mind, there is no maximum file size, and memory
usage is minimal. It takes about 13 seconds on my Windows laptop to validate a 5 million row csv file, and the
increased memory usage during this time is immeasurably small. The test file had one invalid row (the last one).
If you were to say, copy/paste the same five rows over and over, with one row being invalid, each invalid
message is cached for response. The result would be a linear increase in memory usage in relation to input file
size. This seems a very unlikely use case, so I have not yet pursued streaming the response.

## Notes on State and Duplicate Reference Checks
As a data store was not requested, and is assumed to be out-of-scope given the timeframe of the assignment,
each request is RESTful and idempotent, meaning each execution with the same inputs will give the same results.
There is no back-end state. This means that the reference cache is unique per file upload. The result is, as
is assumed desired behavior, that running the same upload twice will not flag everything in the second upload
as a duplicate.

To change this behavior, a data store would be needed. Simply making the cache static might appear to work at
first, but any time the server restarts for whatever reason, the cache will be reset, and duplicates from before
the reset will not be caught. This results in the behavior changing based on the time of the last server reset,
which cannot be desirable. Also if deployed behind a load balancer, you could reach a different instance (different
cache) on subsequent calls. A proper data store, such as a relational or document database, is the solution for
cross-upload duplicate checks.

## Known Issues
Sometimes when uploading large csv files (5 million records), a 400 Bad Request will be returned because the service
believes it received an invalid csv file. This appears to be a race condition within the Apache Commons CSVParser,
where it sometimes fails to pushback an incomplete row before the subsequent call to next(). Due to the scope of this
assignment, I have chosen to leave this as is. If I were to work on this further I would first try different CSV
streaming libraries, and failing that, write my own. The latter option could be done quickly since in this case we
happen to know that there will be no nested commas between quotes. On hasNext, the reader would simply check for any
newline after any non-whitespace character. On next, the reader would simply pushback the last line if it is not
terminated by a newline or EOF.
