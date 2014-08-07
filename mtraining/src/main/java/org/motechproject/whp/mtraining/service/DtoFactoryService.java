package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.*;

import java.util.List;

/**
 * Includes building
 */
public interface DtoFactoryService {

    List<CoursePlanDto> getAllCoursePlanDtos();

    CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan);

    List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans);

    CoursePlanDto getCoursePlanDtoById(long courseId);

    void createOrUpdateCoursePlanFromDto(CoursePlanDto coursePlanDto);

    CoursePlan generateCoursePlanFromDto(CoursePlanDto coursePlanDto);


    List<ModuleDto> getAllModuleDtos();

    ModuleDto convertModuleToDto(Course module);

    List<ModuleDto> convertModuleListToDtos (List<Course> modules);

    ModuleDto getModuleDtoById(long moduleId);

    void createOrUpdateModuleFromDto(ModuleDto moduleDto);

    Course generateModuleFromDto(ModuleDto moduleDto);


    List<ChapterDto> getAllChapterDtos();

    ChapterDto convertChapterToDto(Chapter chapter);

    List<ChapterDto> convertChapterListToDtos (List<Chapter> chapters);

    ChapterDto getChapterDtoById(long chapterId);

    void createOrUpdateChapterFromDto(ChapterDto chapterDto);

    Chapter generateChapterFromDto(ChapterDto chapterDto);


    List<LessonDto> getAllLessonDtos();

    LessonDto convertLessonToDto(Lesson lesson);

    List<LessonDto> convertLessonListToDtos (List<Lesson> lessons);

    LessonDto getLessonDtoById(long lessonId);

    void createOrUpdateLessonFromDto(LessonDto lessonDto);

    Lesson generateLessonFromDto(LessonDto lessonDto);


    List<QuizDto> getAllQuizDtos();

    QuizDto convertQuizToDto(Quiz quiz);

    List<QuizDto> convertQuizListToDtos (List<Quiz> quizzes);

    QuizDto getQuizDtoById(long quizId);

    void createOrUpdateQuizFromDto(QuizDto quizDto);

    Quiz generateQuizFromDto(QuizDto quizDto);

    QuestionDto convertQuestionToDto(Question question);

    List<QuestionDto> convertQuestionListToDtos (List<Question> questions);

    Question convertDtoToQuestion(QuestionDto questionDto);

    List<Question> convertDtosToQuestionList (List<QuestionDto> questionDtos);

}
