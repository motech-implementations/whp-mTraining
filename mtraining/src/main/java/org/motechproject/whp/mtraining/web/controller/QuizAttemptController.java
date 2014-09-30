package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;
import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.motechproject.whp.mtraining.repository.QuestionAttemptDataService;
import org.motechproject.whp.mtraining.service.QuizAttemptService;
import org.motechproject.whp.mtraining.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class QuizAttemptController {

    @Autowired
    QuizAttemptService quizAttemptService;

    @Autowired
    QuestionAttemptDataService questionAttemptDataService;

    @RequestMapping("/quizAttempts")
    @ResponseBody
    public List<QuizAttempt> getAllQuizAttempts() {
        List<QuizAttempt> quizAttempts = quizAttemptService.getAllQuizAttempts();
        // fix for JDODetachedFieldAccessException with multi level inheritance in MDS
        for(QuizAttempt quizAttempt : quizAttempts) {
            List<QuestionAttempt> questionAttempts = new ArrayList<>();
            for(QuestionAttempt questionAttempt : quizAttempt.getQuestionAttempts()) {
                questionAttempts.add(questionAttemptDataService.findQuestionAttemptById(questionAttempt.getId()));
            }
            quizAttempt.setQuestionAttempts(questionAttempts);
        }
        return quizAttempts;
    }

}
