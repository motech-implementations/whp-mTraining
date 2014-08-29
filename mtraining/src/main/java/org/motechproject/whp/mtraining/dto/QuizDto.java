package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.map.annotate.JsonView;
import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.domain.views.PublishCourseView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO representation for Quiz class
 */
public class QuizDto extends CourseUnitMetadataDto {

    @JsonView({PublishCourseView.class})
    private List<QuestionDto> questions;

    private Set<Long> parentIds;

    @JsonView({PublishCourseView.class})
    private double passPercentage;

    private boolean isInRelation;

    public QuizDto() {
    }

    public QuizDto(Integer id, String name, String description, CourseUnitState state, String filename,
                   DateTime creationDate, DateTime modificationDate, List<QuestionDto> questions, Set<Long> parentIds) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        if (questions == null) {
            this.questions = new ArrayList<>();
        } else {
            this.questions = questions;
        }
        if (parentIds == null) {
            this.parentIds = new LinkedHashSet<>();
        } else {
            this.parentIds = parentIds;
        }
    }

    public QuizDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate,
                   double passPercentage, List<QuestionDto> questions) {
        super(id, name, state, creationDate, modificationDate);
        this.passPercentage = passPercentage;
        if (questions == null) {
            this.questions = new ArrayList<>();
        } else {
            this.questions = questions;
        }
        if (parentIds == null) {
            this.parentIds = new LinkedHashSet<>();
        }
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public boolean isInRelation() {
        return isInRelation;
    }

    public void setInRelation(boolean isInRelation) {
        this.isInRelation = isInRelation;
    }

    @JsonView({PublishCourseView.class})
    public int getNoOfQuestionsToBePlayed() {
        return questions.size();
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }
}
