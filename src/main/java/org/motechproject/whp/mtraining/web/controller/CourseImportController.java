package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.web.model.ErrorModel;
import org.motechproject.whp.mtraining.web.parser.CsvParser;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.web.service.CourseStructureService;
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
import java.util.Arrays;
import java.util.List;

@Controller
public class CourseImportController {

    private CsvParser csvParser;
    private CourseStructureService courseStructureService;
    private static final Logger LOG = LoggerFactory.getLogger(CourseImportController.class);

    @Autowired
    public CourseImportController(CsvParser csvParser, CourseStructureService courseStructureService) {
        this.csvParser = csvParser;
        this.courseStructureService = courseStructureService;
    }

    @RequestMapping(value = "/course-structure/import", method = RequestMethod.POST)
    @ResponseBody
    public List<ErrorModel> importCourseStructure(@RequestParam("multipartFile") CommonsMultipartFile multipartFile) throws Exception {
        try {
            List<CourseStructureCsvRequest> courseStructureCsvRequests = csvParser.parse(multipartFile, CourseStructureCsvRequest.class);
            return courseStructureService.parseToCourseStructure(courseStructureCsvRequests);
        } catch (RuntimeException ex) {
            LOG.error(ex.getMessage());
            return new ArrayList<>(Arrays.asList(new ErrorModel(ex.getMessage())));
        }
    }
}
