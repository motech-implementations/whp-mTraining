package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.CourseConfigurationDto;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.whp.mtraining.dto.LocationDto;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.domain.Content;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
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

    private MTrainingService courseService;
    private MotechUserService motechUserService;

    @Autowired
    public CourseImportService(MTrainingService courseService, MotechUserService motechUserService) {
        this.courseService = courseService;
        this.motechUserService = motechUserService;
    }


    public Course importCourse(List<CourseCsvRequest> requests) {
        Map<String, Content> contentMap = formContents(requests, contentAuthor());
        addChildContents(contentMap, requests);
        Content courseContent = contentMap.get(requests.get(0).getNodeName());
        Course course = (Course) courseContent.toDto();

        return courseService.updateCourse(course);
    }

    public void importCourseConfig(List<CourseConfigurationRequest> requests) {
        for (CourseConfigurationRequest request : requests) {
            //courseConfigService.addOrUpdateCourseConfiguration(new CourseConfigurationDto(request.getCourseName(),
            //        valueOf(request.getCourseDurationInDays()), new LocationDto(request.getBlock(), request.getDistrict(), request.getState())));
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