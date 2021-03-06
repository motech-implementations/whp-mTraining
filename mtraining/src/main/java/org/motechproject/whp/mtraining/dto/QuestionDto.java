package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.views.PublishCourseView;

import java.util.List;
import java.util.UUID;

/**
 * DTO Representation for Question class
 */
public class QuestionDto extends MdsEntity {

    @JsonView({PublishCourseView.class})
    private String name;

    @JsonView({PublishCourseView.class})
    private String description;

    private String correctOption;

    @JsonView({PublishCourseView.class})
    private List<Integer> options;

    @JsonView({PublishCourseView.class})
    private String externalId;

    private String explainingAnswerFilename;

    @JsonView({PublishCourseView.class})
    private UUID contentId;

    @JsonView({PublishCourseView.class})
    private int version;

    public QuestionDto() {
    }

    public QuestionDto(String name, String description, String correctOption, List<Integer> options, String externalId, String explainingAnswerFilename, int version) {
        this.name = name;
        this.description = description;
        this.correctOption = correctOption;
        this.options = options;
        this.externalId = externalId;
        this.explainingAnswerFilename = explainingAnswerFilename;
        this.version = version;
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

    public List<Integer> getOptions() {
        return options;
    }

    public void setOptions(List<Integer> options) {
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

    @JsonView({PublishCourseView.class})
    public AnswerDto getAnswer() {
        return new AnswerDto(correctOption, explainingAnswerFilename);
    }

    @JsonIgnore
    public void setAnswer(AnswerDto answerDto) {
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
