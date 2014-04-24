package org.motechproject.whp.mtraining.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.QuestionDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "question", identityType = IdentityType.APPLICATION)
public class Question extends CourseContent {

    @Persistent(column = "question_audio_file_name")
    private String externalId;

    @Embedded(members = {
            @Persistent(name = "externalId", columns = @Column(name = "answer_audio_file_name")),
            @Persistent(name = "correctOption", columns = @Column(name = "correct_option")),
    })
    @Persistent
    private Answer answer;
    @Persistent
    private String description;

    /**
     * This is a comma separated list of valid answer options
     */
    @Persistent(column = "valid_answer_options")
    private String answerOptions;

    public Question(String name, UUID contentId, Integer version, String description, String externalId, String createdBy, DateTime createdOn, List<String> answerOptions, Answer answer, boolean isActive) {
        super(name, contentId, version, createdBy, createdOn, isActive);
        this.externalId = externalId;
        this.answer = answer;
        this.answerOptions = commaSeparatedOptions(answerOptions);
        this.description = description;
    }

    public Question(QuestionDto questionDto) {
        this(questionDto.getName(), questionDto.getContentId(), questionDto.getVersion(), questionDto.getDescription(), questionDto.getExternalContentId(), questionDto.getCreatedBy(), questionDto.getCreatedOn(),
                questionDto.getOptions(),
                mapToAnswer(questionDto.getAnswer()),
                questionDto.isActive());
    }

    private static Answer mapToAnswer(AnswerDto answerDto) {
        if (answerDto == null) {
            return null;
        }
        return new Answer(answerDto);
    }

    private String commaSeparatedOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        return StringUtils.join(options, ",");
    }

    @JsonIgnore
    public String getAnswerOptions() {
        return answerOptions;
    }

    public Answer getAnswer() {
        return answer;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }


    @JsonProperty("options")
    public String[] getAnswerOptionsArray() {
        if (StringUtils.isBlank(answerOptions)) {
            return new String[0];
        }
        return answerOptions.split(",");
    }
}
