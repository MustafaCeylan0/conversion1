package com.seng.conversion.test;

import com.seng.conversion.controller.RestController;
import com.seng.conversion.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class RestControllerTest {

    private RestController restController;
    private Converter converter;

    @BeforeEach
    void setUp() {
        converter = Mockito.mock(Converter.class);
        restController = new RestController(converter);
    }

    @Test
    void convertFileToDocx() throws ExecutionException, InterruptedException, IOException {
        testConvertEndpoint("docx");
    }

    @Test
    void convertFileToTxt() throws ExecutionException, InterruptedException, IOException {
        testConvertEndpoint("txt");
    }

    @Test
    void convertFileToPng() throws ExecutionException, InterruptedException, IOException {
        testConvertEndpoint("png");
    }

    @Test
    void convertFileToJpg() throws ExecutionException, InterruptedException, IOException {
        testConvertEndpoint("jpg");
    }

    @Test
    void convertFileToXlsx() throws ExecutionException, InterruptedException, IOException {
        testConvertEndpoint("xlsx");
    }

    private void testConvertEndpoint(String format) throws ExecutionException, InterruptedException, IOException {
        // Read the content of the test PDF file from the test resources
        Path pdfPath = Paths.get("src", "test", "files", "sending", "pdf.pdf");
        byte[] pdfContent = Files.readAllBytes(pdfPath);

        // Create a MockMultipartFile with the content of the test PDF
        MultipartFile mockFile = new MockMultipartFile("file", "pdf.pdf", "application/pdf", pdfContent);

        // Mock the converter to return a future with a filename containing the desired format
        String uid = "12345"; // Example UID, in a real scenario, you'd generate this
        String expectedInputFilename = "pdf-" + uid + ".pdf";
        String expectedOutputFilename = "pdf-" + uid + "." + format;
        Mockito.when(converter.convertFile(eq(expectedInputFilename), eq(expectedOutputFilename)))
                .thenReturn(CompletableFuture.completedFuture(expectedOutputFilename));

        // Call the convertFile method of RestController with the mock file and desired format
        CompletableFuture<ResponseEntity<String>> response = restController.convertFile(mockFile, format);

        // Assert that the response status is OK and the body contains the correct download link
        assertEquals(HttpStatus.OK, response.get().getStatusCode());
        assertTrue(response.get().getBody().contains("/download/" + expectedOutputFilename));
    }



    // Additional tests can be added here for error cases, invalid formats, empty files, etc.
}
