package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CsvRequest;
import org.motechproject.whp.mtraining.domain.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class CourseImportService {

    private CourseService courseService;
    private CourseUpdater courseUpdater;

    @Autowired
    public CourseImportService(CourseService courseService, CourseUpdater courseUpdater) {
        this.courseService = courseService;
        this.courseUpdater = courseUpdater;
    }

    public ContentIdentifierDto importCourse(List<CsvRequest> requests) {
        Map<String, Content> contentMap = formContents(requests);

        addChildContents(contentMap, requests);

        Content courseContent = contentMap.get(requests.get(0).getNodeName());
        CourseDto courseDto = (CourseDto) courseContent.toDto();
        courseUpdater.update(asList(courseDto));

        return courseService.addOrUpdateCourse(courseDto);
    }

    private Map<String, Content> formContents(List<CsvRequest> requests) {
        Map<String, Content> contentMap = new HashMap<>();
        for (CsvRequest request : requests) {
            String noOfQuizQuestions = request.getNoOfQuizQuestions();
            Content content = new Content(request.getNodeName(), request.getNodeType(), request.getStatus(), request.getDescription(),
                    request.getFileName(), isBlank(noOfQuizQuestions) ? 0 : Integer.parseInt(noOfQuizQuestions), request.getOptionsAsList(),
                    request.getCorrectAnswer(), request.getCorrectAnswerFileName(), isBlank(request.getPassPercentage()) ? null : Long.getLong(request.getPassPercentage()));
            contentMap.put(request.getNodeName(), content);
        }
        return contentMap;
    }

    private void addChildContents(Map<String, Content> contentMap, List<CsvRequest> requests) {
        for (CsvRequest request : requests) {
            Content content = contentMap.get(request.getNodeName());
            Content parentContent = contentMap.get(request.getParentNode());
            if (parentContent != null)
                parentContent.addChildContent(content);
        }
    }
}

