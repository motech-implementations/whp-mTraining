package org.motechproject.whp.mtraining.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service("contentOperationService")
public class ContentOperationServiceImpl implements ContentOperationService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

    public static final String DESCRIPTION_MAPPING_NAME = "description";
    public static final String FILENAME_MAPPING_NAME = "filename";
    public static final String QUESTION_MAPPING_NAME = "name";
    public static final String ANSWER_MAPPING_NAME = "answer";
    public static final String OPTIONS_MAPPING_NAME = "options";
    public static final String ANSWER_FILENAME_MAPPING_NAME = "answerFilename";

    @Override
    public String getFromJsonString(String jsonString, String mappingName) {
        if (jsonString == null){
            return null;
        }
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(jsonString,
                    new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            LOG.error("mtraining.error.getFromJsonString" + e.getMessage());
            return null;
        }
        return map.get(mappingName);
    }

    @Override
    public String codeIntoJsonString(String jsonString, String mappingName, String value) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode;
        if (jsonString == ""){
            objectNode = new ObjectNode(JsonNodeFactory.instance);
        }
        else {
            try {
                objectNode = (ObjectNode) mapper.readTree(jsonString);
            } catch (IOException e) {
                LOG.error("mtraining.error.codeIntoJsonString" + e.getMessage());
                return jsonString;
            }
        }
        objectNode.put(mappingName, value);
        return objectNode.toString();
    }

    @Override
    public void getFileNameAndDescriptionFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content) {
        courseUnitMetadataDto.setDescription(getFromJsonString(content, DESCRIPTION_MAPPING_NAME));
        courseUnitMetadataDto.setFilename(getFromJsonString(content, FILENAME_MAPPING_NAME));
    }

    @Override
    public String codeFileNameAndDescriptionIntoContent(String filename, String description) {
        String content = "";
        content = codeIntoJsonString(content, FILENAME_MAPPING_NAME, filename);
        content = codeIntoJsonString(content, DESCRIPTION_MAPPING_NAME, description);
        return content;
    }

    @Override
    public void getQuestionNameAndDescriptionFromQuestion(QuestionDto questionDto, String question) {
        questionDto.setName(getFromJsonString(question, QUESTION_MAPPING_NAME));
        questionDto.setDescription(getFromJsonString(question, DESCRIPTION_MAPPING_NAME));
    }

    @Override
    public String codeQuestionNameAndDescriptionIntoQuestion(String questionName, String description) {
        String question = "";
        question = codeIntoJsonString(question, QUESTION_MAPPING_NAME, questionName);
        question = codeIntoJsonString(question, DESCRIPTION_MAPPING_NAME, description);
        return  question;
    }

    @Override
    public void getAnswersAndFilesNamesFromAnswer(QuestionDto questionDto, String answer) {
        questionDto.setCorrectAnswer(getFromJsonString(answer, ANSWER_MAPPING_NAME));
        questionDto.setOptions(getFromJsonString(answer, OPTIONS_MAPPING_NAME));
        questionDto.setFilename(getFromJsonString(answer, FILENAME_MAPPING_NAME));
        questionDto.setExplainingAnswerFilename(getFromJsonString(answer, ANSWER_FILENAME_MAPPING_NAME));
    }

    @Override
    public String codeAnswersAndFilesNamesIntoAnswer(String correctAnswer, String options, String filename, String explainingAnswerFilename) {
        String answer = "";
        answer = codeIntoJsonString(answer, ANSWER_MAPPING_NAME, correctAnswer);
        answer = codeIntoJsonString(answer, OPTIONS_MAPPING_NAME, options);
        answer = codeIntoJsonString(answer, FILENAME_MAPPING_NAME, filename);
        answer = codeIntoJsonString(answer, ANSWER_FILENAME_MAPPING_NAME, explainingAnswerFilename);
        return  answer;
    }
}
