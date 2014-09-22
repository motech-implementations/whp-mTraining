package org.motechproject.whp.mtraining.dto;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.mds.annotations.Entity;

@Entity
public class AnswerSheetDto extends MdsEntity {

    @Field
    private ContentIdentifier question;
    @Field
    private String selectedOption;

    public AnswerSheetDto(ContentIdentifier question, String selectedOption) {
        this.question = question;
        this.selectedOption = selectedOption;
    }

    public ContentIdentifier getQuestion() {
        return question;
    }

    public void setQuestion(ContentIdentifier question) {
        this.question = question;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
