package org.motechproject.whp.mtraining.dto;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.mds.annotations.Entity;
import java.util.List;

@Entity
public class QuizAnswerSheetDto extends MdsEntity {

    @Field
    private String remediId;
    @Field
    private ContentIdentifier quizIdentifier;
    @Field
    private List<AnswerSheetDto> answerSheetDtos;

    public QuizAnswerSheetDto(String remediId, ContentIdentifier quizIdentifier, List<AnswerSheetDto> answerSheetDtos) {
        this.quizIdentifier = quizIdentifier;
        this.answerSheetDtos = answerSheetDtos;
    }

    public String getRemediId() {
        return remediId;
    }

    public void setRemediId(String remediId) {
        this.remediId = remediId;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifier quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public List<AnswerSheetDto> getAnswerSheetDtos() {
        return answerSheetDtos;
    }

    public void setAnswerSheetDtos(List<AnswerSheetDto> answerSheetDtos) {
        this.answerSheetDtos = answerSheetDtos;
    }
}
