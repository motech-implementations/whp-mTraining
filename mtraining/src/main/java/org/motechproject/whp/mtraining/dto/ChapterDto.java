package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO representation for Chapter
 */
public class ChapterDto extends CourseUnitMetadataDto {

    private List<LessonDto> lessons;

    private Set<Long> parentIds;

    private QuizDto quiz;

    public ChapterDto() {
    }

    public ChapterDto(Integer id, String name, String description, CourseUnitState state, String filename,
                      DateTime creationDate, DateTime modificationDate, List<LessonDto> lessons, Set<Long> parentIds, QuizDto quiz) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.lessons = lessons;
        this.quiz = quiz;

        if (parentIds == null) {
            this.parentIds = new LinkedHashSet<>();
        } else {
            this.parentIds = parentIds;
        }
    }

    public ChapterDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate) {
        super(id, name, state, creationDate, modificationDate);
    }

    public List<LessonDto> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonDto> lessons) {
        this.lessons = lessons;
    }

    public QuizDto getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizDto quiz) {
        this.quiz = quiz;
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }
}
