package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.domain.Content;
import org.motechproject.whp.mtraining.domain.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseImportService {

    private CourseService courseService;

    @Autowired
    public CourseImportService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void importCourse(List<CourseStructureCsvRequest> requests) {
        Map<String, Content> contentMap = formContents(requests);

        addChildContents(contentMap, requests);

        Content courseContent = contentMap.get(requests.get(0).getNodeName());
        CourseDto courseDto = (CourseDto) courseContent.toDto();

        courseService.addCourse(courseDto);
    }

    private Map<String, Content> formContents(List<CourseStructureCsvRequest> requests) {
        Map<String, Content> contentMap = new HashMap<>();
        for (CourseStructureCsvRequest request : requests) {
            Content content = new Content(request.getNodeName(), ContentType.from(request.getNodeType()), request.getStatus(), request.getDescription(), request.getFileName());
            contentMap.put(request.getNodeName(), content);
        }
        return contentMap;
    }

    private void addChildContents(Map<String, Content> contentMap, List<CourseStructureCsvRequest> requests) {
        for (CourseStructureCsvRequest request : requests) {
            Content content = contentMap.get(request.getNodeName());
            Content parentContent = contentMap.get(request.getParentNode());
            if (parentContent != null)
                parentContent.addChildContent(content);
        }
    }
}

