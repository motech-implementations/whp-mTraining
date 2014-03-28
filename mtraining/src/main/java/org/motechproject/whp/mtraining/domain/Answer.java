package org.motechproject.whp.mtraining.domain;

import org.motechproject.mtraining.dto.AnswerDto;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
@EmbeddedOnly
public class Answer {

    @Persistent
    private String externalId;
    @Persistent
    private String correctOption;

    public Answer(String externalId, String correctAnswer) {
        this.externalId = externalId;
        this.correctOption = correctAnswer;
    }

    public Answer(AnswerDto answerDto) {
        this.externalId = answerDto.getExternalId();
        this.correctOption = answerDto.getCorrectOption();
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getExternalId() {
        return externalId;
    }
}
