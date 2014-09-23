package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;

import java.util.List;
import java.util.UUID;

/**
 * Includes operation over String content value
 * and other variable manipulation
 */
public interface ContentOperationService {

    void getMetadataFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content);

    String codeIntoContent(String filename, String description, UUID uuid, int version);

    void getQuestionNameAndDescriptionFromQuestion(QuestionDto questionDto, String question);

    String codeIntoQuestion(String questionName, String description, UUID uuid, int version);

    void getAnswersAndFilesNamesFromAnswer(QuestionDto questionDto, String answer);

    String codeAnswersAndFilesNamesIntoAnswer(String correctOption, List<Integer> options, String filename, String explainingAnswerFilename);

    String codeIntoQuizContent(String description, UUID uuid, int version, int noOfQuestionsToBePlayed);

    UUID getUuidFromJsonString(String content);

    int getVersionFromJsonString(String content);

    int getNoOfQuestionsToBePlayedFromJson(String json);
}
