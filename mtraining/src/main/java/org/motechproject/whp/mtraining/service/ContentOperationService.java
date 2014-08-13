package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;

/**
 * Includes operation over String content value
 * and other variable manipulation
 */
public interface ContentOperationService {

    String getFromJsonString(String jsonString, final String mappingName);

    String codeIntoJsonString(String jsonString, final String mappingName, final String value);

    void getFileNameAndDescriptionFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content);

    String codeFileNameAndDescriptionIntoContent(String filename, String description);

    void getQuestionNameAndDescriptionFromQuestion(QuestionDto questionDto, String question);

    String codeQuestionNameAndDescriptionIntoQuestion(String questionName, String description);

    void getAnswersAndFilesNamesFromAnswer(QuestionDto questionDto, String answer);

    String codeAnswersAndFilesNamesIntoAnswer(String correctAnswer, String options, String filename, String explainingAnswerFilename);
}
