package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.web.parser.CsvParser;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.web.request.CsvImportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.StringReader;

@Controller
public class CourseImportController {

    private CsvParser csvParser;

    @Autowired
    public CourseImportController(CsvParser csvParser) {
        this.csvParser = csvParser;
    }

    @RequestMapping(value = "/course-structure/import", method = RequestMethod.POST)
    public ModelAndView importCourseStructure(@ModelAttribute("csvImport") CsvImportRequest csvImportRequest) throws Exception {
        csvParser.parse(new StringReader(csvImportRequest.getStringContent()), CourseStructureCsvRequest.class);
        return null;
    }
}
