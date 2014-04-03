package org.motechproject.whp.mtraining.csv.web.controller;


import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.csv.response.CsvImportResponse;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.service.impl.CourseImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Controller
public class CourseImportController {
    private static final Logger LOG = LoggerFactory.getLogger(CourseImportController.class);

    private CsvParser csvParser;
    private CourseImportService courseImportService;
    private CourseStructureValidator courseStructureValidator;

    @Autowired
    public CourseImportController(CsvParser csvParser, CourseStructureValidator courseStructureValidator, CourseImportService courseImportService) {
        this.csvParser = csvParser;
        this.courseStructureValidator = courseStructureValidator;
        this.courseImportService = courseImportService;
    }

    @RequestMapping(value = "/course-structure/import", method = RequestMethod.POST)
    @ResponseBody
    public CsvImportResponse importCourseStructure(@RequestParam("multipartFile") CommonsMultipartFile multipartFile) {
        try {
            List<CourseCsvRequest> courseCsvRequests = csvParser.parse(multipartFile, CourseCsvRequest.class);
            List<CsvImportError> errors = courseStructureValidator.validate(courseCsvRequests);
            List<String> errorMessages = new ArrayList<>();
            if (!errors.isEmpty()) {
                return CsvImportResponse.failure(errors);
            }
            ContentIdentifierDto importedCourseIdentifier = courseImportService.importCourse(courseCsvRequests);
            return CsvImportResponse.success(format("Course: %s with version %s has been imported successfully",
                    importedCourseIdentifier.getContentId(),
                    importedCourseIdentifier.getVersion()));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return CsvImportResponse.failure(asList(new CsvImportError(ex.getMessage())));
        }
    }
}
