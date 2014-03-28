package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
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
    AllCoursePublicationAttempts allCoursePublicationAttempts;

    @Test
    public void shouldAddAndRetrieveACourse() {
        assertThat(allCoursePublicationAttempts.all().size(), Is.is(0));

        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, 3, true);

        allCoursePublicationAttempts.add(coursePublicationAttempt);

        CoursePublicationAttempt retrievedCoursePublicationAttempt = allCoursePublicationAttempts.byCourseId(courseId);
        assertNotNull(retrievedCoursePublicationAttempt);
    }

    @Before
    @After
    public void clearAllCoursePublicationStatus() {
        allCoursePublicationAttempts.deleteAll();
    }

    @Test
    public void shouldReturnLastSuccessfulPublicationAttempt() {
        assertThat(allCoursePublicationAttempts.all().size(), Is.is(0));
        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttemptOld = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt coursePublicationAttemptNew = new CoursePublicationAttempt(courseId, 2, true);
        CoursePublicationAttempt coursePublicationAttemptNewest = new CoursePublicationAttempt(courseId, 3, false);
        allCoursePublicationAttempts.add(coursePublicationAttemptOld);
        allCoursePublicationAttempts.add(coursePublicationAttemptNew);
        allCoursePublicationAttempts.add(coursePublicationAttemptNewest);

        CoursePublicationAttempt lastSuccessfulCoursePublicationAttempt = allCoursePublicationAttempts.getLastSuccessfulCoursePublicationAttempt();
        assertEquals(courseId, lastSuccessfulCoursePublicationAttempt.getCourseId());
        assertEquals(Integer.valueOf(2), lastSuccessfulCoursePublicationAttempt.getVersion());
    }
}
