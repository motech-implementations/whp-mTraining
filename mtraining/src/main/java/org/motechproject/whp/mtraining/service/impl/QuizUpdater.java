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
    private List<QuizDto> existingQuizzes;

    @Autowired
    public QuizUpdater(QuizService quizService, QuestionUpdater questionUpdater) {
        this.quizService = quizService;
        this.questionUpdater = questionUpdater;
        this.existingQuizzes = new ArrayList<>();
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
        if (existingQuizzes.isEmpty()) {
            existingQuizzes = quizService.getAllQuizes();
        }
        return existingQuizzes;
    }

    @Override
    protected boolean isEqual(QuizDto quizDto1, QuizDto quizDto2) {
        return quizDto1.getName().equalsIgnoreCase(quizDto2.getName());
    }

    public void invalidateCache() {
        existingQuizzes.clear();
        questionUpdater.invalidateCache();
    }
}
