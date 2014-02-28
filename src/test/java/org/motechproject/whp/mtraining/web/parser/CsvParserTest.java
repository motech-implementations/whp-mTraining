package org.motechproject.whp.mtraining.web.parser;

import org.junit.Test;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
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
        String resourceName = "fileWithoutHeading.csv";
        File file = new File("./src/test/resources/fileWithoutHeading.csv");
        InputStream inputStream = new FileInputStream(file);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);

        List<CourseStructureCsvRequest> actualCourseCsvContent = csvParser.parse(mockMultipartFile, CourseStructureCsvRequest.class);

        assertEquals(4, actualCourseCsvContent.size());
    }
}
