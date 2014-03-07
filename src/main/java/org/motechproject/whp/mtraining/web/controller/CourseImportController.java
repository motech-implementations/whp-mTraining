package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.response.CourseImportResponse;
import org.motechproject.whp.mtraining.csv.validator.CourseImportError;
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

import java.util.List;

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
    public CourseImportResponse importCourseStructure(@RequestParam("multipartFile") CommonsMultipartFile multipartFile) {
        try {
            List<CourseStructureCsvRequest> courseStructureCsvRequests = csvParser.parse(multipartFile, CourseStructureCsvRequest.class);
            List<CourseImportError> errors = courseStructureValidator.validate(courseStructureCsvRequests);
            if (!errors.isEmpty()) {
                return CourseImportResponse.failure(errors);
            }
            courseImportService.importCourse(courseStructureCsvRequests);
            return CourseImportResponse.success();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return CourseImportResponse.failure(asList(new CourseImportError(ex.getMessage())));
        }
    }
}
