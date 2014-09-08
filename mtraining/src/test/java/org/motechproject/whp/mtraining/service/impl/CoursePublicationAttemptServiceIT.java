package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.junit.rules.TestName;
import org.osgi.framework.BundleContext;


import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CoursePublicationAttemptServiceIT extends BasePaxIT {

    @Rule
    public TestName testName = new TestName();

    @Inject
    CoursePublicationAttemptService coursePublicationAttemptService;

    @Test
    public void shouldAddAndRetrieveACourse() {
        long courseId = 123L;
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, true);

        coursePublicationAttemptService.createCoursePublicationAttempt(coursePublicationAttempt);

        CoursePublicationAttempt retrievedCoursePublicationAttempt = coursePublicationAttemptService.getCoursePublicationAttemptByCourseId(courseId);
        assertNotNull(retrievedCoursePublicationAttempt);

        coursePublicationAttemptService.deleteCoursePublicationAttempt(retrievedCoursePublicationAttempt);
    }

}
