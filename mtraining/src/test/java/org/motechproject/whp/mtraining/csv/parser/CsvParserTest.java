package org.motechproject.whp.mtraining.csv.parser;

import org.junit.Test;
import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CsvParserTest {
    private CsvParser csvParser = new CsvParser();
    private MockMultipartFile mockMultipartFile;

    @Test
    public void shouldParseGivenCsvFileToGivenBeanType() throws IOException {
        String resourceName = "file.csv";
        File file = new File("./src/test/resources/file.csv");
        InputStream inputStream = new FileInputStream(file);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);

        List<CourseStructureCsvRequest> actualCourseCsvContent = csvParser.parse(mockMultipartFile, CourseStructureCsvRequest.class);

        assertEquals(4, actualCourseCsvContent.size());
    }

    @Test
    public void shouldParseGivenCsvFileToGivenBeanTypeIgnoringExtraColumnOfCsv() throws IOException {
        String resourceName = "fileWithExtraHeading.csv";
        File file = new File("./src/test/resources/fileWithExtraHeading.csv");
        InputStream inputStream = new FileInputStream(file);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);

        List<CourseStructureCsvRequest> actualCourseCsvContent = csvParser.parse(mockMultipartFile, CourseStructureCsvRequest.class);

        assertEquals(4, actualCourseCsvContent.size());
    }

    @Test(expected=RuntimeException.class)
    public void shouldThrowExceptionIfCsvFileDoesNotContainAllTheHeaders() throws IOException {
        String resourceName = "fileWithMissingHeading.csv";
        File file = new File("./src/test/resources/fileWithMissingHeading.csv");
        InputStream inputStream = new FileInputStream(file);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);
        csvParser.parse(mockMultipartFile, CourseStructureCsvRequest.class);
    }
}
