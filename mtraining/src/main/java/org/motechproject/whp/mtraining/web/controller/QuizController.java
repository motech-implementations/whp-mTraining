package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.repository.QuizDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web API for Quiz
 */
@Controller
public class QuizController {

    @Autowired
    QuizDataService quizDataService;

    @RequestMapping("/quizzes")
    @ResponseBody
    public List<Quiz> getAllQuizzes() {
        return quizDataService.retrieveAll();
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Quiz getQuiz(@PathVariable long quizId) {
        return quizDataService.findQuizById(quizId);
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizDataService.create(quiz);
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public Quiz updateQuiz(@RequestBody Quiz quiz) {
        return quizDataService.update(quiz);
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeQuiz(@PathVariable long quizId) {
        Quiz quiz = quizDataService.findQuizById(quizId);
        quizDataService.delete(quiz);
    }

}
