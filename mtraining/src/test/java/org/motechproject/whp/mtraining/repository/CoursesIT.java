package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class CoursesIT {

    @Autowired
    Courses courses;

    @Test
    public void shouldAddAndRetrieveACourse() {
        assertThat(courses.all().size(), Is.is(0));

        UUID courseId = UUID.randomUUID();
        Course course = new Course(courseId, 3, true);

        courses.add(course);

        Course retrievedCourse = courses.byCourseId(courseId);
        assertNotNull(retrievedCourse);
    }

    @Before
    @After
    public void clearCourses() {
        courses.deleteAll();
    }

    @Test
    public void shouldReturnLatestCourse() {
        assertThat(courses.all().size(), Is.is(0));
        UUID courseId = UUID.randomUUID();
        Course courseOld = new Course(courseId, 1, true);
        Course courseNew = new Course(courseId, 2, true);
        Course courseNewest = new Course(courseId, 3, true);
        courses.add(courseOld);
        courses.add(courseNew);
        courses.add(courseNewest);
        Course retreivedCourse = courses.getLatestCourse();
        assertEquals(courseId, retreivedCourse.getCourseId());
        assertEquals(Integer.valueOf(3), retreivedCourse.getVersion());
    }
}
