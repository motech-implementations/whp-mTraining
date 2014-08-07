package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("dtoFactoryService")
public class DtoFactoryServiceImpl implements DtoFactoryService {

    @Autowired
    CoursePlanService coursePlanService;

    @Autowired
    ContentOperationService contentOperationService;

    public static final String DESCRIPTION_MAPPING_NAME = "description";
    public static final String FILENAME_MAPPING_NAME = "filename";

    @Override
    public List<CoursePlanDto> getAllCoursePlanDtos() {
        List<CoursePlan> allCourses = coursePlanService.getAllCoursePlans();
        return convertCoursePlanListToDtos(allCourses);
    }

    @Override
    public CoursePlanDto getCoursePlanDtoById(long courseId) {
        return convertCoursePlanToDto(coursePlanService.getCoursePlanById(courseId));
    }

    @Override
    public void createOrUpdateCoursePlanFromDto(CoursePlanDto coursePlanDto) {
        CoursePlan coursePlan;
        if (coursePlanDto.getId() == 0) {
            coursePlanService.createCoursePlan(generateCoursePlanFromDto(coursePlanDto));
        } else {
            coursePlan = coursePlanService.getCoursePlanById(coursePlanDto.getId());
            coursePlan.setName(coursePlanDto.getName());
            coursePlan.setState(coursePlanDto.getState());
            coursePlan.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (coursePlanDto.getFilename(), coursePlanDto.getDescription()));
            coursePlanService.updateCoursePlan(coursePlan);
        }
    }

    @Override
    public CoursePlan generateCoursePlanFromDto(CoursePlanDto coursePlanDto) {
        return new CoursePlan(coursePlanDto.getName(), coursePlanDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(coursePlanDto.getFilename(), coursePlanDto.getDescription()));
    }
    @Override
    public CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan) {
        CoursePlanDto coursePlanDto = new CoursePlanDto(coursePlan.getId(),coursePlan.getName(),coursePlan.getState(),
                coursePlan.getCreationDate(), coursePlan.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(coursePlanDto, coursePlan.getContent());

        return coursePlanDto;
    }

    @Override
    public List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans) {
        List<CoursePlanDto> coursePlanDtos = new ArrayList<>();

        for (CoursePlan coursePlan : coursePlans) {
            coursePlanDtos.add(convertCoursePlanToDto(coursePlan));
        }
        return coursePlanDtos;
    }
}
