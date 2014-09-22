package org.motechproject.whp.mtraining.dto;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.mds.annotations.Entity;

@Entity
public class QuestionResultDto extends MdsEntity {

    @Field
    private ContentIdentifier questionIdentifier;
    @Field
    private String selectedOption;
    @Field
    private Boolean correct;

    public QuestionResultDto(ContentIdentifier questionIdentifier, String selectedOption, Boolean correct) {
        this.questionIdentifier = questionIdentifier;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public ContentIdentifier getQuestionIdentifier() {
        return questionIdentifier;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public Boolean isCorrect() {
        return correct;
    }
}
