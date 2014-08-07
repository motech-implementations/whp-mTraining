package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;

import java.util.List;

/**
 * Includes building
 */
public interface DtoFactoryService {

    List<CoursePlanDto> getAllCoursePlanDtos();

    CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan);

    List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans);

    CoursePlanDto getCoursePlanDtoById(long courseId);

    void createOrUpdateCoursePlanFromDto(CoursePlanDto coursePlanDto);

    CoursePlan generateCoursePlanFromDto(CoursePlanDto coursePlanDto);


    List<ModuleDto> getAllModuleDtos();

    ModuleDto convertModuleToDto(Course module);

    List<ModuleDto> convertModuleListToDtos (List<Course> modules);

    ModuleDto getModuleDtoById(long moduleId);

    void createOrUpdateModuleFromDto(ModuleDto moduleDto);

    Course generateModuleFromDto(ModuleDto moduleDto);


}
