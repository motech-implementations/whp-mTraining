package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.map.annotate.JsonView;
import org.motechproject.whp.mtraining.domain.views.PublishCourseView;

import java.util.UUID;

/**
 * DTO Representation for Question class
 */
public class QuestionDto {

    @JsonView(PublishCourseView.class)
    private String name;

    @JsonView({PublishCourseView.class})
    private String description;

    private String correctOption;

    private String options;

    @JsonView({PublishCourseView.class})
    private String externalId;

    @JsonView({PublishCourseView.class})
    private String explainingAnswerFilename;

    @JsonView({PublishCourseView.class})
    private UUID contentId;

    public QuestionDto() {
    }

    public QuestionDto(String name, String description, String correctOption, String options, String externalId, String explainingAnswerFilename) {
        this.name = name;
        this.description = description;
        this.correctOption = correctOption;
        this.options = options;
        this.externalId = externalId;
        this.explainingAnswerFilename = explainingAnswerFilename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExplainingAnswerFilename() {
        return explainingAnswerFilename;
    }

    public void setExplainingAnswerFilename(String explainingAnswerFilename) {
        this.explainingAnswerFilename = explainingAnswerFilename;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }
}
