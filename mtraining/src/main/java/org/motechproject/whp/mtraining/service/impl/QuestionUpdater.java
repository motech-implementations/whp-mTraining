package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionUpdater extends Updater<QuestionDto> {

    private QuestionService questionService;
    private List<QuestionDto> existingQuestions;

    @Autowired
    public QuestionUpdater(QuestionService questionService) {
        this.questionService = questionService;
        this.existingQuestions = new ArrayList<>();
    }

    @Override
    protected void updateContentId(QuestionDto questionDto, QuestionDto existingChapterDto) {
        questionDto.setContentId(existingChapterDto.getContentId());
    }

    @Override
    protected void updateChildContents(QuestionDto questionDto) {
    }

    @Override
    protected List<QuestionDto> getExistingContents() {
        if (existingQuestions.isEmpty()) {
            existingQuestions = questionService.getAllQuestions();
        }
        return existingQuestions;
    }

    @Override
    protected boolean isEqual(QuestionDto quizDto1, QuestionDto quizDto2) {
        return quizDto1.getName().equalsIgnoreCase(quizDto2.getName());
    }

    public void invalidateCache() {
        existingQuestions.clear();
    }
}
