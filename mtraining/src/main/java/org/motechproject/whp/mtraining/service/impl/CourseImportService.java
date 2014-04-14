package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseConfigurationDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.ConfigurationService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.domain.Content;
import org.motechproject.whp.mtraining.csv.request.CourseConfigRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class CourseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseImportService.class);

    private CourseService courseService;
    private CourseUpdater courseUpdater;
    private MotechUserService motechUserService;
    private ConfigurationService courseConfigService;

    @Autowired
    public CourseImportService(CourseService courseService, CourseUpdater courseUpdater, MotechUserService motechUserService, ConfigurationService courseConfigService) {
        this.courseService = courseService;
        this.courseUpdater = courseUpdater;
        this.motechUserService = motechUserService;
        this.courseConfigService = courseConfigService;
    }


    public ContentIdentifierDto importCourse(List<CourseCsvRequest> requests) {
        Map<String, Content> contentMap = formContents(requests, contentAuthor());
        addChildContents(contentMap, requests);
        Content courseContent = contentMap.get(requests.get(0).getNodeName());
        CourseDto courseDto = (CourseDto) courseContent.toDto();
        courseUpdater.update(asList(courseDto));

        return courseService.addOrUpdateCourse(courseDto);
    }

    public void importCourseConfig(List<CourseConfigRequest> requests) {
        for (CourseConfigRequest request : requests) {
            courseConfigService.addOrUpdateCourseConfiguration(new CourseConfigurationDto(request.getCourseName(), valueOf(request.getCourseDurationInDays())));
        }
    }

    private Map<String, Content> formContents(List<CourseCsvRequest> requests, String contentAuthor) {
        Map<String, Content> contentMap = new HashMap<>();
        for (CourseCsvRequest request : requests) {
            String noOfQuizQuestions = request.getNoOfQuizQuestions();

            Integer numberOfQuizQuestions = isBlank(noOfQuizQuestions) ? 0 : Integer.parseInt(noOfQuizQuestions);
            Double passPercentage = isBlank(request.getPassPercentage()) ? null : Double.parseDouble(request.getPassPercentage());

            Content content = new Content(request.getNodeName(), request.getNodeType(), request.getStatus(), request.getDescription(),
                    request.getFileName(), numberOfQuizQuestions, request.getOptionsAsList(),
                    request.getCorrectAnswer(), request.getCorrectAnswerFileName(), passPercentage, contentAuthor);
            contentMap.put(request.getNodeName(), content);
        }
        return contentMap;
    }

    private void addChildContents(Map<String, Content> contentMap, List<CourseCsvRequest> requests) {
        for (CourseCsvRequest request : requests) {
            Content content = contentMap.get(request.getNodeName());
            Content parentContent = contentMap.get(request.getParentNode());
            if (parentContent != null)
                parentContent.addChildContent(content);
        }
    }

    private String contentAuthor() {
        UserDto currentUser = motechUserService.getCurrentUser();
        return currentUser == null ? null : currentUser.getUserName();
    }
}