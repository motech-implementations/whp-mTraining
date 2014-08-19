package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CourseImportServiceIT extends BasePaxIT {

    @Inject
    private CoursePlanService coursePlanService;
    @Inject
    private MTrainingService mTrainingService;
    @Inject
    private MotechUserService motechUserService;
    @Inject
    private CourseConfigurationService courseConfigService;

    private CourseImportService courseImportService;

    @Before
    public void before() {
        courseImportService = new CourseImportService(coursePlanService, courseConfigService, motechUserService);
    }

    @Test
    public void shouldInvokeCourseServiceToAddACourseEventuallyByConstructingContentTree() {
        List<CourseCsvRequest> requests = asList(
                new CourseCsvRequest("courseplan", "course", CourseUnitState.Active, null, "courseplan description", "coursePlanFileName"),
                new CourseCsvRequest("course1", "module", CourseUnitState.Active, "courseplan", "course description", "courseFileName"),
                new CourseCsvRequest("chapter1", "CHAPTER", CourseUnitState.Active, "course1", "chapter1 description", "chapter1FileName"),
                new CourseCsvRequest("chapter2", "CHAPTER", CourseUnitState.Active, "course1", "chapter2 description", "chapter2FileName"),
                new CourseCsvRequest("lesson1", "lesson", CourseUnitState.Active, "chapter1", "lesson1 description", "filename1"),
                new CourseCsvRequest("lesson2", "lesson", CourseUnitState.Active, "chapter1", "lesson2 description", "filename2"),
                new CourseCsvRequest("lesson3", "lesson", CourseUnitState.Active, "chapter2", "lesson3 description", "filename3"),
                new CourseCsvRequest("lesson4", "lesson", CourseUnitState.Active, "chapter2", "lesson4 description", "filename4"),
                new CourseCsvRequest("lesson5", "lesson", CourseUnitState.Inactive, "chapter2", "lesson5 description", "filename4")
        );

        Long id = courseImportService.importCoursePlan(requests).getId();
        Course savedCourse = coursePlanService.getCoursePlanById(id).getCourses().get(0);

        assertCourseDetails(savedCourse);
        assertChapter(asList(savedCourse.getChapters().get(0)), asList(savedCourse.getChapters().get(1)));
    }

    private void assertCourseDetails(Course savedCourse) {
        assertEquals("course1", savedCourse.getName());
        assertEquals("courseFileName", savedCourse.getContent());
    }

    private void assertChapter(List<Chapter> module1Chapters, List<Chapter> module2Chapters) {
        assertChapterDetails(module1Chapters, "chapter1", "chapter1FileName");
        assertChapterDetails(module2Chapters, "chapter2", "chapter2FileName");
        assertMessageDto(module1Chapters.get(0).getLessons(), module2Chapters.get(0).getLessons());
    }

    private void assertChapterDetails(List<Chapter> chapters, String expectedName, String expectedFileName) {
        assertEquals(1, chapters.size());
        assertEquals(expectedName, chapters.get(0).getName());
        assertEquals(expectedFileName, chapters.get(0).getContent());
    }

    private void assertMessageDto(List<Lesson> chapter1Lessons, List<Lesson> chapter2Lessons) {
        assertEquals(2, chapter1Lessons.size());
        assertMessageDetails(chapter1Lessons.get(0), "lesson1", "lesson1 description", "filename1", CourseUnitState.Active);
        assertMessageDetails(chapter1Lessons.get(1), "lesson2", "lesson2 description", "filename2", CourseUnitState.Active);
        assertEquals(3, chapter2Lessons.size());
        assertMessageDetails(chapter2Lessons.get(0), "lesson3", "lesson3 description", "filename3", CourseUnitState.Active);
        assertMessageDetails(chapter2Lessons.get(1), "lesson4", "lesson4 description", "filename4", CourseUnitState.Active);
        assertMessageDetails(chapter2Lessons.get(2), "lesson5", "lesson5 description", "filename4", CourseUnitState.Inactive);
    }

    private void assertMessageDetails(Lesson lesson, String expectedName, String expectedDescription, String expectedFileName, CourseUnitState expectedStatus) {
        assertEquals(expectedName, lesson.getName());
        assertEquals(expectedFileName, lesson.getContent());
        assertEquals(expectedStatus, lesson.getState());
    }
}