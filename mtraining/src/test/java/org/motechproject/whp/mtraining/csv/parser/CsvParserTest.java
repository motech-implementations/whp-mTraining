package org.motechproject.whp.mtraining.csv.parser;

import org.junit.Test;
import org.motechproject.whp.mtraining.csv.request.CsvRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CsvParserTest {
    private CsvParser csvParser = new CsvParser();
    private MockMultipartFile mockMultipartFile;

    @Test
    public void shouldParseGivenCsvFileToGivenBeanType() throws IOException, URISyntaxException {
        String resourceName = "file.csv";
        InputStream inputStream = getFileInputStream(resourceName);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);

        List<CsvRequest> actualCourseCsvContent = csvParser.parse(mockMultipartFile, CsvRequest.class);

        assertEquals(4, actualCourseCsvContent.size());
    }

    @Test
    public void shouldParseGivenCsvFileToGivenBeanTypeIgnoringExtraColumnOfCsv() throws IOException, URISyntaxException {
        String resourceName = "fileWithExtraHeading.csv";
        InputStream inputStream = getFileInputStream(resourceName);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);

        List<CsvRequest> actualCourseCsvContent = csvParser.parse(mockMultipartFile, CsvRequest.class);

        assertEquals(4, actualCourseCsvContent.size());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfCsvFileDoesNotContainAllTheHeaders() throws IOException, URISyntaxException {
        String resourceName = "fileWithMissingHeading.csv";
        InputStream inputStream = getFileInputStream(resourceName);
        mockMultipartFile = new MockMultipartFile(resourceName, resourceName, "", inputStream);
        csvParser.parse(mockMultipartFile, CsvRequest.class);
    }

    private InputStream getFileInputStream(String resourceName) throws URISyntaxException, FileNotFoundException {
        URL systemResource = ClassLoader.getSystemResource(resourceName);
        assertNotNull(systemResource);
        File file = new File(systemResource.toURI());
        return new FileInputStream(file);
    }

}
