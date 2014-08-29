package org.motechproject.whp.mtraining.web.controller;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.web.domain.CourseProgress;
import org.motechproject.whp.mtraining.web.domain.FlagResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

public class FlagController {

    @Autowired
    DtoFactoryService dtoFactoryService;

    @RequestMapping(value = "/bookmark", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public FlagResponse getFlag(@PathVariable long callerId, @PathVariable String uniqueId, @PathVariable UUID sessionId) {
        ModuleDto course = dtoFactoryService.getAllModuleDtos().get(0);
        if (course == null) {
            return null;
        }
        Location location = new Location("state");
        String courseStartTime = DateTime.now().toString();
        int timeLeftToCompleteCourse = 0;
        String courseStatus = course.getState().toString();


        FlagBuilder flagBuilder = new FlagBuilder();
        Flag flag = flagBuilder.buildFlagFromFirstActiveMetadata(course.getExternalId(), course);
        CourseProgress courseProgress = new CourseProgress(courseStartTime, flag, timeLeftToCompleteCourse, courseStatus);
        return new FlagResponse(callerId, uniqueId, sessionId, location, courseProgress);
    }
}
