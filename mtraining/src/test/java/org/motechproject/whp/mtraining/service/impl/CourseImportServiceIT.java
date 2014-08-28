package org.motechproject.whp.mtraining.service.impl;

import org.junit.After;
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
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
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
    @Inject
    private ContentOperationService contentOperationService;

    private CourseImportService courseImportService;

    @Before
    public void before() {
        courseImportService = new CourseImportService(coursePlanService, courseConfigService, motechUserService, contentOperationService);
        deleteFromDatabase();
    }

    @Test
    public void shouldInvokeCourseServiceToAddACourseEventuallyByConstructingContentTree() {
        List<CourseCsvRequest> requests = asList(
                new CourseCsvRequest("CourseImportServiceIT coursePlan", "course", CourseUnitState.Active, null, "coursePlan description", "coursePlanFileName"),
                new CourseCsvRequest("CourseImportServiceIT course1", "module", CourseUnitState.Active, "CourseImportServiceIT coursePlan", "course description", "courseFileName"),
                new CourseCsvRequest("CourseImportServiceIT course2", "module", CourseUnitState.Active, "CourseImportServiceIT coursePlan", "course2 description", "course2FileName"),
                new CourseCsvRequest("CourseImportServiceIT chapter1", "CHAPTER", CourseUnitState.Active, "CourseImportServiceIT course1", "chapter1 description", "chapter1FileName"),
                new CourseCsvRequest("CourseImportServiceIT chapter2", "CHAPTER", CourseUnitState.Active, "CourseImportServiceIT course2", "chapter2 description", "chapter2FileName"),
                new CourseCsvRequest("CourseImportServiceIT lesson1", "lesson", CourseUnitState.Active, "CourseImportServiceIT chapter1", "lesson1 description", "filename1"),
                new CourseCsvRequest("CourseImportServiceIT lesson2", "lesson", CourseUnitState.Active, "CourseImportServiceIT chapter1", "lesson2 description", "filename2"),
                new CourseCsvRequest("CourseImportServiceIT lesson3", "lesson", CourseUnitState.Active, "CourseImportServiceIT chapter2", "lesson3 description", "filename3"),
                new CourseCsvRequest("CourseImportServiceIT lesson4", "lesson", CourseUnitState.Active, "CourseImportServiceIT chapter2", "lesson4 description", "filename4"),
                new CourseCsvRequest("CourseImportServiceIT lesson5", "lesson", CourseUnitState.Inactive, "CourseImportServiceIT chapter2", "lesson5 description", "filename4")
        );

        Long id = courseImportService.importCoursePlan(requests).getId();
        CoursePlan coursePlan = coursePlanService.getCoursePlanById(id);

        assertCoursePlanDetails(coursePlan);
    }

    @After
    public void deleteFromDatabase(){
        List<CoursePlan> coursePlans = coursePlanService.getCoursePlanByName("CourseImportServiceIT coursePlan");
        for (CoursePlan coursePlan : coursePlans) {
            coursePlanService.deleteCoursePlan(coursePlan);
        }

        List<Course> courses = mTrainingService.getCourseByName("CourseImportServiceIT course1");
        courses.addAll(mTrainingService.getCourseByName("CourseImportServiceIT course2"));
        for (Course course : courses) {
            mTrainingService.deleteCourse(course.getId());
        }

        List<Chapter> chapters = mTrainingService.getChapterByName("CourseImportServiceIT chapter1");
        chapters.addAll(mTrainingService.getChapterByName("CourseImportServiceIT chapter2"));
        for (Chapter chapter : chapters) {
            mTrainingService.deleteChapter(chapter.getId());
        }

        List<Lesson> lessons = mTrainingService.getLessonByName("CourseImportServiceIT lesson1");
        lessons.addAll(mTrainingService.getLessonByName("CourseImportServiceIT lesson2"));
        lessons.addAll(mTrainingService.getLessonByName("CourseImportServiceIT lesson3"));
        lessons.addAll(mTrainingService.getLessonByName("CourseImportServiceIT lesson4"));
        lessons.addAll(mTrainingService.getLessonByName("CourseImportServiceIT lesson5"));
        for (Lesson lesson : lessons) {
            mTrainingService.deleteLesson(lesson.getId());
        }
    }

    private void assertCoursePlanDetails(CoursePlan coursePlan) {
        CoursePlanDto coursePlanDto = new CoursePlanDto();
        contentOperationService.getFileNameAndDescriptionFromContent(coursePlanDto, coursePlan.getContent());
        assertEquals("CourseImportServiceIT coursePlan", coursePlan.getName());
        assertEquals("coursePlan description", coursePlanDto.getDescription());
        assertEquals("coursePlanFileName", coursePlanDto.getExternalId());

        assertCourse(coursePlan.getCourses().get(0), coursePlan.getCourses().get(1));
    }

    private void assertCourse(Course course1, Course course2) {
        assertCourseDetails(course1, "CourseImportServiceIT course1", "courseFileName", "course description");
        assertCourseDetails(course2, "CourseImportServiceIT course2", "course2FileName", "course2 description");
        assertChapter(course1.getChapters(), course2.getChapters());
    }

    private void assertCourseDetails(Course course, String expectedName, String expectedFileName, String expectedDescription) {
        ModuleDto moduleDto = new ModuleDto();
        contentOperationService.getFileNameAndDescriptionFromContent(moduleDto, course.getContent());

        assertEquals(expectedName, course.getName());
        assertEquals(expectedFileName, moduleDto.getExternalId());
        assertEquals(expectedDescription, moduleDto.getDescription());
    }

    private void assertChapter(List<Chapter> chapterList1, List<Chapter> chapterList2) {
        assertChapterDetails(chapterList1, "CourseImportServiceIT chapter1", "chapter1FileName", "chapter1 description");
        assertChapterDetails(chapterList2, "CourseImportServiceIT chapter2", "chapter2FileName", "chapter2 description");
        assertLesson(chapterList1.get(0).getLessons(), chapterList2.get(0).getLessons());
    }

    private void assertChapterDetails(List<Chapter> chapters, String expectedName, String expectedFileName, String expectedDescription) {
        assertEquals(1, chapters.size());
        ChapterDto chapterDto = new ChapterDto();
        contentOperationService.getFileNameAndDescriptionFromContent(chapterDto, chapters.get(0).getContent());

        assertEquals(expectedName, chapters.get(0).getName());
        assertEquals(expectedFileName, chapterDto.getExternalId());
        assertEquals(expectedDescription, chapterDto.getDescription());
    }

    private void assertLesson(List<Lesson> chapter1Lessons, List<Lesson> chapter2Lessons) {
        assertEquals(2, chapter1Lessons.size());
        assertLessonDetails(chapter1Lessons.get(0), "CourseImportServiceIT lesson1", "filename1", "lesson1 description", CourseUnitState.Active);
        assertLessonDetails(chapter1Lessons.get(1), "CourseImportServiceIT lesson2", "filename2", "lesson2 description", CourseUnitState.Active);
        assertEquals(3, chapter2Lessons.size());
        assertLessonDetails(chapter2Lessons.get(0), "CourseImportServiceIT lesson3", "filename3", "lesson3 description", CourseUnitState.Active);
        assertLessonDetails(chapter2Lessons.get(1), "CourseImportServiceIT lesson4", "filename4", "lesson4 description", CourseUnitState.Active);
        assertLessonDetails(chapter2Lessons.get(2), "CourseImportServiceIT lesson5", "filename4","lesson5 description",  CourseUnitState.Inactive);
    }

    private void assertLessonDetails(Lesson lesson, String expectedName, String expectedFileName, String expectedDescription, CourseUnitState expectedStatus) {
        LessonDto lessonDto = new LessonDto();
        contentOperationService.getFileNameAndDescriptionFromContent(lessonDto, lesson.getContent());

        assertEquals(expectedName, lesson.getName());
        assertEquals(expectedStatus, lesson.getState());
        assertEquals(expectedFileName, lessonDto.getExternalId());
        assertEquals(expectedDescription, lessonDto.getDescription());
    }
}
