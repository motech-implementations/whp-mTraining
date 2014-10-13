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

    @Before
    public void setup() {
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
        if (coursePlan != null) {
            coursePlanService.deleteCoursePlan(coursePlan);
        }

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
        CoursePlanDto newCoursePlanDto = new CoursePlanDto();
        CoursePlanDto coursePlanDto = new CoursePlanDto(0, COURSE_PLAN_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null, null);

        dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
        CoursePlan coursePlan = coursePlanService.getCoursePlanByName(COURSE_PLAN_NAME);

        coursePlanDto.setId(coursePlan.getId());
        coursePlanDto.setDescription(NEW_DESCRIPTION);
        coursePlanDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(coursePlanDto);
        coursePlan = coursePlanService.getCoursePlanByName(COURSE_PLAN_NAME);
        contentOperationService.getMetadataFromContent(newCoursePlanDto, coursePlan.getContent());
        assertEquals(newCoursePlanDto.getDescription(), NEW_DESCRIPTION);
        assertEquals(newCoursePlanDto.getExternalId(), NEW_FILENAME);
    }

    private void shouldCreateAndUpdateCourseFromModuleDto() {
        List<Course> courses;
        ModuleDto newModuleDto = new ModuleDto();
        ModuleDto moduleDto = new ModuleDto(0, COURSE_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null);

        dtoFactoryService.createOrUpdateFromDto(moduleDto);
        courses = mTrainingService.getCourseByName(COURSE_NAME);
        assertEquals(courses.size(), 1);
        Course course = courses.get(0);
        contentOperationService.getMetadataFromContent(newModuleDto, course.getContent());
        assertEquals(newModuleDto.getDescription(), DESCRIPTION);
        assertEquals(newModuleDto.getExternalId(), FILENAME);

        moduleDto.setId(course.getId());
        moduleDto.setDescription(NEW_DESCRIPTION);
        moduleDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(moduleDto);
        courses = mTrainingService.getCourseByName(COURSE_NAME);
        assertEquals(courses.size(), 1);
        course = courses.get(0);
        contentOperationService.getMetadataFromContent(newModuleDto, course.getContent());
        assertEquals(newModuleDto.getDescription(), NEW_DESCRIPTION);
        assertEquals(newModuleDto.getExternalId(), NEW_FILENAME);
    }

    private void shouldCreateAndUpdateChapterFromChapterDto() {
        List<Chapter> chapters;
        ChapterDto newChapterDto = new ChapterDto();
        ChapterDto chapterDto = new ChapterDto(0, CHAPTER_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null, null);

        dtoFactoryService.createOrUpdateFromDto(chapterDto);
        chapters = mTrainingService.getChapterByName(CHAPTER_NAME);
        assertEquals(chapters.size(), 1);
        Chapter chapter = chapters.get(0);
        contentOperationService.getMetadataFromContent(newChapterDto, chapter.getContent());
        assertEquals(newChapterDto.getDescription(), DESCRIPTION);
        assertEquals(newChapterDto.getExternalId(), FILENAME);

        chapterDto.setId(chapter.getId());
        chapterDto.setDescription(NEW_DESCRIPTION);
        chapterDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(chapterDto);
        chapters = mTrainingService.getChapterByName(CHAPTER_NAME);
        assertEquals(chapters.size(), 1);
        chapter = chapters.get(0);
        contentOperationService.getMetadataFromContent(newChapterDto, chapter.getContent());
        assertEquals(newChapterDto.getDescription(), NEW_DESCRIPTION);
        assertEquals(newChapterDto.getExternalId(), NEW_FILENAME);
    }

    private void shouldCreateAndUpdateLessonFromLessonDto() {
        List<Lesson> lessons;
        LessonDto newLessonDto = new LessonDto();
        LessonDto lessonDto = new LessonDto(0, LESSON_NAME, DESCRIPTION, CourseUnitState.Inactive, FILENAME,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null);

        dtoFactoryService.createOrUpdateFromDto(lessonDto);
        lessons = mTrainingService.getLessonByName(LESSON_NAME);
        assertEquals(lessons.size(), 1);
        Lesson lesson = lessons.get(0);
        contentOperationService.getMetadataFromContent(newLessonDto, lesson.getContent());
        assertEquals(newLessonDto.getDescription(), DESCRIPTION);
        assertEquals(newLessonDto.getExternalId(), FILENAME);

        lessonDto.setId(lesson.getId());
        lessonDto.setDescription(NEW_DESCRIPTION);
        lessonDto.setExternalId(NEW_FILENAME);

        dtoFactoryService.createOrUpdateFromDto(lessonDto);
        lessons = mTrainingService.getLessonByName(LESSON_NAME);
        assertEquals(lessons.size(), 1);
        lesson = lessons.get(0);
        contentOperationService.getMetadataFromContent(newLessonDto, lesson.getContent());
        assertEquals(newLessonDto.getDescription(), NEW_DESCRIPTION);
        assertEquals(newLessonDto.getExternalId(), NEW_FILENAME);
    }

    private void shouldCreateAndUpdateQuizFromQuizDto() {
        List<Quiz> quizzes;
        QuizDto newQuizDto = new QuizDto();
        QuizDto quizDto = new QuizDto(0, QUIZ_NAME, DESCRIPTION, CourseUnitState.Inactive,
                ISODateTimeUtil.nowInTimeZoneUTC(), ISODateTimeUtil.nowInTimeZoneUTC(), null, null);

        dtoFactoryService.createOrUpdateFromDto(quizDto);
        quizzes = mTrainingService.getQuizByName(QUIZ_NAME);
        assertEquals(quizzes.size(), 1);
        Quiz quiz = quizzes.get(0);
        contentOperationService.getMetadataFromContent(newQuizDto, quiz.getContent());
        assertEquals(newQuizDto.getDescription(), DESCRIPTION);

        quizDto.setId(quiz.getId());
        quizDto.setDescription(NEW_DESCRIPTION);

        dtoFactoryService.createOrUpdateFromDto(quizDto);
        quizzes = mTrainingService.getQuizByName(QUIZ_NAME);
        assertEquals(quizzes.size(), 1);
        quiz = quizzes.get(0);
        contentOperationService.getMetadataFromContent(newQuizDto, quiz.getContent());
        assertEquals(newQuizDto.getDescription(), NEW_DESCRIPTION);
    }
}
