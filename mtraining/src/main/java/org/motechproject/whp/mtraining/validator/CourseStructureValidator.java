package org.motechproject.whp.mtraining.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.impl.ContentOperationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
* Validator for validating fields which represent the course structure:
* {@link Course}
* {@link Chapter}
* {@link Lesson}
*/

@Component
public class CourseStructureValidator {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private CoursePlanService coursePlanService;

    public boolean isPresentInDb(CourseUnitMetadataDto courseUnitMetadataDto) {
        CourseUnitMetadata existing = null;
        List<CourseUnitMetadata> courseUnitMetadataList = new ArrayList<>();
        String nodeName = courseUnitMetadataDto.getName();
        if (courseUnitMetadataDto instanceof CoursePlanDto) {
            existing = coursePlanService.getCoursePlanByName(nodeName);
        } else if (courseUnitMetadataDto instanceof ModuleDto) {
            courseUnitMetadataList.addAll(mTrainingService.getCourseByName(nodeName));
        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            courseUnitMetadataList.addAll(mTrainingService.getChapterByName(nodeName));
        } else if (courseUnitMetadataDto instanceof LessonDto) {
            courseUnitMetadataList.addAll(mTrainingService.getLessonByName(nodeName));
        } else if (courseUnitMetadataDto instanceof QuizDto) {
            courseUnitMetadataList.addAll(mTrainingService.getQuizByName(nodeName));
        }
        if (existing != null || !courseUnitMetadataList.isEmpty()) {
            LOG.error("element of that name exists in the database");

            return false;
        }
        return true;
    }
}
