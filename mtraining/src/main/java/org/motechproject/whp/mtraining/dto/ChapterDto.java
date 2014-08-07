package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.List;

/**
 * DTO representation for Chapter
 */
public class ChapterDto extends CourseUnitMetadataDto {

    private List<LessonDto> lessons;

    private QuizDto quiz;

    public ChapterDto() {
    }

    public ChapterDto(Integer id, String name, String description, CourseUnitState state, String filename,
                      DateTime creationDate, DateTime modificationDate, List<LessonDto> lessons, QuizDto quiz) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.lessons = lessons;
        this.quiz = quiz;
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
}
