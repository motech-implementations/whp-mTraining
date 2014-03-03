package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.web.model.*;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseImportService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseImportService.class);

    private CourseStructureValidator courseStructureValidator;

    @Autowired
    public CourseImportService(CourseStructureValidator courseStructureValidator) {
        this.courseStructureValidator = courseStructureValidator;
    }

    public List<ErrorModel> parse(List<CourseStructureCsvRequest> requests) {
        return courseStructureValidator.validate(requests);
    }
}

