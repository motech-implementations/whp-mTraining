package org.motechproject.whp.mtraining.csv.web.controller;


import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.csv.response.CsvImportResponse;
import org.motechproject.whp.mtraining.csv.validator.CourseCsvStructureValidator;
import org.motechproject.whp.mtraining.exception.CourseNotFoundException;
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

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.motechproject.whp.mtraining.csv.response.CsvImportResponse.failure;

@Controller
public class CourseImportController {
    private static final Logger LOG = LoggerFactory.getLogger(CourseImportController.class);

    private CsvParser csvParser;
    private CourseImportService courseImportService;
    private CourseCsvStructureValidator courseStructureValidator;

    @Autowired
    public CourseImportController(CsvParser csvParser, CourseCsvStructureValidator courseStructureValidator, CourseImportService courseImportService) {
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
            if (!errors.isEmpty()) {
                return failure(errors);
            }
            courseImportService.importCourseStructure(courseCsvRequests);
            return CsvImportResponse.success(format("Course Structure has been imported successfully"));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return failure(asList(new CsvImportError(ex.getMessage())));
        }
    }

    @RequestMapping(value = "/course-config/import", method = RequestMethod.POST)
    @ResponseBody
    public CsvImportResponse importCourseConfigs(@RequestParam("multipartFile") CommonsMultipartFile multipartFile) {
        try {
            List<CourseConfigurationRequest> courseConfigurationRequests = csvParser.parse(multipartFile, CourseConfigurationRequest.class);
            List<CsvImportError> errors = newArrayList();
            for (CourseConfigurationRequest courseConfigurationRequest : courseConfigurationRequests) {
                errors.addAll(courseConfigurationRequest.validate());
            }
            if (!errors.isEmpty()) {
                return failure(errors);
            }
            courseImportService.importCourseConfig(courseConfigurationRequests);
            return CsvImportResponse.success("Configuration for courses have been imported successfully");
        } catch (CourseNotFoundException courseNotFoundException) {
            return failure(asList(new CsvImportError(courseNotFoundException.getCourseName(), "Course", "Course could not be found in the CSV file." )));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return failure(asList(new CsvImportError(ex.getMessage())));
        }
    }
}
