package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.Question;
import org.motechproject.whp.mtraining.repository.QuestionDataService;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    QuestionDataService questionDataService;

    @Autowired
    ContentOperationService contentOperationService;

    @Override
    public Question getQuestionByContentId(String contentId) {
        if (contentId == null || contentId.isEmpty()) {
            return null;
        }
        List<Question> questions = questionDataService.retrieveAll();
        for (Question question : questions) {
            String uuid = contentOperationService.getUuidFromJsonString(question.getQuestion()).toString();
            if (uuid.equals(contentId)) {
                return question;
            }
        }
        return null;
    }


}
