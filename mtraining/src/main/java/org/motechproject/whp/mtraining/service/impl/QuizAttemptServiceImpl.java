package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.motechproject.whp.mtraining.repository.QuizAttemptDataService;
import org.motechproject.whp.mtraining.service.QuizAttemptService;
import org.motechproject.whp.mtraining.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("quizAttemptService")
public class QuizAttemptServiceImpl implements QuizAttemptService {

    @Autowired
    QuizAttemptDataService quizAttemptDataService;

    @Override
    public QuizAttempt createQuizAttempt(QuizAttempt quizAttempt) {
        return quizAttemptDataService.create(quizAttempt);
    }

    @Override
    public QuizAttempt updateQuizAttempt(QuizAttempt quizAttempt) {
        return quizAttemptDataService.update(quizAttempt);
    }

    @Override
    public void deleteQuizAttempt(QuizAttempt quizAttempt) {
        quizAttemptDataService.delete(quizAttempt);
    }

    @Override
    public List<QuizAttempt> getAllQuizAttempts() {
        return quizAttemptDataService.retrieveAll();
    }

}
