package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.*;

import java.util.List;

/**
 * Includes building
 */
public interface DtoFactoryService {

    List<CoursePlanDto> getAllCourseDtosWithChildCollections();

    void createOrUpdateFromDto(CourseUnitMetadataDto courseUnitMetadataDto);

    List<CoursePlanDto> getAllCoursePlanDtos();

    CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan);

    List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans);

    CoursePlanDto getCoursePlanDtoById(long courseId);


    List<ModuleDto> getAllModuleDtos();

    ModuleDto convertModuleToDto(Course module);

    List<ModuleDto> convertModuleListToDtos (List<Course> modules);

    ModuleDto getModuleDtoById(long moduleId);


    List<ChapterDto> getAllChapterDtos();

    ChapterDto convertChapterToDto(Chapter chapter);

    List<ChapterDto> convertChapterListToDtos (List<Chapter> chapters);

    ChapterDto getChapterDtoById(long chapterId);


    List<LessonDto> getAllLessonDtos();

    LessonDto convertLessonToDto(Lesson lesson);

    List<LessonDto> convertLessonListToDtos (List<Lesson> lessons);

    LessonDto getLessonDtoById(long lessonId);


    List<QuizDto> getAllQuizDtos();

    QuizDto convertQuizToDto(Quiz quiz);

    List<QuizDto> convertQuizListToDtos (List<Quiz> quizzes);

    QuizDto getQuizDtoById(long quizId);

    QuestionDto convertQuestionToDto(Question question);

    List<QuestionDto> convertQuestionListToDtos (List<Question> questions);

    List<Question> convertDtosToQuestionList (List<QuestionDto> questionDtos);

}
