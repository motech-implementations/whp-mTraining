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

        public CourseUnitMetadataDto getDto(CourseUnitMetadata courseUnitMetadata);

    public QuestionDto getDto(Question question);

    public List<?> getDtos(List<?> list);

    List<CoursePlanDto> getAllCoursePlanDtos();

    List<ModuleDto> getAllModuleDtos();

    List<ChapterDto> getAllChapterDtos();

    List<LessonDto> getAllLessonDtos();

    List<QuizDto> getAllQuizDtos();

    CoursePlanDto getCoursePlanDtoById(long courseId);

    ModuleDto getModuleDtoById(long moduleId);

    ChapterDto getChapterDtoById(long chapterId);

    LessonDto getLessonDtoById(long lessonId);

    QuizDto getQuizDtoById(long quizId);
}
