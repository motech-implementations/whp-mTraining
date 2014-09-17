package org.motechproject.whp.mtraining.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the {@link org.motechproject.whp.mtraining.service.DtoFactoryService} class.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class DtoFactoryServiceIT extends BasePaxIT {

    @Inject
    DtoFactoryService dtoFactoryService;

    @Inject
    CoursePlanService coursePlanService;

    @Inject
    MTrainingService mTrainingService;

    @Inject
    ContentOperationService contentOperationService;

    private final String COURSE_PLAN_NAME = "DtoFactoryServiceIT CoursePlan";
    private final String COURSE_NAME = "DtoFactoryServiceIT Course";
    private final String CHAPTER_NAME = "DtoFactoryServiceIT Chapter";
    private final String LESSON_NAME = "DtoFactoryServiceIT Lesson";
    private final String QUIZ_NAME = "DtoFactoryServiceIT Quiz";

    private final String DESCRIPTION = "Test description";
    private final String NEW_DESCRIPTION = "New description";

    private final String FILENAME = "some short filename";
    private final String NEW_FILENAME = "new filename";

    private String content;
    private String newContent;

    @Before
    public void setup() {
        content = contentOperationService.codeIntoContent(FILENAME, DESCRIPTION, UUID.randomUUID(), 0);
        newContent = contentOperationService.codeIntoContent(NEW_FILENAME, NEW_DESCRIPTION, UUID.randomUUID(), 0);
        deleteFromDatabase();
    }

    @Test
    public void testDtoFactoryService() {
        shouldCreateAndUpdateCoursePlanFromCoursePlanDto();
        shouldCreateAndUpdateCourseFromModuleDto();
        shouldCreateAndUpdateChapterFromChapterDto();
        shouldCreateAndUpdateLessonFromLessonDto();
        shouldCreateAndUpdateQuizFromQuizDto();
    }

    @After
    public void deleteFromDatabase() {
        CoursePlan coursePlan = coursePlanService.getCoursePlanByName(COURSE_PLAN_NAME);
            coursePlanService.deleteCoursePlan(coursePlan);

        List<Course> courses = mTrainingService.getCourseByName(COURSE_NAME);
        for (Course course : courses) {
            mTrainingService.deleteCourse(course.getId());
        }

        List<Chapter> chapters = mTrainingService.getChapterByName(CHAPTER_NAME);
        for (Chapter chapter : chapters) {
            mTrainingService.deleteChapter(chapter.getId());
        }

        List<Lesson> lessons = mTrainingService.getLessonByName(LESSON_NAME);
        for (Lesson lesson : lessons) {
            mTrainingService.deleteLesson(lesson.getId());
        }

        List<Quiz> quizzes = mTrainingService.getQuizByName(QUIZ_NAME);
        for (Quiz quiz : quizzes) {
            mTrainingService.deleteQuiz(quiz.getId());
        }
    }

    private void shouldCreateAndUpdateCoursePlanFromCoursePlanDto() {
        List<CoursePlan> coursePlans;
        CoursePlan coursePlan;
        CoursePlanDto coursePlanDto = new CoursePlanDto(0, COURSE_PLAN_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null);

        dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
        coursePlan = coursePlanService.getCoursePlanByName(COURSE_PLAN_NAME);

        coursePlanDto.setId(coursePlan.getId());
        coursePlanDto.setDescription(NEW_DESCRIPTION);
        coursePlanDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
        coursePlan = coursePlanService.getCoursePlanByName(COURSE_PLAN_NAME);
        assertEquals(coursePlan.getContent(), newContent);
    }

    private void shouldCreateAndUpdateCourseFromModuleDto() {
        List<Course> courses;
        Course course;
        ModuleDto moduleDto = new ModuleDto(0, COURSE_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null);

        dtoFactoryService.createOrUpdateFromDto(moduleDto);
        courses = mTrainingService.getCourseByName(COURSE_NAME);
        assertEquals(courses.size(), 1);
        course = courses.get(0);
        assertEquals(course.getContent(), content);

        moduleDto.setId(course.getId());
        moduleDto.setDescription(NEW_DESCRIPTION);
        moduleDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(moduleDto);
        courses = mTrainingService.getCourseByName(COURSE_NAME);
        assertEquals(courses.size(), 1);
        course = courses.get(0);
        assertEquals(course.getContent(), newContent);
    }

    private void shouldCreateAndUpdateChapterFromChapterDto() {
        List<Chapter> chapters;
        Chapter chapter;
        ChapterDto chapterDto = new ChapterDto(0, CHAPTER_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null, null);

        dtoFactoryService.createOrUpdateFromDto(chapterDto);
        chapters = mTrainingService.getChapterByName(CHAPTER_NAME);
        assertEquals(chapters.size(), 1);
        chapter = chapters.get(0);
        assertEquals(chapter.getContent(), content);

        chapterDto.setId(chapter.getId());
        chapterDto.setDescription(NEW_DESCRIPTION);
        chapterDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(chapterDto);
        chapters = mTrainingService.getChapterByName(CHAPTER_NAME);
        assertEquals(chapters.size(), 1);
        chapter = chapters.get(0);
        assertEquals(chapter.getContent(), newContent);
    }

    private void shouldCreateAndUpdateLessonFromLessonDto() {
        List<Lesson> lessons;
        Lesson lesson;
        LessonDto lessonDto = new LessonDto(0, LESSON_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null);

        dtoFactoryService.createOrUpdateFromDto(lessonDto);
        lessons = mTrainingService.getLessonByName(LESSON_NAME);
        assertEquals(lessons.size(), 1);
        lesson = lessons.get(0);
        assertEquals(lesson.getContent(), content);

        lessonDto.setId(lesson.getId());
        lessonDto.setDescription(NEW_DESCRIPTION);
        lessonDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(lessonDto);
        lessons = mTrainingService.getLessonByName(LESSON_NAME);
        assertEquals(lessons.size(), 1);
        lesson = lessons.get(0);
        assertEquals(lesson.getContent(), newContent);
    }

    private void shouldCreateAndUpdateQuizFromQuizDto() {
        List<Quiz> quizzes;
        Quiz quiz;
        QuizDto quizDto = new QuizDto(0, QUIZ_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null);

        dtoFactoryService.createOrUpdateFromDto(quizDto);
        quizzes = mTrainingService.getQuizByName(QUIZ_NAME);
        assertEquals(quizzes.size(), 1);
        quiz = quizzes.get(0);
        assertEquals(quiz.getContent(), content);

        quizDto.setId(quiz.getId());
        quizDto.setDescription(NEW_DESCRIPTION);
        quizDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(quizDto);
        quizzes = mTrainingService.getQuizByName(QUIZ_NAME);
        assertEquals(quizzes.size(), 1);
        quiz = quizzes.get(0);
        assertEquals(quiz.getContent(), newContent);
    }
}
