package org.motechproject.whp.mtraining.web.parser;

import org.junit.Test;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;

import java.io.StringReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CsvParserTest {
    private CsvParser csvParser = new CsvParser();

    @Test
    public void shouldParseGivenCsvContentReaderToGivenBeanType() {
        String csvContent = "nodeName,nodeType,status,parentNode,description,fileName\n" +
                "Basic TB Symptoms,Message,Active,Chapter TB Symptoms,Message Description,Audio_TB_Basic_Symptoms.filetype";
        CourseStructureCsvRequest expectedCsvRequest = new CourseStructureCsvRequest("Basic TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "Audio_TB_Basic_Symptoms.filetype");

        List<CourseStructureCsvRequest> actualCourseCsvContent = csvParser.parse(new StringReader(csvContent), CourseStructureCsvRequest.class);

        assertEquals(1, actualCourseCsvContent.size());
        assertEquals(expectedCsvRequest, actualCourseCsvContent.get(0));
    }

    @Test
    public void shouldParseGivenCsvContentReaderToGivenBeanTypeWithFileNameAsNullWhenNodeTypeNotMessage() {
        String csvContent = "nodeName,nodeTypeName,status,parentNode,description,fileName\n" +
                "Basic TB Symptoms,Message,Active,Chapter TB Symptoms,Message Description";

        List<CourseStructureCsvRequest> actualCourseCsvContent = csvParser.parse(new StringReader(csvContent), CourseStructureCsvRequest.class);

        assertEquals(1, actualCourseCsvContent.size());
        assertEquals(null,actualCourseCsvContent.get(0).getFileName());
    }
}
