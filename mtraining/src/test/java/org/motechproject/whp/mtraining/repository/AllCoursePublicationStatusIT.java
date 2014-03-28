package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.CoursePublicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllCoursePublicationStatusIT {

    @Autowired
    AllCoursePublicationStatus allCoursePublicationStatus;

    @Test
    public void shouldAddAndRetrieveACourse() {
        assertThat(allCoursePublicationStatus.all().size(), Is.is(0));

        UUID courseId = UUID.randomUUID();
        CoursePublicationStatus coursePublicationStatus = new CoursePublicationStatus(courseId, 3, true);

        allCoursePublicationStatus.add(coursePublicationStatus);

        CoursePublicationStatus retrievedCoursePublicationStatus = allCoursePublicationStatus.byCourseId(courseId);
        assertNotNull(retrievedCoursePublicationStatus);
    }

    @Before
    @After
    public void clearAllCoursePublicationStatus() {
        allCoursePublicationStatus.deleteAll();
    }

    @Test
    public void shouldReturnLatestCourse() {
        assertThat(allCoursePublicationStatus.all().size(), Is.is(0));
        UUID courseId = UUID.randomUUID();
        CoursePublicationStatus coursePublicationStatusOld = new CoursePublicationStatus(courseId, 1, true);
        CoursePublicationStatus coursePublicationStatusNew = new CoursePublicationStatus(courseId, 2, true);
        CoursePublicationStatus coursePublicationStatusNewest = new CoursePublicationStatus(courseId, 3, true);
        allCoursePublicationStatus.add(coursePublicationStatusOld);
        allCoursePublicationStatus.add(coursePublicationStatusNew);
        allCoursePublicationStatus.add(coursePublicationStatusNewest);
        CoursePublicationStatus retreivedCoursePublicationStatus = allCoursePublicationStatus.getLatestCoursePublicationStatus();
        assertEquals(courseId, retreivedCoursePublicationStatus.getCourseId());
        assertEquals(Integer.valueOf(3), retreivedCoursePublicationStatus.getVersion());
    }
}
