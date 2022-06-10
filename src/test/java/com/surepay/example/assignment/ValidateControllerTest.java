package com.surepay.example.assignment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ValidateControllerTest {

	private static final String DATA_DIR = "./src/test/resources";
	private static final String ENDPOINT = "/validate";
	private static final String BOUNDARY = "A12B34567";
	private static final String MLT_PART_FMT = "--%s\r\n"
		+ "Content-Disposition: form-data; name=\"file\"; filename=\"%s\"\r\n"
		+ "Content-Type: application/octet-stream\r\n\r\n"
		+ "%s\r\n"
		+ "--%s--\r\n";
		
	@Autowired
	private MockMvc mvc;

	@Test
	public void postCSV() throws Exception {
		Path path = Path.of(DATA_DIR, "records2.csv");
		final String expected = "[\"Expected end balance -20.23 for transaction 194261 with starting balance 21.6 and mutation -41.83, but was -100\","
			+ "\"A transaction with reference 112806 has already been processed.\","
			+ "\"A transaction with reference 112806 has already been processed.\","
			+ "\"Expected end balance 3 for transaction 222222 with starting balance 5 and mutation -2, but was 1\"]";

		mockUpload(path, expected);
	}
	
	@Test
	public void postJSON() throws Exception {
		Path path = Path.of(DATA_DIR, "records2.json");
		final String expected = "[\"Expected end balance 4490 for transaction 167875 with starting balance 5429 and mutation -939, but was 6368\","
			+ "\"Expected end balance 4980 for transaction 165102 with starting balance 3980 and mutation 1000, but was 4981\","
			+ "\"A transaction with reference 165102 has already been processed.\","
			+ "\"Expected end balance 4980 for transaction 165102 with starting balance 3980 and mutation 1000, but was 4981\"]";

		mockUpload(path, expected);
	}
	
	/**
	 * sends the file at the given path to the ValidateController and confirms the response is as expected
	 */
	private void mockUpload(Path path, String expectedOutput) throws Exception {
		Map<String, String> contentTypeParams = new HashMap<String, String>();
		contentTypeParams.put("boundary", BOUNDARY);
		MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);
	
		RequestBuilder builder = post(ENDPOINT)
			.contentType(mediaType)
			.content(buildMultipartContent(path));
	  	mvc.perform(builder)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(expectedOutput));
	}

	/**
	 * builds the contents of the file at the given path into valid multipart message content
	 */
	private String buildMultipartContent(Path path) throws IOException {
		return String.format(MLT_PART_FMT,
			BOUNDARY, path.getFileName(), Files.readString(path), BOUNDARY);
	}
}