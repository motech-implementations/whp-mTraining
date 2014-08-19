package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
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
    MTrainingService mTrainingService;

    @Autowired
    DtoFactoryService dtoFactoryService;

    @RequestMapping("/quizzes")
    @ResponseBody
    public List<QuizDto> getAllQuizzes() {
        return dtoFactoryService.getAllQuizDtos();
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public QuizDto getQuiz(@PathVariable long quizId) {
        return dtoFactoryService.getQuizDtoById(quizId);
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void createQuiz(@RequestBody QuizDto quiz) {
        dtoFactoryService.createOrUpdateFromDto(quiz);
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateQuiz(@RequestBody QuizDto quiz) {
        dtoFactoryService.createOrUpdateFromDto(quiz);
    }

    @RequestMapping(value = "/quiz/{quizId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeQuiz(@PathVariable long quizId) {
        mTrainingService.deleteQuiz(quizId);
    }

}
