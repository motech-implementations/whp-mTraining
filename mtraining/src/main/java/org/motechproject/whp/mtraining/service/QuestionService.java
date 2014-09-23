package org.motechproject.whp.mtraining.service;

import org.motechproject.mtraining.domain.Question;

public interface QuestionService {

    Question getQuestionByContentId(String contentId);

}
