package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllCoursePublicationAttemptsIT {

    @Autowired
    CoursePublicationAttemptService coursePublicationAttemptService;

    @Test
    public void shouldAddAndRetrieveACourse() {
        assertThat(coursePublicationAttemptService.getAllCoursePublicationAttempt().size(), Is.is(0));

        long courseId = 123L;
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, true);

        coursePublicationAttemptService.createCoursePublicationAttempt(coursePublicationAttempt);

        CoursePublicationAttempt retrievedCoursePublicationAttempt = coursePublicationAttemptService.getCoursePublicationAttemptByCourseId(courseId);
        assertNotNull(retrievedCoursePublicationAttempt);
    }

    @Before
    @After
    public void clearAllCoursePublicationStatus() {
        coursePublicationAttemptService.getAllCoursePublicationAttempt().clear();
    }

    @Test
    public void shouldReturnLastSuccessfulPublicationAttempt() {
        assertThat(coursePublicationAttemptService.getAllCoursePublicationAttempt().size(), Is.is(0));
        long courseId = 123L;
        CoursePublicationAttempt coursePublicationAttemptOld = new CoursePublicationAttempt(courseId, true);
        CoursePublicationAttempt coursePublicationAttemptNew = new CoursePublicationAttempt(courseId, true);
        CoursePublicationAttempt coursePublicationAttemptNewest = new CoursePublicationAttempt(courseId, false);
        coursePublicationAttemptService.createCoursePublicationAttempt(coursePublicationAttemptOld);
        coursePublicationAttemptService.createCoursePublicationAttempt(coursePublicationAttemptNew);
        coursePublicationAttemptService.createCoursePublicationAttempt(coursePublicationAttemptNewest);
        
        //TODO
        //CoursePublicationAttempt lastSuccessfulCoursePublicationAttempt = coursePublicationAttemptService.getLastSuccessfulCoursePublicationAttempt();
        //assertEquals(courseId, lastSuccessfulCoursePublicationAttempt.getCourseId());
    }
}
