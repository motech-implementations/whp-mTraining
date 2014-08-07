package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;

import java.util.List;

/**
 * Includes building
 */
public interface DtoFactoryService {

    List<CoursePlanDto> getAllCoursePlanDtos();

    CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan);

    List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans);
}
