package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.map.annotate.JsonView;
import org.motechproject.whp.mtraining.domain.views.PublishCourseView;

/**
 * Dto representation needed for publishing Course JSON
 */
public class AnswerDto {

    @JsonView({PublishCourseView.class})
    private String correctOption;

    @JsonView({PublishCourseView.class})
    private String externalId;

    public AnswerDto() {
    }

    public AnswerDto(String correctOption, String externalId) {
        this.correctOption = correctOption;
        this.externalId = externalId;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
