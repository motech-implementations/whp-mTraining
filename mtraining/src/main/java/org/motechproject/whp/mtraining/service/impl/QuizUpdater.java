package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuizUpdater extends Updater<QuizDto> {

    private QuizService quizService;
    private QuestionUpdater questionUpdater;
    private List<QuizDto> existingQuizes;

    @Autowired
    public QuizUpdater(QuizService quizService, QuestionUpdater questionUpdater) {
        this.quizService = quizService;
        this.questionUpdater = questionUpdater;
        this.existingQuizes = new ArrayList<>();
    }

    @Override
    protected void updateContentId(QuizDto quizDto, QuizDto existingChapterDto) {
        quizDto.setContentId(existingChapterDto.getContentId());
    }

    @Override
    protected void updateChildContents(QuizDto quizDto) {
        questionUpdater.update(quizDto.getQuestions());
    }

    @Override
    protected List<QuizDto> getExistingContents() {
        if (existingQuizes.isEmpty()) {
            existingQuizes = quizService.getAllQuizes();
        }
        return existingQuizes;
    }

    @Override
    protected boolean isEqual(QuizDto quizDto1, QuizDto quizDto2) {
        return quizDto1.getName().equalsIgnoreCase(quizDto2.getName());
    }

    public void invalidateCache() {
        existingQuizes.clear();
        questionUpdater.invalidateCache();
    }
}
