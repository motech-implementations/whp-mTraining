package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;
import org.motechproject.whp.mtraining.repository.QuestionAttemptDataService;
import org.motechproject.whp.mtraining.service.QuestionAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("questionAttemptService")
public class QuestionAttemptServiceImpl implements QuestionAttemptService {

    @Autowired
    QuestionAttemptDataService questionAttemptDataService;

    @Override
    public QuestionAttempt createQuestionAttempt(QuestionAttempt QuestionAttempt) {
        return questionAttemptDataService.create(QuestionAttempt);
    }

    @Override
    public QuestionAttempt updateQuestionAttempt(QuestionAttempt QuestionAttempt) {
        return questionAttemptDataService.update(QuestionAttempt);
    }

    @Override
    public void deleteQuestionAttempt(QuestionAttempt QuestionAttempt) {
        questionAttemptDataService.delete(QuestionAttempt);
    }

    @Override
    public List<QuestionAttempt> getAllQuestionAttempts() {
        return questionAttemptDataService.retrieveAll();
    }

}
