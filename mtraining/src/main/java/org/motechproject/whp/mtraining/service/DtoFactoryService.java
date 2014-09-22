package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.CourseUnitMetadata;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.dto.QuizDto;

import java.util.List;
import java.util.Set;

/**
 * Includes building
 */
public interface DtoFactoryService {

    List<CoursePlanDto> getAllCourseDtosWithChildCollections();

    CoursePlanDto getCourseDtoWithChildCollections(long courseId);

    CoursePlanDto removeInactiveCollections(CoursePlanDto course);

    void updateCourseAndChildCollections(CoursePlanDto course);

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

    void activateCourse(CoursePlanDto course);

    void updateState(Long id, CourseUnitState state);

    void increaseVersionsByRelations(Set<ManyToManyRelation> relation);

}
